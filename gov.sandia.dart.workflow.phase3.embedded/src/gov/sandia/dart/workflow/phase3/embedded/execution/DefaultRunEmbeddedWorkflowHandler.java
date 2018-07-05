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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.phase3.embedded.AbstractEmbeddedWorkflowHandler;

/**
 * This class does double duty. The "AbstractHandler" part executes in a context menu from the workflow editor. The IActionDelegate part 
 * executes in a context menu from the Project Navigator.
 * @author ejfried
 *
 */
public class DefaultRunEmbeddedWorkflowHandler extends AbstractEmbeddedWorkflowHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = getWorkflowFileFromEditor(event, false);
		if (file != null && EmbeddedRunDatabase.INSTANCE.get(file) == null) {
			File runLocation = getRunLocation(event);
			runWorkflow(file, runLocation);
			return true;
		} else {
			Display.getDefault().beep();
		}
		return null;
	}
	
	@Override
	public void run(IAction action) {
		if (file != null && EmbeddedRunDatabase.INSTANCE.get(file) == null) {			
			runWorkflow(file, file.getParent().getLocation().toFile());

		} else {
			Display.getDefault().beep();
		}
	}
	
	void runWorkflow(IFile workflowFile, File runLocation) {
		if (runLocation == null)
			runLocation = file.getParent().getLocation().toFile();
		runLocation.mkdirs();
		WorkflowDiagramEditor editor = getOpenEditor();

		if (editor != null) {
			// TODO This isn't necessarily right -- the editor might be for some other workflow file.
			editor.setRunLocation(runLocation);
		}
		Job job = new EmbeddedWorkflowJob("Workflow " + workflowFile.getName(), workflowFile, new Path(runLocation.getAbsolutePath()));
		job.schedule();
		
	}
}
