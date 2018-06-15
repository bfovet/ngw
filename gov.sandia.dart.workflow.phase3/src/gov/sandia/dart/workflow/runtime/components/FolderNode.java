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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class FolderNode extends SAWCustomNode {
	public static String FOLDER_OUT_PORT = "folderReference",
						FOLDER_NAME = "folderName";
	
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		File targetFile = null;
		Map<String, Object> results = new HashMap<>();
		String fileName = getStringFromPortOrProperty(runtime, properties, FOLDER_NAME);		
		targetFile = new File(fileName);
		if (!targetFile.isAbsolute())
			targetFile = new File(runtime.getHomeDir(), fileName);
				
		results.put(FOLDER_OUT_PORT, targetFile.getAbsolutePath());
		
		return results;
	}
	
	@Override public List<String> getDefaultInputNames() { return Arrays.asList(FOLDER_NAME); }
	@Override public List<String> getDefaultOutputNames() { return Arrays.asList(FOLDER_OUT_PORT); }
	@Override public List<String> getDefaultOutputTypes() { return Arrays.asList("text"); }

	@Override public List<String> getDefaultProperties() { return Arrays.asList(FOLDER_NAME); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("home_file"); }

	@Override public String getCategory() { return "Pipes"; }
}
