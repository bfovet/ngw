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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public abstract class AbstractExternalNode extends SAWCustomNode {

	public static final String STDIN_PORT_NAME = "stdin";
	public static final String STDOUT_PORT_NAME = "stdout";
	public static final String STDERR_PORT_NAME = "stderr";
	public static final String EXIT_STATUS = "exitStatus";
	protected static final String PROPERTIES_FILE_FLAG = "write properties to a property file";
	
	protected String escapeString(String unescaped) {
		String value = StringEscapeUtils.escapeJava(unescaped);
		return value.replaceAll("'", "\\\\'");
	}
	
	protected String makeIdentifier(String str) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(Character.isUnicodeIdentifierStart(str.charAt(0)) ? str.charAt(0) : "_");
	    str.substring(1).chars().forEach(ch -> sb.append(Character.isUnicodeIdentifierPart(ch) ? (char) ch : '_'));
	    return sb.toString();
	}
	
	protected boolean excludeFromPropertiesFile(String name) { return PROPERTIES_FILE_FLAG.equals(name) || PRIVATE_WORK_DIR.equals(name); }
		
	protected void possiblyWritePropertiesFile(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		if (getPropertiesFileFlag(properties)) {
			String myNodeName = getName();
			try {
				Properties p = new Properties();

				for (Map.Entry<String, String> property : properties.entrySet()) {
					String propertyName = property.getKey();
					if (!excludeFromPropertiesFile(propertyName)) {
						String propertyValue = property.getValue();
						if (!StringUtils.isEmpty(propertyValue)) {
							p.put(makeIdentifier(propertyName), propertyValue);
						}
					}
				}
				
				for (String portName : runtime.getInputNames(myNodeName)) {
					if ((!excludeFromPropertiesFile(portName)) && isConnectedInput(portName, workflow)) {
						String portValue = (String) runtime.getInput(myNodeName, portName, String.class);
						if (!StringUtils.isEmpty(portValue)) {
							p.put(makeIdentifier(portName), portValue);
						}
					}
				}
				
				try (FileOutputStream fos = new FileOutputStream(new File(getComponentWorkDir(runtime, properties), "params.dat"))) {
					p.store(fos, "");
				}
			} catch (Throwable t) {
				throw new SAWWorkflowException("Problem writing to node properties file", t);
			}		
		}
	}
	
	protected boolean getPropertiesFileFlag(Map<String, String> properties) {
		String flagValue = properties.get(PROPERTIES_FILE_FLAG);
		if ("true".equals(flagValue))
			return true;
		else
			return false;
	}
}
