/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.util.Collection;

import gov.sandia.dart.workflow.runtime.core.IWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;

final class NestingMonitor implements IWorkflowMonitor {
	/**
	 * 
	 */
	private final NestedWorkflowNode reportingNode;
	private RuntimeData reportingRuntime;

	/**
	 * @param nestedWorkflowNode
	 */
	NestingMonitor(RuntimeData runtime, NestedWorkflowNode nestedWorkflowNode) {
		reportingNode = nestedWorkflowNode;
		this.reportingRuntime = runtime;
	}

	@Override
	public void enterNode(SAWCustomNode node, RuntimeData runtime) {
		reportingRuntime.status(reportingNode, "Running node " + node.getName() + " in sample " + runtime.getSampleId());				
	}
	
	@Override
	public void breakpointHit(SAWCustomNode node, RuntimeData runtime) {
		reportingRuntime.status(reportingNode, "Breakpoint hit at node " + node.getName() + " in sample " + runtime.getSampleId());				
	}


	@Override
	public void exitNode(SAWCustomNode node, RuntimeData runtime) {
		// Nothing		
	}

	@Override
	public void abortNode(SAWCustomNode node, RuntimeData runtime, Throwable t) {
		// Nothing		
	}

	@Override
	public void terminated(RuntimeData runtime, Throwable t) {
		// Nothing		
	}
	
	@Override
	public void status(SAWCustomNode node, RuntimeData runtime, String status) {
		reportingRuntime.status(reportingNode, status);		
	}

	@Override
	public void close(RuntimeData runtime) throws Exception {
		// Nothing
	}

	@Override
	public void workflowStarted(RuntimeData runtime, Collection<String> startNodes) {
		// Nothing
	}
}
