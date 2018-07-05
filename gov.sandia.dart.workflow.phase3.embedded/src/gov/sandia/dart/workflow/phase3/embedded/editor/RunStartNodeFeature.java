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

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.phase3.embedded.execution.EmbeddedWorkflowJob;

public class RunStartNodeFeature extends AbstractCustomFeature {

	public RunStartNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void execute(final ICustomContext context) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				final PictogramElement pe = context.getPictogramElements()[0];
				Object bo = getFeatureProvider()
						.getBusinessObjectForPictogramElement(pe);
				if (bo instanceof WFNode) {
					String startNode = ((WFNode) bo).getName();

					IFile file = getWorkflowFileFromEditor();

					if (file != null) {
						IPath path = file.getParent().getLocation();						
						File folder = getRunLocationFromEditor();
						if (folder != null)
							path = new Path(folder.getAbsolutePath());
						Job job = new EmbeddedWorkflowJob("Workflow " + file.getName(), file, path, startNode);
						job.schedule();
						return;
					}
				}
				Display.getDefault().beep();
			}
		});
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		return context.getPictogramElements().length == 1;
	}
	
	@Override
	public String getName() {
		return "Run workflow starting from here";
	}
	
	private IFile getWorkflowFileFromEditor() {
		IEditorPart editor = getEditor();
		
		if (editor.isDirty()) {
	
			boolean saveChanges = MessageDialog.openQuestion(null, "Save Changes", 
					"The workflow has been modified. Save your changes and execute?");
			if(saveChanges) {
				editor.doSave(new NullProgressMonitor());				
			} 
		}
		IEditorInput input = editor.getEditorInput();
		return (IFile) input.getAdapter(IFile.class);		
	}
	
	private File getRunLocationFromEditor() {
		IEditorPart editor = getEditor();
		if (editor != null) {
			return ((WorkflowDiagramEditor) editor).getRunLocation();
		}
		return null;
	}

	private IEditorPart getEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
	
		if(!(editor instanceof WorkflowDiagramEditor)) {
			return null;
		}
		return editor;
	}

	@Override
	public boolean hasDoneChanges() {
		return false;
	}	
	
}
