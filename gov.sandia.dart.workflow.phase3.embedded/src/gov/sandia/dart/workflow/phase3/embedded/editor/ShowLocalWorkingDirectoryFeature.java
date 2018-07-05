/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.editor;

import java.net.URI;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;


public class ShowLocalWorkingDirectoryFeature extends AbstractCustomFeature {
	private IFile file;

	public ShowLocalWorkingDirectoryFeature(IFeatureProvider featureProvider, IFile file) {
		super(featureProvider);
		this.file = file;
	}

	@Override
	public String getName() {
		return "Show Files";
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		if (context.getPictogramElements().length != 1)
			return false;

		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		return bo instanceof WFNode;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return false;
	}
	
	@Override
	public void execute(ICustomContext context) {

		final PictogramElement pe = context.getPictogramElements()[0];
		Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
		String name = ((WFNode) bo).getName();		

		try {		
			/* Figure out where the right files are */
			
			MessageDialog.openInformation(null, "Sorry", "Temporarily removed");
//			IContainer dir = file.getParent();
//			IContainer componentDir = dir.getFolder(new Path(name));
//			if (componentDir.exists())
//				dir = componentDir;
//			
//			IPath path = dir.getLocation();
//			URI uri = new URI("file:/");
//			IMachine machine = Machines.getInstance().getMachine(uri);
//			
//			/* Display an empty view */
//			FileView view = (FileView) createView(FileView.ID, "localhost");
//			if(view == null) {
//				return;
//			}
//			
//			/* Populate the view*/
//			view.displayBrowser(machine, path);

		} catch (Exception e) {
			EmbeddedWorkflowPlugin.getDefault().logError("Error while opening file view", e);
		}
	}

	public static IViewPart createView(String viewId, String subId) {
		IViewPart view = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			window = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
		if (window != null) {
			try {
				view = window.getActivePage().showView(viewId, subId, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
		return view;
	}
}
