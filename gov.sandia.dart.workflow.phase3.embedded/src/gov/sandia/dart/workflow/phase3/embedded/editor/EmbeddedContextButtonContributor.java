/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.ContextButtonEntry;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.IContextButtonContributor;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.monitoring.WorkflowTracker;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;

public class EmbeddedContextButtonContributor implements IContextButtonContributor {
	
	@Override
	public List<ContextButtonEntry> getContextButtons(IFeatureProvider featureProvider, CustomContext customContext) {
		List<ContextButtonEntry> buttons = new ArrayList<>();
		PictogramElement[] pe = customContext.getPictogramElements();
		if (pe.length == 1 && featureProvider.getBusinessObjectForPictogramElement(pe[0]) instanceof WFNode) {
			URI uri = featureProvider.getDiagramTypeProvider().getDiagram().eResource().getURI();
			String pathString = uri.toPlatformString(true);
			if(pathString != null)
			{
				WFNode node = (WFNode) featureProvider.getBusinessObjectForPictogramElement(pe[0]);
				WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(node);
				IFile file = editor.getWorkflowFile();

				if (WorkflowTracker.canExecute(node.getName(), file, editor.getRunLocation())) {
					ContextButtonEntry button3 = new ContextButtonEntry(new RunFromNodeFeature(featureProvider), customContext);
					button3.setText(button3.getFeature().getName());
					button3.setIconId(EmbeddedImageProvider.RUN);	
					buttons.add(button3);
				}
				
				ContextButtonEntry button4 = new ContextButtonEntry(new RunToNodeFeature(featureProvider), customContext);
				button4.setText(button4.getFeature().getName());
				button4.setIconId(EmbeddedImageProvider.RUN);	
				buttons.add(button4);
			}
		}
		return buttons;
	}
}
