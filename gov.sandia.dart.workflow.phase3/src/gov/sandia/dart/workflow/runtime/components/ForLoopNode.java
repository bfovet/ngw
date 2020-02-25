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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ForLoopNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		final int start = getIntFromPortOrProperty(runtime, properties, "start");
		final int step = getIntFromPortOrProperty(runtime, properties, "step");
		final int end = getIntFromPortOrProperty(runtime, properties, "end");
		
		int counter = start;
		Object data = getMarkerData(runtime);
		if (data instanceof Integer) {
			counter = (Integer) data;
		}
		
		if (counter + step <= end) {			
			setMarker(runtime, counter + step);
		} else {
			clearMarker(runtime);
		}
		return Collections.singletonMap("f", String.valueOf(counter));
	}	
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("start", "integer"), new PropertyInfo("step", "integer"), new PropertyInfo("end", "integer")); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x"), new InputPortInfo("_LBEGIN_"), new InputPortInfo("start"), new InputPortInfo("step"), new InputPortInfo("end")); }	
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }

	@Override
	public String getCategory() {
		return "Control";
	}

}
