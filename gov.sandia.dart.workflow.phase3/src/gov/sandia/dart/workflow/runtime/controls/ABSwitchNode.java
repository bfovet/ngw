/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.controls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class ABSwitchNode extends SAWCustomNode {

	private static final String A = "a", B = "b";
	private static final String INPUT = "input";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		boolean a = getOptionalBooleanProperty(properties, A);
		Map<String, Object> results = new HashMap<>();

		String output = a ? "a" : "b";
		results.put(output, runtime.getInput(getName(), INPUT, Object.class));
		return results;
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(A, "boolean"));
	}
	
	@Override
	public List<InputPortInfo> getDefaultInputs() {
		return Arrays.asList(new InputPortInfo(INPUT));
	}

	@Override
	public List<OutputPortInfo> getDefaultOutputs() {
		return Arrays.asList(new OutputPortInfo(A), new OutputPortInfo(B));
	}
	
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.CONTROL, NodeCategories.UI); }
}
