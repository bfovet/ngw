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

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Operand extends ExpressionComponent{
	
    double value;
    String identifier;
    
    public Operand(String s) {
    	identifier = s;
    	try {
    		value = Double.parseDouble(s);
    	} catch (NumberFormatException e) {
    		Double C = TokenLibrary.lookup(s);
    		if (C != null){
    			value = C;
    		} else {
    			value = Double.NaN;
    		}
    	}
    }
    
    public Operand(double val) {
        this.value = val;
    }
    
    public double evaluate(Map<String,Double> values) {
        if (!Double.isNaN(value)) {
            return value;
        } else {
            Double v = TokenLibrary.lookup(identifier);
            if (v != null) {
            	return v;
            } else {
            	return values != null ? values.get(identifier) : Double.NaN;
            }
        }
    }
    
    @Override
    public String toString(){
        return toString(0);
    }
    
    public String toString(int depth) {
        String s = "";
        for (int i = 0; i < depth; i++) {
            s += "   ";
        }
        if (identifier != null) {
        	return s + identifier;
        } else {
        	return s + value;
        }
    }
    
    
    public Set<String> getVariables() {
    	return getVariables(new TreeSet<String>());
    }
    
    Set<String> getVariables(Set<String> list) {
    	if (Double.isNaN(value) && !list.contains(identifier) && TokenLibrary.lookup(identifier)==null) {
    		list.add(identifier);
    	}
    	return list;
    }
    
}
