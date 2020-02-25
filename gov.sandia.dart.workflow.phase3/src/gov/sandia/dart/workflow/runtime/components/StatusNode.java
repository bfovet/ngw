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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class StatusNode extends SAWCustomNode {
	private static final String STATUS = "status";
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		Object input = runtime.getInput(getName(), "x", Object.class);
		if (input == null)
			input = "UNDEFINED";

		String status = properties.get(STATUS);
		if (StringUtils.isEmpty(status))
			status = String.valueOf(input);
		runtime.status(this, status);
		return Collections.singletonMap("f", input);
	}

	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }
	@Override public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo("status")); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.UI); }


}
