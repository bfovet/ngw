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
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

public abstract class AbstractZOrderFeature extends AbstractCustomFeature {

	public AbstractZOrderFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canExecute(ICustomContext context) {	
		if (context.getPictogramElements().length != 1)
			return false;
		ContainerShape pe = getContainerShape(context);
		return pe != null;
	}

	protected ContainerShape getContainerShape(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		while (pe instanceof Shape && ! (pe instanceof ContainerShape) && ! (pe instanceof Diagram)) {
			pe = ((Shape) pe).getContainer(); 
		}
		return (pe instanceof ContainerShape) ? (ContainerShape) pe : null;
	}

	@Override
	public boolean hasDoneChanges() {			
		return true;
	}

}
