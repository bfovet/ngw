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

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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

public class RegexNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
				
		Reader reader = null;
		String result = null;
		boolean matches = false;
		reader = (Reader) runtime.getInput(getName(), "x", Reader.class);
		String regex = getRegex(properties);
		
		try {
			if (reader == null)
				throw new SAWWorkflowException("Value missing for required input 'x' in node " + getName());		
			String arg1 = IOUtils.toString(reader);

			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(arg1);			
			matches = m.find();
			result = arg1;
			if (matches) {
				if (m.groupCount() > 0)
					result = m.group(1);					
			}
		} catch (PatternSyntaxException e) {
			throw new SAWWorkflowException("Bad regular expression " + regex + " in node " + getName());		
		} catch (IOException e) {
			throw new SAWWorkflowException("Error reading input in node " + getName());		
		} finally {
			IOUtils.closeQuietly(reader);
		}
		
		String fname = matches ? "match" : "no_match";	
		return Collections.singletonMap(fname, result);		
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("match"), new OutputPortInfo("no_match")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("regex")); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.CONTROL, NodeCategories.TEXT_DATA); }
	
	public String getRegex(Map<String, String> properties) {
		String raw = properties.get("regex");	
		if (StringUtils.isEmpty(raw))
			throw new SAWWorkflowException("Value missing for required parameter 'regex' in node " + getName());
		return raw;
	}
}
