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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
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
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(FOLDER_NAME)); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(FOLDER_OUT_PORT, "text")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(FOLDER_NAME, "home_file")); }

	@Override public String getCategory() { return NodeCategories.INPUT_OUTPUT; }
}
