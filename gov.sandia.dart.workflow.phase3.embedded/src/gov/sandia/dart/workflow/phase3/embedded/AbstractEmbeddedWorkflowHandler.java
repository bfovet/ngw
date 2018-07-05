/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;

public abstract class AbstractEmbeddedWorkflowHandler extends AbstractHandler implements IActionDelegate {

	protected IFile file;

	public AbstractEmbeddedWorkflowHandler() {
		super();
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled();
	}

	protected File getRunLocation(ExecutionEvent event) {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if(!(editor instanceof WorkflowDiagramEditor)) {
			return null;
		}
		WorkflowDiagramEditor weditor = (WorkflowDiagramEditor) editor;
		return weditor.getRunLocation();
	}
	
	protected IFile getWorkflowFileFromEditor(ExecutionEvent event, boolean dirtyOk) {
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		
		if(!(editor instanceof WorkflowDiagramEditor)) {
			return null;
		}
		
		if (editor.isDirty() && !dirtyOk) {
	
			boolean saveChanges = MessageDialog.openQuestion(null, "Save Changes", 
					"The workflow has been modified. Save your changes and execute?");
			if(saveChanges) {
				editor.doSave(new NullProgressMonitor());				
			} else {
				return null;
			}
				
		}
		IEditorInput input = editor.getEditorInput();
		return (IFile) input.getAdapter(IFile.class);		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		file = null;
	
		if(!(selection instanceof IStructuredSelection)) {
			return;
		}
	
		IStructuredSelection iss = (IStructuredSelection) selection;
		boolean enabled = iss.size()==1;
		action.setEnabled(enabled);
		if(!enabled) {
			return;
		}
	
		Object obj = iss.getFirstElement();
		if(obj==null || !(obj instanceof IAdaptable)) {
			return;
		}
		IAdaptable adaptable = (IAdaptable) obj;
		Object adapter = adaptable.getAdapter(IFile.class);
		if(adapter==null || !(adapter instanceof IFile)) {
			return;
		}
	
		file = (IFile) adapter;
	}

	protected WorkflowDiagramEditor getOpenEditor() {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
	
		if(!(editor instanceof WorkflowDiagramEditor)) {
			return null;
		}
		
		return (WorkflowDiagramEditor) editor;
	}



}
