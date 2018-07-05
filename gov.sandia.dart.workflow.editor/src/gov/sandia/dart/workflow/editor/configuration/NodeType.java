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
import java.util.List;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.PaletteBuilder;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class NodeType {

	private static final String PRIVATE_WORK_DIR = "privateWorkDir";
	private String label;
	private String name;
	private String displayLabel = "";

	private String category;
	private List<Input> inputs;
	private List<Output> outputs;
	private List<Prop> properties;

	public NodeType(String name, String category) {
		this.name = name;
		this.category = category;
	}
	
	public NodeType(WFNode node) {
		this(node.getType(), PaletteBuilder.USER_DEFINED);
		setLabel(node.getName());
		setDisplayLabel(node.getLabel());
		List<Prop> properties = new ArrayList<>();
		// TODO This is a total hack. Need a more general mechanism to add this sort of assumed property!
		// TODO "async" is just like this
		boolean hasPrivateWorkDirProperty = false;
		for (Property property: node.getProperties()) {
			if (property.getName().equals(PRIVATE_WORK_DIR)) 
				hasPrivateWorkDirProperty = true;
			properties.add(new Prop(property.getName(), property.getType(), property.getValue()));
		}
		if (!hasPrivateWorkDirProperty)
			properties.add(new Prop(PRIVATE_WORK_DIR, "boolean", "false"));
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


	public String getCategory() {
		return category;
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
		boolean hasPrivateWorkDirProperty = false;
		for (Prop property: properties) {
			if (property.getName().equals(PRIVATE_WORK_DIR)) 
				hasPrivateWorkDirProperty = true;
		}
		if (!hasPrivateWorkDirProperty)
			properties.add(new Prop(PRIVATE_WORK_DIR, "boolean", "false"));
		this.properties = new ArrayList<>(properties);
	}
	
	public void setProperties(Prop...  properties) {
		boolean hasPrivateWorkDirProperty = false;
		for (Prop property: properties) {
			if (property.getName().equals(PRIVATE_WORK_DIR)) 
				hasPrivateWorkDirProperty = true;
		}
		this.properties = new ArrayList<>(Arrays.asList(properties));
		if (!hasPrivateWorkDirProperty)
			this.properties.add(new Prop(PRIVATE_WORK_DIR, "boolean", "false"));
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
