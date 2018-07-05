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

public class Output {
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

}
