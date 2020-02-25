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
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public abstract class NativeResource extends CommonResource
{
	protected final File _file;
	
	protected NativeResource(File file)
	{
		this._file = file;
	}
	
	public File getFile()
	{
		return this._file;
	}
	
	@Override
	public boolean exists()
	{
		return getFile().exists();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonResource#delete()
	 */
	@Override
	public void delete() throws CoreException
	{
		File file = getFile();
		
		if(file!=null && file.exists())
		{
			try {
				FileUtils.forceDelete(file);
			} catch (IOException e) {
				CommonPlugin.getDefault().throwError("Error deleting native resource", e);
			}
		}
	}
	
	@Override
	public ICommonContainer getParent()
	{
		return new NativeContainer(getFile().getParentFile());
	}
	
	@Override
	public File toFile()
	{
		return getFile();
	}
	
	@Override
	public int hashCode()
	{
		return getFile().hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof NativeResource)
		{
			return getFile().equals(((NativeResource) obj).getFile());
		} else {
			return false;
		}
	}
	
	@Override
	public String toString()
	{
		return getFile().toString();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonResource#getPath()
	 */
	@Override
	public IPath getPath()
	{
		return new Path(getFile().getAbsolutePath());
	}
}
