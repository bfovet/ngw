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

import java.util.Map;

public class AddNode extends AbstractBinaryFunctionNode {

	@Override
	protected String getCustomCode(Map<String, String> properties) {
		return "var f = function(arg1, arg2) { return Number(arg1) + Number(arg2); }";

	}

}
