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

public class PropertyInfo implements IPropertyInfo {

	private final String name;
	private final String type;
	private final String defaultValue;
	private final boolean advanced;
	
	public PropertyInfo(String name, String type, String defaultValue, boolean advanced) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.advanced = advanced;
	}

	public PropertyInfo(String name, String type, String defaultValue) {
		this(name, type, defaultValue, false);
	}

	public PropertyInfo(String name, String type) {
		this(name, type, null);
	}

	public PropertyInfo(String name) {
		this(name, RuntimeData.DEFAULT_TYPE);
	}

	@Override
	public String getName() { return name; }
	@Override
	public String getType() { return type; }
	public String getDefaultValue() { return defaultValue; }
	public boolean isAdvanced() { return advanced; }
}
