/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import gov.sandia.dart.workflow.editor.monitoring.WorkflowTracker;
import gov.sandia.dart.workflow.runtime.core.IWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;

/** 
 * This class bridges the monitoring broadcast system built into the workflow
 *  engine, and the monitoring display system built into the editor. 
 */

public class GraphicalWorkflowMonitor implements IWorkflowMonitor, AutoCloseable {
	private IProgressMonitor monitor;
	private IFile file;
	private File workDir;

	public GraphicalWorkflowMonitor(IFile file, File workDir, Collection<String> startNodes, IProgressMonitor monitor) throws IOException {
		this.file = file;
		this.workDir = workDir;
		this.monitor = monitor;
	}
	
	@Override
	public void breakpointHit(SAWCustomNode node, RuntimeData runtime) {
		WorkflowTracker.breakpointHit(node.getName(), file, workDir);
		monitor.worked(1);
	}

	@Override
	public void enterNode(SAWCustomNode node, RuntimeData runtime) {
		WorkflowTracker.nodeEntered(node.getName(), file, workDir);
		monitor.worked(1);
	}
	
	
	@Override
	public void exitNode(SAWCustomNode  node, RuntimeData runtime) {
		WorkflowTracker.nodeExited(node.getName(), file, workDir);
		monitor.worked(1);
	}
	
	@Override
	public void abortNode(SAWCustomNode  node, RuntimeData runtime, Throwable t) {
		WorkflowTracker.nodeAborted(node.getName(), file, workDir, t);
		monitor.worked(1);
	}

	@Override
	public void close(RuntimeData runtime) throws Exception {
		close();
	}

	@Override
	public void terminated(RuntimeData runtime, Throwable t) {
		WorkflowTracker.nodeAborted("workflow", file, workDir, t);
		monitor.worked(1);		
	}
	
	@Override
	public void status(SAWCustomNode node, RuntimeData runtime, String status) {
		WorkflowTracker.status(node.getName(), file, workDir, status);
		monitor.worked(1);
	}

	@Override
	public void close() throws Exception {
		WorkflowTracker.workflowStopped(file, workDir);
		monitor.done();		
	}

	@Override
	public void workflowStarted(RuntimeData runtime, Collection<String> startNodes) {
		WorkflowTracker.workflowStarted(file, workDir, startNodes);
		monitor.beginTask(file.getName(), IProgressMonitor.UNKNOWN);
		
	}
	
}	
