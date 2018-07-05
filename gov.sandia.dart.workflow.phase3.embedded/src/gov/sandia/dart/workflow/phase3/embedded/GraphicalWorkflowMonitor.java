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

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;

import gov.sandia.dart.workflow.editor.monitoring.WorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.IWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;

/** 
 * This class bridges the monitoring broadcast system built into the workflow
 *  engine, and the monitoring display system built into the editor. 
 */

public class GraphicalWorkflowMonitor implements IWorkflowMonitor, AutoCloseable {
	private IProgressMonitor monitor;
	private String workflow;

	public GraphicalWorkflowMonitor(String workflow, IProgressMonitor monitor) throws IOException {
		this.workflow = workflow;
		this.monitor = monitor;
		WorkflowMonitor.workflowStarted(workflow);
		monitor.beginTask(workflow, IProgressMonitor.UNKNOWN);
	}
	
	@Override
	public synchronized void enterNode(SAWCustomNode node, RuntimeData runtime) {
		WorkflowMonitor.nodeEntered(node.getName(), runtime.getWorkflowFile().getName());
		monitor.worked(1);
	}
	
	
	@Override
	public synchronized void exitNode(SAWCustomNode  node, RuntimeData runtime) {
		WorkflowMonitor.nodeExited(node.getName(), runtime.getWorkflowFile().getName());
		monitor.worked(1);
	}
	
	@Override
	public synchronized void abortNode(SAWCustomNode  node, RuntimeData runtime, Throwable t) {
		WorkflowMonitor.nodeAborted(node.getName(), runtime.getWorkflowFile().getName(), t);
		monitor.worked(1);
	}

	@Override
	public void close() throws Exception {
		WorkflowMonitor.workflowStopped(workflow);		
		monitor.done();
	}
	
}	
