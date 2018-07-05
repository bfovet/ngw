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
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.eclipse.ui.IWorkbenchPart;

import gov.sandia.dart.workflow.editor.features.DuplicateNodeFeature;

public class DuplicateAction extends SelectionAction {

	public static final String ID = "gov.sandia.dart.workflow.editor.duplicate";
	private IConfigurationProvider configProvider;
	
	public DuplicateAction(IWorkbenchPart editor, IConfigurationProvider provider) {
		super(editor);
		configProvider = provider;				
	}

	@Override
	protected boolean calculateEnabled() {
		PictogramElement pe[] = getSelectedPictogramElements();
		IFeatureProvider featureProvider = getFeatureProvider();
		if (pe.length != 1 || featureProvider == null) {
			return false;
		}
		DuplicateNodeFeature feature = new DuplicateNodeFeature(featureProvider);
		CustomContext context = new CustomContext();
		context.setInnerPictogramElement(pe[0]);
		context.setPictogramElements(pe);
		return feature.canExecute(context);
	}

	/**
	 * Initializes this action's text and images.
	 */
	@Override
	protected void init() {
		super.init();
		setToolTipText("Duplicate the selected node"); 
		setText("Duplicate");
		setId(ID);
	}

	/**
	 * Duplicates the selected node.
	 */
	@Override
	public void run() {
		PictogramElement pe[] = getSelectedPictogramElements();
		CustomContext context = new CustomContext();
		context.setInnerPictogramElement(pe[0]);
		context.setPictogramElements(pe);
		DuplicateNodeFeature feature = ((WorkflowToolBehaviorProvider) configProvider.getDiagramTypeProvider().getCurrentToolBehaviorProvider()).getDuplicateNodeFeature(context);
		feature.execute(context);
	}
	
	protected PictogramElement[] getSelectedPictogramElements() {
		List<?> selectedObjects = getSelectedObjects();
		List<Object> list = new ArrayList<>();
		for (Iterator<?> iter = selectedObjects.iterator(); iter.hasNext();) {
			Object o = iter.next();
			if (o instanceof EditPart) {
				EditPart editPart = (EditPart) o;
				if (editPart.getModel() instanceof PictogramElement) {
					list.add(editPart.getModel());
				}
			}
		}

		return list.toArray(new PictogramElement[0]);
	}

	protected IFeatureProvider getFeatureProvider() {
		return configProvider.getDiagramTypeProvider().getFeatureProvider();
	}
}
