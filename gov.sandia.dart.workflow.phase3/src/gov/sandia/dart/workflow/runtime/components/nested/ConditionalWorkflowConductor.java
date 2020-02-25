/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;

public class ConditionalWorkflowConductor extends SimpleWorkflowConductor {
	private String condition;
	private ScriptEngine scriptEngine;
	
	public ConditionalWorkflowConductor() {
		super();
		// TODO Would be nice if we could initialize this with data to operate on!
		scriptEngine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	@Override
	public boolean hasNext() {
		try {
			System.err.println("condition is " + condition);
			try {
				Object result = scriptEngine.eval(condition);
				if (result.getClass() == Boolean.class) {
					System.err.println("boolean result");
					return firstTime == true && (Boolean) result;
				} else {
					System.err.println("non-boolean condition result: " + result.getClass().toString());
				}
			} catch (ScriptException e) {
				System.err.println("problem evaluating conditional expression with script engine");
				e.printStackTrace(System.err);
			}
			return firstTime == true && condition.equals("true");
		} finally {
			firstTime = false;
		}
	}

	@Override
	public void setProperties(Map<String, String> properties) {
		this.condition = properties.get("condition");
	}

	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo("condition", "default"));
	}
}
