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

import java.util.Arrays;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.GUIs.MultipleInputDialog;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;

public class GrabOutputVariableFeature extends AbstractCustomFeature {

	private static final String VAR_NAME = "Variable name";

	public GrabOutputVariableFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Grab Output Variable";
	}
	
	@Override
	public void execute(ICustomContext context) {		
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
			MultipleInputDialog dialog = new MultipleInputDialog(shell, "Grab Output Variable from Script");
			dialog.addTextField(VAR_NAME, "", false);

			if (dialog.open() == InputDialog.OK) {
				WFNode node = (WFNode) bo;
				String name = NOWPSettingsEditorUtils.createUniqueName(dialog.getStringValue(VAR_NAME), node.getOutputPorts());					
				if (name.equals(dialog.getStringValue(VAR_NAME))) {
					OutputPort port = DomainFactory.eINSTANCE.createOutputPort();
					port.setName(name);
					port.setType("default");	
					node.getOutputPorts().add(port);
					node.eResource().getContents().add(port);
				} else {
					Display.getCurrent().beep();
				}
			}
		} else {
			Display.getCurrent().beep();
		}
	}

	private static String[] SCRIPT_TYPES = {"bashScript", "cshScript", "pythonScript", "windowsBatchScript"};
	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length != 1)
			return false;

		final PictogramElement pe = context.getPictogramElements()[0];	
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			WFNode node = (WFNode) bo;
			String type = node.getType();
			if (Arrays.binarySearch(SCRIPT_TYPES, type) > -1)
				return true;
		}
		return false;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return true;
	}

}
