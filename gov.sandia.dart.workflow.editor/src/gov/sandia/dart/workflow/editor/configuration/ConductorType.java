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

import java.util.List;

public class ConductorType {

	private String name;
	private List<Prop> properties;

	public ConductorType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	void setProperties(List<Prop> properties) {
		this.properties = properties;
	}

	public List<Prop> getProperties() {
		return properties;
	}
}
