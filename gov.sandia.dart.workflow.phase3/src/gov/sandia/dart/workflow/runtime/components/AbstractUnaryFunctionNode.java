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

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public abstract class AbstractUnaryFunctionNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		Object result = null;
		try {
			ScriptEngine scriptEngine = new ScriptEngineManager()
					.getEngineByName("nashorn");
			scriptEngine.eval(new StringReader(getCustomCode(properties)));

			String arg1 = (String) runtime.getInput(getName(), DEFAULT_INPUT, String.class);

			if (arg1 == null)
				arg1 = "0";
			Invocable invocable = (Invocable) scriptEngine;

			result = invocable.invokeFunction("f", arg1);
						
		} catch (NoSuchMethodException | ScriptException e) {
			throw new SAWWorkflowException("Failed to execute function", e);
		}
		return Collections.singletonMap("f", result);
	}

	protected abstract String getCustomCode(Map<String, String> properties);

	@Override
	public List<String> getDefaultInputNames() {
		return Collections.singletonList("x");
	}

	@Override
	public List<String> getDefaultOutputNames() {
		return Collections.singletonList("f");
	}

	@Override
	public String getCategory() {
		return "Unary Functions";
	}
}
