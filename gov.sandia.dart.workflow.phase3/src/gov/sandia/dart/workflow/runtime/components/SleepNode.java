/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;

public class SleepNode extends AbstractUnaryFunctionNode {
	@Override
	protected String getCustomCode(Map<String, String> properties) {	
		
		//Sleep the specified amount of time, then pipe the input through to the next node		
		String ms = properties.get("ms");		
		return "var f = function(arg1) { "
				+ "var Thread = java.lang.Thread;"				
				+ "Thread.sleep(" + ms + ");"
				+ "return arg1;"
				+ "}";
	}
	
	@Override public String getCategory() { return "Control"; }

	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("ms", "integer")); }
}
