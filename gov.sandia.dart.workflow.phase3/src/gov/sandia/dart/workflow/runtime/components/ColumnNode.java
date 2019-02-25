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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;

public class ColumnNode extends SAWCustomNode {
	private static String[] SEPARATORS = {"\\s+", ","};
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {		
		try {
			Reader data = getCSVInput(runtime, workflow);
			
			int headerLines = getHeaderLines(properties);
			int columnIndex = getColumnIndex(properties);
			String separator = getSeparator(properties);

			String result = extractColumn(data, headerLines, columnIndex, separator);
			
			return Collections.singletonMap("stdout", result.getBytes());
			
		} catch (IOException e) {
			throw new SAWWorkflowException("Problem while reading data", e);
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new SAWWorkflowException("Wrong number of columns in data", e);
		} 
		
		
	}

	private Reader getCSVInput(RuntimeData runtime, WorkflowDefinition workflow) throws FileNotFoundException {
		InputPort port = workflow.getNode(getName()).inputs.get("stdin");
		if (port != null && port.isConnected())
			return (Reader) runtime.getInput(getName(), "stdin", Reader.class);
		port = workflow.getNode(getName()).inputs.get("data");
		if (port != null && port.isConnected())
			return (Reader) runtime.getInput(getName(), "data", Reader.class);
		port = workflow.getNode(getName()).inputs.get("fileName");
		if (port != null && port.isConnected()) {
			String path = (String) runtime.getInput(getName(), "fileName", String.class); 
			return new FileReader(path);
		}
		throw new SAWWorkflowException("No input available for node " + getName());
	}

	public static String extractColumn(Reader data, int headerLines,
			int columnIndex, String separator) throws IOException {
		BufferedReader reader = new BufferedReader(data);
		for (int i=0; i<headerLines; ++i)
			if (reader.readLine() == null)
				break;
		String line = reader.readLine();

		if (line != null) {
			// Confirm that the separator is correct; if it's not we can try some alternatives
			String[] split = line.split(separator);
			if (columnIndex > split.length) {
				separator = null;
				for (String sep: SEPARATORS) {
					split = line.split(sep);
					if (columnIndex < split.length) {
						separator = sep;
						break;
					}
				}
				if (separator == null) {
					throw new SAWWorkflowException("Too few columns in data");	
				}
			}
		}
		
		StringBuilder builder = new StringBuilder();
		while (line != null) {
			line = line.trim();
			if (!line.isEmpty()) {					
				String[] split = line.split(separator);
				builder.append(split[columnIndex - 1]);
				builder.append("\n");
			}
			line = reader.readLine();
		}
		IOUtils.closeQuietly(reader);
		return builder.toString();
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("data"), new InputPortInfo("fileName")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("stdout")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("columnIndex"), new PropertyInfo("headerLines"), new PropertyInfo("separator")); }
	@Override public String getCategory() { return NodeCategories.SEQ_DATA; }
	
	public int getColumnIndex(Map<String, String> properties) {
		try {
			String raw = properties.get("columnIndex");
			if (StringUtils.isEmpty(raw))
				throw new SAWWorkflowException("Missing required parameter 'columnIndex'");
			else
				return (int) Double.parseDouble(raw);
		} catch (Exception e) {
			throw new SAWWorkflowException("Problem with required parameter 'columnIndex'", e);
		}
	}
	
	public int getHeaderLines(Map<String, String> properties) {
		try {
			String raw = properties.get("headerLines");
			if (StringUtils.isEmpty(raw))
				return 1;
			else
				return (int) Double.parseDouble(raw);
		} catch (Exception e) {
			throw new SAWWorkflowException("Problem with parameter 'headerLines'", e);
		}
	}
	
	public String getSeparator(Map<String, String> properties) {
		String raw = properties.get("separator");
		if (StringUtils.isEmpty(raw))
			raw = "\\s+";
		return raw;
	}


}
