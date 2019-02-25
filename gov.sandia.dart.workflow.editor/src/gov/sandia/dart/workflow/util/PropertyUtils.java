/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.util;

import org.eclipse.emf.common.util.EList;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.NamedObjectWithProperties;
import gov.sandia.dart.workflow.domain.Property;

public class PropertyUtils {
	public static final String LINK_INCOMING_FILE_TO_TARGET = "linkIncomingFileToTarget";
	public static final String EXPAND_WILDCARDS = "expandWildcards";
	public static final String READ_IN_FILE = "readInFile";
	public static final String NOT_A_LOCAL_PATH = "notALocalPath";

	public static final String SEND_PATH_OF_OUTPUT_FILE = "sendPathOfOutputFile";
	public static final String OUTPUT_FILE_NAME = "outputFileName";
	public static final String COPY_INCOMING_FILE_TO_TARGET = "copyIncomingFileToTarget";


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
}
