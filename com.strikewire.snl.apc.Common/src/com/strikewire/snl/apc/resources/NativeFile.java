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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.ResourceUtils;

/**
 * @author mjgibso
 *
 */
public class NativeFile extends NativeResource implements ICommonFile
{
	public NativeFile(File file)
	{
		super(file);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonFile#asIFile()
	 */
	@Override
	public IFile asIFile() throws CoreException
	{
		try {
			File file = getFile().getAbsoluteFile().getCanonicalFile();
			return ResourceUtils.getFileForLocation(file.getAbsolutePath(), true);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, CommonPlugin.ID, "Error canonicalizing path", e));
		}
	}

	/**
	 * It is the caller's responsibility to close the stream when finished.
	 */
	@Override
	public InputStream getContents() throws CoreException {
		try {
			return new FileInputStream(getFile());
		} catch (FileNotFoundException e) {
			throw new CoreException(CommonPlugin.getDefault().newErrorStatus("Error opening file", e));
		}
	}
}
