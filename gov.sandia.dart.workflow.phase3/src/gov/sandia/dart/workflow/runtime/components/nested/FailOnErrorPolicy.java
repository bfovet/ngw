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