/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class RunEmbeddedWorkflowWizard extends Wizard {
	
	private RuntimeDirectoryPage page;
	private IFile workflowFile;
	private File runLocation;
	
	public RunEmbeddedWorkflowWizard(IFile workflowFile, File runLocation) {
		this.workflowFile = workflowFile;
		this.runLocation = runLocation;
	}

	@Override
	public void addPages() {
		IPath suggestedPath;
		if (runLocation != null)
			suggestedPath = new Path(runLocation.getAbsolutePath());
		else
			suggestedPath =  new Path(workflowFile.getParent().getLocation().toFile().getAbsolutePath());
		page = new RuntimeDirectoryPage(suggestedPath);
		addPage(page);		
		page.setDescription("Choose Top-Level Runtime Directory");
		page.setTitle("Choose Directory");
		page.setImageDescriptor(WorkflowEditorPlugin.getImageDescriptor("icons/shapes64.gif"));
	}

	@Override
	public String getWindowTitle() {
		return "Run Embedded Workflow";
	}
	
	@Override
	public boolean canFinish() {
		return page.isPageComplete();
	}
	
	private WorkflowDiagramEditor getOpenEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
	
		if(!(editor instanceof WorkflowDiagramEditor)) {
			return null;
		}
		
		return (WorkflowDiagramEditor) editor;
	}
	
	@Override
	public boolean performFinish() {
		IPath path = page.getPath();
		path.toFile().mkdirs();
		WorkflowDiagramEditor editor = getOpenEditor();
		if (editor != null) {
			// TODO This isn't necessarily right -- the editor might be for some other workflow file.
			editor.setRunLocation(path.toFile());
		}
		Job job = new EmbeddedWorkflowJob("Workflow " + workflowFile.getName(), workflowFile, path);
		job.schedule();
		return true;
	}
	
}
