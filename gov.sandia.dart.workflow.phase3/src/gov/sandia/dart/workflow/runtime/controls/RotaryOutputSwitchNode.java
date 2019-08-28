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

public class RotaryOutputSwitchNode extends SAWCustomNode {

	private static final String A = "a", B = "b";
	private static final String INPUT = "input";
	private static final String SELECTOR = "selector";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		String selector = properties.get(SELECTOR);
		if (selector == null || !workflow.getNode(getName()).outputs.containsKey(selector))
			throw new SAWWorkflowException(String.format("Invalid setting in node %s: '%s'", getName(), String.valueOf(selector)));
		
		return Collections.singletonMap(selector, runtime.getInput(getName(), INPUT, Object.class));
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(SELECTOR, "text", A));
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
