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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class DownloadFileNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {		
		String hostname = getStringFromPortOrProperty(runtime, properties, "hostname");
		String username = System.getProperty("user.name");
		String localFile = getStringFromPortOrProperty(runtime, properties, "localFile");
		String remoteFile = getStringFromPortOrProperty(runtime, properties, "remoteFile");
		String path = getStringFromPortOrProperty(runtime, properties, "remotePath");
		
		if (StringUtils.isEmpty(localFile))
			localFile = new File(remoteFile).getName();
		
		File targetFile = new File(localFile);
		if (!targetFile.isAbsolute()) {
			targetFile = new File(runtime.getWorkDirectory(), localFile);
		}
		try {
			Remote exec = new Remote(hostname, username);
			if (path != null)
				exec.setPath(path);
			//try {
				exec.connect();
				exec.download(targetFile, remoteFile, runtime);
			//} finally {
			//	exec.disconnect();
			//}
			
		} catch(Exception ex) {
			throw new SAWWorkflowException("Error downloading file", ex);
		}
		
		return Collections.singletonMap("file", targetFile.getAbsolutePath());
	}

	@Override
	public List<String> getDefaultProperties() {
		return Arrays.asList("hostname", "localFile", "remoteFile", "remotePath");
	}

	@Override
	public List<String> getDefaultOutputNames() {
		return Collections.singletonList("file");
	}
	
	@Override
	public List<String> getDefaultOutputTypes() {
		return Collections.singletonList("output_file");
	}
	
	@Override
	public List<String> getDefaultInputNames() {
		return Collections.singletonList("trigger");
	}
	
	//TODO Optionally get file on input?
	@Override
	public List<String> getDefaultInputTypes() {
		return Collections.singletonList("default");
	}
	
	@Override
	public String getCategory() {
		return "Control";
	}
}
