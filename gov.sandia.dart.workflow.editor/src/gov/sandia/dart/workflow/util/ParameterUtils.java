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

import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.configuration.NodeType;

public class ParameterUtils {
	public static boolean isParameter(WFNode node) {
		return "parameter".equals(node.getType());
	}

	public static boolean isParameterType(NodeType type) {
		return "parameter".equals(type.getName());
	}

	public static String getName(WFNode node) {
		return node.getName();
	}

	// TODO This won't actually do; global parameters could have no  port
	public static String getType(WFNode node) {
		if (node.getOutputPorts().size() > 0) {
			OutputPort bo = node.getOutputPorts().get(0);
			return bo.getType();
		} else {
			return "default";
		}			
	}
	
	public static String getValue(WFNode node) {
		return PropertyUtils.getProperty(node, "value");
	}

	public static boolean isGlobal(WFNode node) {
		return node.getOutputPorts().size() == 0 ||
				node.getOutputPorts().get(0).getArcs().size() == 0;
	}

	public static void setValue(WFNode node, String text) {
		PropertyUtils.setProperty(node, "value", text);		
	}
	
	public static void setType(WFNode node, String text) {
		if (node.getOutputPorts().size() > 0) {
			OutputPort bo = node.getOutputPorts().get(0);
			bo.setType(text);
		} 
	}

	public static void setName(WFNode parameter, String value) {
		parameter.setName(value);		
	}
}
