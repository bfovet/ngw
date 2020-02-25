/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.editor;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.DecoratorManager;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.phase3.embedded.execution.EmbeddedWorkflowJob;

/*
 * This feature provides the basics of running a workflow while specifying an initial node or a terminal node.
 */
public abstract class AbstractSpecificRunFeature extends AbstractCustomFeature {

	protected AbstractSpecificRunFeature(IFeatureProvider fp) {
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
					WFNode selectedNode = ((WFNode) bo);

					IFile file = getWorkflowFileFromEditor();

					if (file != null) {
						IPath path = file.getParent().getLocation();						
						File folder = getRunLocationFromEditor();
												
						if (!folder.exists()) {
							if (!folder.mkdirs()) {
								MessageBox diag = new MessageBox(getEditor().getSite().getShell(), SWT.ICON_ERROR | SWT.OK);
								diag.setMessage("Unable to create workdir " + folder.getAbsolutePath());
								diag.open();
								return;
							}
						}
						
						path = new Path(folder.getAbsolutePath());
						Job job = getWorkflowJob(selectedNode, file, path);
						job.addJobChangeListener(new EmbeddedWorkflowJob.DialogChangeListener());
						job.schedule();
						return;
					}
				}
				Display.getDefault().beep();
			}			
		});
	}
	

	protected abstract Job getWorkflowJob(WFNode selectedNode, IFile file, IPath path);
	
	@Override
	public boolean canExecute(ICustomContext context) {
		return context.getPictogramElements().length == 1;
	}
	
	private IFile getWorkflowFileFromEditor() {
		IEditorPart editor = getEditor();
		
		if (editor.isDirty()) {
	
			boolean saveChanges = MessageDialog.openQuestion(null, "Save Changes", 
					"The workflow has been modified. Save your changes and execute?");
			if(saveChanges) {
				editor.doSave(new NullProgressMonitor());				
			} else {
				return null;
			}
		}
		
		if (abortDueToWarnings(editor))
			return null;
		
		IEditorInput input = editor.getEditorInput();
		return (IFile) input.getAdapter(IFile.class);		
	}

	private boolean abortDueToWarnings(IEditorPart editor) {
		WorkflowDiagramEditor deditor = (WorkflowDiagramEditor) editor;
		Map<EObject, IDecorator> decoratorMap = DecoratorManager.getDecoratorMap(deditor.getDiagramTypeProvider().getDiagram().eResource());
		if (!decoratorMap.isEmpty()) {
			StringBuilder msg = new StringBuilder("The workflow has some warnings. Execute anyway?");
			Set<String> uniqueMessages = decoratorMap.values().stream().map(d -> d.getMessage()).collect(Collectors.toSet());
			for (String m: uniqueMessages) {
				msg.append("\n  ").append(m);
			}
			boolean runAnyway = MessageDialog.openQuestion(null, "Possible Problems", msg.toString());
			if(!runAnyway) {
				return true;
			}
		}
		return false;
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
