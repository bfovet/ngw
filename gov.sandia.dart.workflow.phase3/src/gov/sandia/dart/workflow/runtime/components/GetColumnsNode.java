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

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.Datum;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

public class GetColumnsNode extends SAWCustomNode {
	private static final String DEFAULT_SEPARATOR = "\\s+",
	                            HEADER_LINES_PROPERTY = "number of header lines",
	                            SKIP_FOOTER_PROPERTY = "number of footer lines",
	                            SEPARATOR_PROPERTY = "separator regular expression (optional)",
	                            COLUMN_INDEX_PROPERTY = "column number (optional)",
	                            DATA_IN_PORT = "data",
	                            FILENAME_PORT = "fileName",
	                            COLUMN_NAME_PORT_PROMPT = "RENAME TO GET NAMED COLUMN",
	                            INDEXED_COLUMN_PORT = "indexed column";
	private static final int DEFAULT_NUM_HEADER_LINES = 1;
	private static final int DEFAULT_NUM_FOOTER_LINES = 0;
	
	private static final String[] HEADER_LINES_PROPERTY_NAMES = { HEADER_LINES_PROPERTY, "headerLines" };
	private static final String[] SEPARATOR_PROPERTY_NAMES = { SEPARATOR_PROPERTY, "seperator" };
	
	protected int getNumHeaderLines(Map<String, String> properties) {
		String propertyValue = null;
		for (String propertyName : HEADER_LINES_PROPERTY_NAMES) {
			propertyValue = properties.get(propertyName);
			if (!StringUtils.isEmpty(propertyValue))
				break;
		}
		if (StringUtils.isEmpty(propertyValue))
			return DEFAULT_NUM_HEADER_LINES;
		try {
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException e) {
			throw new NodeException("CAN'T PARSE NUMBER OF HEADER LINES INTEGER");
		}
	}
	
	protected int getFooterLines(Map<String, String> properties) {
		String propertyValue = properties.get(SKIP_FOOTER_PROPERTY);
		if (StringUtils.isEmpty(propertyValue))
			return DEFAULT_NUM_FOOTER_LINES;
		try {
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException e) {
			throw new NodeException("CAN'T PARSE INTEGER in footer-lines-to-ignore");
		}
	}
	
	protected int getColumnIndex(Map<String, String> properties) {
		String propertyValue = properties.get(COLUMN_INDEX_PROPERTY);
		if (StringUtils.isEmpty(propertyValue))
			return 0;
		try {
			return Integer.parseInt(propertyValue);
		} catch (NumberFormatException e) {
			throw new NodeException("CAN'T PARSE INTEGER in " + COLUMN_INDEX_PROPERTY + " property");
		}
	}
	
	protected String getSeparator(Map<String, String> properties) {
		for (String propertyName : SEPARATOR_PROPERTY_NAMES) {
			String separator = properties.get(propertyName);
			if (!StringUtils.isEmpty(separator)) {
				return separator;
			}
		}
		return DEFAULT_SEPARATOR;
	}
	
	protected BufferedReader getDataReader(Map<String, String> properties, RuntimeData runtime, Node thisNode) {
		
		Reader data = (Reader) runtime.getInput(getName(), "columns", Reader.class);
		if (data == null) {
			data = (Reader) runtime.getInput(getName(), "data", Reader.class);
		}
		if (data == null) {
			String fileName = getOptionalStringFromPortOrProperty(runtime, properties, "fileName");
			if (!StringUtils.isEmpty(fileName))
				try {
					data = new FileReader(fileName);
				} catch (FileNotFoundException e) {
					throw new NodeException("Can't find columnar data file named \"" + fileName + "\"");
				}
		}
		if (data == null) {
			WorkflowDefinition.Property columnProperty = thisNode.properties.get("columns");
			if (columnProperty == null) 
				throw new NodeException("Please add an input file");
			data = (Reader) (new Datum(columnProperty.type, columnProperty.value, String.class)).getAs(Reader.class);
		}	
		return new BufferedReader(data);
	}

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		Node thisNode = workflow.getNode(getName());
		int numHeaderLines = getNumHeaderLines(properties);
		int numFooterLinesToIgnore = getFooterLines(properties);
		int columnIndex = getColumnIndex(properties);
		String separator = getSeparator(properties);
		
		BufferedReader reader = getDataReader(properties, runtime, thisNode);
		
		List<String> outputPortNames = thisNode.outputs.values().stream().map(p -> p.name).collect(Collectors.toList());

		Map<String, Integer> portToColumnIndex = new HashMap<String, Integer>();
		Map<String, StringBuilder> portToColumnBuffer = new HashMap<String, StringBuilder>();
		int minNumColumns = 0;
		int lineNum = 0;
		
