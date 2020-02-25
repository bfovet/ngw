/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.ui.view;

import java.util.Set;

import gov.sandia.dart.chart.xyplot.expression.Function;

public class MaterialPlottable extends Plottable implements ISingleVariablePlottable {
	
	protected String var;
	
	public MaterialPlottable(Function[] function) {

		super(function);
	}

	public MaterialPlottable(Function function) {

		super(new Function[] {function});
	}

	public void setVariable(String variable) {
		if (getFreeVariables().contains(variable)) {
			this.var = variable;
		} else {
			throw new VariableNotFoundException(variable);
		}
	}
	
	@Override
	public Set<String> getFreeVariables() {
		Set<String> variables = super.getFreeVariables();
		if (var != null) {
			variables.remove(var);
		}
		return variables;
	}

	public String getVariable() {
		return var;
	}
	
}
