/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.configuration;

import java.util.ArrayList;
import java.util.List;

import gov.sandia.dart.workflow.domain.Property;

public class Prop {
	public enum TYPE {
		TEXT, MULTITEXT, HOME_FILE, BOOLEAN, DEFAULT, INTEGER, DECIMAL, PARAMETER;

		public static TYPE safeValueOf(String text) {
			try {
				return valueOf(text.toUpperCase());
			} catch (Exception e) {
				return DEFAULT;
			}
		}
		
		// TODO Cache?
		public static String[] labels() {
			TYPE[] values = values();
			String[] labels = new String[values.length];
			for (int i = 0; i<values.length; ++i) {
				labels[i] = values[i].toString();
			}
			return labels;				
		}
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	private final String name, value;
	private final TYPE type;		
	private boolean advanced;

	public Prop(Property p){
		this(p.getName(), p.getType(), p.getValue(), p.isAdvanced());
	}
	
	// Set this property to a Standard Type
	Prop(String name, TYPE type) {
		this (name, type, "", false);
	}
	
	public Prop(String name, TYPE type, String value) {
		this(name, type, value, false);
	}
	
	public Prop(String name, TYPE type, String value, boolean advanced) {
		this.name = name;
		this.type =  type == null ? TYPE.DEFAULT : type;
		this.value = value;		
		this.advanced = advanced;
	}
	
	public Prop(String name, String typeName, String value) {
		this(name, typeName, value, false);
	}
	
	public Prop(String name, String typeName, String value, boolean advanced) {
		this.name = name;	
		TYPE typeValue = null;
		try {	
			 typeValue = TYPE.valueOf(typeName.toUpperCase());

		} catch (Exception ex) {
			if ("local_file".equalsIgnoreCase(typeName))
				typeValue = TYPE.HOME_FILE;
		}
		this.type =  typeValue == null ? TYPE.DEFAULT : typeValue;

		this.value = value;
		this.advanced = advanced;
	}

	public Prop(String name, String type) {
		this(name, type, "", false);
	}

	public TYPE getType() {
		return type;
	}
	
	public String getTypeName(){				
		return type.toString();
	}


	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isAdvanced() {
		return advanced;
	}

	public static String[] availableTypes() {
		TYPE[] baseTypes = TYPE.values();
		List<String> allTypes = new ArrayList<>();
		for( TYPE type : baseTypes){
			allTypes.add(type.toString());
		}
		return allTypes.toArray(new String[allTypes.size()]);
	}
}
