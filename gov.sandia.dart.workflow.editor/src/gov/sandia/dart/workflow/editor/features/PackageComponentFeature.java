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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.packaging.PackageComponentWizard;

public class PackageComponentFeature extends AbstractCustomFeature {

	public PackageComponentFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void execute(ICustomContext context) {
		
		try {
			List<WFNode> nodes = getSelectedWFNodes(context);		
			if (nodes.isEmpty())
				return;
			String name = nodes.get(0).getName().replaceAll("[^a-zA-Z0-9\\-]", "_");
			IFile wfFile = getWorkflowFile();
			IPath suggested = wfFile.getLocation().removeLastSegments(1).append(name + ".jar");
			WizardDialog wizardDialog = new WizardDialog(Display.getCurrent().getActiveShell(), new PackageComponentWizard(nodes,  suggested));
			if (wizardDialog.open() == WizardDialog.OK) {
				new Job("Refresh") {
					@Override
					protected IStatus run(IProgressMonitor monitor) {
						try {
							wfFile.getParent().refreshLocal(1, monitor);
						} catch (CoreException e) {
							// Whatever
						}
						return Status.OK_STATUS;
					}					
				}.schedule();
			}
		} catch (Exception e) {
			ErrorDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage(), WorkflowEditorPlugin.getDefault().newErrorStatus(e));
		}
	}

	private List<WFNode> getSelectedWFNodes(ICustomContext context) {
		List<WFNode> wfnodes = new ArrayList<>();
		PictogramElement[] PEs = context.getPictogramElements();
		for (PictogramElement pe: PEs) {
			Object o = getBusinessObjectForPictogramElement(pe);
			if (o instanceof WFNode)
				wfnodes.add((WFNode) o);
		}
		return wfnodes;
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		return !getSelectedWFNodes(context).isEmpty();		

	}
	
	@Override
	public boolean hasDoneChanges() {
		return false;
	}

	
	@Override
	public String getName() {
		return "Package Component";
	}
	
	private IFile getWorkflowFile() {
		URI uri = getDiagram().eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		return ifile;
	}


}
