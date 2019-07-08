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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class ParameterFileNode extends SAWCustomNode {
	private static final String FILENAME = "fileName";

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {	
		try {
			File file = getFileFromPortOrProperty(runtime, properties, FILENAME);
			Properties p = new Properties();
			// All we're really doing is double-checking that the file is OK. The parameters are actually
			// injected into the runtime during the precheck in WorkflowProcess
			try (FileInputStream fis = new FileInputStream(file)) {
				p.load(fis);
			}
			return Collections.singletonMap("f", p);
		} catch (IOException e) {
			throw new SAWWorkflowException("Error reading properties file in node '" + getName() + "'", e);
		}
	}

	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f", "map")); }

	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(FILENAME, "home_file")); }

	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.WORKFLOW, "Sources"); }
}	
