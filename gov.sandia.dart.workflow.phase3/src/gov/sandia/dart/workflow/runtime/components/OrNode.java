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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class OrNode extends SAWCustomNode {

	private static final String OUTPUT = "output";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		// This is a little hacky right now. We're depending on SAWCustomNode to clean out the data map for this node
		// before  recording a new input, so this should only ever return the last piece of data.
		Collection<String> names = runtime.getInputNames(getName());
		
		if (names.size() != 1)
			throw new SAWWorkflowException("Internal error in OrNode: " + names);
		
		String name = names.iterator().next();
		return Collections.singletonMap(OUTPUT, runtime.getInput(getName(), name, Object.class));
	}
	
	@Override
	public List<InputPortInfo> getDefaultInputs() {
		return Arrays.asList(new InputPortInfo("left"), new InputPortInfo("right"));
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
