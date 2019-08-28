/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;

public class ListWorkflowConductor implements WorkflowConductor {

	private String iterationParameter;
	private String[] parameterValues;
	private int index;

	public ListWorkflowConductor() {
	}
	
	@Override
	public void setProperties(Map<String, String> properties) { 
		// TODO Error checking!
		this.iterationParameter = properties.get("parameter");
		this.parameterValues = properties.get("values").split("\\s+");
	}

	@Override
	public boolean hasNext() {
		return index < parameterValues.length - 1;
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, String> next() {
		return new HashMap<String, String>() {
			{put(iterationParameter, parameterValues[++index]);}
		};
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		index = -1;
		return this;
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo("parameter", "parameter"), new PropertyInfo("values", "default"));
	}
}
