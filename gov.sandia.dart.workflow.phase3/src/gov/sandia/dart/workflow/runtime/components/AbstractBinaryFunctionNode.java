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
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public abstract class AbstractBinaryFunctionNode extends SAWCustomNode {

	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		Object result = null;
		try {
			ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
			scriptEngine.eval(new StringReader(getCustomCode(properties)));

			String arg1 = (String) runtime.getInput(getName(), "x", String.class);
			if (arg1 == null)
				throw new SAWWorkflowException("No input 'x' for node " + getName());

			String arg2 = (String) runtime.getInput(getName(), "y", String.class);		    
			if (arg2 == null)
				throw new SAWWorkflowException("No input 'y' for node " + getName());
			
			Invocable invocable = (Invocable) scriptEngine;

			result = invocable.invokeFunction("f", arg1, arg2);
			runtime.log().debug("{0} = {1} ({2}, {3})", getName(), result, arg1, arg2);
		} catch (NoSuchMethodException | ScriptException e) {
			throw new SAWWorkflowException("Failed to execute function", e);
		}
		return Collections.singletonMap("f", result);			
	}

	protected abstract String getCustomCode(Map<String, String> properties);
	
	@Override public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("x"), new InputPortInfo("y")); }
	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }
	@Override public String getCategory() { return NodeCategories.SCALAR_OPS; }
	
}
