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

public class Operator extends ExpressionComponent{
    
	
    final static public Map<String,IOperand> KNOWN_FUNCTIONS = new HashMap<String,IOperand>();
    static{
        KNOWN_FUNCTIONS.put("ident",new IOperand(){
            public double calculate(double[] args) {
                return args[0];
            }
        });
        KNOWN_FUNCTIONS.put("add",new IOperand(){
            public double calculate(double[] args) {
                return args[0] + args[1];
            }
        });
        KNOWN_FUNCTIONS.put("sub",new IOperand(){
            public double calculate(double[] args) {
                return args[0] - args[1];
            }
        });
        KNOWN_FUNCTIONS.put("mul",new IOperand(){
            public double calculate(double[] args) {
                return args[0] * args[1];
            }
        });
        KNOWN_FUNCTIONS.put("div",new IOperand(){
            public double calculate(double[] args) {
                return args[0] / args[1];
            }
        });
        KNOWN_FUNCTIONS.put("pow",new IOperand(){
            public double calculate(double[] args) {
                return Math.pow(args[0], args[1]);
            }
        });
        KNOWN_FUNCTIONS.put("neg",new IOperand(){
            public double calculate(double[] args) {
                return -args[0];
            }
        });
        KNOWN_FUNCTIONS.put("sin",new IOperand(){
            public double calculate(double[] args) {
                return Math.sin(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("cos",new IOperand(){
            public double calculate(double[] args) {
                return Math.cos(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("tan",new IOperand(){
            public double calculate(double[] args) {
                return Math.tan(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("abs",new IOperand(){
            public double calculate(double[] args) {
                return Math.abs(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("sqrt",new IOperand(){
            public double calculate(double[] args) {
                return Math.sqrt(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("log",new IOperand(){
            public double calculate(double[] args) {
                return Math.log(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("ln",new IOperand(){
            public double calculate(double[] args) {
                return Math.log(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("log10",new IOperand(){
            public double calculate(double[] args) {
                return Math.log10(args[0]);
            }
        });
        KNOWN_FUNCTIONS.put("exp",new IOperand(){
            public double calculate(double[] args) {
                return Math.exp(args[0]);
            }
        });
    }
    
    
    protected String identifier;
    protected Type type;
    
    public Operator(String id) {
        this.identifier = id;
    }
    
    public double compute(double ... args) {
        IOperand fn = KNOWN_FUNCTIONS.get(identifier);
        if (fn != null) {
            return fn.calculate(args);
        } else {
            throw new RuntimeException("ERROR: unknown identifier.");
        }
    }
    
    public String toString(int depth) {
        String s = "";
        for (int i = 0; i < depth ; i++) {
            s += "   ";
        }
        return s + this.identifier;
    }
    
    @Override
    public String toString() {
        return toString(0);
    }
}
