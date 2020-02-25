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

final public class FunctionBuilder {
	
	private static String PIECEWISE_CONSTANT = "piecewise constant";
	private static String PIECEWISE_LINEAR = "piecewise linear";
	
	/**
	 * FunctionBuilder is a pure utility class and should never be instantiated 
	 */
	private FunctionBuilder(){
	}	
	
	/**
	 * Build a function representing a scatter plot
	 * @param x the x values
	 * @param y the y values
	 */

	public static ScatterFunction buildSimpleScatter(String name, double[]x, double[]y) {
		return new ScatterFunction(name, x, y);
	}
	
	/**
	 * Build a function representing a histogram
	 * @param data the raw data
	 * @param bins the desired number of bins, or -1 for auto
	 * @return a Function that draws a histogram
	 */
	public static HistogramFunction buildHistogram(String name, double[] data, int bins) {
		return new HistogramFunction(name, data, bins);
	}
	
	public static PiecewiseAnalyticFunction buildSimplePiecewiseAnalytic(String name, double[] x, String[] functions) {
		return buildPiecewiseAnalytic(name,1.0,1.0,0.0,0.0,x,functions);
	}
	
	public static BasicAnalyticFunction buildSimpleAnalytic(String name, String function){
		return buildAnalytic(name,1.0,1.0,0.0,0.0,function);
	}
	
	public static PiecewiseLinearFunction buildSimplePiecewiseLinear(String name, double[] x, double[] y) {
		return buildPiecewiseLinear(name,1.0,1.0,0.0,0.0,x,y);
	}
	
	public static Function buildSimplePiecewiseFunction(String name, double[] x, double[] y, String type) {
		return buildPiecewiseFunction(name,1.0,1.0,0.0,0.0,x,y,type);
	}
	
	public static PiecewiseConstantFunction buildSimplePiecewiseConstant(String name, double[] x, double[] y) {
		return buildPiecewiseConstant(name,1.0,1.0,0.0,0.0,x,y);
	}

	//TODO take a look at this and refactor a bit
	public static Function buildSimpleSalinas(String name, String type, double[][] data) {
		return buildSalinas(name,1.0,1.0,0.0,0.0,type,data);
	}
	
	public static BasicAnalyticFunction buildSimpleBasicFunction(String name, Expression expression){
		return buildBasicFunction(name,1.0,1.0,0.0,0.0,expression);

	}

	/**
	 * whsieh@sandia.gov (7/19/2012)
	 * 
	 * The 'build' method is the main interface for this class, and the intended way
	 * for classes outside of this package to obtain Function objects. Currently, the
	 * different ways to build a Function include using the parameters:
	 * 
	 * String[][]: defines a piecewise analytic function. Each top-level array is the
	 * same length as the number of parts in the piecewise function. The first contains
	 * the expression thresholds/lower bounds (in order) and the second contains the
	 * expression strings.
	 * 
	 * String: defines a basic analytic function. Tokens that define local variables are
	 * separated by a semicolon, e.g. "amp=5; per=2*pi; amp*sin(per*x)" is equivalent to
	 * "5*sin(2*pi*x)"
	 * 
	 * Expression: defines a basic analytic function. Directly instantiates a new Function
	 * with no local variables.
	 * 
	 * double[][]: defines a piecewise linear function. Each sub-array contains a pair of
	 * data points, and the top-level array is a collection of these sub-arrays. Note that
	 * defining a piecewise linear function rather than a piecewise constant function was
	 * an arbitrary decision in this case.
	 * 
	 * double[][], String: defines either a piecewise linear function OR a piecewise con-
	 * stant function depending on the value of the second parameter. If the String is
	 * "piecewise constant" (ignoring case) then a piecewise constant Function is constructed,
	 * and if the String is "piecewise linear" (again ignoring case) then a piecewise linear
	 * Function is constructed. (TODO I should have probably changed the String argument to
	 * an enumerated type)
	 * 
	 * String, double[][]: defines a function of a specific type, followed by its tabular data.
	 * This is intended for constructing functions defined using Salinas. Types include poly-
	 * nomial, linear, loglog, and random. (TODO Again, I should have probably changed the 
	 * String argument to an enumerated type)
	 * 
	 * @param name, the name of the function
	 * @param scaleX, the scale of the x-axis
	 * @param scaleY, the scale of the y-axis
	 * @param offsetX, the offset of the x-axis
	 * @param offsetY, the offset of the y-axis
	 * @param params, an array of arbitrary parameters needed to build the Function
	 * @return a Function object if construction was successful, and null otherwise
	 */
	
	public static PiecewiseAnalyticFunction buildPiecewiseAnalytic(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, double[] x, String[] functions) {
		return new PiecewiseAnalyticFunction(x,functions,name,scaleX, scaleY, offsetX, offsetY);
	}
	
