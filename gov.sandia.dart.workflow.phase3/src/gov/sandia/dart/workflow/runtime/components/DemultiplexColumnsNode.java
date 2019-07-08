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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.Datum;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

public class DemultiplexColumnsNode extends SAWCustomNode {
	private static final String DEFAULT_SEPARATOR = "\\s+";
	private static final String SKIP_FOOTER_PROPERTY = "number of footer lines to ignore (optional)";
	private static final String SEPARATOR_PROPERTY = "separator regular expression (optional)";
	
	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		Node thisNode = workflow.getNode(getName());
		
		int numFooterLinesToIgnore = 0;
		try {
			numFooterLinesToIgnore = Integer.parseInt(properties.get(SKIP_FOOTER_PROPERTY));
		} catch (NumberFormatException e) {
			throw new SAWWorkflowException("CAN'T PARSE INTEGER in footer-lines-to-ignore");
		}
		
		String separator = properties.get(SEPARATOR_PROPERTY);
		if (StringUtils.isEmpty(separator))
			separator = DEFAULT_SEPARATOR;
		
		Reader data = (Reader) runtime.getInput(getName(), "columns", Reader.class);
		if (data == null) {
			WorkflowDefinition.Property columnProperty = thisNode.properties.get("columns");
			if (columnProperty == null) 
				throw new SAWWorkflowException("Please add an input file");
			data = (Reader) (new Datum(columnProperty.type, columnProperty.value, String.class)).getAs(Reader.class);
		}
			
		BufferedReader reader = new BufferedReader(data);
				
		String headerLine;
		try {
			headerLine = reader.readLine();
		} catch (IOException e) {
			throw new SAWWorkflowException("exception reading input", e);
		}
		String[] columnNames = headerLine.trim().split(separator);
		final int numColumns = columnNames.length;
		if (numColumns < 1) {
			throw new SAWWorkflowException("no columns found");
		}
		final ArrayList<String> columnNameList = new ArrayList<String>(Arrays.asList(columnNames));
				
		List<String> outputPortNames = thisNode.outputs.values().stream().map(p -> p.name).collect(Collectors.toList());
		Map<String, Integer> portToColumnIndex = new HashMap<String, Integer>();
		Map<String, StringBuilder> portToColumnBuffer = new HashMap<String, StringBuilder>();
		
		for (String portName : outputPortNames) {
			List<Integer> matchingIndices = IntStream.range(0, numColumns).filter(i -> columnNameList.get(i).equals(portName)).boxed().collect(Collectors.toList());
			if (matchingIndices.isEmpty())
				throw new SAWWorkflowException("no matching column for output port " + portName);
			if (matchingIndices.size() > 1)
				throw new SAWWorkflowException("more than one matching column for output port " + portName);
			portToColumnIndex.put(portName, matchingIndices.get(0));
			portToColumnBuffer.put(portName, new StringBuilder());
			System.err.println("mapping column #" + String.valueOf(matchingIndices.get(0)) + " to port " + portName);
		}

		String nextLine;
		try {
			if (numFooterLinesToIgnore > 0)
				reader = new ExcludeFooterLinesReader(reader, numFooterLinesToIgnore);
			nextLine = reader.readLine();
		} catch (IOException e) {
			throw new SAWWorkflowException("exception reading input", e);
		}
		int lineNum = 2;
		while (nextLine != null) {
			String[] fields = nextLine.trim().split(separator);
			if (fields.length < numColumns)
				throw new SAWWorkflowException("expecting " + String.valueOf(numColumns) + " columns, only found " + String.valueOf(fields.length) + " on input line number " + String.valueOf(lineNum));
			outputPortNames.forEach(portName -> portToColumnBuffer.get(portName).append(fields[portToColumnIndex.get(portName)]).append(System.lineSeparator()));
			try {
				nextLine = reader.readLine();
			} catch (IOException e) {
				throw new SAWWorkflowException("exception reading input", e);
			}
			lineNum++;
		}

		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputPortNames.forEach(p -> outputMap.put(p, portToColumnBuffer.get(p).toString()));
		return outputMap;
	}

	@Override
	public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("columns")); }
	@Override
	public List<OutputPortInfo> getDefaultOutputs() { return Collections.emptyList(); }
	@Override
	public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo(SKIP_FOOTER_PROPERTY),
																			new PropertyInfo(SEPARATOR_PROPERTY)); }

	@Override
	public String getCategory() { return NodeCategories.SEQ_DATA; }
	
	private class ExcludeFooterLinesReader extends BufferedReader {
		private int internalIndex = 0;
		private int numBufferLines = 0;
		private List<String> lineBuffer = new ArrayList<String>();
		private boolean hitEOF = false;

		public ExcludeFooterLinesReader(Reader in, int numFooterLines) throws IOException {
			super(in);
			while (numFooterLines > 0) {
				String nextLine = super.readLine();
				if (nextLine == null)
					throw new SAWWorkflowException(String.format("expected %d footer lines, only found %d",
							numFooterLines+numBufferLines, numBufferLines));
				lineBuffer.add(nextLine);
				numBufferLines++;
				numFooterLines--;
			}
		}
		
		@Override
		public String readLine() throws IOException {
			if (hitEOF == true)
				return null;
			String nextLineInFile = super.readLine();
			if (nextLineInFile == null) {
				hitEOF = true;
				return null;
			}
			String nextLineToReturn = lineBuffer.get(internalIndex);
			lineBuffer.set(internalIndex, nextLineInFile);
			internalIndex = (internalIndex + 1) % numBufferLines;
			return nextLineToReturn;
		}
	}
}
