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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class RotaryInputSwitchNode extends SAWCustomNode {

	private static final String A = "a", B = "b";
	private static final String OUTPUT = "output";
	private static final String SELECTOR = "selector";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		String selector = properties.get(SELECTOR);
		if (selector == null || !workflow.getNode(getName()).inputs.containsKey(selector))
			throw new SAWWorkflowException(String.format("Invalid setting in node %s: '%s'", getName(), String.valueOf(selector)));
		
		Object input = runtime.getInput(getName(), selector, Object.class);
		
		return Collections.singletonMap(OUTPUT, input);
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(SELECTOR, "text", A));
	}
	
	@Override
	public List<InputPortInfo> getDefaultInputs() {
		return Arrays.asList(new InputPortInfo(A), new InputPortInfo(B));
	}

	@Override
	public List<OutputPortInfo> getDefaultOutputs() {
		return Arrays.asList(new OutputPortInfo(OUTPUT));
	}
	
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.CONTROL, NodeCategories.UI); }
}
