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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class StringCatNode extends SAWCustomNode {
	
	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		StringBuilder result = new StringBuilder();
		String myNodeName = getName();
				
		List<String> inputNames = new ArrayList<>(runtime.getInputNames(myNodeName));
		Collections.sort(inputNames);
		inputNames.stream().forEach(portName -> result.append(runtime.getInput(myNodeName, portName, String.class)));
		
		return Collections.singletonMap("f", result.toString());
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x"), new InputPortInfo("y")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }
	@Override public String getCategory() { return NodeCategories.TEXT_DATA; }
}
