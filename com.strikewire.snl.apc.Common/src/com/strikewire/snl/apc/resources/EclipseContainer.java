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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.ResourceUtils;



/**
 * @author mjgibso
 *
 */
public class EclipseContainer extends EclipseResource implements ICommonContainer
{
	public EclipseContainer(IContainer container)
	{
		super(container);
	}
	
	@Override
	public boolean exists(IPath path)
	{
		return getContainer().exists(path);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonContainer#existsIgnoreCase(java.lang.String)
	 */
	@Override
	public boolean existsIgnoreCase(String child) throws CoreException
	{
		if(StringUtils.isBlank(child))
		{
			return false;
		}
		
		for(IResource member : getContainer().members())
		{
			if(member.getName().equalsIgnoreCase(child))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public IContainer getContainer()
	{
		return (IContainer) super.getResource();
	}
	
	@Override
	public ICommonFile getFile(IPath path)
	{
		return new EclipseFile(getContainer().getFile(path));
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonContainer#getContainer(org.eclipse.core.runtime.IPath)
	 */
	@Override
	public ICommonContainer getContainer(IPath path)
	{
		return new EclipseContainer(getContainer().getFolder(path));
	}
	
	@Override
	public boolean mkdir()
	{
		IContainer cont = getContainer();
		if(cont instanceof IFolder && !cont.exists())
		{
			try {
				((IFolder) cont).create(false, true, new NullProgressMonitor());
			} catch (CoreException e) {
				CommonPlugin.getDefault().log(e.getStatus());
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean mkdirs()
	{
		try {
			ResourceUtils.createFolders(getContainer());
			return true;
		} catch (CoreException e) {
			// TODO would it be better to just throw this?
			CommonPlugin.getDefault().log(e.getStatus());
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.resources.ICommonContainer#importFile(com.strikewire.snl.apc.resources.ICommonFile)
	 */
	@Override
	public void importFile(ICommonFile file) throws CoreException
	{
		// TODO could probably be more OO
		
		IFile iFile = file.asIFile();
		
		if(iFile != null)
		{
			ResourceUtils.createFolders(getContainer());
			IFile destFile = getContainer().getFile(new Path(iFile.getName()));
			
			if(destFile.exists())
			{
				destFile.delete(true, true, new NullProgressMonitor());
			}
			
			iFile.copy(destFile.getFullPath(), true, new NullProgressMonitor());
		} else {
			// TODO error.  Shouldn't happen
		}
	}
}
