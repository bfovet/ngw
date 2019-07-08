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

public class OnOffSwitchNode extends SAWCustomNode {

	private static final String ON_OFF = "onOff";
	private static final String INPUT = "input";
	private static final String OUTPUT = "output";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		boolean onOff = getOptionalBooleanProperty(properties, ON_OFF);
		Map<String, Object> results = new HashMap<>();
		if (onOff) {
			for (String name: workflow.getNode(getName()).inputs.keySet()) {
				if (name.equals(INPUT))
					results.put(OUTPUT, runtime.getInput(getName(), name, Object.class));
				else
					results.put(name, runtime.getInput(getName(), name, Object.class));
		    }
		} 
		return results;
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(ON_OFF, "boolean"));
	}
	
	@Override
	public List<InputPortInfo> getDefaultInputs() {
		return Arrays.asList(new InputPortInfo(INPUT));
	}

	@Override
	public List<OutputPortInfo> getDefaultOutputs() {
		return Arrays.asList(new OutputPortInfo(OUTPUT));
	}
	
	@Override
	public String getCategory() {
		return "Control";
	}
}
