/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import com.googlecode.sarasvati.Engine;
import com.googlecode.sarasvati.event.EventActions;
import com.googlecode.sarasvati.event.ExecutionEvent;
import com.googlecode.sarasvati.event.ExecutionListener;

public class WorkflowListener implements ExecutionListener {
	@Override
	public EventActions notify(ExecutionEvent event) {
		if (event.isProcessEvent()) {
			Engine engine = event.getEngine();
			if (engine instanceof WorkflowEngine) {
				WorkflowProcess process = ((WorkflowEngine) engine).getWorkflowProcess();			
				switch(event.getEventType()) {
				case PROCESS_CREATED:
					process.addProcess(event.getProcess()); break;
				case PROCESS_COMPLETED:
				case PROCESS_CANCELED:			
					process.removeProcess(event.getProcess()); break;
				default:
					// Nothing to do
				}
			}
		}
		return null;
	}
}
