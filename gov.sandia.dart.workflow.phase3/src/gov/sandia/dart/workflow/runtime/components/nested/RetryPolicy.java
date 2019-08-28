package gov.sandia.dart.workflow.runtime.components.nested;

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;

public interface RetryPolicy {
	enum Result { SUCCESS, RETRY, FAIL, REPORT }
	boolean execute(SAWWorkflowLogger log);

}
