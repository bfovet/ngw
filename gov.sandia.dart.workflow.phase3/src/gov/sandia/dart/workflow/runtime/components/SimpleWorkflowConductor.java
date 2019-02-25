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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class SimpleWorkflowConductor implements WorkflowConductor {
	protected boolean firstTime = false;
	private Map<String, Object> responses;
	public SimpleWorkflowConductor() {
	}

	@Override
	public boolean hasNext() {
		try {
			return firstTime;
		} finally {
			firstTime = false;
		}
	}

	@Override
	public Map<String, String> next() {
		return new HashMap<>();
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		firstTime = true;
		return this;
	}

	@Override
	public void accumulateResponses(WorkflowProcess process) {
		responses = process.getRuntime().getResponses();
	}			
	
	@Override
	public Map<String, Object> getResponses() {
		return responses;
	}

	@Override
	public void setProperties(Map<String, String> properties) {}

	@Override
	public String getSampleId() {
		return "DEFAULT";
	}

	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Collections.emptyList();
	}
}
