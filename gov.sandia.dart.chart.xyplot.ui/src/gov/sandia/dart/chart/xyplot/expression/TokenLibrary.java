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
import java.util.concurrent.ConcurrentHashMap;

public final class TokenLibrary {

	final static private Map<String,Double> CONSTANTS = new ConcurrentHashMap<String,Double>();
	
	/**
	 * Add any global constants to this dictionary.
	 */
	static {
		CONSTANTS.put("pi",Math.PI);
		CONSTANTS.put("PI",Math.PI);
		CONSTANTS.put("e",Math.E);
		CONSTANTS.put("E",Math.E);
		CONSTANTS.put("inf",Double.MAX_VALUE/2);
	}
	
	static public void defineConstant(String c, Double value) {
		if (!isDefined(c)) {
			CONSTANTS.put(c, value);
		} else {
			throw new RuntimeException("ERROR: variable " + c + " already exists.");
		}
	}

	static public boolean isDefined(String s) {
		return CONSTANTS.containsKey(s);
	}
	
	static public Double lookup(String s) {
		Double val = CONSTANTS.get(s);
		if (val != null) {
			return val;
		} else {
			return null;
		}
	}
	
}
