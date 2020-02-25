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

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

/**
 * @author mjgibso
 *
 */
public class EclipseFile extends EclipseResource implements ICommonFile
{
	public EclipseFile(IFile file)
	{
		super(file);
	}
	
	public IFile getFile()
	{
		return (IFile) getResource();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonFile#asIFile()
	 */
	@Override
	public IFile asIFile()
	{
		return getFile();
	}

	/**
	 * It is the caller's responsibility to close the stream when finished.
	 */

	@Override
	public InputStream getContents() throws CoreException {
		return getFile().getContents();
	}
}
