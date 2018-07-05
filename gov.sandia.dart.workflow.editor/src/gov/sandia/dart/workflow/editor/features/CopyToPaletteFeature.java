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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.library.UserCustomNodeLibrary;

public class CopyToPaletteFeature extends AbstractCustomFeature {

	public CopyToPaletteFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Copy to Palette";
	}
	
	@Override
	public void execute(ICustomContext context) {		
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			UserCustomNodeLibrary.addNode((WFNode) bo);
		}
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		return context.getPictogramElements().length == 1;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return false;
	}

}
