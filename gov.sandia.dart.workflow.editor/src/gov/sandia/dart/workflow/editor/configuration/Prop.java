/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
	
	private String name, value;
	private TYPE type;		

	public Prop(Property p){
		this(p.getName(), p.getType(), p.getValue());
	}
	
	// Set this property to a Standard Type
	Prop(String name, TYPE type) {
		this.name = name;
		this.type = type;			

		if(this.type == null){
			this.type = TYPE.DEFAULT;
		}
		
		this.value = "";
	}
	
	public Prop(String name, TYPE type, String value) {
		this(name, type);
		this.value = value;		
	}
	
	Prop(String name, String typeName) {
		this.name = name;	
		try {	
			this.type = TYPE.valueOf(typeName.toUpperCase());
		} catch (Exception ex) {
			if ("local_file".equalsIgnoreCase(typeName))
				this.type = TYPE.HOME_FILE;
			else
				System.out.println("Warning: unknown property type name " + typeName + " encountered.");
		}
		
		if(this.type == null){
			type = TYPE.DEFAULT;
		}
		
		this.value = "";
	}

	public Prop(String name, String type, String value) {
		this(name, type);
		this.value = value;		
	}

	public TYPE getType() {
		return type;
	}
	
	public String getTypeName(){		
		if(type == null){
			type = TYPE.DEFAULT;
		}
		
		return type.toString();
	}


	public String getName() {
		return name;
	}
	
	public String getValue() {
		return value;
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
