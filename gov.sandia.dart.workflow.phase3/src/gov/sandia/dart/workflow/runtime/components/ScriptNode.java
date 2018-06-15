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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptNode extends SAWCustomNode {

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
		Bindings bindings = scriptEngine.getBindings(ScriptContext.GLOBAL_SCOPE);

		for (String name: runtime.getInputNames(getName())) {
			bindings.put(name, runtime.getInput(getName(), name, String.class));
		}
		bindings.putAll(properties);
		runtime.log().debug("bindings are {0}", bindings.entrySet());

		try {
			Object result = scriptEngine.eval(new StringReader(getCustomCode(properties)));
			return Collections.singletonMap("f", result);
		} catch (ScriptException e) {
			throw new SAWWorkflowException("Failed to execute expression", e);
		}		
	}

	public String getCustomCode(Map<String, String> properties) {
		return properties.get("customCode");
	}

	@Override
	public List<String> getDefaultInputNames() { return Collections.singletonList("x"); }

	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList("f"); }	
	@Override public List<String> getDefaultProperties() { return Arrays.asList("customCode"); }	
	@Override public List<String> getDefaultPropertyTypes() { return Collections.singletonList("multitext"); }
}
