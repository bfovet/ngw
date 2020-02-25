/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.NamedObjectWithProperties;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;

public class PropertyUtils {
	public static final String LINK_INCOMING_FILE_TO_TARGET = "linkIncomingFileToTarget";
	public static final String EXPAND_WILDCARDS = "expandWildcards";
	public static final String READ_IN_FILE = "readInFile";
	public static final String NOT_A_LOCAL_PATH = "notALocalPath";
	public static final String TRIM_WHITESPACE = "trimWhitespace";

	public static final String SEND_PATH_OF_OUTPUT_FILE = "sendPathOfOutputFile";
	public static final String OUTPUT_FILE_NAME = "outputFileName";
	public static final String COPY_INCOMING_FILE_TO_TARGET = "copyIncomingFileToTarget";
	public static final String NEW_FILE_NAME = "newFileName";

	public static final String CLEAR_WORK_DIR = "clear private work directory";
	public static final String PRIVATE_WORK_DIR = "use private work directory";
	public static final String OLD_PRIVATE_WORK_DIR = "privateWorkDir"; // TODO: possibly (hopefully) will be able to age this out at some point
	public static final String ASYNC = "async";
	public static final String HIDE_IN_NAVIGATOR = "hide in navigator";


	private PropertyUtils() {}
	
	public static void setProperty(NamedObjectWithProperties node, String name, String value) {
		setProperty(node, name, "default", value);
	}
	
	public static void setProperty(NamedObjectWithProperties node, String name, String type, String value) {
		Property p = getPropertyElement(node, name);
		if (p != null) {
			p.setValue(value);
			return;
		}
		p = DomainFactory.eINSTANCE.createProperty();
		p.setType(type);
		p.setName(name);
		p.setValue(value);
		node.getProperties().add(p);
		if (node.eResource() != null)
			node.eResource().getContents().add(p);
	}
	
	public static String getProperty(NamedObjectWithProperties node, String name) {
		Property p = getPropertyElement(node, name);
		return p == null ? null : p.getValue();
	}
	
	public static Property getPropertyElement(NamedObjectWithProperties node, String name) {
		EList<Property> properties = node.getProperties();
		for (Property p: properties) {
			if (name.equals(p.getName())) {
				return p;
			}
		}
		return null;
	}
	
	public static boolean hasProperty(NamedObjectWithProperties node, String name) {
		EList<Property> properties = node.getProperties();
		for (Property p: properties) {
			if (name.equals(p.getName())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTrue(NamedObjectWithProperties node, String name) {
		EList<Property> properties = node.getProperties();
		for (Property p: properties) {
			if (name.equals(p.getName())) {
				return "true".equals(p.getValue());
			}
		}
		return false;
	}

	public static String resolveProperty(WFNode node, String name) {
		Property p = getPropertyElement(node, name);
		if (p == null)
			return null;
		else
			return resolveProperty(p.getValue());
	}
	
	// TODO These are based on stuff temporarily copied from WorkflowProcess. We need them to
	// resolve properties in the editor, but we can't just call the method in
	// WorkflowProcess since it would add a dependency. We probably need to factor this out
	// to a library both can use? I'm not sure what is best architecturally.
	//
	// The ability for the editor to understand these runtime values is a requested feature.
	
	private static final int PASSES=12;
	// Public for testing
	private static String resolveProperty(String value) {
		Pattern var = Pattern.compile("\\$\\{([^}]+)\\}");
		int pass = 0;
		Map<String, String> systemProperties = getSystemProperties();
		for (pass=0; pass < PASSES; ++pass) {
			boolean found = false;
			if (value.indexOf("$") == -1) {
				continue;					
			}
			Matcher matcher = var.matcher(value);
			boolean matched = matcher.find();
			if (matched) {
				String otherName = matcher.group(1);
				if (systemProperties.containsKey(otherName)) {
					value = value.replace("${" + otherName + "}", systemProperties.get(otherName));						
				}
			}				

			if (!found)
				break;
		}			
		return value;
	}
	
	private static Map<String, String> sysProps;
	private synchronized static Map<String, String> getSystemProperties() {
		if (sysProps == null) {
			sysProps = new HashMap<>();
			sysProps.put(USER_NAME,   Matcher.quoteReplacement(System.getProperty(USER_NAME)));
			sysProps.put(USER_HOME,        Matcher.quoteReplacement(System.getProperty(USER_HOME)));
			sysProps.put(JAVA_VERSION, Matcher.quoteReplacement(System.getProperty(JAVA_VERSION)));
		}
		return sysProps;
	}

	private static final String JAVA_VERSION = "java.version";
	private static final String USER_HOME = "user.home";
	private static final String USER_NAME = "user.name";

}
