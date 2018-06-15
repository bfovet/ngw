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
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Experimental (ab)use of Foss' marker mechanism to rejoin the workflow
 * at a specified checkpoint that was set earlier via a setCheckpointNode.
 * 
 * This node will look for the name of the target setCheckpointNode 
 * via the default property "nameofCheckpoint", but will prefer a name
 * specified via an input port called "nameOfCheckpoint" if such a port
 * is found to have been added.
 * 
 * @author mrglick
 *
 */
public class RejoinCheckpointNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		String nameOfCheckpoint = (String) runtime.getInput(getName(), "nameOfCheckpoint", String.class);

		if (nameOfCheckpoint == null || nameOfCheckpoint.isEmpty()) {
			nameOfCheckpoint = properties.get("nameOfCheckpoint");
			if (nameOfCheckpoint == null) {
				throw new SAWWorkflowException("no designated checkpoint to rejoin!");
			}
			runtime.log().trace(getName() + ": rejoining checkpoint " + nameOfCheckpoint + ", specified via property");
		} else
			runtime.log().trace(getName() + ": rejoining checkpoint " + nameOfCheckpoint + ", specified via input port");

		Object marker = getMarker(runtime, nameOfCheckpoint);
		if (marker == null)
			throw new SAWWorkflowException("No marker checkpoint named " + nameOfCheckpoint);

		loop(marker);	

		return Collections.emptyMap();
	}

	@Override public List<String> getDefaultProperties() { return Collections.singletonList("nameOfCheckpoint"); }
	@Override public List<String> getDefaultPropertyTypes() { return Collections.singletonList("text"); }
	@Override public List<String> getDefaultInputNames() { return Collections.singletonList("x"); }

	@Override
	public String getCategory() {
		return "Control";
	}
}
