/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.remote;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class RemoteCommandNode extends SAWCustomNode {

	public static final String HOSTNAME = "hostname";
	public static final String REMOTE_PATH = "remotePath";
	public static final String USERNAME = "username";
	public static final String JMPHOST = "jumphost";
	public static final String JMPUSER = "jumpuser";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {
		String hostname = getStringFromPortOrProperty(runtime, properties, HOSTNAME);
	 	String username = System.getProperty("user.name");		
		String command = getStringFromPortOrProperty(runtime, properties, "command");
		String path = getStringFromPortOrProperty(runtime, properties, REMOTE_PATH);

		try {
			Remote exec = new Remote(hostname, username);
			if (!StringUtils.isEmpty(path))
				exec.setPath(path);
			//try {
				exec.connect(runtime.log());
				runtime.log().debug(getName() + ": executing command '" + render(command) + "'");
				ByteArrayOutputStream errStream = new ByteArrayOutputStream();
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();

				int exitCode = exec.execute(command, outStream, errStream, runtime);
				Map<String, Object> outputs = new HashMap<>();
				outputs.put("output", outStream.toString());
				outputs.put("error", errStream.toString());
				outputs.put("exitCode", exitCode);
				return outputs;

			//} finally {
			//	exec.disconnect();
			//}
			
		} catch(Exception ex) {
			throw new SAWWorkflowException("Error executing command", ex);
		}
		
	}

	@Override
	public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(HOSTNAME), new PropertyInfo("command"), new PropertyInfo(REMOTE_PATH));	}
	
	@Override
	public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("output", "default"), new OutputPortInfo("error", "default"), new OutputPortInfo("exitCode", "int")); }
	
	@Override
	public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("trigger", "default")); }
	
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.EXTERNAL_PROCESSES, NodeCategories.REMOTE); }
	
}
