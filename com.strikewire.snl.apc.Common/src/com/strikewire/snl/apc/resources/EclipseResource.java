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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public abstract class EclipseResource extends CommonResource
{
	protected final IResource _resource;
	
	protected EclipseResource(IResource resource)
	{
		this._resource = resource;
	}
	
	public IResource getResource()
	{
		return this._resource;
	}
	
	@Override
	public boolean exists()
	{
		return getResource().exists();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonResource#delete()
	 */
	@Override
	public void delete() throws CoreException
	{
		IResource res = getResource();
		
		if(res==null || !res.exists())
		{
			return;
		}
		
		if(res.isLinked())
		{
			IPath resLoc = res.getLocation();
			if(resLoc != null)
			{
				File realFile = resLoc.toFile();
				if(realFile!=null && realFile.exists())
				{
					try {
						FileUtils.forceDelete(realFile);
					} catch (IOException e) {
						CommonPlugin.getDefault().throwError("Error deleting underlying file for linked resource", e);
					}
				}
			}
		}
		
		res.delete(false, new NullProgressMonitor());
	}
	
	@Override
	public ICommonContainer getParent()
	{
		return new EclipseContainer(getResource().getParent());
	}
	
	@Override
	public File toFile()
	{
		return new File(getResource().getLocationURI());
	}
	
	@Override
	public int hashCode()
	{
		return getResource().hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof EclipseResource)
		{
			return getResource().equals(((EclipseResource) obj).getResource());
		} else {
			return false;
		}
	}
	
	@Override
	public String toString()
	{
		return getResource().toString();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonResource#getPath()
	 */
	@Override
	public IPath getPath()
	{
		return getResource().getFullPath();
	}
}
