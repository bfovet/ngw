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
import org.eclipse.graphiti.features.impl.AbstractAddShapeFeature;
import org.eclipse.graphiti.mm.algorithms.PlatformGraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.ICreateService;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeCreateService;

import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.editor.rendering.PortGARenderer;
import gov.sandia.dart.workflow.editor.rendering.ResponseGARenderer;
 
public class AddResponseFeature extends AbstractAddShapeFeature {
 
    public AddResponseFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
	public boolean canAdd(IAddContext context) {
        // check if user wants to add a Response
        if (context.getNewObject() instanceof Response) {
            // check if user wants to add to a diagram
            if (context.getTargetContainer() instanceof Diagram) {
                return true;
            }
        }
        return false;
    }
 
    @Override
	public PictogramElement add(IAddContext context) {
		IPeCreateService peCreateService = Graphiti.getPeCreateService();
		IGaService gaService = Graphiti.getGaService();
		ICreateService createService = Graphiti.getCreateService();

		ContainerShape shape = peCreateService.createContainerShape(context.getTargetContainer(), true);
		IGaCreateService gaCreateService = Graphiti.getGaCreateService();
		PlatformGraphicsAlgorithm ga = gaCreateService.createPlatformGraphicsAlgorithm(shape, ResponseGARenderer.ID);
		Response addednode = (Response) context.getNewObject();

		// define a default size for the shape
		int width = context.getWidth() > 0 ? context.getWidth() : computeNodeWidth(addednode);
		int height = context.getHeight() > 0 ? context.getHeight() : computeNodeHeight(addednode);
		ga.setWidth(width);
		ga.setHeight(height);
		ga.setX(context.getX());
		ga.setY(context.getY());
		link(shape, context.getNewObject());
		
		FixPointAnchor anchor = peCreateService.createFixPointAnchor(shape);
		anchor.setLocation(createService.createPoint(0, 10));
		anchor.setReferencedGraphicsAlgorithm(ga);
		anchor.setUseAnchorLocationAsConnectionEndpoint(false);
		PlatformGraphicsAlgorithm portShape = gaService.createPlatformGraphicsAlgorithm(anchor, PortGARenderer.ID);
		gaService.setLocationAndSize(portShape, 0, -5, 10, 10);

		link(anchor, addednode);
		
		layoutPictogramElement(shape);
		addToDiagram(addednode);	

		IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
		directEditingInfo.setMainPictogramElement(shape);
		directEditingInfo.setPictogramElement(shape);
		directEditingInfo.setGraphicsAlgorithm(ga);

		return shape;
    }
    
	public void addToDiagram(Response addednode) {
		if (addednode.eResource() == null) {
			EList<EObject> contents = getDiagram().eResource().getContents();
			contents.add(addednode);
		}
	}

	private int computeNodeHeight(Response addednode) {
		return 24;
	}

	private int computeNodeWidth(Response addednode) {
		return 100;
	}

}
