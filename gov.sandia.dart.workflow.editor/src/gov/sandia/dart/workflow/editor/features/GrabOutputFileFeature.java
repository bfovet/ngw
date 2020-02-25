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
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.GUIs.MultipleInputDialog;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class GrabOutputFileFeature extends AbstractCustomFeature {

	private static final String PORT_NAME = "Port name";
	private static final String FILE_NAME = "File name";

	public GrabOutputFileFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public String getName() {
		return "Grab Output File";
	}
	
	@Override
	public void execute(ICustomContext context) {		
		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			Shell shell = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell();
			MultipleInputDialog dialog = new MultipleInputDialog(shell, "Grab Output File From Workflow Node") {
				@Override public int open() {
					addTextField(PORT_NAME, "", false);
					addTextField(FILE_NAME, "", false);
					validators.add(new Validator() {
						@Override
						protected boolean validate() {
							String text = ((Text) controlList.stream().
									filter(c -> c.getData(FIELD_NAME).equals(PORT_NAME)).findFirst().get()).getText();
							return !text.contains(".");
						}
					});
					return super.open();
				}
			};

			if (dialog.open() == InputDialog.OK) {
				WFNode node = (WFNode) bo;
				String name = NOWPSettingsEditorUtils.createUniqueName(dialog.getStringValue(PORT_NAME), node.getOutputPorts());					
				String file = dialog.getStringValue(FILE_NAME);				

				OutputPort port = DomainFactory.eINSTANCE.createOutputPort();
				port.setName(name);
				port.setType("output_file");	
				node.getOutputPorts().add(port);
				node.eResource().getContents().add(port);
				PropertyUtils.setProperty(port, "filename", file);

			}
		} else {
			Display.getCurrent().beep();
		}
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length != 1)
			return false;

		final PictogramElement pe = context.getPictogramElements()[0];	
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			// TODO Should be more restrictive
			WFNode node = (WFNode) bo;
			return !"parameter".equals(node.getType());
		}
		return false;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return true;
	}

}
