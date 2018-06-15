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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class FailNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		String arg1 = (String) runtime.getInput(getName(), DEFAULT_INPUT, String.class);
		if (arg1 == null)
			arg1 = "";

		String result = arg1.trim();
		if (StringUtils.isNotEmpty(getFormatString(properties))) {
			result = String.format(getFormatString(properties), result);
		}
		runtime.getOut().println(result);
		throw new SAWWorkflowException(result);
	}

	public String getFormatString(Map<String, String> properties) {
		return properties.get("formatString");
	}

	@Override public List<String> getDefaultInputNames() { return Collections.singletonList("x"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("formatString"); }

	@Override
	public String getCategory() {
		return "Control";
	}

}
