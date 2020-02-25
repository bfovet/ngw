/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.dakota;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class DakotaParamsFile {
	
	////////////
	// FIELDS //
	////////////
	
	public static final String VARIABLES            = "variables";
	public static final String FUNCTIONS            = "functions";
	public static final String DERIVATIVE_VARIABLES = "derivative_variables";
	public static final String ANALYSIS_COMPONENTS  = "analysis_components";
	public static final String EVAL_ID              = "eval_id";
	
	private Map<String, String> input;
	
	/////////////////
	// CONSTRUCTOR //
	/////////////////

	// TODO Syntax checking
	public DakotaParamsFile(String filename) throws IOException {
		Map<String, String> input = new LinkedHashMap<>();
		BufferedReader reader = new BufferedReader(new FileReader(filename));
		String line;
		while ((line = reader.readLine()) != null) {
			String[] tokens = line.trim().split("\\s+", 0);
			if (tokens.length == 2)
				input.put(tokens[1], tokens[0]);
		}
		reader.close();
		this.input = input;
	}
	
	/////////////
	// GETTERS //
	/////////////

	public List<String> getVariableNames() {
		Iterator<Entry<String, String>> iterator = input.entrySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Entry<String, String> next = iterator.next();
			if (next.getKey().equals(VARIABLES)) {
				count = Integer.parseInt(next.getValue());
				break;
			}
		}
		List<String> variables = new ArrayList<String>();
		for (int i=0; i<count; ++i)
			variables.add(iterator.next().getKey());
		return variables;
	}
	
	public List<String> getNamesFromColonDelimitedSection(String sectionName) {
		Iterator<Entry<String, String>> iterator = input.entrySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Entry<String, String> next = iterator.next();
			if (next.getKey().equals(sectionName)) {
				count = Integer.parseInt(next.getValue());
				break;
			}
		}
		List<String> functions = new ArrayList<String>();
		for (int i=0; i<count; ++i) {
			String name = iterator.next().getKey();
			name = name.substring(name.indexOf(':') + 1);
			functions.add(name);
		}
		return functions;
	}

	public String getValue(String name) {
		return input.get(name);
	}
	
	///////////////////////////
	// GETTERS (CONVENIENCE) //
	///////////////////////////
	
	public List<String> getResponseNames() {
		return getNamesFromColonDelimitedSection(FUNCTIONS);
	}
	
	public List<String> getDerivativeVariableNames() {
		return getNamesFromColonDelimitedSection(DERIVATIVE_VARIABLES);
	}
	
	public List<String> getAnalysisComponentNames() {
		return getNamesFromColonDelimitedSection(ANALYSIS_COMPONENTS);
	}
	
	public String getEvalId() {
		return getValue(EVAL_ID);
	}
}
