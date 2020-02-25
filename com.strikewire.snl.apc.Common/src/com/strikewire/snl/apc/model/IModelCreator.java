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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

public interface IModelCreator {
	
	public static final String EXTENSION_POINT_ID = "modelCreator";

	public void createModel(String modelName, IPath modelLocation, IFile simPath);

	public void addPart(String modelName, String string, String path, String path2);

}
