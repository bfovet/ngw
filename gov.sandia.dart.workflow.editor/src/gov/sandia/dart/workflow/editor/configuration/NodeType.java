/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.PaletteBuilder;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class NodeType {

	private String label;
	private String name;
	private String displayLabel = "";

	private List<String> categories;
	private List<Input> inputs;
	private List<Output> outputs;
	private List<Prop> properties;
	
	private static Map<String, Prop> commonProps = new HashMap<>();
	static {
		commonProps.put("privateWorkDir", new Prop("privateWorkDir", "boolean", "false"));
		commonProps.put("async", new Prop("async", "boolean", "false"));
	}

	public NodeType(String name) {
		this.name = name;
	}

	public NodeType(String name, String category) {
		this.name = name;
		this.categories = Arrays.asList(category);
	}
	
	public NodeType(String name, List<String> categories) {
		this.name = name;
		this.categories = categories;
	}
	
	public NodeType(WFNode node) {
		this(node.getType());
		setCategories(Arrays.asList(PaletteBuilder.USER_DEFINED));
		setLabel(node.getName());
		setDisplayLabel(node.getLabel());
		List<Prop> properties = new ArrayList<>();

		for (Property property: node.getProperties()) {
 			properties.add(new Prop(property.getName(), property.getType(), property.getValue()));
		}

		setProperties(properties);
		
		List<Input> inputs = new ArrayList<>();
		for (InputPort input: node.getInputPorts()) {
			inputs.add(new Input(input.getName(), input.getType()));
		}
		setInputs(inputs);
		
		// TODO Handle all properties of all ports
		List<Output> outputs = new ArrayList<>();
		for (OutputPort output: node.getOutputPorts()) {
			String value = PropertyUtils.getProperty((Port) output, "filename");
			outputs.add(new Output(output.getName(), output.getType(), value));
		}
		setOutputs(outputs);
	}

	public String getName() {
		return name;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}

	public void setCategories(List<String> categories) {
		this.categories = categories;
	}

	public List<String> getCategories() {
		return categories;
	}

	public void setInputs(List<Input> inputs) {
		this.inputs = inputs;
	}
	
	public void setInputs(Input... inputs) {
		this.inputs = Arrays.asList(inputs);
	}

	public void setOutputs(List<Output> outputs) {
		this.outputs = outputs;
	}
	
	public void setOutputs(Output... outputs) {
		this.outputs = Arrays.asList(outputs);
	}

	public void setProperties(List<Prop> properties) {
		properties = new ArrayList<>(properties);
		Map<String, Prop> common = new HashMap<>(commonProps); 
		for (Prop property: properties) {
			if (common.containsKey(property.getName())) {
				common.remove(property.getName());
			}
		}
		for (Prop property: common.values()) {
			properties.add(property);
		}
		
		this.properties = properties;
	}
	
	public void setProperties(Prop...  properties) {
		setProperties(Arrays.asList(properties));
	}
	
	public List<Prop> getProperties() {
		return properties == null ? Collections.emptyList() : properties;
	}
	
	public List<Output> getOutputs() {
		return outputs == null ? Collections.emptyList() : outputs;
	}

	public List<Input> getInputs() {
		return inputs == null ? Collections.emptyList() : inputs;
	}	
	
	public String getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel == null ? "" : displayLabel;
	}

}
