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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;

public class SubstringNode extends AbstractUnaryFunctionNode {
	@Override
	protected String getCustomCode(Map<String, String> properties) {			
		String start = properties.get("start");		
		String length = properties.get("length");		
		return "var f = function(arg1) { "
				+ "return arg1.substr(" +  start + ", " + length + ");"
				+ "}";
	}
	
	@Override public String getCategory() { return NodeCategories.TEXT_DATA; }

	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("start", "integer"), new PropertyInfo("length", "integer")); }
//	@Override public List<String> getDefaultProperties() { return Arrays.asList("start", "length"); }
//	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("integer", "integer"); }
}
