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

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.phase3.embedded.AbstractEmbeddedWorkflowHandler;

public class StopEmbeddedWorkflowHandler extends AbstractEmbeddedWorkflowHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFile file = getWorkflowFileFromEditor(event, true);
		if (file != null && EmbeddedRunDatabase.INSTANCE.get(file) != null) {
			stopWorkflow(file);
	
		} else {
			Display.getDefault().beep();
		}
		return null;
	}

	private void stopWorkflow(IFile file) {
		Job job = EmbeddedRunDatabase.INSTANCE.get(file);
		if (job != null) {
			job.cancel();
		}
	}

	@Override
	public void run(IAction action) {
		if (file != null && EmbeddedRunDatabase.INSTANCE.get(file) != null) {
			stopWorkflow(file);
	
		} else {
			Display.getDefault().beep();
		}		
	}
}
