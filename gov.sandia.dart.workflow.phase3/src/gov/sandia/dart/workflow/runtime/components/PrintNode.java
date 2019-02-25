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
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;

public class PrintNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		StringBuilder builder = new StringBuilder();
		Collection<InputPort> values = workflow.getNode(getName()).inputs.values();
		String formatString = getFormatString(runtime, properties);

		if (!values.parallelStream().anyMatch(e -> e.isConnected())) {
			runtime.getOut().println(formatString);
			builder.append(formatString).append("\n");

		} else {
			for (InputPort port: values) {

				String arg1 = (String) runtime.getInput(getName(), port.name, String.class);
				// TODO If connected input gives null, should we throw?
				if (arg1 != null) {
					String result = arg1.trim();
					if (StringUtils.isNotEmpty(formatString)) {
						result = String.format(formatString, result);
					}

					runtime.getOut().println(result);
					builder.append(result).append("\n");
				}
			}
		}
		return Collections.singletonMap("f", builder.toString());
	}

	public String getFormatString(RuntimeData runtime, Map<String, String> properties) {
		return getOptionalStringFromPortOrProperty(runtime, properties, "formatString");
	}

	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }
	@Override public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("formatString")); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.INPUT_OUTPUT); }


}
