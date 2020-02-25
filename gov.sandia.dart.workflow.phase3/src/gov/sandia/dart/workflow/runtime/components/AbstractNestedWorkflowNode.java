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

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;

public abstract class AbstractNestedWorkflowNode extends SAWCustomNode {

	protected static final String WORKDIR_NAME_TEMPLATE = "workdirNameTemplate";

	public AbstractNestedWorkflowNode() {
		super();
	}

	protected String getWorkdirName(Map<String, String> properties, String sampleId) {
		String template = properties.get(WORKDIR_NAME_TEMPLATE);
		if (StringUtils.isEmpty(template))
			template = "workdir";
		return template + sampleId;
	}
	
}
