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
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class NativeContainer extends NativeResource implements ICommonContainer
{
	/**
	 * @param file
	 */
	public NativeContainer(File file)
	{
		super(file);
	}
	
	@Override
	public boolean exists(IPath path)
	{
		return new File(getFile(), path.toOSString()).exists();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonContainer#existsIgnoreCase(java.lang.String)
	 */
	@Override
	public boolean existsIgnoreCase(String child) throws CoreException
	{
		String[] children = getFile().list();
		if(children!=null && children.length>0)
		{
			for(String childFileName : children)
			{
				if(StringUtils.equalsIgnoreCase(childFileName, child))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public ICommonFile getFile(IPath path)
	{
		return new NativeFile(new File(getFile(), path.toOSString()));
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonContainer#getContainer(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public ICommonContainer getContainer(IPath path)
	{
		return new NativeContainer(new File(getFile(), path.toOSString()));
	}
	
	@Override
	public boolean mkdir()
	{
		return getFile().mkdir();
	}
	
	@Override
	public boolean mkdirs()
	{
		return getFile().mkdirs();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonContainer#importFile(com.strikewire.snl.apc.resources.ICommonFile)
	 */
	@Override
	public void importFile(ICommonFile file) throws CoreException
	{
		try {			
			FileUtils.copyFileToDirectory(file.toFile(), getFile());
		} catch (IOException e) {
			throw CommonPlugin.getDefault().newError("Error copying file", e);
		}
	}
}
