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

public class Input {
	public enum TYPE {
		DEFAULT;

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
	
	public static final String STDIN = "stdin";
	private String name;
	private String type;

	public Input(String name, String type) {
		this.name = name;
		this.type = type;

	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
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
