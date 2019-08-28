package gov.sandia.dart.workflow.runtime.components;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.Datum;
import gov.sandia.dart.workflow.runtime.core.NodeMemento;
import gov.sandia.dart.workflow.runtime.core.NodeMemento.Channel;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Connection;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;

public abstract class AbstractRestartableNode extends SAWCustomNode {

	public AbstractRestartableNode() {
		super();
	}

	/**
	 * Restart/continue implementation
	 */
	protected void clearMemento(File componentWorkDir) {
		NodeMemento.delete(getName(), componentWorkDir);
	}

	@Override
	public boolean canReuseExistingState(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		
		// We save the original inputs, then do the possibly-input-modifying staging process. We have to do this
		// before comparing the current inputs to the previous memento, so we're comparing the actual runtime inputs.
		// Later we'll restore the original inputs, so that we can clear the CWD if needed before doing the actual staging. This
		// is inefficient and I think it's indicative of a suboptimal design, but it'll work for now.
		Map<String, Datum> originalInputs = cacheOriginalInputs(runtime);
		defaultStageUserDefinedInputFiles(properties, workflow, runtime, componentWorkDir);
		
		try {
			NodeMemento oldMemento = NodeMemento.load(getName(), componentWorkDir);
			NodeMemento newMemento = createMemento(properties, workflow, runtime);			
			String compareResult = oldMemento.comparePropertiesAndInputs(newMemento);
			if (compareResult != null) {
				runtime.log().info("{0}: testing previous state, mementos differ at {1}", getName(), compareResult);
				return false;
			}
			
			// TODO Variables?
			return outputsAvailable(workflow, runtime, properties);
			
		} catch (IOException e) {
			// No usable memento.

		} finally {
			restoreOriginalInputs(originalInputs, runtime);
		}
		
		return false;
	}

	private void restoreOriginalInputs(Map<String, Datum> originalInputs, RuntimeData runtime) {
		for (String name: originalInputs.keySet()) {
			runtime.putRawInput(getName(), name, originalInputs.get(name));
		}
	}

	private Map<String, Datum> cacheOriginalInputs(RuntimeData runtime) {
		Collection<String> names = runtime.getInputNames(getName());
		Map<String, Datum> cachedInputs = new HashMap<>();
		for (String name: names) {
			cachedInputs.put(name, runtime.getRawInput(getName(), name));
		}
		return cachedInputs;
	}

	// If you have an output_file port whose filename is generally not set, then you probably should override this 
	protected boolean outputsAvailable(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		Map<String, OutputPort> outputs = workflow.getNode(getName()).outputs;
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		for (String name: outputs.keySet()) {
			OutputPort port = outputs.get(name);
			if (isConnectedOutput(name, workflow) && OUTPUT_FILE.equals(port.type)) {
				String file = getFilenameForOutputPort(properties, workflow, runtime, outputs.get(name));
				if (isGlobPattern(file)) {
					List<String> matches = new ArrayList<>();
					try {
						glob(file, componentWorkDir, matches);									
					} catch(IOException ioe) { }	
					
					if (matches.size() == 0) {
						runtime.log().info("{0}: testing previous state, no files match {1}", getName(), file);					
						return false;
					} else {
						continue;
					}
				}
				if (!new File(componentWorkDir, file).exists()) {
					runtime.log().info("{0}: testing previous state, output file {1} not available", getName(), file);					
					return false;
				}
			}
		}		
		return true;
	}

	@Override
	public Map<String, Object> getPreviousResults(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		// By convention, valid state has already been confirmed. 
		Map<String, Object> map = new HashMap<>();
		try {
			NodeMemento oldMemento = NodeMemento.load(getName(), getComponentWorkDir(runtime, properties));
			oldMemento.getOutputs(map);
			
		} catch (IOException e) {
			throw new SAWWorkflowException("Internal error: can't load memento in getPreviousResults() in node " + getName());
		}
	
		return map;
	}

	protected NodeMemento createMemento(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		NodeMemento memento = new NodeMemento(getName());
	
		// TODO Exclude some, or handle internal ports and properties specially?
		for (String property: properties.keySet()) {
			Map<String, Property> pdefs = workflow.getNode(getName()).properties;
			if (pdefs.containsKey(property)) {
				String type = pdefs.get(property).type;
				if ("home_file".equals(type)) {
					File file = getFileFromProperty(runtime, properties, property);
					if (file != null && file.exists())
						memento.addObject(Channel.PROPERTY, property, tokenForFile(file));
					else
						memento.addObject(Channel.PROPERTY, property, properties.get(property));
				} else {
					memento.addObject(Channel.PROPERTY, property, properties.get(property));
				}
			}
		}

		for (String port: workflow.getNode(getName()).inputs.keySet()) {
			if (isConnectedInput(port, workflow)) {
				InputPort inputPort = workflow.getNode(getName()).inputs.get(port);
				Connection connection = inputPort.connection;
				Property p = connection.properties.get(NOT_A_LOCAL_PATH);
				// This may look like the path to a local file. The user may not want us to treat it as such;
				// they can set a flag on the incoming connection.
				boolean isALocalFile = p == null || "false".equals(p.value);
				String[] values = (String[]) runtime.getInput(getName(), port, String[].class);
				StringBuilder sb = new StringBuilder();
				for (String value: values) {
					if (sb.length() > 0)
						sb.append(";");
					boolean included = false;
					File probe = new File(value);
					if (probe.exists() && isALocalFile) {
						sb.append(tokenForFile(probe));
						included = true;
					}	
					if (!included) {
						sb.append(value);
					}
				}
				memento.addObject(Channel.INPUT, port, sb.toString());
			}
		}
				
		return memento;
	}
	long THRESHOLD = 10_000_000L;
	private Object tokenForFile(File file) {
		if (!file.isDirectory() && file.length() > 0 && file.length() < THRESHOLD) {
			try {
				return FileUtils.checksumCRC32(file);
			} catch(IOException ex) {
				// FALL THROUGH
			}
		}
		return file.lastModified();
	}
	
	protected NodeMemento addOutputsToMemento(NodeMemento memento, Map<String, Object> outputs) {
		if (outputs != null) {
			for (String name: outputs.keySet()) {
				memento.addObject(Channel.OUTPUT, name, outputs.get(name));
			}
		}
		return memento;
	}

}