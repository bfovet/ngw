package gov.sandia.dart.workflow.runtime.components;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.NodeDatabase;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Conductor;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class NestedWorkflowNode extends SAWCustomNode {

	private static final String RESPONSE_PORT = "responses_csv";
	private static final String CONCURRENCY = "concurrency";
	private static final String ERROR_MODE = "errorMode";
	// TODO Use an enum?
	private static final String FAIL = "fail";
	private static final String IGNORE = "ignore";
	private static final String RETRY = "retry";
	protected final String FILENAME = "fileName";

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {								

		Map<String, Map<Integer, Object>> responses = new TreeMap<>();		
		AtomicInteger id = new AtomicInteger(1);
		AtomicReference<Exception> error = new AtomicReference<>();

		
		Map<String, String> paramsFromOuterScope = new TreeMap<>();
		for (Map.Entry<String, Object> entry: runtime.getParameters().entrySet()) {
			paramsFromOuterScope.put(entry.getKey(), String.valueOf(entry.getValue()));
		}

		List<WorkflowConductor> conductors = getConductors(properties, workflow, runtime);

		try (UsedPrintStream ps = new UsedPrintStream(new File(getComponentWorkDir(runtime, properties), getResponseFileName()))) {		
			ExecutorService service = Executors.newFixedThreadPool(getConcurrency(properties));
			executeSamples(conductors, paramsFromOuterScope, 0, properties, workflow, runtime, id, responses, ps, service, error);
			service.shutdown();
			// TODO We may need to fail faster if there is an exception?
			service.awaitTermination(1, TimeUnit.DAYS);
			if (error.get() != null)
				throw error.get();
			
		} catch (SAWWorkflowException e) {
			throw e;
		} catch (Exception e) {
			// TODO This is wrong and will obscure actual exceptions.
			throw new SAWWorkflowException("Error executing sample " + (id.get() - 1) + " in nested workflow" , e);
		}

		Map<String, Object> nestedResponses = getResponses(responses);
		nestedResponses.put(RESPONSE_PORT, new File(getComponentWorkDir(runtime, properties), getResponseFileName()));
		return nestedResponses;

	}

	protected String getResponseFileName() {
		return getName() + ".responses.csv";
	}	
	
	void executeSamples(List<WorkflowConductor> conductors, Map<String, String> paramsFromOuterScope, int depth,
			Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, AtomicInteger id,
			Map<String, Map<Integer, Object>> responses, UsedPrintStream ps, ExecutorService service, AtomicReference<Exception> error) {
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
							} catch (Exception ex) {
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
				// TODO We're not passing all the parameters through!
				Map<String, String> runParams = new TreeMap<>(paramsFromOuterScope);
				runParams.putAll(paramsFromConductor);
				executeSamples(conductors, runParams, depth + 1, properties, workflow, runtime, id, responses, ps, service, error);
			}
		}
	}

	private void runOneSample(Map<String, String> paramsFromOuterScope, Map<String, String> paramsFromConductor,
			WorkflowConductor conductor, Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime, Map<String, Map<Integer, Object>> responses, int index, UsedPrintStream ps) {
		Map<String, String> runParams = new TreeMap<>(paramsFromOuterScope);
		runParams.putAll(paramsFromConductor);
		String sampleId = conductor instanceof SimpleWorkflowConductor ? "DEFAULT" : String.valueOf(index);
		WorkflowProcess process = createWorkflowProcess(properties, runtime, sampleId);		
		transferParameters(properties, workflow, runtime, process);				
		transferEnvVars(runtime, process);
		File workDirectory = process.getWorkDir();
		try (FileWriter w = new FileWriter(new File(workDirectory, "workflow.properties"))) {
			Properties p = new Properties();
			p.putAll(runParams);
			p.store(w, "props");
		} catch (IOException ioe) {
			// Whatever
		}
		defaultStageUserDefinedInputFiles(properties, workflow, runtime, workDirectory);
		process.setParameters(runParams);
		runWithCancellation(runtime, properties, process);
		accumulateResponses(responses, process, index);
		recordResults(responses, ps, index, runParams, process);		
	}

	private synchronized void recordResults(Map<String, Map<Integer, Object>> responses, UsedPrintStream ps, int index,
			Map<String, String> runParams, WorkflowProcess process) {
		if (!ps.isUsed()) {
			ps.print("Sample");
			for (String key: runParams.keySet()) {
				ps.print(","+ key);
			}
			for (String key: responses.keySet()) {
				ps.print(","+ key);
			}
			ps.println();
		}
		ps.print(index);
		for (String key: runParams.keySet()) {
			ps.print(","+ runParams.get(key));
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
			Class<?> responseType = inferResponseType(responseName, oneValue);
			nestedResponses.put(responseName, typedArray(responseType, values, values.size()));
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
		// Nothing. We'll do this for individual workdirs
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
	
	public String getErrorMode(Map<String, String> properties) {
		String mode = FAIL;
			String c = properties.get(ERROR_MODE);
			if (StringUtils.isNotBlank(c)) {
				switch (c) {
				case FAIL:
				case IGNORE:
				case RETRY:
					return c;
				default:
					throw new SAWWorkflowException("Invalid error mode " + c + " in node " + getName());
				}
			}
		return mode;	
	}

	@Override
	public String getCategory() { return "Control"; }

	/**		
	 * The new workflow gets its parameters from four places, in increasing order of priority:
	 * 1) Parameters of the host workflow
	 * 2) Properties of this nested workflow node
	 * 3) Input ports of this nested workflow node
	 */
	
	// TODO We actually only want to set parameters that exist in the workflow definition; we
	// will need to rearrange containment somewhat to make this possible.
	private void transferParameters(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, WorkflowProcess process) {
		properties.forEach( (name, value) -> {
			if (!FILENAME.equals(name) && !"parameter".equals(name) && !"values".equals(name)) {
					process.setParameter(name, value);
			}
		});
		
		for (WorkflowDefinition.InputPort port : workflow.getNode(getName()).inputs.values()) {
			Object value = runtime.getInput(getName(), port.name, String.class);
			if (value != null)
				process.setParameter(port.name, (String) value );
		}
	}

	private void transferEnvVars(RuntimeData runtime, WorkflowProcess process) {
		for (Map.Entry<String, String> entry: runtime.getenv().entrySet()) {
			process.addEnvVar(entry.getKey(), entry.getValue());
		}
	}
	
	private void runWithCancellation(RuntimeData runtime, Map<String, String> properties, WorkflowProcess process) {
		ICancelationListener listener = () -> process.cancel();
		try { 
			runtime.addCancelationListener(listener);
			runWithErrorMode(runtime, properties, process); 				
		} finally {
			runtime.removeCancelationListener(listener);
		}
	}

	protected void runWithErrorMode(RuntimeData runtime, Map<String, String> properties, WorkflowProcess process) {
		String mode = getErrorMode(properties);
		switch(mode) {
		case FAIL:
			// If there's an exception, we just let it go.
			process.run();
			break;
		case IGNORE:
			// Just use dummy responses
			try {
				process.run(); 
			} catch (Exception ex) {
				runtime.log().error("Evaluation failed, ignoring", ex);
			}
			break;
		case RETRY:
			// Catch exceptions and just try again
			// TODO Retry count
			try {
				process.run();
			} catch (Exception ex) {
				runtime.log().error("Evaluation failed, retrying", ex);
				process.run(); 
			}
			break;
		}	
	}

	private WorkflowProcess createWorkflowProcess(Map<String, String> properties, RuntimeData runtime, String sampleId) {
		File workDir = getComponentWorkDir(runtime, properties);
		if (!"DEFAULT".equals(sampleId)) {
			workDir = new File(workDir, "workdir" + sampleId);
			workDir.mkdirs();
		}
		String filename = getFilename(properties);
		File subworkflowFile = new File(filename);
		if (!subworkflowFile.isAbsolute())
			subworkflowFile = new File(runtime.getHomeDir(), filename);
		File subHomeDir = subworkflowFile.getParentFile();
		WorkflowProcess process = new WorkflowProcess()
			.setMonitor(runtime.getWorkflowMonitor())
			.setWorkflowFile(subworkflowFile)
			.setHomeDir(subHomeDir)
			.setWorkDir(workDir)
			.setOut(runtime.getOut());
		return process;
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

	// TODO We don't have a mechanism for enum-valued properties anymore; do we need one?
	@Override public List<String> getDefaultProperties() { return Arrays.asList(FILENAME, CONCURRENCY, ERROR_MODE); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("home_file", "integer", "text"); }	
	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList(RESPONSE_PORT);}

}