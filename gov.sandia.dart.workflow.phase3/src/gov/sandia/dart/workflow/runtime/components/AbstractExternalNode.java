/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;

public abstract class AbstractExternalNode extends SAWCustomNode {

	public static final String STDIN_PORT_NAME = "stdin";
	public static final String STDOUT_PORT_NAME = "stdout";
	public static final String STDERR_PORT_NAME = "stderr";
	public static final String EXIT_STATUS = "exitStatus";
	protected static final String PROPERTIES_FILE_FLAG = "write properties to a property file";
	protected void possiblyWritePropertiesFile(Map<String, String> properties, RuntimeData runtime) {
		if (getPropertiesFileFlag(properties)) {
			try {
				Properties p = new Properties();
				p.putAll(properties);
				p.remove(PROPERTIES_FILE_FLAG);
				try (FileOutputStream fos = new FileOutputStream(new File(getComponentWorkDir(runtime, properties), getName()+".properties"))) {
					p.store(fos, "Node properties");
				}		
			} catch (Throwable t) {
				throw new SAWWorkflowException("Problem writing to node properties file", t);
			}		
		}
	}
	private boolean getPropertiesFileFlag(Map<String, String> properties) {
		String flagValue = properties.get(PROPERTIES_FILE_FLAG);
		if (flagValue != null && flagValue.equals("true"))
			return true;
		else
			return false;
	}

}
