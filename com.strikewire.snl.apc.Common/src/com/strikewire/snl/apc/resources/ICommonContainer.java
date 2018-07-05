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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * @author mjgibso
 *
 */
public interface ICommonContainer extends ICommonResource
{
	/**
	 * @see IContainer#exists(org.eclipse.core.runtime.IPath)
	 */
	public boolean exists(IPath path);
	
	public boolean existsIgnoreCase(String child) throws CoreException;
	
	/**
	 * @see IContainer#getFile(IPath)
	 */
	public ICommonFile getFile(IPath path);
	
	public ICommonContainer getContainer(IPath path);
	
	/**
	 * @see File#mkdirs()
	 */
	public boolean mkdirs();
	
	/**
	 * @see mkdir();
	 */
	public boolean mkdir();
	
	// TODO need a getChildren method
	
	// TODO create more general copy/import methods on ICommonResource methods.  Adding just this specific one for now
	// because I don't have time to implement a larger more general OO solution.
	public void importFile(ICommonFile file) throws CoreException;
}
