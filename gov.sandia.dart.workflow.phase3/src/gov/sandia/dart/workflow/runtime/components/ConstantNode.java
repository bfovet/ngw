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

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ConstantNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {	
		return Collections.singletonMap("f", getValue(properties));		
	}

	public String getValue(Map<String, String> properties) {
		final String value = properties.get("value");
		return value;
	}

	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList("f"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("value"); }

	@Override public String getCategory() { return "Sources"; }
}	
