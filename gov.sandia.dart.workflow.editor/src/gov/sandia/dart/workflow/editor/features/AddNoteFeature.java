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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaCreateService;
import org.eclipse.graphiti.services.IPeCreateService;

import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.editor.rendering.NoteGARenderer;
 
public class AddNoteFeature extends AbstractAddShapeFeature {
   
    public static final int CORNER = 10;

	public AddNoteFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
	public boolean canAdd(IAddContext context) {
        // check if user wants to add a WFNode
        if (context.getNewObject() instanceof Note) {
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
		ContainerShape shape = peCreateService.createContainerShape(context.getTargetContainer(), true);
		IGaCreateService gaCreateService = Graphiti.getGaCreateService();
		PlatformGraphicsAlgorithm ga = gaCreateService.createPlatformGraphicsAlgorithm(shape, NoteGARenderer.ID);
		Note addednode = (Note) context.getNewObject();

		// define a default size for the shape
		int width = context.getWidth() > 0 ? context.getWidth() : computeNodeWidth(addednode);
		int height = context.getHeight() > 0 ? context.getHeight() : computeNodeHeight(addednode);
		ga.setWidth(width);
		ga.setHeight(height);
		ga.setX(context.getX());
		ga.setY(context.getY());
		link(shape, context.getNewObject());
		layoutPictogramElement(shape);
		addToDiagram(addednode);		

		IDirectEditingInfo directEditingInfo = getFeatureProvider().getDirectEditingInfo();
		directEditingInfo.setMainPictogramElement(shape);
		directEditingInfo.setPictogramElement(shape);
		directEditingInfo.setGraphicsAlgorithm(ga);
	        
		return shape;
    }
    
	public void addToDiagram(Note addednode) {
		if (addednode.eResource() == null) {
			EList<EObject> contents = getDiagram().eResource().getContents();
			contents.add(addednode);
		}
	}

	private int computeNodeHeight(Note addednode) {
		return 75;
	}

	private int computeNodeWidth(Note addednode) {
		return 150;
	}

    
    
    
}
