/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.model;

public class PartDescription {
	
	String name;
	String type;
	String resourcePath;
	String meshPath;
	
	public PartDescription(String name, String type, String resourcePath, String meshPath)
	{
		this.name = name;
		this.type = type;
		this.resourcePath = resourcePath;
		this.meshPath = meshPath;
	}
	
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getResourcePath() {
		return resourcePath;
	}
	public String getMeshPath() {
		return meshPath;
	}

}
