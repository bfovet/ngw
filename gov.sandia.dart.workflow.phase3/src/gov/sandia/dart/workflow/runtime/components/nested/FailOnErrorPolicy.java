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

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

class FailOnErrorPolicy implements RetryPolicy {
	private final WorkflowProcess process;

	FailOnErrorPolicy(WorkflowProcess process) {
		this.process = process;
	}

	@Override
	public boolean execute(SAWWorkflowLogger log) {
		process.run();
		return false;
	}
}
