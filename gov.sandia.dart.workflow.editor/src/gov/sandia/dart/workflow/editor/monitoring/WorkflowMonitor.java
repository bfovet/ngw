/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.monitoring;

import java.util.HashSet;
import java.util.Set;

public class WorkflowMonitor { 	
	private static Set<IWorkflowListener> listeners = new HashSet<>();

	public static void nodeEntered(String name, String workflow) {
		for (IWorkflowListener listener: listeners) {
			listener.nodeEntered(name, workflow);
		}
	}
	public static void nodeExited(String name, String workflow) {
		for (IWorkflowListener listener: listeners) {
			listener.nodeExited(name, workflow);
		}
	}
	
	public static void workflowStopped(String workflow) {
		for (IWorkflowListener listener: listeners) {
			listener.workflowStopped(workflow);
		}		
	}
	
	public static void nodeAborted(String name, String workflow, Throwable t) {
		for (IWorkflowListener listener: listeners) {
			listener.nodeAborted(name, workflow, t);
		}
	}

	public static void workflowStarted(String workflow) {
		for (IWorkflowListener listener: listeners) {
			listener.workflowStarted(workflow);
		}
	}	
	
	public static void addWorkflowListener(IWorkflowListener listener) {
		listeners.add(listener);
	}
	
	public static void removeWorkflowListener(IWorkflowListener listener) {
		listeners.remove(listener);
	}

}
