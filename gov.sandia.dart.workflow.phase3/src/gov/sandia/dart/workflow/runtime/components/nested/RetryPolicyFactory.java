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
