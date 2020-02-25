/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.NodeMemento;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

/**
 * 
 * A simple node that runs an external process.
 *
 * If the node has an input port named "stdin", data from this port is provided to the external
 * process on its standard input. If the node has output ports named "stdout" and/or "stderr",
 * any data from the standard output and standard error of the external process are copied to
 * those ports, respectively. 
 * 
 * @author mrglick
 */

public class ExternalProcessNode extends AbstractExternalNode {
	private static final String COMMAND_PROPERTY = "command";

	@Override
	protected boolean excludeFromPropertiesFile(String name) { return COMMAND_PROPERTY.equals(name) || super.excludeFromPropertiesFile(name); }
	
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties,  WorkflowDefinition workflow, RuntimeData runtime) {
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		
		clearMemento(componentWorkDir);
		NodeMemento memento = createMemento(properties, workflow, runtime);
		
		WorkflowDefinition.Node thisNode = workflow.getNode(getName());
		possiblyWritePropertiesFile(properties, workflow, runtime);
		int exitStatus = 0;

		String filenameRoot = getFilenameRoot();
		File stdoutReceptor = new File(componentWorkDir, filenameRoot + ".log");
		File stderrReceptor = new File(componentWorkDir, filenameRoot + ".err");

		try {
			Process process = setUpProcess(properties, runtime);
			ICancelationListener listener = () -> ProcessUtils.destroyProcess(process);
			runtime.addCancelationListener(listener);			
			try { 						
				exitStatus = mediateStandardIO(process, thisNode, stdoutReceptor, stderrReceptor, runtime);
			} finally {
				runtime.removeCancelationListener(listener);
			}
		} catch (Throwable t) {
			throw new SAWWorkflowException("Problem executing external program", t);
		}	

		if (exitStatus != 0 && !isConnectedOutput(EXIT_STATUS, workflow)) {
			throw new SAWWorkflowException(String.format("Script exited with status %d", exitStatus));
		}
		
		Map<String, Object> outputs = Collections.emptyMap();
		
		try {
			outputs = postProcessOutputs(thisNode, exitStatus, stdoutReceptor, stderrReceptor, runtime);
		} catch (IOException ex) {
			throw new SAWWorkflowException(String.format("%s: Can't read log files", getName()));
		}
		
		try {
			addOutputsToMemento(memento, outputs).save(componentWorkDir);
		} catch (IOException e) {
			// No memento, I guess
		}
		return outputs;
	}

	protected Process setUpProcess(Map<String, String> properties, RuntimeData runtime) throws IOException {
		ProcessBuilder processBuilder = ProcessUtils.createProcess(runtime);
		List<String> commandArgs = new ArrayList<>();
		Pattern ptn = Pattern.compile("([^\"]\\S*|\".+?\")\\s*"); // thank you, StackOverflow
		Matcher matchPattern = ptn.matcher(getCommand(properties));
		while (matchPattern.find()) {
			String fragment = matchPattern.group(1);
			if (fragment.startsWith("\""))
				fragment = fragment.substring(1, fragment.length()-1);
			commandArgs.add(fragment);
		}
		
		runtime.log().debug("setting up to execute command {0}", Arrays.toString(commandArgs.toArray()));
		processBuilder.command(commandArgs);
		processBuilder.directory(getComponentWorkDir(runtime, properties));
		return processBuilder.start();
	}
		
	private int mediateStandardIO(Process p,
			Node thisNode,
			File stdoutReceptor,
			File stderrReceptor,
			RuntimeData runtime) throws Throwable {

		int exitStatus = UNSET;
		try (PrintStream stdoutPrintStream = new PrintStream(stdoutReceptor);	
		     PrintStream stderrPrintStream = new PrintStream(stderrReceptor)) {	
		
		// Send data from STDIN to process input
		// TODO This shouldn't be necessary. Need to just be able to ask for input datum and get the right thing.

		InputPort port = thisNode.inputs.get(STDIN_PORT_NAME);
		if (port != null) {
			byte inBytes[] = null;
			if ("input_file".equals(port.type)) {
				String fileName = (String) runtime.getInput(getName(), port.name, String.class);					
				if (StringUtils.isEmpty(fileName) || ! new File(fileName).exists()) {
					throw new SAWWorkflowException(String.format("Input file %s for STDIN on node %s does not exist", fileName, getName()));
				}
				inBytes = FileUtils.readFileToByteArray(new File(fileName));
			} else {
				inBytes = (byte[]) runtime.getInput(getName(), STDIN_PORT_NAME, byte[].class);
			}
			if (inBytes != null) {
				p.getOutputStream().write(inBytes);
			}
			p.getOutputStream().close();
		}

		Thread t1, t2;
		(t1 = new Thread(new Squirter(p.getInputStream(), stdoutPrintStream), "externalProcessNode stdout")).start();
		(t2 = new Thread(new Squirter(p.getErrorStream(), stderrPrintStream), "externalProcessNode stderr")).start();
		while (exitStatus == UNSET && !runtime.isCancelled()) {
			try {
				exitStatus = p.waitFor();
				break;
			} catch (InterruptedException ex) {
				// May be a spurious wakeup. Check for cancellation, and go check exit status again.
			}
		}
		t2.join();
		t1.join();
		}
		return exitStatus;
	}
	
	private Map<String, Object> postProcessOutputs(Node thisNode,
			int exitStatus,
			File stdoutReceptor,
			File stderrReceptor,
			RuntimeData runtime) throws IOException {
		
		Map<String, Object> outputMap = new HashMap<>();
		
		// EJFH Doing these "ifs" to allow for the ports maybe having been removed. Not sure I approve of that, but...		
		if (thisNode.outputs.get(STDOUT_PORT_NAME) != null) {
			String content =  FileUtils.readFileToString(stdoutReceptor);
			outputMap.put(STDOUT_PORT_NAME, content);
			runtime.getOut().println(content);
		}
		
		if (thisNode.outputs.get(STDERR_PORT_NAME) != null) {
			String content =  FileUtils.readFileToString(stderrReceptor);
			outputMap.put(STDERR_PORT_NAME, content);
			runtime.getErr().println(content);
		}
		
		if (thisNode.outputs.get(EXIT_STATUS) != null) {
			outputMap.put(EXIT_STATUS, exitStatus);
		}
		
		return outputMap;
	}
	
	public String getCommand(Map<String, String> properties) {
		return properties.get("command");
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(STDIN_PORT_NAME)); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(STDOUT_PORT_NAME), new OutputPortInfo(STDERR_PORT_NAME), new OutputPortInfo(EXIT_STATUS)); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(COMMAND_PROPERTY, "text"), new PropertyInfo(PROPERTIES_FILE_FLAG, "boolean"),
			new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true")); }
//	@Override public List<String> getDefaultProperties() { return Arrays.asList(COMMAND_PROPERTY, PROPERTIES_FILE_FLAG); }
//	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("text", "boolean"); }
	@Override public String getCategory() { return NodeCategories.EXTERNAL_PROCESSES; }
}
