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

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class StringTemplateSubstitutionNode extends SAWCustomNode {
	private static final String TEMPLATE_LABEL = "template";
	private static final String OUTPUT_LABEL = "result";

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		String templateString = getTemplateString(properties, runtime);
		
		// substitute every property/port name with its value
		for (String propertyName : properties.keySet()) {
			if (isInternalProperty(propertyName))
				continue;
			String propertyValue = properties.get(propertyName);
			if (propertyValue != null) {
				runtime.log().debug("replacing {0} with {1}", propertyName, propertyValue);
				templateString = templateString.replaceAll(propertyName, propertyValue);
			}
		}
		for (String portName : runtime.getInputNames(getName())) {
			if (isTemplateLabel(portName))
				continue;
			String portValue = (String) runtime.getInput(getName(), portName, String.class);
			if (portValue != null)
				templateString = templateString.replaceAll(portName, portValue);
		}

		if (workflow.getNode(getName()).outputs.containsKey("instantiatedTemplate"))
			return Collections.singletonMap("instantiatedTemplate", templateString);
		else
			return Collections.singletonMap(OUTPUT_LABEL, templateString);
	}
	
	private String getTemplateString(Map<String, String> properties, RuntimeData runtime) {
		String templateString = null;
		
		try {
			templateString = getStringFromPortOrProperty(runtime, properties, "template");
		} catch (SAWWorkflowException ex) {
			templateString = getStringFromPortOrProperty(runtime, properties, "templateString"); // legacy name
			runtime.log().debug("templateString port/property name DEPRECATED");
		}		
		return templateString;
	}
	
	protected boolean isInternalProperty(String propertyName) {
		return propertiesContains(reservedProperties, propertyName) || isTemplateLabel(propertyName);
	}
	
	private boolean isTemplateLabel(String s) {
		return s.equals(TEMPLATE_LABEL) || s.equals("templateString");
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(TEMPLATE_LABEL)); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(OUTPUT_LABEL)); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(TEMPLATE_LABEL)); }
	@Override public String getCategory() { return NodeCategories.TEXT_DATA; }
}
