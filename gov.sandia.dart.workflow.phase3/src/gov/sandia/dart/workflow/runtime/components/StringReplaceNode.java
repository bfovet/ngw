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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringReplaceNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		String input = getStringFromPortOrProperty(runtime, properties, "x");
		
		String regex = (String) runtime.getInput(getName(), "regex", String.class);
		if (regex == null)
			regex = properties.get("regex");
		
		if (regex != null) {
			String replacement = getStringFromPortOrProperty(runtime, properties, "replacement");
			return Collections.singletonMap("f", input.replaceAll(regex, replacement));
		}
		
		// no regex port or property, so substitute every property/port name with its value
		for (String propertyName : properties.keySet()) {
			if (propertyName.equals("x"))
				continue;
			String propertyValue = properties.get(propertyName);
			if (propertyValue != null)
				input = input.replaceAll(propertyName, propertyValue);
		}
		for (String portName : runtime.getInputNames(getName())) {
			if (portName.equals("x"))
				continue;
			String portValue = (String) runtime.getInput(getName(), portName, String.class);
			if (portValue != null)
				input = input.replaceAll(portName, portValue);
		}
		return Collections.singletonMap("f", input);
	}
	
	@Override public List<String> getDefaultInputNames() { return Arrays.asList("x", "regex", "replacement"); }
	@Override public List<String> getDefaultOutputNames() { return Arrays.asList("f"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("regex", "replacement"); }
	@Override public String getCategory() { return "String Functions"; }
}
