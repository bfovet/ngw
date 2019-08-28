package gov.sandia.dart.workflow.runtime.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * The representation of a workflow parameter at runtime. Some properties -- name, type, global, declared -- are immutable.
 * TODO Error checking on all constructors
 */

public class RuntimeParameter {
	public static String NAME = "_name";
	public static String VALUE = "_value";
	public static String TYPE = "_type";
	public static String GLOBAL = "_global";
	public static String DECLARED = "_declared";
	public static Set<String> STANDARD_PROPERTIES = new HashSet<>(Arrays.asList(NAME, VALUE, TYPE, GLOBAL, DECLARED));
	
	private Map<String, Object> metadata = new HashMap<>();
	
	public RuntimeParameter(String name, Object value, String type, boolean global, boolean declared) {
		metadata.put(NAME, name);
		metadata.put(VALUE, value);
		metadata.put(TYPE, type);
		metadata.put(GLOBAL, global);
		metadata.put(DECLARED, declared);
	}
	
	public RuntimeParameter(RuntimeParameter other) {
		metadata.putAll(other.metadata);
	}
	
	RuntimeParameter(Map<String, Object> metadata2) {
		metadata.putAll(metadata2);
	}

	public boolean isGlobal() {
		return "true".equals(String.valueOf(metadata.get(GLOBAL)));
	}
	
	public boolean isDeclared() {
		return "true".equals(String.valueOf(metadata.get(DECLARED)));
	}
	
	public Object getValue() {
		return metadata.get(VALUE);
	}
	
	public String getType() {
		return (String) metadata.get(TYPE);
	}
	
	public String getName() {
		return (String) metadata.get(NAME);
	}
	
	public void setValue(Object value) {
		metadata.put(VALUE, value);
	}
	
	public void setMetadataValue(String name, Object value) {
		if (StringUtils.isBlank(name) || STANDARD_PROPERTIES.contains(name))
			throw new SAWWorkflowException("Can't set '" + name + "' on parameter " + getName());
		metadata.put(name, value);
	}
	
	public Object getMetadataValue(String name) {
		return metadata.get(name);
	}
	
	public Map<String, Object> getMetadata() {
		return Collections.unmodifiableMap(metadata);
	}
	
}
