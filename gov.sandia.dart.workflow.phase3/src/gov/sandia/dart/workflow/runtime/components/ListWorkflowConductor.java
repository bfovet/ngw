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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class ListWorkflowConductor implements WorkflowConductor {

	private String iterationParameter;
	private String[] parameterValues;
	private int index;
	private Map<String, Map<Integer, Object>> responses = new HashMap<>();

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
	public String getSampleId() {
		return String.valueOf((index + 1));
	}
	
	@Override
	public void accumulateResponses(WorkflowProcess process) {
		Map<String, Object> currentResponses = process.getRuntime().getResponses();
		
		for (String responseName:currentResponses.keySet()) {
			if (!responses.containsKey(responseName)) {
				responses.put(responseName, new HashMap<>());
			}
			(responses.get(responseName)).put(index, currentResponses.get(responseName));
		}		
	}			
	
	// TODO If there's a response type we could use it here.
	private Class<?> inferResponseType(String responseName, Object value) {
		return value.getClass();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T[] typedArray(Class<T> type, Map<Integer, Object> input, int length) {
	    T[] typedCopy = (T[]) Array.newInstance(type, length);

	    for (Map.Entry<Integer, Object> entry: input.entrySet()) {
	        typedCopy[entry.getKey()] = (T) entry.getValue();
	    }
	    return typedCopy;
	}

	@Override
	public Map<String, Object> getResponses() {
		HashMap<String, Object> nestedResponses = new HashMap<>();
				 
		for (String responseName : responses.keySet()) {
			Map<Integer, Object> values = responses.get(responseName);
			Object oneValue = values.values().iterator().next();
			Class<?> responseType = inferResponseType(responseName, oneValue);
			nestedResponses.put(responseName, typedArray(responseType, values, parameterValues.length));
		}
		return nestedResponses;
	}

	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo("parameter", "parameter"), new PropertyInfo("values", "default"));
	}
}
