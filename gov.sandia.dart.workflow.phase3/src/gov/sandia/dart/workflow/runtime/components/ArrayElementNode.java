package gov.sandia.dart.workflow.runtime.components;

import java.lang.reflect.Array;
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

public class ArrayElementNode extends SAWCustomNode {

	private static final String ELEMENT = "element";
	private static final String INDEX = "index";
	private static final String ARRAY = "array";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		int index = getIntFromPortOrProperty(runtime, properties, INDEX);		
		Object array = runtime.getInput(getName(), ARRAY, Object.class);
		if (array == null) {
			throw new SAWWorkflowException("Input array missing in node " + getName());

		}
		
		if (!array.getClass().isArray()) 
			array = runtime.getInput(getName(), ARRAY, String[].class);

		int length = Array.getLength(array);
		if (index == -1) {
			index = length - 1;
		}

		if (index < 0 || index >= length)
			throw new SAWWorkflowException("Array index out of bounds at node " + getName() + ": " + index);

		return Collections.singletonMap(ELEMENT, Array.get(array, index));
	}

	@Override
	public List<InputPortInfo> getDefaultInputs() {
		return Arrays.asList(new InputPortInfo(ARRAY), new InputPortInfo(INDEX, "int"));
	}
	
	@Override
	public List<OutputPortInfo> getDefaultOutputs() {
		return Arrays.asList(new OutputPortInfo(ELEMENT));
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo(INDEX, "int"));
	}
	
	@Override
	public String getCategory() {
		return NodeCategories.SEQ_DATA;
	}

}
