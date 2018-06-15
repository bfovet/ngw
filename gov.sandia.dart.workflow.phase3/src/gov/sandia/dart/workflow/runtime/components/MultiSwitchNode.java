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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

/*
 * Pass the data from property-or-port "passThrough" to the output port
 * named from the property-or-port "selector".
 */
public class MultiSwitchNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
				
		String selector = getStringFromPortOrProperty(runtime, properties, "selector");
		Object data = getObjectFromPortOrProperty(runtime, properties, "passThrough");
								
		return Collections.singletonMap(selector, data);			
	}

	@Override public List<String> getDefaultInputNames() { return Arrays.asList("selector", "passThrough"); }
	@Override public String getCategory() { return "Control"; }
}
