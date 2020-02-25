/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public final class Datum {
	private final Object value;
	private final Class<?> type;
	private String outputType;
	
	public Datum(String outputType, Object value, Class<?> type) {
		this.value = value;
		this.type = type;
		this.outputType = outputType;
	}
	
	public Object getValue() {
		return value;
	}

	public Class<?> getType() {
		return type;
	}
	
	public String getOutputType() {
		return outputType;
	}
	 
	// TODO One could imagine a vast and sophisticated conversion broker here.

	public Object getAs(Class<?> type2) {
		boolean isPath =
				"home_file".equals(outputType) ||
				"local_file".equals(outputType) ||
				"exodus_file".equals(outputType) ||
				"output_file".equals(outputType);
					
		if (type2.isAssignableFrom(type))
			return value;
		
		if (type.isArray() && type2.isAssignableFrom(type.getComponentType()) && Array.getLength(value) == 1)
			return Array.get(value, 0);
				
		if (isPath) {
			try {
				if (type2 == Reader.class)
					return new FileReader(asString());
				else if (type2 == InputStream.class)
					return new FileInputStream(asString());
				else if (type2 == double[].class) {
					String data = FileUtils.readFileToString(new File(asString()));						
					return parseDoubleArray(data);
				} else if (type2 == int[].class) {
					String data = FileUtils.readFileToString(new File(asString()));						
					return parseIntArray(data);
				} 
			} catch (IOException e) {
				throw new SAWWorkflowException(String.format("Error reading file %s", asString()), e);
			}
		}
		

		if (type2 == byte[].class) {
			return asString().getBytes();	
			
		} else if (type2 == double[].class) {
			if (type == java.lang.Double[].class)
				return ArrayUtils.toPrimitive((java.lang.Double[]) value);
			else if (type == String[].class)
				return parseDoubleArray(StringUtils.join((String[])value, " ")); 
			else
				return parseDoubleArray(asString());
		} else if (type2 == int[].class) {
			if (type == java.lang.Integer[].class)
				return ArrayUtils.toPrimitive((java.lang.Integer[]) value);
			else if (type == String[].class)
				return parseIntArray(StringUtils.join((String[])value, " ")); 
			else
				return parseIntArray(asString());							
		} else if (type2 == String.class) {
			return asString();	

		} else if (type2 == String[].class) {
			return asString().split("\\r?\\n");

		} else if (type2 == Reader.class) {
			return new StringReader(asString());
			
		} else if (type2 == InputStream.class) {
			return new ByteArrayInputStream(asString().getBytes());
		
		} else if (type2 == Map.class) {
			String value = asString();
			// Assume JSON format for the map.
			try {
				if (new File(value).exists()) {
					value = FileUtils.readFileToString(new File(value), Charset.defaultCharset());
				}

				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(value, Map.class);
			} catch (IOException e) {
				throw new SAWWorkflowException(String.format("Error translating JSON: %s", value), e);
			}
			
		} else {
			return value;
		}					
	}

	private Object parseDoubleArray(String data) {
		List<Double> doubles = new ArrayList<>();
		String[] split = data.split("\\s+");
		for (String token: split) {
			try {
				doubles.add(Double.parseDouble(token));
			} catch (NumberFormatException e) {
				// Not sure what the best way to behave here is,
				// but let's try just skipping bad tokens for now.
			}
		}
		double[] result = new double[doubles.size()];
		for (int i=0; i<result.length; ++i) {
			result[i] = doubles.get(i);
		}
		return result;
	}
	
	private Object parseIntArray(String data) {
		List<Integer> ints = new ArrayList<>();
		String[] split = data.split("\\s+");
		for (String token: split) {
			try {
				ints.add(Integer.parseInt(token));
			} catch (NumberFormatException e) {
				// Not sure what the best way to behave here is,
				// but let's try just skipping bad tokens for now.
			}
		}
		int[] result = new int[ints.size()];
		for (int i=0; i<result.length; ++i) {
			result[i] = ints.get(i);
		}
		return result;
	}

	private String asString() {
		if (type == String.class) {
			return (String) value;
		} else if (type == byte[].class) {
			return new String((byte[]) value).trim();
		} else if (type == char[].class) {
			return new String((char[]) value).trim();
		} else if (value instanceof int[]) {
			return StringUtils.join((int[]) value, '\n');
		} else if (value instanceof double[]) {
			return StringUtils.join((int[]) value, '\n');
		} else if (value instanceof short[]) {
			return StringUtils.join((short[]) value, '\n');
		} else if (value instanceof float[]) {
			return StringUtils.join((float[]) value, '\n');
		} else if (value instanceof Boolean[]) {
			// TODO Confirm that this is necessary?
			StringBuilder stringOfBools = new StringBuilder();
			for (Boolean boolValue : (Boolean[]) value) {
				stringOfBools.append(String.valueOf(boolValue));
				stringOfBools.append("\n");
			}
			return stringOfBools.toString();
		} else if (value.getClass().isArray() && !value.getClass().getComponentType().isPrimitive()) {
			return trimAndJoin((Object[]) value, '\n');
		} else if (value instanceof Iterable<?>) {	
			return trimAndJoin((Iterable<?>) value, '\n');
		} else if (value instanceof Map<?,?>) {
			// Use JSON to convert the Map to a String.
			try {
				ObjectMapper mapper = new ObjectMapper();
	            return mapper.writeValueAsString(value);
	        } catch (JsonProcessingException e) {
	            e.printStackTrace();
	        }
			return "";
		} else {
			return String.valueOf(value).trim();
		}
	}

	private String trimAndJoin(Iterable<?> value2, char c) {
		StringBuilder builder = new StringBuilder();
		for (Object value: value2) {
			builder.append(value.toString().trim());
			builder.append(c);
		}		
		return builder.toString().trim();
	}

	private String trimAndJoin(Object[] value2, char c) {
		StringBuilder builder = new StringBuilder();
		for (Object value: value2) {
			builder.append(value.toString().trim());
			builder.append(c);
		}		
		return builder.toString().trim();
	}
}