		if (columnIndex > 0 && !outputPortNames.contains(INDEXED_COLUMN_PORT))
			throw new NodeException("output port \"" + INDEXED_COLUMN_PORT +
					"\" required when value for \"" + COLUMN_INDEX_PROPERTY + "\" property is specified");
			
		if (outputPortNames.contains(INDEXED_COLUMN_PORT)) {
			if (isConnectedOutput(INDEXED_COLUMN_PORT, workflow)) {
				if (columnIndex < 1)
					throw new NodeException("value for \"" + COLUMN_INDEX_PROPERTY + "\" property must be specified when \"" +
												INDEXED_COLUMN_PORT + "\" output port is connected");
				minNumColumns = columnIndex;
				portToColumnIndex.put(INDEXED_COLUMN_PORT, columnIndex-1);
				portToColumnBuffer.put(INDEXED_COLUMN_PORT, new StringBuilder());
			}
			outputPortNames.remove(INDEXED_COLUMN_PORT);
		}
		
		if (outputPortNames.size() > 0) {
			if (numHeaderLines < 1)
				throw new NodeException("at least one header line must be specified when output ports corresponding to column names are present");
				
			String headerLine = "";
			try {
				while (numHeaderLines > 0) {
					headerLine = reader.readLine();
					lineNum += 1;
				    System.err.println("read header line " + headerLine);
				    numHeaderLines -= 1;
				}
			} catch (IOException e) {
				throw new NodeException("exception reading input", e);
			}
			String[] columnNames = headerLine.trim().split(separator);
			int numColumns = columnNames.length;
			if (numColumns < outputPortNames.size()) {
				throw new NodeException("not enough column names found");
			}
			final ArrayList<String> columnNameList = new ArrayList<String>(Arrays.asList(columnNames));
					
			for (String portName : outputPortNames) {
				List<Integer> matchingIndices = IntStream.range(0, numColumns).filter(i -> columnNameList.get(i).equals(portName)).boxed().collect(Collectors.toList());
				if (matchingIndices.isEmpty())
					throw new NodeException("no matching column for output port " + portName);
				if (matchingIndices.size() > 1)
					throw new NodeException("more than one matching column for output port " + portName);
				int matchingIndex = matchingIndices.get(0);
				minNumColumns = Math.max(minNumColumns, matchingIndex + 1);
				portToColumnIndex.put(portName, matchingIndex);
				portToColumnBuffer.put(portName, new StringBuilder());
				System.err.println("mapping column #" + String.valueOf(matchingIndex+1) + " to port " + portName);
			}
		}
		
		String nextLine;
		try {
			if (numFooterLinesToIgnore > 0)
				reader = new ExcludeFooterLinesReader(reader, numFooterLinesToIgnore);
			nextLine = reader.readLine();
			lineNum += 1;
			System.err.println("got first nextLine " + nextLine);
		} catch (IOException e) {
			throw new NodeException("exception reading input", e);
		}
		while (nextLine != null) {
			String[] fields = nextLine.trim().split(separator);
			if (fields.length < minNumColumns)
				throw new NodeException("expecting at least " + String.valueOf(minNumColumns) +
						" columns, only found " + String.valueOf(fields.length) + " on input line number " + String.valueOf(lineNum));
			outputPortNames.forEach(portName -> portToColumnBuffer.get(portName).append(fields[portToColumnIndex.get(portName)]).append(System.lineSeparator()));
			try {
				nextLine = reader.readLine();
			} catch (IOException e) {
				throw new NodeException("exception reading input after line #" + String.valueOf(lineNum), e);
			}
			lineNum += 1;
		}

		Map<String, Object> outputMap = new HashMap<String, Object>();
		outputPortNames.forEach(p -> outputMap.put(p, portToColumnBuffer.get(p).toString()));
		return outputMap;
	}

	@Override
	public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo(DATA_IN_PORT),
																		 new InputPortInfo(FILENAME_PORT)); }
	@Override
	public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(COLUMN_NAME_PORT_PROMPT),
																		   new OutputPortInfo(INDEXED_COLUMN_PORT)); }
	@Override
	public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(
			new PropertyInfo(HEADER_LINES_PROPERTY, RuntimeData.DEFAULT_TYPE, String.valueOf(DEFAULT_NUM_HEADER_LINES)),
			new PropertyInfo(SKIP_FOOTER_PROPERTY, RuntimeData.DEFAULT_TYPE, String.valueOf(DEFAULT_NUM_FOOTER_LINES)),
			new PropertyInfo(SEPARATOR_PROPERTY), new PropertyInfo(COLUMN_INDEX_PROPERTY)); }

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
					throw new NodeException(String.format("expected %d footer lines, only found %d",
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
