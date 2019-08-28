/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.configuration.Input;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.Output;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.util.PropertyUtils;

public abstract class AbstractCreateNodeFeature extends AbstractCreateFeature {

	protected AbstractCreateNodeFeature(IFeatureProvider fp, String name,
			String description) {
		super(fp, name, description);
	}

	protected static void addPortsAndProperties(WFNode newNode, NodeType nodeType) {
		for (Input input: nodeType.getInputs()) {
			InputPort ip = DomainFactory.eINSTANCE.createInputPort();
			ip.setName(input.getName());
			ip.setType(input.getType());
	        newNode.getInputPorts().add(ip);
		}
				
		for (Output output: nodeType.getOutputs()) {
			OutputPort op = DomainFactory.eINSTANCE.createOutputPort();
			op.setName(output.getName());
			op.setType(output.getType());
			if (!StringUtils.isEmpty(output.getFilename()))
				PropertyUtils.setProperty(op, "filename", output.getFilename());
	        newNode.getOutputPorts().add(op);
		}
			    
		for (Prop property: nodeType.getProperties()) {
			Property p = DomainFactory.eINSTANCE.createProperty();
			p.setName(property.getName());
			p.setType(property.getTypeName());
			p.setValue(property.getValue());
			p.setAdvanced(property.isAdvanced());
			newNode.getProperties().add(p);
		}
	}
	
	
	public static WFNode duplicateNode(WFNode node) {
		NodeType nodeType = new NodeType(node);
		WFNode newNode = DomainFactory.eINSTANCE.createWFNode();               
		newNode.setName(node.getName());
		newNode.setType(node.getType());
		newNode.setLabel(node.getLabel());
		addPortsAndProperties(newNode, nodeType);
		return newNode;
	}

}
