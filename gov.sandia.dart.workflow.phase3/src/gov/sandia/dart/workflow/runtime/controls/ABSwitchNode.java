package gov.sandia.dart.workflow.runtime.controls;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
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
	
	@Override
	public String getCategory() {
		return "Control";
	}
}
