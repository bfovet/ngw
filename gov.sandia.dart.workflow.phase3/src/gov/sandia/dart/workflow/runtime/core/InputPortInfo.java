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

public class InputPortInfo implements IPropertyInfo {

	private String name;
	private String type;
	
	public InputPortInfo(String name, String type) {
		this.name = name;
		this.type = type;
	}
	public InputPortInfo(String name) {
		this.name = name;
		this.type = RuntimeData.DEFAULT_TYPE;
	}

	public String getName() { return name; }
	public String getType() { return type; }

}