	public static BasicAnalyticFunction buildAnalytic(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, String function) {
		function = SierraExpressionParser.convertMultiplication(function);
		// Separate local variables from the actual expression by returning an information object
		BasicFunctionInfo i = SierraExpressionParser.parseBasicSierraFunction(function);
		// Build and return a basic analytic function
		return buildBasicFunction(i.expression,i.localVars,name,scaleX, scaleY, offsetX, offsetY);		
	}
	
	public static Function buildPiecewiseFunction(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, double[] x, double[] y, String type) {
		if(PIECEWISE_CONSTANT.equals(type)){
			return buildPiecewiseConstant(name, scaleX, scaleY, offsetX, offsetY, x, y);
		}else if(PIECEWISE_LINEAR.equals(type)){
			return buildPiecewiseLinear(name, scaleX, scaleY, offsetX, offsetY, x, y);
		}else{
			return null;
		}
	}
			
	public static PiecewiseLinearFunction buildPiecewiseLinear(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, double[] x, double[] y) {
		return new PiecewiseLinearFunction(name,scaleX, scaleY, offsetX, offsetY, x, y);
	}
	
	public static PiecewiseConstantFunction buildPiecewiseConstant(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, double[] x, double[] y) {
		return new PiecewiseConstantFunction(name,scaleX, scaleY, offsetX, offsetY, x, y);
	}

	//TODO take a look at this and refactor a bit
	public static Function buildSalinas(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, String type, double[][] data) {
		// If 'type' indicates a linear function...
		if (type.equals("linear")) {
			// Build and return a piecewise linear function
			return new PiecewiseLinearFunction(name,scaleX, scaleY, offsetX, offsetY,data[0],data[1]);
		// If 'type' indicates a polynomial function...
		} else if (type.equals("polynomial")) {
			// Build and return a polynomial function
			return buildPolynomial(data,name,scaleX,scaleY,offsetX,offsetY);
		// If 'type' indicates a log-log function...
		} else if (type.equals("loglog")) {
			for (int i = 0; i < data.length; i++) {
				for (int c = 0; c < data[i].length; c++) {
					data[i][c] = Math.log(data[i][c]);
				}
			}
			return new PiecewiseLinearFunction(name,scaleX, scaleY, offsetX, offsetY,data[0],data[1]);
		// If 'type' indicates a random function...
		} else if (type.equals("random")) {
			// TODO Handle random functions in Salinas
		}
		return null;
	}
	
	public static BasicAnalyticFunction buildBasicFunction(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, Expression expression) {
		return buildBasicFunction(expression ,new HashMap<String,Expression>(),name,scaleX, scaleY, offsetX, offsetY);		
	}
	

	
	private static Function build(String name, double scaleX,
			double scaleY, double offsetX, double offsetY, Object...params) {
		// If only one parameter is received...
		if (params.length == 1) {
			// Let 'in' be the only parameter
			Object in = params[0];
			// If 'in' is a 2D String array...
			if (in instanceof String[][]) {
				// Then 'in' could potentially define a piecewise analytic function
				// Build and return a piecewise analytic function
//				return buildPiecewiseFunction((String[][])in,name,scaleX, scaleY, offsetX, offsetY);
			// If 'in' is a simple String...
			} else if (in instanceof String) {
				// Then 'in' could potentially define a basic analytic function
				// Start by detecting implicit multiplication
				in = SierraExpressionParser.convertMultiplication((String)in);
				// Separate local variables from the actual expression by returning an information object
				BasicFunctionInfo i = SierraExpressionParser.parseBasicSierraFunction((String)in);
				// Build and return a basic analytic function
				return buildBasicFunction(i.expression,i.localVars,name,scaleX, scaleY, offsetX, offsetY);
			// If 'in' is an Expression object...
			} else if (in instanceof Expression) {
				// Then 'in' potentially defines a basic analytic function
				// Build and return a basic analytic function
				return buildBasicFunction((Expression)in,new HashMap<String,Expression>(),name,scaleX, scaleY, offsetX, offsetY);
			// If 'in' is a 2D double array...
			} else if (in instanceof double[][]) {
				// We don't know whether this should be a linear or constant function, so we assume it's linear 
				double[][] arg = (double[][])in;
				// Build and return a piecewise linear function
				return buildBasicFunction((Expression)in,new HashMap<String,Expression>(),name,scaleX, scaleY, offsetX, offsetY);
			}
		// If two parameters are received...
		} else if (params.length == 2) {
			// Let 'in1' be the first and 'in2' be the second parameter
			Object in1 = params[0];
			Object in2 = params[1];
			// If 'in1' is a 2D double array...
			if (in1 instanceof double[][]) {
				// Let 'arg' be equal to 'in1'
				double[][] arg = (double[][])in1;
				// If 'in2' is a String...
				if (in2 instanceof String) {
					// Let flag be equal to 'in2'
					String flag = (String)in2;
					// We use the 'flag' parameter to determine whether a linear or constant function should be created
					if (flag.toLowerCase().equals("piecewise linear")) {
						// Build and return a piecewise linear function
						return new PiecewiseLinearFunction(name,scaleX, scaleY, offsetX, offsetY,arg[0],arg[1]);
					} else if (flag.toLowerCase().equals("piecewise constant")) {
						// Build and return a piecewise constant function
						return new PiecewiseConstantFunction(name,scaleX, scaleY, offsetX, offsetY,arg[0],arg[1]);
					}			
				}
			} else if (in1 instanceof String) {
				// Let 'type' be equal to 'in1'
				String type = ((String)in1).toLowerCase();
				// If 'in2' is a 2D double array
				if (in2 instanceof double[][]) {
					// Let 'data' be equal to 'in2'
					double[][] data = (double[][])in2;
					// If 'type' indicates a linear function...
					if (type.equals("linear")) {
						// Build and return a piecewise linear function
						return new PiecewiseLinearFunction(name,scaleX, scaleY, offsetX, offsetY,data[0],data[1]);
					// If 'type' indicates a polynomial function...
					} else if (type.equals("polynomial")) {
						// Build and return a polynomial function
						return buildPolynomial(data,name,scaleX,scaleY,offsetX,offsetY);
					// If 'type' indicates a log-log function...
					} else if (type.equals("loglog")) {
						for (int i = 0; i < data.length; i++) {
							for (int c = 0; c < data[i].length; c++) {
								data[i][c] = Math.log(data[i][c]);
							}
						}
						return new PiecewiseLinearFunction(name,scaleX, scaleY, offsetX, offsetY,data[0],data[1]);
					// If 'type' indicates a random function...
					} else if (type.equals("random")) {
						// TODO Handle random functions in Salinas
					}
				}
			}
		}
		// No function matched the input parameters. The null value is returned by default
		return null;
	}
	
