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

import gov.sandia.dart.chart.xyplot.expression.AnalyticFunction;
import gov.sandia.dart.chart.xyplot.expression.Expression;
import gov.sandia.dart.chart.xyplot.expression.Function;

import java.util.HashSet;
import java.util.Set;

public class Plottable {
	
	// The label of the x axis
	protected String abscissa;
	// The label of the y axis
	protected String ordinate;
	// The functions that are contained by this Plottable
	protected Function[] functions;
	
	/**
	 * Default constructor (does not set axes)
	 * @param f, an array of Functions to plot.
	 */
	public Plottable(Function[] f) {
		abscissa = "";
		ordinate = "";
		this.functions = f;
	}
	
	public Plottable(Function f)
	{
		this(new Function[] {f});
	}
	
	/**
	 * Adds a local variable to each of these functions.
	 * @param name, the name of the variable
	 * @param value, the value of the variable
	 */
	public void addLocalVariable(String name, double value) {
		for (Function f : functions) {
			if (f instanceof AnalyticFunction) {
				((AnalyticFunction)f).addLocalVariable(name,Expression.build(value));
			}
		}
	}
	
	/**
	 * @return the number of Functions contained in this Plottable
	 */
	public int getFunctionCount() {
		return functions.length;
	}
	
	/**
	 * @return the number of free (undefined) variables across all
	 * of the Functions in this Plottable.
	 */
	public Set<String> getFreeVariables() {
		Set<String> variables = new HashSet<String>();
		for (Function f : functions) {
			if (f instanceof AnalyticFunction) 
				variables.addAll(((AnalyticFunction)f).getVariables());
		}
		return variables;
	}
	
	/**
	 * @return the names of the Functions contained in this Plottable
	 */
	public String[] getFunctionNames() {
		String[] names = new String[functions.length];
		for (int i = 0; i < functions.length; i++) {
			names[i] = functions[i].getName();
		}
		return names;
	}
	
	/**
	 * @param abscissa, the new label for the x axis
	 * @param ordinate, the new label for the y axis
	 */
	public void labelAxes(String abscissa, String ordinate) {
		this.abscissa = abscissa;
		this.ordinate = ordinate;
	}
	
}
