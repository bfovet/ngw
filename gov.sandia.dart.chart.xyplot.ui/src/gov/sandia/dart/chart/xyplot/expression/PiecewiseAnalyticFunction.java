/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.chart.xyplot.expression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class PiecewiseAnalyticFunction extends AnalyticFunction {

	
	RangeSet rangeSet;
	double[] rawVals;
	String[] rawExpr;
	private double[] preferredXBounds;
	
	PiecewiseAnalyticFunction(double[] vals, String[] expr, String name, Double scaleX,Double scaleY,Double offsetX,Double offsetY) {
		
		super(name,scaleX,scaleY,offsetX,offsetY);
		this.rawVals = vals;
		this.rawExpr = expr;
		
		List<Double> values = new ArrayList<Double>();
		List<Expression> expressions = new ArrayList<Expression>();
		
		for (double val : vals) {
			values.add(val);
		}
		
		for (String exp : expr) {
			exp = SierraExpressionParser.convertMultiplication(exp);
			expressions.add(SierraExpressionParser.buildExpressionTree(exp));
		}
		
		rangeSet = new RangeSet(values,expressions);
	}
	
	public PiecewiseAnalyticFunction(double[] vals, String[] expr,String name) {
		this(vals,expr,name,1.0,1.0,0.0,0.0);
	}
	
	
	@Override
	public double getValue(double x) {
		Expression e = rangeSet.find(x);
		if (e != null) {
			Set<String> vars = getVariables();
			if (vars.size() <= 1) {
				Map<String,Double> params = new HashMap<String,Double>();
				params.put(vars.toArray(new String[0])[0],x*scaleX + offsetX);
				for (String var : localVariables.keySet()) {
					params.put(var, localVariables.get(var).evaluate(params));
				}
				return scaleY * e.evaluate(params) + offsetY;
			}
		}
		return Double.NaN;
	}
	
	@Override
	public String toString() {
		return rangeSet.toString();
	}
	
	private class Range {
		
		double lower;
		double upper;
		
		public Range(double lower, double upper) {
			this.lower = lower;
			this.upper = upper;
		}
		
		public boolean contains(double x) {
			return lower <= x &&
					x < upper;
		}
		
		@Override
		public String toString() {
			return "[" + lower + " : " + upper + "]";
		}
	}
	
	private class RangeSet {
		
		Range[] ranges;
		Expression[] expressions;
		
		public RangeSet(List<Double> values, List<Expression> expr) {
			
			assert(values.size() == expr.size()):"ERROR: Mismatched range-expression values.";
			
			ranges = new Range[values.size()];
			if(values.size() > 0)
			{
				values.set(0,Double.NEGATIVE_INFINITY); // It does not matter what the first value is.
				
				values.add(Double.POSITIVE_INFINITY);
				for (int i = 0; i < values.size() - 1; i++) {
					ranges[i] = new Range(values.get(i), values.get(i + 1));
				}
			}

			expressions = expr.toArray(new Expression[0]);
		}
		
		public Expression find(double x) {
			return find(x,0,ranges.length);
		}
		
		private Expression find(double x, int startIndex, int endIndex) {
			int i = (startIndex + endIndex)/2;
			if(i > ranges.length - 1)
				return null;
			if (ranges[i].contains(x)) {
				return expressions[i]; 
			} else if (x < ranges[i].lower) {
				return find(x,startIndex,i);
			} else if (x >= ranges[i].upper) {
				return find(x,i,endIndex);
			}
			return null;
		}
		
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < ranges.length; i++) {
				builder.append(ranges[i].toString() + "\n\n");
				builder.append(expressions[i].toString() + "\n\n");
			}
			return builder.toString();
		}
	}

	@Override
	public double getValue(Map<String, Double> varMap) {
		if (varMap.size() == 1) {
			Double x = null;
			Expression e = null;
			for (String var : varMap.keySet()) {
				x = varMap.get(var);
			}
			if (x != null) {
				e = rangeSet.find(x);
			}
			if (e != null) {
				return e.evaluate(varMap);
			}
		}
		// Not applicable if more than one variable.
		return Double.NaN;
	}

	@Override
	public Set<String> getVariables() {
		Set<String> variables = new TreeSet<String>();
		for (Expression e : rangeSet.expressions) {
			for (String var : e.getVariables()) {
				if (!localVariables.containsKey(var)) {
					variables.add(var);
				}
			}
		}
		return variables;
	}

	@Override
	public double[] getPreferredXBounds() {
		double[] vals;
		if(rawVals.length > 1)
			vals = new double[] {rawVals[0],rawVals[rawVals.length-1]};
		else
			vals = new double[]{0.0,1.0};
			
		if (preferredXBounds == null)
			preferredXBounds = vals;
		return preferredXBounds;
	}
	
	public void setPreferredXBounds(double[] bounds) {
		preferredXBounds = bounds;
	}

	@Override
	public double[] getPreferredYBounds() {
		// Placeholder
		return new double[] {-10.0,10.0};
	}	
}
