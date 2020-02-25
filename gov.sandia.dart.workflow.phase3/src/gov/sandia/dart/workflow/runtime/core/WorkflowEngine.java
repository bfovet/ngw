/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import com.googlecode.sarasvati.GuardResult;
import com.googlecode.sarasvati.NodeToken;
import com.googlecode.sarasvati.impl.AcceptTokenGuardResult;
import com.googlecode.sarasvati.impl.DiscardTokenGuardResult;
import com.googlecode.sarasvati.mem.MemEngine;

final class WorkflowEngine extends MemEngine {
	private WorkflowProcess workflow;

	public WorkflowEngine(WorkflowProcess workflow) {
		// Separate application context and graph cache for each engine
		super(String.valueOf(System.currentTimeMillis()), false);
		this.workflow = workflow;
	}
	
	WorkflowProcess getWorkflowProcess() {
		return workflow;
	}
	
	// TODO: override "evaluateGuard" and provide a way for execution to stop after a node; this
	// is how we'll implement stepping.
	@Override
	public GuardResult evaluateGuard(NodeToken token, String guard) {
		if (token.getNode().getName().equals("bashScript111x"))
			return DiscardTokenGuardResult.INSTANCE;
		else
			return AcceptTokenGuardResult.INSTANCE;

	}
}
