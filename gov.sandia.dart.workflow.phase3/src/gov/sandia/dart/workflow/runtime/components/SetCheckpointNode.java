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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

/**
 * Experimental (ab)use of Foss' marker mechanism to set a simple
 * checkpoint in the workflow that you can return to via a RejoinCheckpointNode.
 * 
 * Default input ports
 *     x: value passed through to output port f
 *     
 * Default output ports
 *     f: gets value from input port x
 *     checkpointName: provides the name of this node, for possible use by a RejoinCheckpointNode
 * 
 * Bonus fun behavior: Tries to pass through the values found on any other (user-defined) input
 * ports to any output port with the same name. (May not be used/useful.)
 * 
 * @author mrglick
 *
 */
public class SetCheckpointNode extends SAWCustomNode {
	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {		
		Map<String, Object> resultsMap = new HashMap<>();
		
		setMarker(runtime, "I am a SetCheckpointNode and this is my marker data (which is not an integer).");
		
		resultsMap.put("checkpointName", getName());

		resultsMap.put("f", (Object) runtime.getInput(getName(), "x", Object.class));
		
		passInputPortValuesToMatchingOutputPorts(workflow, runtime, resultsMap);
		
		return resultsMap;
	}
	
	public void passInputPortValuesToMatchingOutputPorts(WorkflowDefinition workflow, RuntimeData runtime, Map<String, Object> resultsMap) {
		WorkflowDefinition.Node me = workflow.getNode(getName());
		Collection<String> outputPortNames = workflow.getNode(getName()).outputs.keySet();

		for (WorkflowDefinition.InputPort port : me.inputs.values()) {
			if (outputPortNames.contains(port.name) && !resultsMap.containsKey(port.name)) // skip output ports that already have values
				resultsMap.put(port.name, (Object) runtime.getInput(getName(), port.name, Object.class)); // will this lose needed type info?
		}
	}

	@Override public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }	
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("f"), new OutputPortInfo("checkpointName")); }

	@Override
	public String getCategory() {
		return "Control";
	}
}
