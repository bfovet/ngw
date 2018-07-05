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

public class Expression extends Operand{
    
    Operator operator;
    List<Operand> operands;
    
    public static Expression build(String exp) {
    	exp = SierraExpressionParser.convertMultiplication(exp);
    	return SierraExpressionParser.buildExpressionTree(exp);
    }
    
    public static Expression build(double val) {
    	return new Expression(new Operator("ident"),new Operand(val));
    }
    
    public Expression(Operator operator,List<Operand> args) {
        super(Double.NaN);
        this.operator = operator;
        this.operands = args;
    }
    
    public Expression(Operator operator,Operand ... args) {
        super(Double.NaN);
        this.operator = operator;
        operands = new ArrayList<Operand>();
        for (Operand oper : args)  {
        	operands.add(oper);
        }
    }
    
    public Expression(Operand arg) {
    	super(arg.value);
    	operands = new ArrayList<Operand>();
    	operands.add(arg);
    	operator = null;
    }
    
    @Override
    public double evaluate(Map<String,Double> values) {
    	if (operator != null) {
	        double[] input = new double[operands.size()];
	        for (int i = 0; i < operands.size(); i++) {
	            input[i] = operands.get(i).evaluate(values);
	        }
	        return operator.compute(input);
    	} else {
    		return operands.get(0).evaluate(values);
    	}
    }
    
    /**
     * NOTE: this is only meant to handle cases where the Expression objects can
     * be evaluated with no further local variables.
     */
    public double getValue(Map<String,Expression> values) {
    	Map<String,Double> dVals = new HashMap<String,Double>();
    	for (String var : values.keySet()) {
    		try {
    			Double val = values.get(var).evaluate(null);
    			dVals.put(var, val);
    		} catch (NullPointerException e) {
    			return Double.NaN;
    		}
    	}
    	return evaluate(dVals);
    }
    
    @Override
    public String toString() {
        return toString(0);
    }
    
    @Override
    public Set<String> getVariables() {
    	Set<String> list = new TreeSet<String>();
    	getVariables(list);
    	return list;
    }
    
    Set<String> getVariables(Set<String> list) {
    	for(Operand o : operands) {
    		o.getVariables(list);
    	}
    	return list;
    }
    
    public String toString(int depth) {
        String s = "";
        s += operator.toString(depth) + " {\n";
        for (Operand operand : operands) {
            s += operand.toString(depth+1) + "\n";
        }
        for(int i = 0; i < depth; i++) {
            s += "   ";
        }
        return s + "}\n";
    }
    
 }
