/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ContextButtonEntry;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.features.CopyToPaletteFeature;
import gov.sandia.dart.workflow.editor.features.DuplicateNodeFeature;
import gov.sandia.dart.workflow.editor.features.GrabOutputFileFeature;
import gov.sandia.dart.workflow.editor.features.GrabOutputVariableFeature;
import gov.sandia.dart.workflow.editor.features.PackageComponentFeature;

public class BasicContextButtonContributor implements IContextButtonContributor {

	@Override
	public List<ContextButtonEntry> getContextButtons( IFeatureProvider featureProvider, CustomContext customContext) {
		List<ContextButtonEntry> buttons = new ArrayList<>(); 
		
		PictogramElement[] pe = customContext.getPictogramElements();
		if (pe.length == 1 && featureProvider.getBusinessObjectForPictogramElement(pe[0]) instanceof WFNode) {
			ContextButtonEntry button = new ContextButtonEntry(new CopyToPaletteFeature(featureProvider), customContext);
			button.setText("Copy to Palette");
			button.setIconId(WorkflowImageProvider.IMG_PALETTE);	   
			buttons.add(button);
		}
		
		PackageComponentFeature feature = new PackageComponentFeature(featureProvider);
		if (feature.canExecute(customContext)) {
			ContextButtonEntry button3 = new ContextButtonEntry(feature, customContext);
			button3.setText("Package Component");
			button3.setIconId(WorkflowImageProvider.IMG_PACKAGE); 
			buttons.add(button3);
		}
		
		ContextButtonEntry button2 = new ContextButtonEntry(new DuplicateNodeFeature(featureProvider), customContext);
		button2.setText("Duplicate");
		button2.setIconId(WorkflowImageProvider.IMG_DUPLICATE);	   
		buttons.add(button2);
		
		GrabOutputFileFeature feature2 = new GrabOutputFileFeature(featureProvider);
		if (feature2.canExecute(customContext)) {
			ContextButtonEntry button3 = new ContextButtonEntry(feature2, customContext);
			button3.setText("Grab Output File");
			button3.setIconId(WorkflowImageProvider.IMG_PORT);	   
			buttons.add(button3);
		}
		
		GrabOutputVariableFeature feature3 = new GrabOutputVariableFeature(featureProvider);
		if (feature3.canExecute(customContext)) {
			ContextButtonEntry button4 = new ContextButtonEntry(feature3, customContext);
			button4.setText("Grab Output Variable");
			button4.setIconId(WorkflowImageProvider.IMG_PORT);	   
			buttons.add(button4);
		}

		return buttons;
	}
}
