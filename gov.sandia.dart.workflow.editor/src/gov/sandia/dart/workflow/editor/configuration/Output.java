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

public class Output {	
	public enum TYPE {
		DEFAULT, OUTPUT_FILE, EXODUS_FILE;

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
	
	public static final String DEFAULT_NAME = "result";
	public static final String STDOUT = "stdout";
	public static final String STDERR = "stderr";
	
	private String type;
	private String name;
	private String filename = "";

	public Output(String name, String type, String filename) {
		this.name = name;
		this.type = type;
		if (filename != null)
			this.filename = filename;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}
	
	public String getFilename() {
		return filename;
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
