/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.io.output.WriterOutputStream;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.AbstractExternalNode;
import gov.sandia.dart.workflow.runtime.components.Squirter;
import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.NodeMemento;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.RuntimeParameter;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

/**
 * 
 * Abstract class for nodes that run an external process.
 *
 * If the node has an input port named "stdin", data from this port is provided to the external
 * process on its standard input. If the node has output ports named "stdout" and/or "stderr",
 * any data from the standard output and standard error of the external process are copied to
 * those ports, respectively. 
 *
 * Temporary files are used to provide data to and receive data from the external process via other named
 * ports, with the details of this implemented by subclasses.
 * 
 * @author mrglick
 *
 */

public abstract class AbstractExternalScriptNode extends AbstractExternalNode {
	public static final String SCRIPT = "script";
	public static final String RESTARTABLE = "restartable";

	protected AbstractExternalScriptNode() {
	}

	protected boolean isInternalInputPort(String portName) {
		return propertiesContains(getDefaultInputs(), portName);
	}
	
	protected boolean isInternalOutputPort(String portName) {
		return propertiesContains(getDefaultOutputs(), portName);
	}
	
	protected boolean isInternalProperty(String propertyName) {
		return propertiesContains(getDefaultProperties(), propertyName);
	}

	protected abstract PrintWriter initializeScript(File workDir, List<String> commandArgs, RuntimeData runtime) throws IOException;
	protected abstract void addInputPortToScript(PrintWriter scriptStream, String portName, String fileName) throws IOException;
	protected abstract void addOutputPortToScript(PrintWriter fos, String portName, String fileName) throws IOException;
	protected abstract void addPropertyToScript(PrintWriter scriptStream, String propertyName, String value) throws IOException;
	protected abstract void finalizeScript(PrintWriter scriptStream) throws IOException;
	protected abstract void addComment(PrintWriter scriptStream, String comment) throws IOException;
	protected abstract void addScriptBody(PrintWriter scriptStream, String script) throws IOException;
	
	@Override
	protected boolean excludeFromPropertiesFile(String name) { return SCRIPT.equals(name) || super.excludeFromPropertiesFile(name); }

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		
		clearMemento(componentWorkDir);
		NodeMemento memento = createMemento(properties, workflow, runtime);
		String filenameRoot = getFilenameRoot();		
		File outFile = new File(componentWorkDir, filenameRoot + ".log");
		File errFile = new File(componentWorkDir, filenameRoot + ".err");

		WorkflowDefinition.Node nodeDef = workflow.getNode(getName());
		possiblyWritePropertiesFile(properties, workflow, runtime);

		Map<String, Object> outputs = new HashMap<>();
		Map<String, String> outputPortNameToFileName = new HashMap<>();
		Set<String> propertyNames = properties.keySet();
		int status = -1;

		String scriptText = getUserScript(properties, runtime, nodeDef);
		
		try {
			// Build complete script with prolog and postscript
			List<String> commandArgs = new ArrayList<>();
			// TODO Uniquify script name
			try (PrintWriter fos = initializeScript(componentWorkDir, commandArgs, runtime)) {		
				if (getPropertiesFileFlag(properties)) {
					addScriptBody(fos, scriptText); // GEORGE MODE
				} else {
					addGlobalParametersToScript(workflow, runtime, fos);
					addInputPortsToScript(runtime, workflow, nodeDef, componentWorkDir, fos);
					addPropertiesToScript(properties, propertyNames, fos);
					addComment(fos, "End of definitions");
					addScriptBody(fos, scriptText);
					addComment(fos, "End of user script");
					addOutputPortsToScript(workflow, runtime, properties, nodeDef, outputPortNameToFileName, fos);			
				}
				finalizeScript(fos);
				
			} catch (Exception e) {
				throw new SAWWorkflowException("Exception writing to temporary script file.", e);
			}

			status = executeScript(runtime, outFile, errFile, workflow, componentWorkDir, commandArgs);					
			
		} catch (Throwable t) {
			throw new SAWWorkflowException(getName() + ": problem executing script ... try examining " + filenameRoot + ".err", t);
			
		}
		
