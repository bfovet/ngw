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
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.StatUtils;

public class MinNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {
		double[] arg1 = (double[]) runtime.getInput(getName(), DEFAULT_INPUT, double[].class);
		double result = StatUtils.min(arg1);
		return Collections.singletonMap("f", result);		
	}
	
	@Override
	public List<String> getDefaultInputNames() {
		return Collections.singletonList("x");
	}

	@Override
	public List<String> getDefaultOutputNames() {
		return Collections.singletonList("f");
	}

	@Override
	public String getCategory() {
		return "Arrays";
	}


}
