/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;

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
	public Map<String, Object> doExecute(Map<String, String> properties,  WorkflowDefinition workflow, RuntimeData runtime) {
		
		WorkflowDefinition.Node thisNode = workflow.getNode(getName());
		possiblyWritePropertiesFile(properties, runtime);
		int exitStatus = 0;

		// TODO This needs a rewrite. If there's an error executing the process, the outputs are lost.
		ByteArrayOutputStream stdoutReceptor = new ByteArrayOutputStream();
		ByteArrayOutputStream stderrReceptor = new ByteArrayOutputStream();

		try {
			Process process = setUpProcess(properties, runtime);
			ICancelationListener listener = () -> process.destroy();
			runtime.addCancelationListener(listener);			
			try { 				
				
				exitStatus = mediateStandardIO(process, thisNode, stdoutReceptor, stderrReceptor, runtime);
			} finally {
				runtime.removeCancelationListener(listener);
			}
		} catch (Throwable t) {
			throw new SAWWorkflowException("Problem executing external program", t);
		}	

		return postProcessOutputs(thisNode, exitStatus, stdoutReceptor, stderrReceptor, runtime);
	}

	protected Process setUpProcess(Map<String, String> properties, RuntimeData runtime) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.environment().putAll(runtime.getenv());
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
			OutputStream stdoutReceptor,
			OutputStream stderrReceptor,
			RuntimeData runtime) throws Throwable {
		PrintStream stdoutPrintStream = new PrintStream(stdoutReceptor);	
		PrintStream stderrPrintStream = new PrintStream(stderrReceptor);	
		
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
		int exitStatus = p.waitFor();
		t2.join();
		t1.join();
		return exitStatus;
	}
	
	private Map<String, Object> postProcessOutputs(Node thisNode,
			int exitStatus,
			ByteArrayOutputStream stdoutReceptor,
			ByteArrayOutputStream stderrReceptor,
			RuntimeData runtime) {
		
		Map<String, Object> outputMap = new HashMap<>();
		
		// EJFH Doing these "ifs" to allow for the ports maybe having been removed. Not sure I approve of that, but...		
		if (thisNode.outputs.get(STDOUT_PORT_NAME) != null) {
				outputMap.put(STDOUT_PORT_NAME, stdoutReceptor.toByteArray());
				runtime.getOut().println(stdoutReceptor.toString());
		}
		
		if (thisNode.outputs.get(STDERR_PORT_NAME) != null) {
				outputMap.put(STDERR_PORT_NAME, stderrReceptor.toByteArray());
				runtime.getErr().println(stdoutReceptor.toString());
		}
		if (thisNode.outputs.get(EXIT_STATUS) != null) {
			outputMap.put(EXIT_STATUS, exitStatus);
		}
		
		return outputMap;
	}
	
	public String getCommand(Map<String, String> properties) {
		return properties.get("command");
	}
	
	@Override public List<String> getDefaultInputNames() { return Arrays.asList(STDIN_PORT_NAME); }
	@Override public List<String> getDefaultOutputNames() { return Arrays.asList(STDOUT_PORT_NAME, STDERR_PORT_NAME, EXIT_STATUS); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList(COMMAND_PROPERTY, PROPERTIES_FILE_FLAG); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("text", "boolean"); }
	@Override public String getCategory() { return "Pipes"; }
}