		if (status != 0 && !isConnectedOutput(EXIT_STATUS, workflow)) {
			throw new SAWWorkflowException(String.format("Script exited with status %d", status));
		}		
		
		sendInternalOutputs(outFile, errFile, status, nodeDef, outputs);		
		sendCustomOutputs(runtime, nodeDef, properties, outputs, outputPortNameToFileName);
		
		try {
			addOutputsToMemento(memento, outputs).save(componentWorkDir);
		} catch (IOException e) {
			// No memento, I guess
		}

		return outputs;

	}

	/**
	 * We look for the property named "script" and check its type. If the type is "home_file", assume
	 * the property names the script, and read it in. For any other type, we assume the property contains the script
	 * text itself. In both cases, as of right now, the script text from the workflow definition is used; parameter
	 * names are not expanded.	 
	 */
	private String getUserScript(Map<String, String> properties, RuntimeData runtime, WorkflowDefinition.Node nodeDef) {
		try {
			Object input = runtime.getInput(getName(), SCRIPT, String.class);
			if (input != null) {
				String path = String.valueOf(input);
				if (!StringUtils.isEmpty(path)) {
					File f = new File(path);
					if (!f.isAbsolute())
						f = new File(getComponentWorkDir(runtime, properties), path);
					return FileUtils.readFileToString(f, Charset.defaultCharset());
				}
			}

			WorkflowDefinition.Property scriptProperty = nodeDef.properties.get(SCRIPT);
			if (scriptProperty == null) {
				throw new SAWWorkflowException("No script property!");
			}
			if ("home_file".equals(scriptProperty.type)) {

				String fileName = properties.get(SCRIPT);
				File f = new File(fileName);
				if (!f.isAbsolute())
					f = new File(runtime.getHomeDir(), fileName);
				return FileUtils.readFileToString(f, Charset.defaultCharset());

			} else {
				// TODO: Should we use script text after parameter subs have been made? Can conflict with Bash syntax :-(
				return scriptProperty.value; 
			}
		} catch (Exception e) {
			throw new SAWWorkflowException("Exception reading from specified script file.", e);
		}
	}

	/**
	 * After the script has executed, for each output that's not built-in, or handled automatically by
	 * the framework, we'll look for a file with the same name as the port; if it exists, we'll read
	 * in the file and send the contents to the port.
	 */
	private void sendCustomOutputs(RuntimeData runtime, WorkflowDefinition.Node nodeDef,
			Map<String, String> properties, Map<String, Object> outputs,
			Map<String, String> outputPortNameToFileName) {
		for (WorkflowDefinition.OutputPort port : nodeDef.outputs.values()) {
			if (StringUtils.isEmpty(port.name)) {
				continue; // why is this here?

			} else if (isInternalOutputPort(port.name) || isSystemOutputPort(port)) {
				continue;

			} else {
				String fileName = outputPortNameToFileName.get(port.name);
				File file = new File(getComponentWorkDir(runtime, properties), fileName);
				if (!file.exists()) {
					runtime.log().warn(getName() + ": script did not create file " + fileName + " for output port " + port.name);
				} else {
					sendFileContentsToOutputPort(file, port.name, outputs);
				}
			}
		}
	}

	private void sendFileContentsToOutputPort(File file, String portName, Map<String, Object> outputs) {
		try (FileInputStream fis = new FileInputStream(file)) {
			outputs.put(portName, IOUtils.toByteArray(fis));
		} catch (Exception e) {
			throw new SAWWorkflowException(getName() + ": Exception reading from " + file.getAbsolutePath(), e);
		}		
	}

	private void sendInternalOutputs(File outBytes, File errBytes, int exitStatus,
			WorkflowDefinition.Node thisNode, Map<String, Object> outputs) {
		try {
			// EJFH Doing these "ifs" to allow for the ports maybe having been removed. Not sure I approve of that, but...		
			if (thisNode.outputs.get(STDOUT_PORT_NAME) != null) {
				outputs.put(STDOUT_PORT_NAME, FileUtils.readFileToString(outBytes, Charset.defaultCharset()));
			}

			if (thisNode.outputs.get(STDERR_PORT_NAME) != null) {
				outputs.put(STDERR_PORT_NAME,FileUtils.readFileToString(errBytes, Charset.defaultCharset()));
			}
		} catch (IOException ioe) {
			throw new SAWWorkflowException(getName() + ": can't read log files", ioe);
		}
		if (thisNode.outputs.get(EXIT_STATUS) != null) {
			outputs.put(EXIT_STATUS, exitStatus);
		}
	}

	private int executeScript(RuntimeData runtime, File outFile, File errFile,
			WorkflowDefinition workflow, File componentWorkDir, List<String> commandArgs)
			throws IOException, InterruptedException {
		Process p;
		ProcessBuilder proc = ProcessUtils.createProcess(runtime);
		proc.command(commandArgs);
		proc.directory(componentWorkDir);
		runtime.log().debug("about to execute command " + Arrays.toString(commandArgs.toArray()));
		ICancelationListener listener = null;
		int exitStatus = UNSET;

		try (FileOutputStream outBytes = new FileOutputStream(outFile);
			FileOutputStream errBytes = new FileOutputStream(errFile);){
			//proc.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			p = proc.start();
			listener = () -> ProcessUtils.destroyProcess(p);
			runtime.addCancelationListener(listener);
			
			byte inBytes[] = (byte[]) runtime.getInput(getName(), STDIN_PORT_NAME, byte[].class);
			if (inBytes != null) {
				p.getOutputStream().write(inBytes);
			}
			try {
				p.getOutputStream().close();
			} catch (IOException ex) {
				// Possible that process has already exited!
			}

			Thread t1, t2;
			if (isConnectedOutput(STDOUT_PORT_NAME, workflow))
				t1 = new Thread(new Squirter(p.getInputStream(), new PrintStream(outBytes)));
			else
				t1 = new Thread(new Squirter(p.getInputStream(), new PrintStream(new TeeOutputStream(new WriterOutputStream(runtime.getOut()), outBytes))));

			if (isConnectedOutput(STDERR_PORT_NAME, workflow))
				t2 = new Thread(new Squirter(p.getErrorStream(), new PrintStream(errBytes)));
			else
				t2 = new Thread(new Squirter(p.getErrorStream(), new PrintStream(new TeeOutputStream(new WriterOutputStream(runtime.getErr()), errBytes))));
			t1.start();
			t2.start();

			while (exitStatus == UNSET && !runtime.isCancelled()) {
				try {
					exitStatus = p.waitFor();
					break;
				} catch (InterruptedException ex) {
					// May be a spurious wakeup. Check for cancellation, and go check exit status again.
				}
			}

			t1.join(300);
			t2.join(300);
		} finally {
			runtime.removeCancelationListener(listener);
		}
		return exitStatus;
	}
	
	/**
	 * Before the script executes, the individual properties of this script node -- except for the internal properties,
	 * like "script" -- are prepended to the script as variable definitions. 
	 */
	private void addPropertiesToScript(Map<String, String> properties, Set<String> propertyNames, PrintWriter fos)
			throws IOException {
		for (String propertyName : propertyNames) {
			if (!isInternalProperty(propertyName)) {
				String propertyValue = properties.get(propertyName);
				if (!StringUtils.isEmpty(propertyValue)) {
					String escaped = escapeString(propertyValue);
					String identifier = makeIdentifier(propertyName);
					addPropertyToScript(fos, identifier, escaped);
				}
			}
		}
	}

	/**
	 * Before the script executes, the individual properties of this script node -- except for the internal properties,
	 * like "script" -- are prepended to the script as variable definitions. 
	 * TODO We ought to be handling escaping better, I think.
	 */
	private void addGlobalParametersToScript(WorkflowDefinition workflow, RuntimeData runtime, PrintWriter fos)
			throws IOException {
		for (String name: runtime.getParameterNames()) {
			RuntimeParameter p = runtime.getParameter(name);
			if (p.isGlobal()) {
				String identifier = makeIdentifier(p.getName());
				String propertyValue = String.valueOf(p.getValue());				
				String escaped = escapeString(propertyValue);
				addPropertyToScript(fos, identifier, escaped);	
			}
		}
	}

	/**
	 * After the script executes, for each output port (except the internal ones
	 * like STDOUT, and the system handled ones like output_file type) we copy the
	 * value of a variable named after the port to a specific file, generally with
	 * the same name
	 */

	private void addOutputPortsToScript(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties, WorkflowDefinition.Node nodeDef,
			Map<String, String> outputPortNameToFileName, PrintWriter fos) throws IOException {
		for (WorkflowDefinition.OutputPort port : nodeDef.outputs.values()) {
			if (!isInternalOutputPort(port.name) && !isSystemOutputPort(port)) {
				if (StringUtils.isEmpty(port.name)) {
					runtime.log().warn(getName() + ": seeing an empty output port name");
					continue;
				}
				String fileName = getFilenameForOutputPort(properties, workflow, runtime, port);
				outputPortNameToFileName.put(port.name, fileName);
				addOutputPortToScript(fos, port.name, fileName);
			}
		}
	}
	
	private boolean isSystemOutputPort(OutputPort port) {
		String type = port.type;
		return "output_file".equals(type) || "exodus_file".equals(type);
	}


	/**
	 * For each connected input port, we define a variable (named after the port) to hold the value
	 * (retrieved as a String) found on the port.
	 * @param workflow TODO
	 */
	
	private void addInputPortsToScript(RuntimeData runtime, WorkflowDefinition workflow, WorkflowDefinition.Node nodeDef, File componentWorkDir, PrintWriter fos) throws IOException {
		for (WorkflowDefinition.InputPort port : nodeDef.inputs.values()) {
			if (!isInternalInputPort(port.name) && isConnectedInput(port.name, workflow)) {
				String value = (String) runtime.getInput(getName(), port.name, String.class);
				if (value != null) {
					String identifier = makeIdentifier(port.name);
					String escaped = escapeString(value);
					addInputPortToScript(fos, identifier, escaped);
				}
			}
		}
	}
	
	protected void addInterpreterArguments(String id, RuntimeData runtime, String dflt, List<String> commandArgs) {
		String key =	 "NGW_INTERP_" + id.toUpperCase();
		String interp = runtime.getenv(key);
		if (StringUtils.isEmpty(interp)) {
			interp = dflt;	
		}
		// On Windows, we can end up with extra quotes pretty easily.
		interp = StringUtils.unwrap(interp, '"');
		commandArgs.addAll(Arrays.asList(interp.split("\\s+")));					
	}
	
	@Override public final List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(STDIN_PORT_NAME), new InputPortInfo(SCRIPT)); }
	@Override public final List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(STDOUT_PORT_NAME), new OutputPortInfo(STDERR_PORT_NAME), new OutputPortInfo(EXIT_STATUS)); }
	@Override public final List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(SCRIPT, "multitext"),
			new PropertyInfo(PROPERTIES_FILE_FLAG, "boolean"),
			new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true"),
			new PropertyInfo(RESTARTABLE, "boolean", "true", true));
	}
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.SCRIPTING, NodeCategories.EXTERNAL_PROCESSES); }

	@Override
	public boolean canReuseExistingState(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		String value = properties.get(RESTARTABLE);
		if (value == null || Boolean.parseBoolean(value)) {
			return super.canReuseExistingState(workflow, runtime, properties);
		} else {
			return false;
		}
			
	}
}


