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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This really needs to be replaced with a proper tokenizer, but for now this will do.
 *
 */
public class Tokenizer {
    private final static String[][] REGEX_SEPARATORS = {
    	{"\\*","*"},    	
    	{"/","/"},
    	{"([^eE])\\+","$1 +"}, 
    	{"([^eE])-","$1 -"},
    	{"\\(","("},
    	{"\\)",")"},
    	{",",","},
    	{"\\^", "^"}
    };

	public List<String> tokenize(String in) {
    	in = in.replaceAll("\"","").replaceAll(";","");
        for (String[] reg_pattern : REGEX_SEPARATORS) {
            in = in.replaceAll(reg_pattern[0], " " + reg_pattern[1] + " ");
        }
        
        String[] tokens = in.trim().split(" ");
        List<String> tokenList = new CopyOnWriteArrayList<String>();
        for ( String token : tokens) {
            if (token.trim().length() != 0) {
                tokenList.add(token.trim());
            }
        }
        return tokenList;

	}
}
