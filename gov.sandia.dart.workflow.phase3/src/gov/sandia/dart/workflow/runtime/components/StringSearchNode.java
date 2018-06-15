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

public class StringSearchNode extends AbstractUnaryFunctionNode {
	@Override
	protected String getCustomCode(Map<String, String> properties) {			
		String searchValue = properties.get("searchValue");		
		return "var f = function(arg1) { "
				+ "return arg1.search(\"" + searchValue + "\");"
				+ "}";
	}
	
	@Override public String getCategory() { return "String Functions"; }

	@Override public List<String> getDefaultProperties() { return Arrays.asList("searchValue"); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("default"); }
}
