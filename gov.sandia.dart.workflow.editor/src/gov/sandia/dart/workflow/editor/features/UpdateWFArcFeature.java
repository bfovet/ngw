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

import java.util.Objects;

import org.eclipse.emf.common.util.EList;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.WFArc;

public class UpdateWFArcFeature extends AbstractUpdateFeature {

	public UpdateWFArcFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo =
				getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof WFArc);
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFArc) {
			// What name should be
			final WFArc arc = (WFArc) bo;
			String rightName = CreateArcFeature.getWFArcName(arc);
			if (!Objects.equals(rightName, arc.getName()))
				return Reason.createTrueReason("Name is out of date");			
		}
		return Reason.createFalseReason();

	}

	@Override
	public boolean update(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (bo instanceof WFArc) {
			WFArc arc = (WFArc) bo;
			String name = CreateArcFeature.getWFArcName(arc);
			arc.setName(name);

			// Set name in pictogram model
			if (pictogramElement instanceof Connection) {
				EList<ConnectionDecorator> decorators = ((Connection) pictogramElement).getConnectionDecorators();
				if (decorators != null) {
					for (ConnectionDecorator decorator: decorators) {
						final GraphicsAlgorithm graphicsAlgorithm = decorator.getGraphicsAlgorithm();
						if (graphicsAlgorithm instanceof Text) {
							((Text) graphicsAlgorithm).setValue(name);
						}
					} 
				}
			}
			return true;
		}
		return false;
	}

}
