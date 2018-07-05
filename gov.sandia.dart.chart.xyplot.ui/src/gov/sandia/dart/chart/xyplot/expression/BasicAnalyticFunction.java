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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BasicAnalyticFunction extends AnalyticFunction {
    
    Expression exp;
    
    BasicAnalyticFunction(String name,double scaleX, double scaleY, double offsetX, double offsetY) {
    	super(name,scaleX,scaleY,offsetX,offsetY);
    }
    
	BasicAnalyticFunction(String name) {
    	this(name,0.0,0.0,1.0,1.0);
    }

	@Override
    public Set<String> getVariables() {
        if (exp != null) {
        	Set<String> list = exp.getVariables(); 
            return list;
        } 
        return null;
    }

    @Override
    public double getValue(double x) {
        Map<String,Double> m = new HashMap<String,Double>();
        Set<String> varList = getVariables();
        for (String var : varList) {
            if (!localVariables.containsKey(var)) {
                m.put(var,scaleX * (x + offsetX));
            }
        }
        return getValue(m);
    }
    
    public BasicAnalyticFunction getFunction(String var, Double value) {
    	BasicAnalyticFunction b = FunctionBuilder.buildBasicFunction(
    			getName()+"("+var+":"+(Math.floor((value*100)))/100+")",scaleX,scaleY,offsetX,offsetY,exp);
    	b.localVariables.put(var, new Expression(new Operator("ident"),new Operand(value)));
    	return b;
    }
    
    public static void main(String[] args) {
    	BasicAnalyticFunction b = FunctionBuilder.buildSimpleAnalytic("<lambda>", "x^2 + y^2");
    }
    
    @Override
    public double getValue(Map<String,Double> m) {
        if (exp != null) {
            for(String var : localVariables.keySet()) {
                m.put(var, localVariables.get(var).evaluate(m));
            }
            return scaleY * (exp.evaluate(m) + offsetY);
        } else {
            return Double.NaN;
        }
    }
    
    @Override
	public String toString() {
    	return getName() + ":\n\n" + exp.toString();
    }

	@Override
	public double[] getPreferredXBounds() {
		return new double[] {-10,10};
	}

	@Override
	public double[] getPreferredYBounds() {
		return new double[] {-10,10};
	}
	
}
