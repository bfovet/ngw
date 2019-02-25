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

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class CompareNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
				
		String name = "false";
		Object result = null;
		String arg1 = null;
		try {
			ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
			scriptEngine.eval(new StringReader(getCustomCode(properties, runtime)));

			arg1 = getStringFromPortOrProperty(runtime, properties, "x");
//			arg1 = (String) runtime.getInput(getName(), "x", String.class);
//			if (arg1 == null)
//				throw new SAWWorkflowException("No input 'x' for node " + getName());

			String arg2 = getStringFromPortOrProperty(runtime, properties, "y");		    
//			String arg2 = (String) runtime.getInput(getName(), "y", String.class);		    
//			if (arg2 == null)
//				throw new SAWWorkflowException("No input 'x' for node " + getName());
			
			Invocable invocable = (Invocable) scriptEngine;

			result = invocable.invokeFunction("f", arg1, arg2);

			Boolean b = Boolean.valueOf(result.toString());
			name = b.toString();
									
		} catch (NoSuchMethodException | ScriptException e) {
			throw new SAWWorkflowException("Failed to execute function", e);
		}

		// There are two outputs, "true" and "false". This will only send a token to the one that
		// matches the value of the boolean.
		String fname = name;
		return Collections.singletonMap(fname, arg1);			
	}

	protected String getCustomCode(Map<String, String> properties, RuntimeData runtime) {
		return String.format("var f = function(arg1, arg2) { return Number(arg1) %s Number(arg2) ; }", getOperator(properties, runtime));
	}
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x"), new InputPortInfo("y")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("true"), new OutputPortInfo("false")); }
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("operator")); }
//	@Override public List<String> getDefaultProperties() { return Arrays.asList("operator"); }
	@Override public String getCategory() { return "Control"; }
	
	public String getOperator(Map<String, String> properties, RuntimeData runtime) {
		String operator = properties.get("operator");
		if (operator == null || operator.isEmpty()) {
			operator = "==";
			runtime.log().info(getName() + ": no operator specified, presuming \"" + operator + "\"");
		}
		return operator;		
	}
}
