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

import java.util.Collections;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class PiNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime) {
		return Collections.singletonMap("f", Math.PI);
	}

	@Override
	public String getCategory() {
		return NodeCategories.SCALAR_OPS;
	}
}
