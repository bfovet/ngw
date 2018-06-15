/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.AbstractExternalNode;
import gov.sandia.dart.workflow.runtime.components.Squirter;
import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Parameter;

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

	protected AbstractExternalScriptNode() {
	}

	protected boolean isInternalInputPort(String portName) {
		return getDefaultInputNames().contains(portName);
	}
	
	protected boolean isInternalOutputPort(String portName) {
		return getDefaultOutputNames().contains(portName);
	}
	
	protected boolean isInternalProperty(String propertyName) {
		return getDefaultProperties().contains(propertyName);		
	}

	protected abstract PrintWriter initializeScript(File workDir, List<String> commandArgs, RuntimeData runtime) throws IOException;
	protected abstract void addInputPortToScript(PrintWriter scriptStream, String portName, String fileName) throws IOException;
	protected abstract void addOutputPortToScript(PrintWriter fos, String portName, String fileName) throws IOException;
	protected abstract void addPropertyToScript(PrintWriter scriptStream, String propertyName, String value) throws IOException;
	protected abstract void finalizeScript(PrintWriter scriptStream) throws IOException;
	protected abstract void addComment(PrintWriter scriptStream, String comment) throws IOException;
	protected abstract void addScriptBody(PrintWriter scriptStream, String script) throws IOException;


	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
		ByteArrayOutputStream errBytes = new ByteArrayOutputStream();

		WorkflowDefinition.Node nodeDef = workflow.getNode(getName());
		possiblyWritePropertiesFile(properties, runtime);

		Map<String, Object> outputs = new HashMap<>();
		Map<String, String> outputPortNameToFileName = new HashMap<>();
		Set<String> propertyNames = properties.keySet();
		int status = -1;

		String scriptText = getUserScript(properties, runtime, nodeDef);
		
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		try {
			// Build complete script with prolog and postscript
			List<String> commandArgs = new ArrayList<>();
			// TODO Uniquify script name
			try (PrintWriter fos = initializeScript(componentWorkDir, commandArgs, runtime)) {						
				addInputPortsToScript(runtime, nodeDef, componentWorkDir, fos);
				addPropertiesToScript(properties, propertyNames, fos);
				addGlobalParametersToScript(workflow, runtime, fos);
				addComment(fos, "End of definitions");
				addScriptBody(fos, scriptText);
				addComment(fos, "End of user script");
				addOutputPortsToScript(workflow, runtime, properties, nodeDef, outputPortNameToFileName, fos);				
				finalizeScript(fos);
				
			} catch (Exception e) {
				throw new SAWWorkflowException("Exception writing to temporary script file.", e);
			}

			status = executeScript(runtime, outBytes, errBytes, nodeDef, componentWorkDir, commandArgs);					
			
		} catch (Throwable t) {
			String filenameRoot = getFilenameRoot();
			throw new SAWWorkflowException(getName() + ": problem executing script ... try examining " + filenameRoot + ".err", t);
			
		} finally {
			try {
				// TODO Uniquify file names
				String filenameRoot = getFilenameRoot();
				FileUtils.copyInputStreamToFile(new ByteArrayInputStream(outBytes.toByteArray()), new File(componentWorkDir, filenameRoot + ".log"));
				FileUtils.copyInputStreamToFile(new ByteArrayInputStream(errBytes.toByteArray()), new File(componentWorkDir, filenameRoot + ".err"));			
			} catch (IOException e) {
				runtime.log().warn(getName() + ": error saving script output");
			}
		}
		
		if (status != 0) {
			throw new SAWWorkflowException(String.format("Script exited with status %d", status));
		}
		
		sendInternalOutputs(outBytes, errBytes, status, nodeDef, outputs);		
		sendCustomOutputs(runtime, nodeDef, properties, outputs, outputPortNameToFileName);
		
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
					return FileUtils.readFileToString(f);
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
				return FileUtils.readFileToString(f);

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

	private void sendInternalOutputs(ByteArrayOutputStream outBytes, ByteArrayOutputStream errBytes, int exitStatus,
			WorkflowDefinition.Node thisNode, Map<String, Object> outputs) {
		// EJFH Doing these "ifs" to allow for the ports maybe having been removed. Not sure I approve of that, but...		
		if (thisNode.outputs.get(STDOUT_PORT_NAME) != null) {
			outputs.put(STDOUT_PORT_NAME, outBytes.toByteArray());
		}

		if (thisNode.outputs.get(STDERR_PORT_NAME) != null) {
			outputs.put(STDERR_PORT_NAME, errBytes.toByteArray());
		}
		
		if (thisNode.outputs.get(EXIT_STATUS) != null) {
			outputs.put(EXIT_STATUS, exitStatus);
		}
	}

	private int executeScript(RuntimeData runtime, ByteArrayOutputStream outBytes, ByteArrayOutputStream errBytes,
			WorkflowDefinition.Node nodeDef, File componentWorkDir, List<String> commandArgs)
			throws IOException, InterruptedException {
		int status;
		Process p;
		ProcessBuilder proc = new ProcessBuilder();
		proc.environment().putAll(runtime.getenv());
		proc.command(commandArgs);
		proc.directory(componentWorkDir);
		runtime.log().debug("about to execute command " + Arrays.toString(commandArgs.toArray()));
		ICancelationListener listener = null;
		try {
			//proc.redirectOutput(ProcessBuilder.Redirect.INHERIT);
			p = proc.start();
			listener = () -> p.destroy();
			runtime.addCancelationListener(listener);
			
			byte inBytes[] = (byte[]) runtime.getInput(getName(), STDIN_PORT_NAME, byte[].class);
			if (inBytes != null) {
				p.getOutputStream().write(inBytes);
			}
			p.getOutputStream().close();

			Thread t1, t2;
			if (nodeDef.outputs.containsKey(STDOUT_PORT_NAME) && !nodeDef.outputs.get(STDOUT_PORT_NAME).connections.isEmpty())
				t1 = new Thread(new Squirter(p.getInputStream(), new PrintStream(outBytes)));
			else
				t1 = new Thread(new Squirter(p.getInputStream(), new PrintStream(new TeeOutputStream(new WriterOutputStream(runtime.getOut()), outBytes))));

			if (nodeDef.outputs.containsKey(STDERR_PORT_NAME) && !nodeDef.outputs.get(STDERR_PORT_NAME).connections.isEmpty())
				t2 = new Thread(new Squirter(p.getErrorStream(), new PrintStream(errBytes)));
			else
				t2 = new Thread(new Squirter(p.getErrorStream(), new PrintStream(new TeeOutputStream(new WriterOutputStream(runtime.getErr()), errBytes))));
			t1.start();
			t2.start();

			status = p.waitFor();		

			t1.join(300);
			t2.join(300);
		} finally {
			runtime.removeCancelationListener(listener);
		}
		return status;
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
				String escaped = escapeString(propertyValue);
				addPropertyToScript(fos, propertyName, escaped);
			}
		}
	}
	
	protected String escapeString(String unescaped) {
		String value = StringEscapeUtils.escapeJava(unescaped);
		return value.replaceAll("'", "\\\\'");

	}

	/**
	 * Before the script executes, the individual properties of this script node -- except for the internal properties,
	 * like "script" -- are prepended to the script as variable definitions. 
	 * TODO We ought to be handling escaping better, I think.
	 */
	private void addGlobalParametersToScript(WorkflowDefinition workflow, RuntimeData runtime, PrintWriter fos)
			throws IOException {
		for (Parameter p: workflow.getParameters().values()) {
			if (p.global) {
				String propertyValue = String.valueOf(runtime.getParameter(p.name));
				String escaped = escapeString(propertyValue);
				addPropertyToScript(fos, p.name, escaped);	
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
	 * TODO: map to legal filename so port names are less restricted?
	 */
	
	private String getFileNameForInputPort(String portName) throws IOException {		
		return portName; 
	}


	/**
	 * For each input port, we define a variable (named after the port) to hold the value
	 * (retrieved as a String) found on the port.
	 */
	
	private void addInputPortsToScript(RuntimeData runtime, WorkflowDefinition.Node nodeDef, File componentWorkDir, PrintWriter fos) throws IOException {
		for (WorkflowDefinition.InputPort port : nodeDef.inputs.values()) {
			if (!isInternalInputPort(port.name)) {
				String value = (String) runtime.getInput(getName(), port.name, String.class);
				String escaped = escapeString(value);
				addInputPortToScript(fos, port.name, escaped);
			}
		}
	}
	
	protected void addInterpreterArguments(String id, RuntimeData runtime, String dflt, List<String> commandArgs) {
		String key =	 "NGW_INTERP_" + id.toUpperCase();
		String interp = runtime.getenv(key);
		if (StringUtils.isEmpty(interp)) {
			interp = dflt;	
		}
		commandArgs.addAll(Arrays.asList(interp.split("\\s+")));					
	}
	
	protected String getFilenameRoot() {
		return getName().replaceAll("\\W+", "_");
	}
	
	
	@Override public final List<String> getDefaultInputNames() { return Arrays.asList(STDIN_PORT_NAME, SCRIPT); }
	@Override public final List<String> getDefaultOutputNames() { return Arrays.asList(STDOUT_PORT_NAME, STDERR_PORT_NAME, EXIT_STATUS); }
	@Override public final List<String> getDefaultProperties() { return Arrays.asList(SCRIPT, PROPERTIES_FILE_FLAG); }
	@Override public final List<String> getDefaultPropertyTypes() { return Arrays.asList("multitext", "boolean"); }
	@Override public String getCategory() { return "Pipes"; }

}

