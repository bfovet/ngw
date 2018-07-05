/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Nov 26, 2013 at 5:16:29 AM
 */
package com.strikewire.snl.apc.text;

import org.eclipse.core.resources.IFile;

/**
 * @author mjgibso
 *
 */
public class FileDocumentManager extends CachingDocumentManager
{
	private final IFile _file;
	
	/**
	 * 
	 */
	public FileDocumentManager(IFile file)
	{
		this._file = file;
	}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.text.CachingDocumentManager#getFile()
	 */
	@Override
	protected IFile getFile()
	{
		return this._file;
	}

}
