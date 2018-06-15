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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class RepeatWorkflowConductor implements WorkflowConductor {

	private int index, count;
	private Map<String, Map<Integer, Object>> responses = new HashMap<>();

	public RepeatWorkflowConductor() {
	}
	
	@Override
	public void setProperties(Map<String, String> properties) { 
		// TODO Error checking!
		this.count = Integer.parseInt(properties.get("count"));
	}

	@Override
	public boolean hasNext() {
		return index < (count - 1);
	}

	@Override
	public Map<String, String> next() {
		++index;
		return Collections.emptyMap();
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
			nestedResponses.put(responseName, typedArray(responseType, values, count));
		}
		return nestedResponses;
	}

	@Override
	public List<String> getDefaultProperties() {
		return Arrays.asList("count");
	}
	
	@Override
	public List<String> getDefaultPropertyTypes() {
		return Arrays.asList("integer");	}
}
