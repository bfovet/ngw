/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.resources;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;


/**
 * @author mjgibso
 *
 */
public interface ICommonResource
{
	/**
	 * @see IResource#exists()
	 */
	public boolean exists();
	
	public void delete() throws CoreException;
	
	/**
	 * @see IResource#getParent();
	 */
	public ICommonContainer getParent();
	
	public File toFile();
	
	public IPath getPath();
	
	public String getName();
}
