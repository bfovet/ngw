/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.AbstractNestedWorkflowNode;
import gov.sandia.dart.workflow.runtime.core.Datum;
import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.LoggingWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.NodeDatabase;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.RuntimeParameter;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Conductor;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class NestedWorkflowNode extends AbstractNestedWorkflowNode {

	private static final String RESPONSE_PORT = "responses_csv";
	private static final String CONCURRENCY = "concurrency";
	public static final String FILENAME = "fileName";
	private HashMap<String, Datum> cachedInputs;
	private static Set<String> myBuiltIns = new HashSet<>(Arrays.asList(FILENAME, CONCURRENCY, WORKDIR_NAME_TEMPLATE));
	private static Set<String> reservedPropertyNames = new HashSet<>(getReservedProperties().stream().map(p -> p.getName()).collect(Collectors.toList()));

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {								

		Map<String, Map<Integer, Object>> responses = new TreeMap<>();		
		AtomicInteger id = new AtomicInteger(1);
		AtomicReference<Throwable> error = new AtomicReference<>();

		Map<String, RuntimeParameter> paramsFromOuterScope = new TreeMap<>();
		for (String name: runtime.getParameterNames()) {
			RuntimeParameter p = runtime.getParameter(name);
			if (p.isGlobal() && !isBuiltIn(name)) {
				paramsFromOuterScope.put(name, new RuntimeParameter(p));
			}
		}
		combine(paramsFromOuterScope, getParamsFromInputsAndProperties(properties, workflow, runtime));

		List<WorkflowConductor> conductors = getConductors(properties, workflow, runtime);

		try (UsedPrintStream ps = new UsedPrintStream(new File(getComponentWorkDir(runtime, properties), getResponseFileName()))) {		
			ExecutorService service = Executors.newFixedThreadPool(getConcurrency(properties));
			executeSamples(conductors, paramsFromOuterScope, 0, properties, workflow, runtime,   id, responses, ps, service, error);
			service.shutdown();
			// TODO We may need to fail faster if there is an exception?
			service.awaitTermination(365, TimeUnit.DAYS);

			if (error.get() != null)
				throw error.get();
			
		} catch (SAWWorkflowException e) {
			throw e;
		} catch (Throwable e) {
			throw new SAWWorkflowException("Error executing sample " + (id.get() - 1) + " in nested workflow" , e);
		}
		synchronized(responses) {
			Map<String, Object> nestedResponses = getResponses(responses);
			nestedResponses.put(RESPONSE_PORT, new File(getComponentWorkDir(runtime, properties), getResponseFileName()));
			return nestedResponses;
		}

	}	
	
	private boolean isBuiltIn(String name) {
		return RuntimeData.isBuiltIn(name) || myBuiltIns.contains(name) || reservedPropertyNames.contains(name);
	}

	protected String getResponseFileName() {
		return getName() + ".responses.csv";
	}	
	
	void executeSamples(List<WorkflowConductor> conductors, Map<String, RuntimeParameter> paramsFromOuterScope, int depth,
			Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, AtomicInteger id,
			Map<String, Map<Integer, Object>> responses, UsedPrintStream ps, ExecutorService service, AtomicReference<Throwable> error) {
		WorkflowConductor conductor = conductors.get(depth);
		if (depth == conductors.size() - 1) {
			for (Map<String, String> paramsFromConductor: conductor) {
				int index = id.getAndIncrement();
				if (runtime.isCancelled())
					break;			
				service.submit(
						() -> {
							try {
								// TODO I think cancel is broken
								if (error.get() == null && !runtime.isCancelled())
									runOneSample(paramsFromOuterScope, paramsFromConductor, conductor, properties, workflow, runtime, responses, index, ps);
							} catch (Throwable ex) {
								// If were here, the failure mode has already been applied and we're supposed to abort. This will prevent
								// any new tasks from being executing but will allow running tasks to complete.
								service.shutdown();
								error.set(ex);
							}
						}
				);
			}			
		} else {
			for (Map<String, String> paramsFromConductor: conductor) {
				Map<String, RuntimeParameter> runParams = new TreeMap<>(paramsFromOuterScope);
				combine(runParams, paramsFromConductor);
				executeSamples(conductors, runParams, depth + 1, properties, workflow, runtime, id, responses, ps, service, error);
			}
		}
	}
	
	protected void runOneSample(Map<String, RuntimeParameter> paramsFromOuterScope, Map<String, String> paramsFromConductor,
			WorkflowConductor conductor, Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime, Map<String, Map<Integer, Object>> responses, int index, UsedPrintStream ps) throws IOException {
		Map<String, RuntimeParameter> runParams = new TreeMap<>(paramsFromOuterScope);
		combine(runParams, paramsFromConductor);
		String sampleId = conductor instanceof SimpleWorkflowConductor ? "DEFAULT" : String.valueOf(index);
		WorkflowProcess process = createWorkflowProcess(properties, runtime, sampleId);		
		resetProcess(properties, workflow, runtime, runParams, process);

		process.addMonitor(new NestingMonitor(runtime, this));
		File workDirectory = process.getWorkDir();		
		try (LoggingWorkflowMonitor monitor = new LoggingWorkflowMonitor(new File(workDirectory, LoggingWorkflowMonitor.DEFAULT_NAME))) {			
			process.addMonitor(monitor);

			ICancelationListener listener = () -> process.cancel();
			try { 
				runtime.addCancelationListener(listener);
				RetryPolicy policy = RetryPolicyFactory.getRetryPolicy(getComponentWorkDir(runtime, properties), process);					
				while (policy.execute(runtime.log())) {
					resetProcess(properties, workflow, runtime, runParams, process);
				}
			} finally {
				runtime.removeCancelationListener(listener);

			}
		} 
		synchronized(responses) {
			accumulateResponses(responses, process, index);
			recordResults(responses, ps, index, runParams, process);		
		}
	}

	private File resetProcess(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime,
			Map<String, RuntimeParameter> runParams, WorkflowProcess process) throws IOException {
		process.closeLogger();
		if (shouldClearComponentWorkDir(properties)) {
			final String response_file =  getResponseFileName();
			cleanDirectory(process.getWorkDir(), new FileFilter() {			
				@Override
				public boolean accept(File pathname) {
					String name = pathname.getName();
					return !name.equals(response_file) && !name.equals(LoggingWorkflowMonitor.DEFAULT_NAME);
				}
			}, runtime);
		}
		transferEnvVars(runtime, process);
		File workDirectory = process.getWorkDir();
		try (FileWriter w = new FileWriter(new File(workDirectory, "workflow.properties"))) {
			Properties p = new Properties();
			runParams.keySet().forEach(k -> p.put(k, String.valueOf(runParams.get(k).getValue())));
			p.store(w, "props");
		} catch (IOException ioe) {
			// Whatever
		}
		restoreCachedInputs(runtime);
		defaultStageUserDefinedInputFiles(properties, workflow, runtime, workDirectory);
		process.setParametersFromParent(runParams);
		return workDirectory;
	}

	private void cleanDirectory(File workDir, FileFilter filter, RuntimeData runtime) {
        File[] files = workDir.listFiles(filter);
        if (files != null) {
        	for (File file: files) {
        		if (file.isFile() && !file.delete()) {
					runtime.log().warn("Could not delete file " + file.getPath() + ", may cause problems soon");
        		} else if (file.isDirectory()) {
        			cleanDirectory(file, filter, runtime);
        			file.delete();
        		}        			
        	}
        } else {
			runtime.log().warn("Failed to list files in sample dir, may cause problems soon");
        }
		
	}

	private void restoreCachedInputs(RuntimeData runtime) {
		if (cachedInputs != null) {
			for (String name: cachedInputs.keySet()) {
				runtime.putRawInput(getName(), name, cachedInputs.get(name));
			}
		}
	}

	private void combine(Map<String, RuntimeParameter> runParams, Map<String, String> paramsFromConductor) {
		for (Map.Entry<String, String> entry: paramsFromConductor.entrySet()) {
			RuntimeParameter p = runParams.get(entry.getKey());
			if (p != null) {
				RuntimeParameter newP = new RuntimeParameter(p);
				newP.setValue(entry.getValue());
				runParams.put(entry.getKey(), newP);

			} else {
				runParams.put(entry.getKey(), new RuntimeParameter(entry.getKey(), entry.getValue(), "default", true, false));
			}
		}
	}

	private synchronized void recordResults(Map<String, Map<Integer, Object>> responses, UsedPrintStream ps, int index,
			Map<String, RuntimeParameter> runParams, WorkflowProcess process) {
		if (!ps.isUsed()) {
			ps.print("Sample");
			for (String key: runParams.keySet()) {
				if (!RuntimeData.isBuiltIn(key)) {
					ps.print(","+ key);
				}
			}
			for (String key: responses.keySet()) {
				ps.print(","+ key);
			}
			ps.println();
		}
		ps.print(index);
		for (String key: runParams.keySet()) {
			if (!RuntimeData.isBuiltIn(key)) {
				ps.print(","+ runParams.get(key).getValue());
			}
		}
		for (String key: responses.keySet()) {
			ps.print(","+ process.getRuntime().getResponses().get(key));
		}
		ps.println();
		ps.flush();
	}
	
	
	
	private void accumulateResponses(Map<String, Map<Integer, Object>> responses, WorkflowProcess process, int index) {
		Map<String, Object> currentResponses = process.getRuntime().getResponses();
		
		for (String responseName:currentResponses.keySet()) {
			if (!responses.containsKey(responseName)) {
				responses.put(responseName, new TreeMap<>());
			}
			(responses.get(responseName)).put(index, currentResponses.get(responseName));
		}		
		
	}
	
	public Map<String, Object> getResponses(Map<String, Map<Integer, Object>> responses) {
		TreeMap<String, Object> nestedResponses = new TreeMap<>();
				 
		for (String responseName : responses.keySet()) {
			Map<Integer, Object> values = responses.get(responseName);
			Object oneValue = values.values().iterator().next();
			if (values.size() == 0 || oneValue == null) {
				throw new SAWWorkflowException(getName() + ": no value for response " + responseName);
			} else if (values.size() == 1) {
				nestedResponses.put(responseName, oneValue);
			} else {
				Class<?> responseType = inferResponseType(responseName, oneValue);
				nestedResponses.put(responseName, typedArray(responseType, values, values.size()));
			}
		}
		return nestedResponses;
	}
	
	// TODO If there's a response type we could use it here.
	private Class<?> inferResponseType(String responseName, Object value) {
		return value.getClass();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] typedArray(Class<T> type, Map<Integer, Object> input, int length) {
	    T[] typedCopy = (T[]) Array.newInstance(type, length);
	    int index = 0;
	    for (Map.Entry<Integer, Object> entry: input.entrySet()) {
	        typedCopy[index++] = (T) entry.getValue();
	    }
	    return typedCopy;
	}

	
	@Override
	protected void stageUserDefinedInputFiles(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		// Don't actually do the normal work of this method; do it later for individual workdirs. But we will
		// cache the original inputs, so any that are rewritten by defaultStageUserDefinedInputFiles() can be restored.
		Collection<String> names = runtime.getInputNames(getName());
		cachedInputs = new HashMap<>();
		for (String name: names) {
			cachedInputs.put(name, runtime.getRawInput(getName(), name));
		}
	}

	public String getFilename(Map<String, String> properties) {
		return properties.get(FILENAME);
	}
	
	public int getConcurrency(Map<String, String> properties) {
		int concurrency = 1;
		try {
			String c = properties.get(CONCURRENCY);
			if (c != null) {
				concurrency = Integer.parseInt(c);
				if (concurrency < 1)
					concurrency = 1;
				else if (concurrency > 32)
					concurrency = 32;
			}
		} catch (NumberFormatException nfe) {
			// Fall through
		}
		return concurrency;
	}

	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.CONTROL, NodeCategories.WORKFLOW); }

	/**		
	 * The new workflow gets its parameters from four places, in increasing order of priority:
	 * 1) Parameters of the host workflow
	 * 2) Properties of this nested workflow node
	 * 3) Input ports of this nested workflow node
	 */
		
	private Map<String,String> getParamsFromInputsAndProperties(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		Map<String, String> paramsFromInputsAndProperties = new TreeMap<>();
		properties.forEach( (name, value) -> {
			if (!FILENAME.equals(name) && !"parameter".equals(name) && !"values".equals(name) && !isBuiltIn(name)) {
				paramsFromInputsAndProperties.put(name, value);
			}
		});
		for (WorkflowDefinition.InputPort port : workflow.getNode(getName()).inputs.values()) {
			String value = (String) runtime.getInput(getName(), port.name, String.class);
			if (value != null) {
				paramsFromInputsAndProperties.put(port.name, value);
			}
		}
		return paramsFromInputsAndProperties;
	}

	private void transferEnvVars(RuntimeData runtime, WorkflowProcess process) {
		for (Map.Entry<String, String> entry: runtime.getenv().entrySet()) {
			process.addEnvVar(entry.getKey(), entry.getValue());
		}
	}
	
	protected WorkflowProcess createWorkflowProcess(Map<String, String> properties, RuntimeData runtime, String sampleId) throws IOException {
		File workDir = getComponentWorkDir(runtime, properties);
		if (!"DEFAULT".equals(sampleId)) {
			workDir = new File(workDir, getWorkdirName(properties, sampleId));
			workDir.mkdirs();
		}
		File subworkflowFile = getSubWorkflowFile(properties, runtime);
		File subHomeDir = getSubWorkflowHomeDir(subworkflowFile, runtime);
		WorkflowProcess process = new WorkflowProcess()
			.setWorkflowFile(subworkflowFile)
			.setHomeDir(subHomeDir)
			.setWorkDir(workDir)
			.setSampleId(sampleId)
			.setOut(runtime.getOut());
		return process;
	}
	
	protected File getSubWorkflowFile(Map<String, String> properties, RuntimeData runtime) throws IOException
	{
		String filename = getFilename(properties);
		File subworkflowFile = new File(filename);
		if (!subworkflowFile.isAbsolute())
		{
			subworkflowFile = new File(runtime.getHomeDir(), filename);
		}
		
		return subworkflowFile;
	}
	
	protected File getSubWorkflowHomeDir(File subWorkflowFile, RuntimeData runtime)
	{
		return runtime.getHomeDir();
	}

	protected List<WorkflowConductor> getConductors(Map<String, String> properties2, WorkflowDefinition workflow, RuntimeData runtime) {
		try {
			List<WorkflowConductor> conductors = new ArrayList<>();
			for (Conductor conductor: workflow.getNode(getName()).conductors) {
				Property conductorProp = conductor.properties.get("conductor");
				if (conductorProp != null) {
					String conductorType = conductorProp.value;
					// TODO Should we throw instead?
					if (StringUtils.isEmpty(conductorType) || !NodeDatabase.conductorTypes().containsKey(conductorType))
						conductorType = "simple";
					Class<? extends WorkflowConductor> clazz = NodeDatabase.conductorTypes().get(conductorType);
					WorkflowConductor result = clazz.newInstance();
					Map<String, String> properties = new TreeMap<>();
					for (Property p: conductor.properties.values()) {
						String expanded = performStandardSubstitutions(workflow, runtime, p.value, properties2);
						properties.put(p.name, expanded);
					}
					result.setProperties(properties);
					conductors.add(result);
				}
			}
			return conductors.size() > 0 ? conductors : Collections.singletonList(new SimpleWorkflowConductor());

		} catch (Exception e) {	
			throw new SAWWorkflowException("Can't instantiate conductor", e);
		} 
	}

	@Override public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(FILENAME, "home_file"),
			new PropertyInfo(CONCURRENCY, "integer"),
			new PropertyInfo(WORKDIR_NAME_TEMPLATE, "text"),
			new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo(RESPONSE_PORT));}

}
