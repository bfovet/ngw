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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
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
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("true"), new OutputPortInfo("false")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("compareString")); }
	@Override public String getCategory() { return "Control"; }
	
	public String getCompareString(Map<String, String> properties) {
		String s = properties.get("compareString");
		if (s == null)
			s = "";
		return s.trim();
	}
}
