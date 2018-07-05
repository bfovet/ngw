/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.dakota;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.part.FileEditorInput;

public class NewDakotaStudyWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage page;

	public NewDakotaStudyWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		addPage(page = new WizardNewFileCreationPage("New Dakota Study", selection) {
			@Override
			protected boolean validatePage() {
				if (!super.validatePage())
					return false;
				String newText = getFileName();
				if (newText.contains(" ")) {
					setErrorMessage("Names cannot contain spaces");
					return false;							
				}
				return true;				
			}
		});
		page.setFileExtension("in");
		page.setDescription("Create new Dakota study file");
		page.setTitle("New Dakota Study");
		page.setAllowExistingResources(false);
		page.setImageDescriptor(WorkflowEditorPlugin.getImageDescriptor("icons/dakota16.png"));
		
	}

	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}
	
	@Override
	public boolean performFinish() {
		try {
			IFile file = page.createNewFile();
			InputStream stream = getClass().getResource("/templates/dakota_stub.in").openStream();
			file.setContents(stream, IResource.FORCE, null);
			stream.close();
			FileEditorInput fei = new FileEditorInput(file);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(fei, "gov.sandia.dart.dakota.ui.editor");
		} catch (Exception e) {		
			MessageDialog.openError(getShell(), "Error creating dakota study", "Error creating dakota study: " + e.getMessage());
			WorkflowEditorPlugin.getDefault().logError(e);
		}
		return true;
	}
}
