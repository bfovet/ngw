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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SierraExpressionParser {
    
    final static String[] INFIX_OPERATORS = {"*","/","+","-","^"};
    final static List<String> OPERATORS = new LinkedList<String>(Arrays.asList(
    		new String[] {"rand", "srand", "randomize", "time", "random", "exp", "ln", "log",
    				"log10", "pow", "sqrt", "erfc", "erf", "acos", "asin", "atan",
    				"atan2", "ceil", "cos", "cosh", "floor", "sin", "sinh", "tan", "tanh",
    				"abs", "fabs", "deg", "mod", "fmod", "ipart", "fpart", "max", "min",
    				"poltorectx", "poltorecty", "rad", "recttopola", "recttopolr",
    				"cos_ramp", "cosine_ramp", "sign", "unit_step", "weibull_pdf",
    				"normal_pdf", "gamma_pdf", "log_uniform_pdf", "uniform_pdf",
    				"exponential_pdf","add","sub","div","mul","*","/","+","-","^"}));
    final static Map<String,Integer> ARG_COUNT_MAP = new HashMap<String,Integer>();
    
    static {
    	ARG_COUNT_MAP.put("neg", 1);
    	ARG_COUNT_MAP.put("rand", 0);
    	ARG_COUNT_MAP.put("srand", 1);
    	ARG_COUNT_MAP.put("randomize", 0);
    	ARG_COUNT_MAP.put("time", 0);
    	ARG_COUNT_MAP.put("random", 0);
    	ARG_COUNT_MAP.put("exp", 1);
    	ARG_COUNT_MAP.put("ln", 1);
    	ARG_COUNT_MAP.put("log", 1);
    	ARG_COUNT_MAP.put("log10", 1);
    	ARG_COUNT_MAP.put("pow", 2);
    	ARG_COUNT_MAP.put("sqrt", 1);
    	ARG_COUNT_MAP.put("erfc", 1);
    	ARG_COUNT_MAP.put("erf", 1);
    	ARG_COUNT_MAP.put("acos", 1);
    	ARG_COUNT_MAP.put("asin", 1);
    	ARG_COUNT_MAP.put("atan", 1);
    	ARG_COUNT_MAP.put("atan2", 2);
    	ARG_COUNT_MAP.put("ceil", 1);
    	ARG_COUNT_MAP.put("cos", 1);
    	ARG_COUNT_MAP.put("cosh", 1);
    	ARG_COUNT_MAP.put("floor", 1);
    	ARG_COUNT_MAP.put("sin", 1);
    	ARG_COUNT_MAP.put("sinh", 1);
    	ARG_COUNT_MAP.put("tan", 1);
    	ARG_COUNT_MAP.put("tanh", 1);
    	ARG_COUNT_MAP.put("abs", 1);
    	ARG_COUNT_MAP.put("fabs", 1);
    	ARG_COUNT_MAP.put("deg", 1);
    	ARG_COUNT_MAP.put("mod", 2);
    	ARG_COUNT_MAP.put("fmod", 2);
    	ARG_COUNT_MAP.put("ipart", 1);
    	ARG_COUNT_MAP.put("fpart", 1);
    	ARG_COUNT_MAP.put("max", 2);
    	ARG_COUNT_MAP.put("min", 2);
    	ARG_COUNT_MAP.put("poltorectx", 2);
    	ARG_COUNT_MAP.put("poltorecty", 2);
    	ARG_COUNT_MAP.put("rad", 1);
    	ARG_COUNT_MAP.put("recttopola", 2);
    	ARG_COUNT_MAP.put("recttopolr", 2);
    	ARG_COUNT_MAP.put("cos_ramp", 3);
    	ARG_COUNT_MAP.put("cosine_ramp", 3);
    	ARG_COUNT_MAP.put("sign", 1);
    	ARG_COUNT_MAP.put("unit_step", 3);
    	ARG_COUNT_MAP.put("weibull_pdf", 3);
    	ARG_COUNT_MAP.put("normal_pdf", 3);
    	ARG_COUNT_MAP.put("gamma_pdf", 3);
    	ARG_COUNT_MAP.put("log_uniform_pdf", 3);
    	ARG_COUNT_MAP.put("uniform_pdf", 2);
    	ARG_COUNT_MAP.put("exponential_pdf", 2);
    	ARG_COUNT_MAP.put("add",2);
    	ARG_COUNT_MAP.put("sub",2);
    	ARG_COUNT_MAP.put("div",2);
    	ARG_COUNT_MAP.put("mul",2);
    }
        
    /**
     * Tests if an input String denotes a valid Sierra expression. This is the only method
     * on the interface of this class. To actually construct a Function, see the methods in
     * FunctionBuilder.
     */
    public static String isValid(String raw) {
    	raw = raw.replaceAll("\"","");
    	raw = convertMultiplication(raw);
    	String[] phrases = raw.split(";");
    	for (int i = 0; i < phrases.length-1; i++) {
    		if(isValidPhrase(phrases[i]).equals("")) {
    			return "";
    		}
    	}
    	return isValidExpression(phrases[phrases.length-1]);
    }
    
    
    /* STATIC NON-INTERFACE METHODS BELOW THIS LINE */
    
    
    /**
     * Reads a raw String and returns the information necessary to create a new basic 
     * analytic function.
     * 	Given: parseBasicSierraFunction("m=1; b=2; m*x+b ")
     * 	Returns: a BasicFunctionInfo containing:
	 *		expression, signifying the expression add(mul(m,x),b)
	 * 		localVariables, { m:1 , b:2 }
     */
    static BasicFunctionInfo parseBasicSierraFunction(String raw) {
    	/* Remove all unnecessary quotations */
    	raw = raw.replaceAll("\"","");
    	/* Create a new array to house a Map of local variables, as well as the 
         * expression tree itself */
        Map<String,Expression> localVariables = new HashMap<String,Expression>();
        Expression exp = null;
        /* Split the raw string into n phrases */
    	String[] phrases = raw.split(";");
    	/* The first n-1 phrases define local variables... */
    	for (int i = 0; i < phrases.length-1; i++) {
            String phrase = phrases[i];
            String[] pair = phrase.trim().split("\\=");
            /* Add the local variable definition to the dictionary */
            localVariables.put(pair[0].trim(),buildExpressionTree(pair[1]));
        }
    	/* Build the main expression tree */
    	exp = buildExpressionTree(phrases[phrases.length-1]);
    	return new BasicFunctionInfo(exp,localVariables);
    }
    
    /**
     * Converts any instances of "implicit multiplication" (e.g. as in 4x and
     * 2cos(3x)) into explicitly defined multiplication (4*x and 2*cos(3*x))
     * 
     * This step allows function parsers to understand expressions that involve
     * implicit multiplication.
     */
    static String convertMultiplication(String raw) {
    	// not sure if this is needed.  Sierra syntax requires explicit *.
		Pattern quickMult = Pattern.compile("[0-9][0-9]*[a-dg-z][a-df-z	]*");
		Matcher m = quickMult.matcher(raw);
		while (m.find()) {
			String original = m.group();
			for (int index = 0; index < original.length(); index++) {
				if (!Character.isDigit(original.charAt(index))) {
					raw = raw.replaceFirst(original, original.substring(0,index) + " * " + original.substring(index));
				} else {
					continue;
				}
			}
		}
		return raw;
    }
    
    static Expression buildExpressionTree(String exp) {
    	String complaint = isValidExpression(exp);
        if (complaint.equals("")) {
            List<String> tokens = toPrefixNotation((new Tokenizer().tokenize(exp)));
            Type first = readToken(tokens.get(0));
            if (first == Type.PRIMITIVE || first == Type.VARIABLE) {
                return new Expression(new Operator("ident"),new LinkedList<Operand>
                        (Arrays.asList(new Operand[] {new Operand(tokens.get(0))})));
            }
            return evaluateExpression(tokens);
        } else {
            throw new RuntimeException(complaint);
        }
    }
    
    /**
     * Checks whether or not a raw input string, such as
     * sin(x/2) * (sqrt(y) + z - 3), is a valid, parsable expression. 
     */
    private static String isValidExpression(String raw) {
        List<String> tokens = SierraExpressionParser.toPrefixNotation((new Tokenizer().tokenize(raw)));
        if (tokens == null) {
            return "empty expression";
        }
        if(!validateParentheses(tokens)) {
            return "mismatched parentheses";
        }
        return isValid(tokens);
    }
    
    private static String isValidPhrase(String def) {
    	int equalsIndex = def.indexOf("=");
    	if( 0 < equalsIndex && equalsIndex < def.length()-1) {
    		return "";
    	} else {
    		return "malformed local definition: " + def;
    	}
    }
    
    private static Expression evaluateExpression(List<String> tokens) {
        String first = tokens.remove(0);
        Type t = readToken(first);
        switch(t) {
            case PRIMITIVE:
            case VARIABLE:
                return new Expression(new Operand(first));
            case OPERATOR:
                List<Operand> operands = null;
                try {
                    operands = extractOperands(tokens,ARG_COUNT_MAP.get(first));
                } catch (RuntimeException e) {
                    System.out.println(e + " " + first);
                    throw e;
                }
                return new Expression(new Operator(first),operands);
            default:
                throw new RuntimeException("ERROR: malformed expression.");
        }
    }
    
    private static List<String> toPrefixNotation(List<String> tokens, String oper) {
    	int index;
    	for (index = 0; index < tokens.size(); index++) {
            String token = tokens.get(index);
                if (token.equals(oper)) {
                	boolean b = false;
                	if (token.equals("-")) {
                		
                		Type t = null;
                		if (index > 0) {
                			String prev = tokens.get(index-1);
                			t = readToken(prev);
                		}
                		if (index == 0 || t==Type.COMMA || t==Type.OPEN_PAREN || t==Type.OPERATOR) {
    	                    int next = findNextArg(tokens,index);
    	                    if (next == -1) {
    	                        return null;
    	                    }
    	                    tokens.add(next,")");
    	                    tokens.set(index,"(");
    	                    tokens.add(index,"neg");
    	                    b = true;
                		}
                		
                	} 
                	if(!b) {
                		
	                    int next = findNextArg(tokens,index);
	                    if (next == -1) {
	                        return null;
	                    }
	                    tokens.add(next,")");
	                    tokens.set(index,",");
	                    int i = findPrevArg(tokens,index);
	                    tokens.add(i,"(");
	                    if (oper.equals("+")) {
	                    	tokens.add(i,"add");
	                    } else if (oper.equals("-")) {
	                    	tokens.add(i,"sub");
	                    } else if (oper.equals("*")) {
	                    	tokens.add(i,"mul");
	                    } else if (oper.equals("/")) {
	                    	tokens.add(i,"div");
	                    } else if (oper.equals("^")) {
	                    	tokens.add(i,"pow");
	                    }                   
	                    index--;
                	}
            	}
            
    	}
    	return tokens;
    }
    
    private static List<String> toPrefixNotation(List<String> tokens) {
    	toPrefixNotation(tokens,"^");
    	toPrefixNotation(tokens,"*");
    	toPrefixNotation(tokens,"/");
    	toPrefixNotation(tokens,"+");
    	toPrefixNotation(tokens,"-");
    	return tokens;
    }
       
    private static List<Operand> extractOperands(List<String> tokens,Integer count) {
    	if (count == null) {
    		throw new RuntimeException("ERROR: unrecognized operator");
    	}
    	// System.out.println("Extracting " + count + " operands out of:\n\t" + tokens);
    	List<Operand> operands = new ArrayList<Operand>();
    	String first;
    	Type t;
    	int commaCount = count-1;
    	while(count > 0) {
            first = tokens.get(0);
            t = readToken(first);
            switch(t) {
                case OPEN_PAREN:
                    tokens.remove(0);
                    break;
                case CLOSE_PAREN:
                    tokens.remove(0);
                    break;
                case COMMA:
                    commaCount--;
                    tokens.remove(0);
                    break;
                case VARIABLE:
                case PRIMITIVE:
                    tokens.remove(0);
                    operands.add(new Operand(first));
                    count--;
                    break;
                case OPERATOR:
                    operands.add(evaluateExpression(tokens));
                    count--;
                    break;
            }
    	}

    	// System.out.println("\ncommas: " + commaCount);
    	if (commaCount != 0) {
            throw new RuntimeException("ERROR: could not parse arguments for operator ");
    	}
    	return operands;
    }
    
    private static Type readToken(String token) {
    	if (token.equals("(")) {
            return Type.OPEN_PAREN;
    	}
    	if (token.equals(")")) {
            return Type.CLOSE_PAREN;
    	}
    	if (token.equals(",")) {
            return Type.COMMA;
    	}
        for (String oper : OPERATORS) {
            if (token.equals(oper)) {
                return Type.OPERATOR;
            }
        }
        try {
            Double.parseDouble(token);
            return Type.PRIMITIVE;
        } catch (NumberFormatException e) {
            return Type.VARIABLE;
        }
    } 
        
    private static int findNextArg(List<String> tokens, int startIndex) {
    	int lastIndex = startIndex;
    	int parens = 0;
    	boolean foundArg = false;
    	do {
            if (tokens.size() <= lastIndex || parens < 0) {
                return -1;
            }
            Type t = readToken(tokens.get(lastIndex));
            switch(t) {
                case PRIMITIVE:
                case VARIABLE:
                    foundArg = true;
                    break;
                case OPEN_PAREN:
                    parens++;
                    break;
                case CLOSE_PAREN:
                    parens--;
                    break;
                default:
                    break;
            }
            lastIndex ++;
    	} while (parens != 0 || !foundArg);
    	return lastIndex;
    	
    }

    private static int findPrevArg(List<String> tokens, int startIndex) {
    	int prevIndex = startIndex-1;
    	int parens = 0;
    	if (prevIndex > -1) {
	        switch(readToken(tokens.get(prevIndex))) {
	            case PRIMITIVE:
	            case VARIABLE:
	                return prevIndex;
	            case CLOSE_PAREN:
	                parens++;
	                prevIndex--;
	                break;
	        }
    	}
    	
    	while(prevIndex > 0) {
            Type t = readToken(tokens.get(prevIndex));
            switch(t) {
                case CLOSE_PAREN:
                    parens++;
                    break;
                case OPEN_PAREN:
                    parens--;
                    break;
            }
            if (parens == 0) {
            	int prevprevIndex = prevIndex - 1;
            	if (prevprevIndex < 0) {
            		return 0;
            	} else {
            		Type prevprevT = readToken(tokens.get(prevprevIndex));
            		if (prevprevT == Type.OPERATOR) {
            			return prevIndex-1;
            		} else if (prevprevT == Type.OPEN_PAREN) {
            			return prevIndex;
            		}
            	}
            }
            prevIndex--;
    	}
    	return 0;
    }
        
    /**
     * Checks whether the list of tokens provided is a valid, parsable
     * expression.
     * A list of tokens is a valid expression if and only if its operator will
     * receive an valid list of arguments of proper length.
     */
    private static String isValid(List<String> tokens) {
        if(tokens.isEmpty()) {
            return "empty expression detected while parsing.";
        } else {
            String first = tokens.get(0);
            Type type = readToken(first);
            switch(type) {
                case PRIMITIVE:
                case VARIABLE:
                    if (tokens.size() == 1) {
                        return "";
                    } else {
                        return "encountered missing token while parsing";
                    }
                case COMMA:
                    return "unexpected ',' at beginning of expression.";
                case CLOSE_PAREN:
                    return " unexpected ')' at beginning of expression.";
                case OPEN_PAREN:
                    int closingIndex = findClosingParenthesis(tokens,0);
                    if (closingIndex != tokens.size()-1) {
                    	if(tokens.size() > 2) {
	                        return "extra tokens detected: " + 
	                                tokens;
                    	} else {
                    		return "empty expression detected while parsing";
                    	}
                    } else {
                        tokens.remove(tokens.size()-1);
                        tokens.remove(0);
                        return isValid(tokens);
                    }
                case OPERATOR:
                    String closingParen = tokens.remove(tokens.size()-1);
                    tokens.remove(0);
                    String openingParen = tokens.remove(0);
                    if (!closingParen.equals(")")) {
                        return "mismatched parentheses,"
                                + " expected ')' instead of: " + closingParen;
                    } else if (!openingParen.equals("(")) {
                        return "mismatched parentheses,"
                                + " expected '(' instead of: " + openingParen;
                    }
                    return areValid(tokens,ARG_COUNT_MAP.get(first));
            }
        }
        throw new UnsupportedOperationException();
    }
    
    /**
     * Checks whether a list of tokens can be collected into a valid set of
     * operands.
     * A list of tokens is a valid list of operands if and only if it contains
     * the correct number of comma-separated expressions.
     */
    private static String areValid(List<String> tokens, int args) {
        boolean valid = true;
        while(!tokens.isEmpty() && args > 0) {
            String first = tokens.get(0);
            Type type = readToken(first);
            int closeParen;
            int openParen = 0;
            switch(type) {
                case OPERATOR:
                    openParen = 1;
                case OPEN_PAREN:
                    closeParen = findClosingParenthesis(tokens,openParen);
                    if (closeParen == -1) {
                        return "malformed expression";
                    } else {
                        List<String> sublist = new ArrayList<String>();
                        for(int i = 0; i < closeParen+1; i++) {
                            sublist.add(tokens.remove(0));
                        }
                        valid = valid && isValid(sublist).equals("");
                        if (!tokens.isEmpty()) {
                            if(!tokens.remove(0).equals(",")) {
                                return "expected operator at: " + tokens;
                            }
                        }
                        break;
                    }
                case VARIABLE:
                case PRIMITIVE:
                    String t = tokens.remove(0);
                    if (!tokens.isEmpty()) {
                        if(!tokens.remove(0).equals(",")) {
                            return "expected ',' after " + t;
                        }
                    }
                    break;
                case COMMA:
                    return "unexpected ',' at: " + tokens;
                case CLOSE_PAREN:
                    return "unexpected ')' at: " + tokens;
            }
            args--;
        }
        if (args == 0 && !tokens.isEmpty()) {
            return "leftover tokens: " + tokens;
        } else if (tokens.isEmpty() && args != 0) {
            return "you require more args (" + args + ")";
        }
        return "";
    }
    
    private static boolean validateParentheses(List<String> tokens){
        int open = 0,closed = 0;
        for(String token : tokens) {
            Type t = readToken(token);
            if (t == Type.CLOSE_PAREN) {
                closed ++;
            } else if (t == Type.OPEN_PAREN) {
                open++;
            }
        }
        return closed == open;
    }
    
    private static int findClosingParenthesis(List<String> tokens, int startIndex) {
        if (!tokens.get(startIndex).equals("(")) {
            return -1;
        } else {
            int closingParen = findNextArg(tokens,startIndex);
            if (closingParen == -1) {
                return -1;
            }
            closingParen--;
            if (!tokens.get(closingParen).equals(")")) {
                return -1;
            }
            return closingParen;
        }
        
    }
    
}

enum Type {
    
    OPERATOR,
    PRIMITIVE,
    VARIABLE,
    OPEN_PAREN,
    CLOSE_PAREN,
    COMMA
    
}
