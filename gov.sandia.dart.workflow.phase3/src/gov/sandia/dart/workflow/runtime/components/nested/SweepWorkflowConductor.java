/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;

public class SweepWorkflowConductor implements WorkflowConductor {

	private static final String PARAMETER = "parameter";
	private static final String START = "start";
	private static final String STEP = "step";
	private static final String END = "end";
	private double start, step, end, value;
	private String parameter;
	
	public SweepWorkflowConductor() {
	}
	
	@Override
	public void setProperties(Map<String, String> properties) { 
		// TODO Error checking!
		parameter = stringProperty(properties, PARAMETER);
		start = doubleProperty(properties, START);
		step = doubleProperty(properties, STEP);
		end = doubleProperty(properties, END);
	}
	
	@Override
	public boolean hasNext() {
		return value <= end;
	}

	@SuppressWarnings("serial")
	@Override
	public Map<String, String> next() {
		// TODO Improve performance using a ThreadLocal to hold instances of this for reuse
		DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMaximumFractionDigits(340); //340 = DecimalFormat.DOUBLE_FRACTION_DIGITS

		Map<String,String> map = new HashMap<String, String>() {
			{put(parameter, df.format(value));}			
		};
		value += step;		
		return map;
	}

	@Override
	public Iterator<Map<String, String>> iterator() {
		value = start;
		return this;
	}

	protected int intProperty(Map<String, String> properties, String name) {
		String c = properties.get(name);
		try {
			if (c != null) {
				return Integer.parseInt(c);
			} else {
				throw new SAWWorkflowException(String.format("Missing value for parameter '%s'", name));
			}
		} catch (NumberFormatException e) {
			throw new SAWWorkflowException(String.format("Bad value for parameter '%s': %s", name, c), e);
		}
	}
	
	protected double doubleProperty(Map<String, String> properties, String name) {
		String c = properties.get(name);
		try {
			if (c != null) {
				return Double.parseDouble(c);
			} else {
				throw new SAWWorkflowException(String.format("Missing value for parameter '%s'", name));
			}
		} catch (NumberFormatException e) {
			throw new SAWWorkflowException(String.format("Bad value for parameter '%s': %s", name, c), e);
		}		
	}

	protected String stringProperty(Map<String, String> properties, String name) {
		String c = properties.get(name);
		if (c != null) {
			return c;
		} else {
			throw new SAWWorkflowException(String.format("Missing value for parameter '%s'", name));
		}
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(new PropertyInfo("parameter", "parameter"), new PropertyInfo("start"), new PropertyInfo("step"), new PropertyInfo("end"));
	}

}
