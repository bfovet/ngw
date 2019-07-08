package gov.sandia.dart.workflow.runtime.components;

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
	public void exitNode(SAWCustomNode node, RuntimeData runtime) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abortNode(SAWCustomNode node, RuntimeData runtime, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminated(RuntimeData runtime, Throwable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void status(SAWCustomNode node, RuntimeData runtime, Object status) {
		reportingRuntime.status(reportingNode, status);
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
}