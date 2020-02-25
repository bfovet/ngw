/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
		commonProps.put(PropertyUtils.PRIVATE_WORK_DIR, new Prop(PropertyUtils.PRIVATE_WORK_DIR, "boolean", "false", false));
		commonProps.put(PropertyUtils.ASYNC, new Prop(PropertyUtils.ASYNC, "boolean", "false", false));
		commonProps.put("clear private work directory", new Prop("clear private work directory", "boolean", "false", false));
		commonProps.put(PropertyUtils.HIDE_IN_NAVIGATOR, new Prop(PropertyUtils.HIDE_IN_NAVIGATOR, "boolean", "false", true));
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
 			properties.add(new Prop(property));
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

	public NodeType setLabel(String label) {
		this.label = label;
		return this;
	}
	
	public String getLabel() {
		return label;
	}

	public NodeType setCategories(List<String> categories) {
		this.categories = categories;
		return this;
	}

	public NodeType setCategories(String... categories) {
		this.categories = Arrays.asList(categories);
		return this;
	}

	public List<String> getCategories() {
		return categories;
	}

	public NodeType setInputs(List<Input> inputs) {
		this.inputs = inputs;
		return this;
	}
	
	public NodeType setInputs(Input... inputs) {
		this.inputs = Arrays.asList(inputs);
		return this;
	}

	public NodeType setOutputs(List<Output> outputs) {
		this.outputs = outputs;
		return this;
	}
	
	public NodeType setOutputs(Output... outputs) {
		this.outputs = Arrays.asList(outputs);
		return this;
	}

	public NodeType setProperties(List<Prop> properties) {
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
		return this;
	}
	
	public NodeType setProperties(Prop...  properties) {
		setProperties(Arrays.asList(properties));
		return this;
	}
	
	public List<Prop> getProperties() {
		if (properties == null) {
			setProperties(Collections.emptyList());
		} 
		return properties;		
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

	public NodeType setDisplayLabel(String displayLabel) {
		this.displayLabel = displayLabel == null ? "" : displayLabel;
		return this;
	}

}
