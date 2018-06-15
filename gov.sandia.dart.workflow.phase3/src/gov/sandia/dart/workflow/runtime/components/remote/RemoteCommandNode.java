/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class RemoteCommandNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {
		String hostname = getStringFromPortOrProperty(runtime, properties, "hostname");
	 	String username = System.getProperty("user.name");		
		String command = getStringFromPortOrProperty(runtime, properties, "command");
		String path = getStringFromPortOrProperty(runtime, properties, "remotePath");

		try {
			Remote exec = new Remote(hostname, username);
			if (!StringUtils.isEmpty(path))
				exec.setPath(path);
			//try {
				exec.connect();
				runtime.log().debug(getName() + ": executing command '" + render(command) + "'");
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				String result = exec.execute(command, baos, runtime);
				String errors = baos.toString();
				Map<String, Object> outputs = new HashMap<>();
				outputs.put("output", result);
				outputs.put("error", errors);
				return outputs;

			//} finally {
			//	exec.disconnect();
			//}
			
		} catch(Exception ex) {
			throw new SAWWorkflowException("Error executing command", ex);
		}
		
	}

	@Override
	public List<String> getDefaultProperties() {
		return Arrays.asList("hostname", "command", "remotePath");
	}
	
	@Override
	public List<String> getDefaultOutputNames() {
		return Arrays.asList("output", "error");
	}
	
	@Override
	public List<String> getDefaultOutputTypes() {
		return Arrays.asList("default", "default");
	}
	
	@Override
	public List<String> getDefaultInputNames() {
		return Collections.singletonList("trigger");
	}
	
	@Override
	public List<String> getDefaultInputTypes() {
		return Collections.singletonList("default");
	}
	
	@Override
	public String getCategory() {
		return "Control";
	}
	
}