	public static void main(String[] args) {
		System.out.println(buildPolynomial(new double[][] {{1,2,3},{9,8,7}},"",1.0,1.0,0.0,0.0).getValue(2));
	}
	
	/* PACKAGE-LEVEL UTILITY METHODS */
	
	/**
	 * Builds a polynomial function given tabular data in Salinas format.
	 * 	Given: buildPolynomial(new double[][] {{1,2,3},{9,8,7}},"",1.0,1.0,0.0,0.0)
	 * 	Returns: A function signifying "9x + 8x^2 + 7x^3"
	 */
	static BasicAnalyticFunction buildPolynomial(double[][] data,String name,
			double scaleX, double scaleY, double offsetX, double offsetY) {
		
		Expression polynomial = null;
		Operator add = new Operator("add");
		
		for (int i = 0; i < data[0].length; i++) {
			
			Operator pow = new Operator("pow");
			Operator mul = new Operator("mul");
			Operand var = new Operand("x");
			
			Operand power = new Operand(data[0][i]);
			Operand coeff = new Operand(data[1][i]);
			
			Expression term = new Expression(mul,coeff,new Expression(pow,var,power));
			
			if (polynomial == null) {
				polynomial = term;
			} else {
				polynomial = new Expression(add,term,polynomial);
			}
		}
		
		return buildBasicFunction(polynomial,new HashMap<String,Expression>(),name,scaleX, scaleY, offsetX, offsetY);
	}

	/**
	 * Builds a basic analytic function, given an expression and a dictionary of local variables.
	 */
	static BasicAnalyticFunction buildBasicFunction(Expression in,Map<String,Expression> localVariables, String name,
			double scaleX, double scaleY, double offsetX, double offsetY ) {
		try {
			BasicAnalyticFunction f = new BasicAnalyticFunction(name,scaleX,scaleY,offsetX,offsetY);
			f.exp = in;
			f.localVariables = localVariables;
			return f;
		} catch (RuntimeException e) {
			System.err.println("Warning: an error occurred while parsing the function.\n" + e);
			return null;
		}
	}
	
    static Double parseArg(String s) {
    	try {
            return Double.parseDouble(s.substring(s.indexOf("=")+1,s.length()).trim());
    	} catch (NumberFormatException e) {
            throw new RuntimeException("ERROR: unable to parse argument: " + s);
    	}
    }
    
    static String parseIdent(String s) {
    	return s.substring(0,s.indexOf("=")).trim();
    }
}


/** FOR STORING THE EXPRESSION AND LOCAL VARIABLES OF A BASIC FUNCTION
 * NOTE: PACKAGE PROTECTED 
 * */

final class BasicFunctionInfo {
	
	Expression expression;
	Map<String,Expression> localVars;
	
	public BasicFunctionInfo(Expression e, Map<String,Expression> l) {
		this.expression = e;
		this.localVars = l;
	}
}

