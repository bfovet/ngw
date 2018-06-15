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
import java.util.Objects;

public class StringCompareNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
				
		String name = "false";
		String arg1 = getInputString(runtime);
		String arg2 = getCompareString(properties);
								
		Boolean b = Objects.equals(arg1, arg2);
		name = b.toString();

		// There are two outputs, "true" and "false". This will only send a token to the one that
		// matches the value of the boolean.
		String fname = name;
		return Collections.singletonMap(fname, arg1);			
	}

	private String getInputString(RuntimeData runtime) {
		String s = (String) runtime.getInput(getName(), "x", String.class);
		if (s == null)
			s = "";
		return s.trim();
	}
	
	@Override public List<String> getDefaultInputNames() { return Arrays.asList("x"); }
	@Override public List<String> getDefaultOutputNames() { return Arrays.asList("true", "false"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("compareString"); }
	@Override public String getCategory() { return "Control"; }
	
	public String getCompareString(Map<String, String> properties) {
		String s = properties.get("compareString");
		if (s == null)
			s = "";
		return s.trim();
	}
}
