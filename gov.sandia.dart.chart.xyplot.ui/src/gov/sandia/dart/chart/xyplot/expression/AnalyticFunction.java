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

public abstract class AnalyticFunction extends Function {

	protected Map<String,Expression> localVariables;

	protected AnalyticFunction(String name) {
		super(name);
    	this.localVariables = new HashMap<String,Expression>();
	}

	protected AnalyticFunction(String name, double scaleX, double scaleY, double offsetX, double offsetY) {
		super(name, scaleX, scaleY, offsetX, offsetY);
    	this.localVariables = new HashMap<String,Expression>();
	}
	
    public int localVariableCount() {
    	return localVariables.size();
    }
     
    public void addFunction() {
        SierraExpressionParser.OPERATORS.add(getName());
        SierraExpressionParser.ARG_COUNT_MAP.put(getName(), getVariables().size()-localVariables.size());
        Operator.KNOWN_FUNCTIONS.put(getName(), new IOperand(){
            @Override
			public double calculate(double[] args) {
                Set<String> vars = getVariables();
                if(vars.size()-localVariables.size() != args.length) {
                    throw new RuntimeException("Error: incorrect number of arguments.");
                } else {
                    Map<String,Double> inputMap = new HashMap<String,Double>();
                    for(int i = 0; i < args.length; i++) {
                        inputMap.put(vars.toArray(new String[0])[0],args[i]);
                    	vars.remove(0);
                    }
                    return getValue(inputMap);
                }
            }
        });
    }
    
	public void addLocalVariable(String name, Expression value) {
		localVariables.put(name, value); 
	}
	
	public void setLocalVariables(Map<String, Expression> localVars) {
		localVariables = localVars;
	}

	public abstract double getValue(Map<String,Double> varMap);    

    public abstract Set<String> getVariables();


}
