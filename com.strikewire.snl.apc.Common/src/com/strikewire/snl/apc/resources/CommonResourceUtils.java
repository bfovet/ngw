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
 * Created by mjgibso on Feb 27, 2014 at 5:55:59 AM
 */
package com.strikewire.snl.apc.resources;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * @author mjgibso
 *
 */
public class CommonResourceUtils
{
	private CommonResourceUtils()
	{}
	
	public static ICommonContainer getCommonContainerForPath(String path)
	{
		IContainer cont = getContainerForPath(path);
		File file = new File(path);
		
		if(cont != null)
		{
			// if the container doesn't exist, but the file does, we want to return a native
			// container, so don't return the eclipse container
			if(!(!cont.exists() && file.exists()))
			{
				return new EclipseContainer(cont);
			}
		}
		
		// if it exists, it must be a directory
		return (file.exists() && !file.isDirectory()) ? null : new NativeContainer(file);
	}
	
	public static ICommonFile getCommonFileForPath(String path)
	{
		IFile ifile = getFileForPath(path);
		File file = new File(path);
		
		if(ifile != null)
		{
			// if the ifile doesn't exist, but the file does, we want to return a native
			// file, so don't return the eclipse file
			if(!(!ifile.exists() && file.exists()))
			{
				return new EclipseFile(ifile);
			}
		}
		
		// if it exists, it must be a file
		return (file.exists() && !file.isFile()) ? null : new NativeFile(file);
	}
	
	public static boolean workspaceIncludesOpenNonHiddenProjects()
	{
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for(IProject project : projects)
		{
			if(project.isOpen() && !project.isHidden())
			{
				return true;
			}
		}
		return false;
	}
	
	public static IPath getExistingWorkspaceSegments(IPath path)
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		while(path.segmentCount() > 0)
		{
			IResource res = root.findMember(path);
			if(res!=null && res.exists() && !res.isHidden(IContainer.CHECK_ANCESTORS))
			{
				return path;
			}
			path = path.removeLastSegments(1);
		}
		return path;
	}
	
	public static IPath getExistingFilesystemSegments(IPath path)
	{
		while(path.segmentCount() > 0)
		{
			File file = new File(path.toString());
			if(file.exists())
			{
				return path;
			}
			path = path.removeLastSegments(1);
		}
		return path;
	}

	public static boolean isWorkspacePath(String path)
	{
		if(StringUtils.isNotBlank(path))
		{
			// try to guess based on the initial path
			IResource res = findResourceForPath(path);
			if(res != null)
			{
				return true;
			} else {
				File file = new File(path);
				if(file.exists())
				{
					return false;
				}
			}
			
			// if we got here, the current path is not blank, but it
			// doesn't match an existing WS member or or FS file.  Let's
			// try a couple things to see if we can guess if it looks
			// like a FS vs WS path?
			
			// TODO we could check a couple things corresponding to
			// OS, like if on windows, look for the path to start
			// with drive letter implying it's a FS path.  (But then
			// what about mapped network paths.  Maybe should do this
			// kind of thing with URLs/URIs?)
			
			// see where there are the most matching first segments
			IPath WSPath = getExistingWorkspaceSegments(new Path(path));
			IPath FSpath = getExistingFilesystemSegments(new Path(path));
			
			if(WSPath.segmentCount() != FSpath.segmentCount())
			{
				return WSPath.segmentCount() > FSpath.segmentCount();
			}
			
			// if neither matched, or they both matched the same depth,
			// fall through and let's decide simply based on env.
		}
		
		// if we got here, we are to guess if we should use a FS vs WS
		// browser based on env, not initial path.  If we have any
		// non-hidden projects open in the WS, let's default to WS
		// browser, otherwise, default to FS browser.
		
		// TODO, at some point, we might consider adding hooks from
		// a browser dialog to actually create a project (maybe), but
		// not right now.  At that point, might then want to look at
		// some other bit of information to try and determine if the app
		// currently running is one that would prefer the user work in
		// the WS or not.  Maybe simply a preference defined in this
		// plugin, that WS aware apps could include a plugin that would
		// provide a preference initializer to set the value to true
		// indicating workspace awareness/usage-preference.
		
		return workspaceIncludesOpenNonHiddenProjects();
	}
	
	public static IContainer getContainerForPath(String path)
	{
		IResource res = findResourceForPath(path);
		if(res instanceof IContainer)
		{
			return (IContainer) res;
		}
		if(res instanceof IFile)
		{
			return res.getParent();
		}
		
		IContainer cont = ResourcesPlugin.getWorkspace().getRoot().getContainerForLocation(new Path(path));
		if(cont != null)
		{
			return cont;
		}
		
		IPath iPath = new Path(path);
		IProject project = getGoodProject(iPath);
		if(project != null)
		{
			if(iPath.segmentCount() > 1)
			{
				return project.getFolder(iPath.removeFirstSegments(1));
			}
			return project;
		}
		
		return null;
	}
	
	public static IFile getFileForPath(String path)
	{
		IResource res = findResourceForPath(path);
		if(res instanceof IFile)
		{
			return (IFile) res;
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(path));
		if(file != null)
		{
			return file;
		}
		
		IPath iPath = new Path(path);
		IProject project = getGoodProject(iPath);
		if(project != null)
		{
			if(iPath.segmentCount() > 1)
			{
				return project.getFile(iPath.removeFirstSegments(1));
			}
		}
		
		return null;
	}
	
	private static IProject getGoodProject(IPath path)
	{
		if(path.segmentCount() > 0)
		{
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(path.segment(0));
			if(project!=null && project.exists() && project.isOpen() && !project.isHidden())
			{
				return project;
			}
		}
		
		return null;
	}
	
	public static IResource findResourceForPath(String path)
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resMem = root.findMember(path);
		if(resMem!=null && resMem.exists() && !resMem.isHidden(IContainer.CHECK_ANCESTORS))
		{
			return resMem;
		}
		
		IContainer cont = root.getContainerForLocation(new Path(path));
		if(cont!=null && cont.exists() && !cont.isHidden(IContainer.CHECK_ANCESTORS))
		{
			return cont;
		}
		
		IFile file = root.getFileForLocation(new Path(path));
		if(file!=null && file.exists() && !file.isHidden(IContainer.CHECK_ANCESTORS))
		{
			return file;
		}
		
		return null;
	}
	
}
