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

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class ApostproNode extends SAWCustomNode {

	private static final String SKIP = "skip";
	private static final String FIELD = "field";
	private static final String MATCH = "match";
	private static final String INPUT_FILE = "inputFile";
	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {	
		File inputFile = getInputFile(properties, runtime);
		if (getMatch(properties) == null)
			throw new SAWWorkflowException("Invalid value for parameter 'match'");
		String result = "NO_MATCH";
		try {
			Pattern p = Pattern.compile(getMatch(properties));
			List<String> lines = FileUtils.readLines(inputFile);
			for (int i=0; i<lines.size(); ++i) {
				String line = lines.get(i);
				if (p.matcher(line).find()) {
					int lineIndex = i + getSkip(properties);
					if (lineIndex >= lines.size()) {
						throw new SAWWorkflowException("Matched line index plus skip (" + lineIndex + ") extends past end of file");
					}
					String otherLine = lines.get(lineIndex).trim();
					String[] fields = otherLine.split("[,\\s]+");
					int fieldIndex = getField(properties);
					if (fieldIndex >= fields.length) {
						throw new SAWWorkflowException("Field index (" + fieldIndex + ") past end of line \"" + otherLine + "\"");
					}
					result = fields[getField(properties)];
					break;					
				}
			}
			return Collections.singletonMap("f", result);

		} catch (PatternSyntaxException e) {
			throw new SAWWorkflowException("Invalid regular expression for parameter 'match'", e);
		} catch (IOException e) {
			throw new SAWWorkflowException("Error reading file " + inputFile.getAbsolutePath(), e);
		} 
	}

	@Override public List<String> getDefaultInputNames() { return Collections.singletonList(INPUT_FILE); }
	@Override public List<String> getDefaultInputTypes() { return Collections.singletonList("input_file"); }
	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList("f"); }
	@Override public String getCategory() { return "Engineering"; }

	public File getInputFile(Map<String, String> properties, RuntimeData runtime) {
		return getFileFromPortOrProperty(runtime, properties, INPUT_FILE, true, true);
	}

	public String getMatch(Map<String, String> properties) {
		return properties.get(MATCH);
	}

	public int getField(Map<String, String> properties) {
		return Integer.parseInt(properties.get(FIELD));
	}

	public int getSkip(Map<String, String> properties) {
		return Integer.parseInt(properties.get(SKIP));
	}

	@Override public List<String> getDefaultProperties() { return Arrays.asList(INPUT_FILE, MATCH, FIELD, SKIP); }
	@Override public List<String> getDefaultPropertyTypes() { return Arrays.asList("home_file", "default", "default", "default"); }
	
	


}
