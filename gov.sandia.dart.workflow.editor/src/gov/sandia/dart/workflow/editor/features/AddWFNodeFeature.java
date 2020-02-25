/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IDirectEditingInfo;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.impl.AbstractAddFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.PlatformGraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ICreateService;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.rendering.GenericWFNodeGARenderer;
import gov.sandia.dart.workflow.editor.rendering.PortGARenderer;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class AddWFNodeFeature extends AbstractAddFeature {
	
	public static final int NODE_HEIGHT = 75;

	public static final int NODE_WIDTH = 130;

	public static final int PARAMETER_HEIGHT = 24;
	
	public static final String LEND = "_LEND_";
	public static final String LBEGIN = "_LBEGIN_";
	static final int TOP_PORT = 32;
	static final int PORT_SPACING = 15;

	public AddWFNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public PictogramElement add(IAddContext context) {
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ContainerShape shape = peCreateService.createContainerShape(context.getTargetContainer(), true);
		IGaCreateService gaCreateService = Graphiti.getGaCreateService();
		PlatformGraphicsAlgorithm ga = gaCreateService.createPlatformGraphicsAlgorithm(shape, GenericWFNodeGARenderer.ID);
		WFNode addednode = (WFNode) context.getNewObject();

		// define a default size for the shape
		int width = context.getWidth() > 0 ? context.getWidth() : computeNodeWidth(addednode);
		int height = context.getHeight() > 0 ? context.getHeight() : computeNodeHeight(addednode);
		ga.setWidth(width);
		ga.setHeight(height);
		ga.setX(context.getX());
		ga.setY(context.getY());
		link(shape, addednode);
		layoutPictogramElement(shape);
		addToDiagram(addednode);
				
		// INPUT PORTS
		for (int i = 0; i<addednode.getInputPorts().size(); ++i) {
			final InputPort port = addednode.getInputPorts().get(i);        
			createInputAnchor(shape, ga, i, port);
		}

		// OUTPUT PORTS
		for (int i = 0; i<addednode.getOutputPorts().size(); ++i) {
			final OutputPort port = addednode.getOutputPorts().get(i);
			createOutputAnchor(shape, ga, i, port);
		}

		if (ParameterUtils.isParameter(addednode)) {
			IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
			directEditingInfo.setMainPictogramElement(shape);
			directEditingInfo.setPictogramElement(shape);
			directEditingInfo.setGraphicsAlgorithm(ga);
		}
		
		
		return shape;
	}

	public void addToDiagram(WFNode addednode) {
		if (addednode.eResource() == null) {
			EList<EObject> contents = getDiagram().eResource().getContents();
			contents.add(addednode);
			contents.addAll(addednode.getInputPorts());
			for (InputPort port: addednode.getInputPorts())
				contents.addAll(port.getProperties());
			contents.addAll(addednode.getOutputPorts());   
			for (OutputPort port: addednode.getOutputPorts())
				contents.addAll(port.getProperties());
			contents.addAll(addednode.getProperties());        	
		}
	}
	@Override
	public boolean canAdd(IAddContext context) {
		// check if user wants to add a WFNode
		if (context.getNewObject() instanceof WFNode) {
			// check if user wants to add to a diagram
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}	
	public static int computeNodeHeight(WFNode addednode) {
		if (ParameterUtils.isParameter(addednode))
			return PARAMETER_HEIGHT;
		
		int numPorts = Math.max(addednode.getInputPorts().size(), addednode.getOutputPorts().size());
		// Minimum height is tall enough for several lines of text
		return Math.max(NODE_HEIGHT, TOP_PORT + (PORT_SPACING * numPorts));
	}

	public static int computeNodeWidth(WFNode addednode) {
		// About 16 pixels for each character in the text 
		// return (4 + addednode.getType().length()) * 16;
		return NODE_WIDTH;
	}
	
	public void createOutputAnchor(ContainerShape containerShape, GraphicsAlgorithm ga, int i, final OutputPort port) {
		boolean isLoopEnd = port.getName().equals(LEND);
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ICreateService createService = Graphiti.getCreateService();
		IGaService gaService = Graphiti.getGaService();
		FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);	
		int width = ga.getWidth();
		int height = ga.getHeight();
		if (isLoopEnd) {
			anchor.setLocation(createService.createPoint(width/2, height-10));
		} else {
			anchor.setLocation(createService.createPoint(width, getTopPortHeight(port.getNode()) + (i*PORT_SPACING)));
		}
		anchor.setReferencedGraphicsAlgorithm(ga);
		anchor.setUseAnchorLocationAsConnectionEndpoint(false);

		PlatformGraphicsAlgorithm portShape = gaService.createPlatformGraphicsAlgorithm(anchor, PortGARenderer.ID);
		
		if (isLoopEnd) {
			gaService.setLocationAndSize(portShape, -5, -5, 10, 10);
		} else {
			gaService.setLocationAndSize(portShape, -10, -5, 10, 10);
		}
		link(anchor, port);
	}

	public int getTopPortHeight(WFNode wfNode) {
		return ParameterUtils.isParameter(wfNode) ? 14 : TOP_PORT;
	}

	public void createInputAnchor(ContainerShape containerShape, GraphicsAlgorithm ga,
			int i, final InputPort port) {
		boolean isLoopBegin = port.getName().equals(LBEGIN);
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		ICreateService createService = Graphiti.getCreateService();
		IGaService gaService = Graphiti.getGaService();
		int width = ga.getWidth();
		int height = ga.getHeight();

		FixPointAnchor anchor = peCreateService.createFixPointAnchor(containerShape);
		if (isLoopBegin) {
			anchor.setLocation(createService.createPoint(width/2, height-10));
		} else {
			anchor.setLocation(createService.createPoint(0, TOP_PORT + (i*PORT_SPACING)));
		}
		anchor.setReferencedGraphicsAlgorithm(ga);
		anchor.setUseAnchorLocationAsConnectionEndpoint(false);
		PlatformGraphicsAlgorithm portShape = gaService.createPlatformGraphicsAlgorithm(anchor, PortGARenderer.ID);
		if (isLoopBegin) {
			gaService.setLocationAndSize(portShape, -5, -5, 10, 10);
		} else {
			gaService.setLocationAndSize(portShape, 0, -5, 10, 10);
		}
		link(anchor, port);
	}

	public boolean positionInputAnchor(String name, FixPointAnchor a, GraphicsAlgorithm rect, int index) {
		boolean isLoop = LBEGIN.equals(name);
		ICreateService createService = Graphiti.getCreateService();
		Point oldLocation = a.getLocation();
		Point newLocation;
		if (isLoop) 
			newLocation = createService.createPoint(rect.getWidth()/2, rect.getHeight()-10);
		else
			newLocation = createService.createPoint(0, TOP_PORT + (index*PORT_SPACING));
		
		if (!oldLocation.equals(newLocation)) {
			a.setLocation(newLocation);
		}
		return !oldLocation.equals(newLocation);
	}

	public boolean positionOutputAnchor(String name, FixPointAnchor a, GraphicsAlgorithm rect, WFNode dartNode, int index) {
		boolean isLoop = LEND.equals(name);
		ICreateService createService = Graphiti.getCreateService();
		Point oldLocation = a.getLocation();
		Point newLocation;
		if (isLoop)
			newLocation = createService.createPoint(rect.getWidth()/2, rect.getHeight()-10);
		else
			newLocation = createService.createPoint(rect.getWidth(), getTopPortHeight(dartNode) + (index*PORT_SPACING));
		
		if (!oldLocation.equals(newLocation)) {
			a.setLocation(newLocation);
		}
		return !oldLocation.equals(newLocation);
	}

	
	
}
