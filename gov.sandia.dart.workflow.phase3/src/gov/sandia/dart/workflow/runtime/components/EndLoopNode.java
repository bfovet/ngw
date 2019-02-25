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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class EndLoopNode extends SAWCustomNode {
	private Map<String, List<Object>> results = new HashMap<>(); 

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		
		List<Object> theResults = getResults(runtime);		
		Object input = runtime.getInput(getName(), "x", String.class);					
		theResults.add(input);
		Object loopHead = getMarker(runtime, properties.get("target"));
		if (loopHead != null) {
			loop(loopHead);
		} else {
			clearResults(runtime);
			return Collections.singletonMap("f", theResults);
		}

		return Collections.singletonMap("f", "true");		
	}
	
	private void clearResults(RuntimeData runtime) {
		results.remove(String.valueOf(runtime.hashCode()));
	}

	// TODO This is a hack! Data should live in RuntimeData
	private List<Object> getResults(RuntimeData runtime) {
		List<Object> theResults = results.get(String.valueOf(runtime.hashCode()));
		if (theResults == null) {
			theResults = new ArrayList<Object>();
			results.put(String.valueOf(runtime.hashCode()), theResults);
		}
		return theResults;
	}


	@Override public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("f"), new OutputPortInfo("_LEND_")); }

	@Override
	public String getCategory() {
		return "Control";
	}

}
