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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.impl.DefaultMoveShapeFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.sandia.dart.workflow.domain.Image;
import gov.sandia.dart.workflow.domain.Note;

public class MoveThingsOntoNotesFeature extends DefaultMoveShapeFeature {

	public MoveThingsOntoNotesFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		ContainerShape trg = context.getTargetContainer();
		Object trgObj = getFeatureProvider().getBusinessObjectForPictogramElement(trg);		

		if (trgObj instanceof Note || trgObj instanceof Image)
			return true;
		else if (trg instanceof Diagram)
			return true;
		return false;
	}

	@Override
	public void moveShape(IMoveShapeContext context) {
		ContainerShape trg = context.getTargetContainer();
		GraphicsAlgorithm ga = trg.getGraphicsAlgorithm();
		Object trgObj = getFeatureProvider().getBusinessObjectForPictogramElement(trg);	
		if (trgObj instanceof Note || trgObj instanceof Image) {
			MoveShapeContext nc = new MoveShapeContext(context.getShape());
			nc.setDeltaX(context.getDeltaX());
			nc.setDeltaY(context.getDeltaY());
			nc.setX(context.getX() + ga.getX());
			nc.setY(context.getY() + ga.getY());
			nc.setSourceContainer(context.getSourceContainer());
			nc.setTargetContainer(context.getSourceContainer());
			nc.setTargetConnection(context.getTargetConnection());
			super.moveShape(nc);

		} else {
			super.moveShape(context);
		}
		
		
	}
	
}
