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
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
		Map<String, Object> outputs = new HashMap<String, Object>();

		for (String name: runtime.getInputNames(getName())) {
			bindings.put(name, runtime.getInput(getName(), name, String.class));
		}
		bindings.putAll(properties);
		runtime.log().debug("bindings are {0}", bindings.entrySet());

		Object result;
		try {
			result = scriptEngine.eval(new StringReader(getCustomCode(properties)));
		} catch (ScriptException e) {
			throw new SAWWorkflowException("Failed to execute expression", e);
		}		
		for (WorkflowDefinition.OutputPort port : workflow.getNode(getName()).outputs.values()) {
			if (isConnectedOutput(port.name, workflow)) {
				if ("f".equals(port.name)) {
					outputs.put("f", result);
					runtime.log().debug("node \"{0}\": sent object of type {1} to output port f", getName(),
							result.getClass().getTypeName());
				} else {
					Object outputValue = scriptEngine.get(port.name);
					outputs.put(port.name, outputValue); // TODO: should we do something else with null values?
					if (outputValue == null)
						runtime.log().debug("node \"{0}\": sent null value to output port {1}", getName(), port.name);
					else
						runtime.log().debug("node \"{0}\": sent object of type {1} to output port {2}", getName(),
								outputValue.getClass().getTypeName(), port.name);
				}
			}
		}
		return outputs;
	}

	public String getCustomCode(Map<String, String> properties) {
		return properties.get("customCode");
	}

	@Override
	public List<InputPortInfo> getDefaultInputs() { return Collections.singletonList(new InputPortInfo("x")); }

	@Override public List<OutputPortInfo> getDefaultOutputs() { return Collections.singletonList(new OutputPortInfo("f")); }	
	@Override public List<PropertyInfo> getDefaultProperties() { return Arrays.asList(new PropertyInfo("customCode", "multitext")); }
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.SCRIPTING); }
}
