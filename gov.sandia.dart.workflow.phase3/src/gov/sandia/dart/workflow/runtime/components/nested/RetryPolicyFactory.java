package gov.sandia.dart.workflow.runtime.components.nested;

import java.io.File;

import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

class RetryPolicyFactory {

	public static RetryPolicy getRetryPolicy(File configDir, WorkflowProcess process) {
		File configFile = getConfigFile(configDir);
		if (configFile != null)
			return new ConfigurableRetryPolicy(configFile, process);
		else
			return new FailOnErrorPolicy(process); 
	}

	private static File getConfigFile(File configDir) {
		File configFile = new File(configDir, ConfigurableRetryPolicy.CONFIG_FILE_NAME);
		return configFile.exists() ? configFile : null;
	}

}
