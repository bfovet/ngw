/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.wizards;

import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.graphiti.examples.common.FileService;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewWorkflowWizard extends Wizard implements INewWizard {
	private WizardNewFileCreationPage page;

	public NewWorkflowWizard() {
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		addPage(page = new WizardNewFileCreationPage("New Workflow", selection) {
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
		page.setFileExtension("iwf");
		page.setDescription("Create new IWF workflow file");
		page.setTitle("New Workflow");
		page.setAllowExistingResources(false);
		page.setImageDescriptor(WorkflowEditorPlugin.getImageDescriptor("icons/shapes64.gif"));
		
	}

	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}
	
	@Override
	public boolean performFinish() {
		try {
			IFile file = page.createNewFile();
			Diagram diagram = Graphiti.getPeCreateService().createDiagram("dartWorkflow", file.getName(), true);
			URI uri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
			FileService.createEmfFileForDiagram(uri, diagram);
			String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
			DiagramEditorInput editorInput = DiagramEditorInput.createEditorInput(diagram, providerId);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, WorkflowDiagramEditor.ID);
		} catch (Exception e) {		
			MessageDialog.openError(getShell(), "Error creating workflow", "Error creating workflow: " + e.getMessage());
			WorkflowEditorPlugin.getDefault().logError(e);
		}
		return true;
	}
}
