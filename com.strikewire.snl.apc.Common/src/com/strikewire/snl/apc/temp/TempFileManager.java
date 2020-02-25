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
 * Created by mjgibso on Jun 21, 2010 at 8:06:34 AM
 */
package com.strikewire.snl.apc.temp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.ResourceUtils;

/**
 * @author mjgibso
 *
 */
public class TempFileManager
{
	private static boolean cleanupHookAdded_ = false;
	
	private static Collection<ITempFileUser> tempFileUsers_ = null;
	
	public static synchronized void addTempDirCleanupHook()
	{
		if(cleanupHookAdded_)
		{
			return;
		}
		
		PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {
			
			private Collection<File> filesNotInUse_ = null;
			
			@Override
      public boolean preShutdown(IWorkbench workbench, boolean forced)
			{
				// clear unused temp directories
				try {
					filesNotInUse_ = null;
					filesNotInUse_ = getFilesToDelete();
				} catch (Throwable t) {
					if(t instanceof CoreException)
					{
						CommonPlugin.getDefault().log(((CoreException) t).getStatus());
					} else {
						CommonPlugin.getDefault().logError("Error clearing editor temp dir: "+t.getMessage(), t);
					}
				}
				
				return true;
			}
			
			@Override
      public void postShutdown(IWorkbench workbench)
			{
				if(filesNotInUse_ != null)
				{
					for(File file : filesNotInUse_)
					{
						ResourceUtils.recursiveDelete(file);
					}
				}
			}
		});
		
		cleanupHookAdded_ = true;
	}
	
	/**
	 * Method returns a folder guaranteed to be unique and empty.  The folder and its contents will
	 * be deleted when the workbench exits.  To prevent this, the caller can extend the extension
	 * point TempFileUser to be given an opportunity to specify that a file is still in use and
	 * should not be deleted on workbench close.
	 */
	public static synchronized IFolder getUniqueTempDir() throws CoreException
	{
		IFolder tempDir = ResourceUtils.getTempDir();
		IFolder subTempDir = null;
		while(subTempDir==null || subTempDir.exists())
		{
			subTempDir = tempDir.getFolder(String.valueOf(System.currentTimeMillis()));
		}
		subTempDir.create(true, true, new NullProgressMonitor());
		addTempDirCleanupHook();
		return subTempDir;
	}

	public static List<IEditorReference> getOpenEditorRefs()
	{
		List<IEditorReference> refs = new ArrayList<IEditorReference>();
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for(IWorkbenchWindow window : windows)
		{
			IWorkbenchPage[] pages = window.getPages();
			for(IWorkbenchPage page : pages)
			{
				refs.addAll(Arrays.asList(page.getEditorReferences()));
			}
		}
		
		return refs;
	}
	
	private static synchronized Collection<File> getFilesToDelete() throws CoreException
	{
		IFolder tempDir = ResourceUtils.getTempDir();
		if(tempDir==null || !tempDir.exists())
		{
			return null;
		}
		
		File tempDirFile = tempDir.getLocation().toFile();
		Collection<File> filesToDelete = new HashSet<File>();
		if(tempDirFile!=null && tempDirFile.exists() && tempDirFile.isDirectory())
		{
			for(File child : tempDirFile.listFiles())
			{
				Collection<File> childrenToDelete = gatherFilesToDelete(child);
				if(childrenToDelete != null)
				{
					filesToDelete.addAll(childrenToDelete);
				} else {
					filesToDelete.add(child);
				}
			}
		}
		
		return filesToDelete;
	}
	
	/**
	 * Method recursively gathers files and folders to delete.  Method returns the
	 * list of files and folders that are to be deleted, or null if the whole
	 * structure under the given file is to be deleted.
	 */
	private static Collection<File> gatherFilesToDelete(File file)
	{
		if(file==null || !file.exists())
		{
			return null;
		}
		
		if(file.isFile())
		{
			// it's a file, see if it's in use
			if(isFileInUse(file))
			{
				// it's in use, so don't delete it
				return new ArrayList<File>();
			} else {
				// it's not in use, so delete it
				return null;
			}
		} else if(file.isDirectory()) {
			// it's a directory
			File[] children = file.listFiles();
			if(children==null || children.length<1)
			{
				// there are no children, so just delete this directory
				return null;
			} else {
				// there are children, so test them all recursively
				Set<File> filesToDelete = new HashSet<File>();
				boolean deleteAllChildren = true;
				for(File child : children)
				{
					Collection<File> childrenToDelete = gatherFilesToDelete(child);
					// if we get back null
					if(childrenToDelete == null)
					{
						// the whole folder or file is supposed to be deleted.
						// add it to the list of files to be deleted in case all children are not being deleted
						filesToDelete.add(child);
					} else {
						// we got something back, so there's something not to be deleted
						deleteAllChildren = false;
						// add what is supposed to be deleted
						filesToDelete.addAll(childrenToDelete);
					}
				}
				if(deleteAllChildren)
				{
					// we had no children that we were not supposed to delete, so delete this whole folder
					return null;
				} else {
					// there were some children not to be deleted, so return the ones that were
					return filesToDelete;
				}
			}
		} else {
			CommonPlugin.getDefault().logError("Found a java.io.File in the temp dir that wasn't a file or directory: "+file.getAbsolutePath()+".  Going to delete it...", new Exception());
			return null;
		}
	}
	
	private static boolean isFileInUse(File file)
	{
		for(ITempFileUser tempFileUser : getTempFileUsers())
		{
			if(tempFileUser.isTempFileInUse(file))
			{
				return true;
			}
		}
		
		return false;
	}
	
//	private static Collection<File> getLocalFiles(IEditorInput editorInput)
//	{
//		Set<File> files = new HashSet<File>();
//		
//		// try for a uri input
//		if(editorInput instanceof IURIEditorInput)
//		{
//			URI fileUri = ((IURIEditorInput) editorInput).getURI();
//			files.add(new File(fileUri));
//		}
//		
//		// try for a multi editor input
//		if(editorInput instanceof MultiEditorInput)
//		{
//			IEditorInput[] inputs = ((MultiEditorInput) editorInput).getInput();
//			if(inputs!=null && inputs.length>0)
//			{
//				for(IEditorInput subInput : inputs)
//				{
//					files.addAll(getLocalFiles(subInput));
//				}
//			}
//		}
//		
//		// try for a compare editor input
//		if(editorInput instanceof CompareEditorInput)
//		{
//			CompareEditorInput compareInput = (CompareEditorInput) editorInput;
//			Object edition = compareInput.getSelectedEdition();
//			System.out.println("duh: "+edition);
//		}
//		
//		// try adapters ---------------------
//		// try uri adapter
//		URI uri = (URI) editorInput.getAdapter(URI.class);
//		if(uri != null)
//		{
//			files.add(new File(uri));
//		}
//		
//		// try file adapter
//		File file = (File) editorInput.getAdapter(File.class);
//		if(file != null)
//		{
//			files.add(file);
//		}
//		
//		// try resource adapter
//		IResource res = (IResource) editorInput.getAdapter(IResource.class);
//		if(res != null)
//		{
//			files.add(res.getLocation().toFile());
//		}
//		
//		// try ifile adapter
//		IFile ifile = (IFile) editorInput.getAdapter(IFile.class);
//		if(ifile != null)
//		{
//			files.add(ifile.getLocation().toFile());
//		}
//		
//		// try ipath
//		IPath ipath = (IPath) editorInput.getAdapter(IPath.class);
//		if(ipath != null)
//		{
//			files.add(ipath.toFile());
//		}
//		
//		// try IStorage
//		IStorage istorage = (IStorage) editorInput.getAdapter(IStorage.class);
//		if(istorage != null)
//		{
//			files.add(istorage.getFullPath().toFile());
//		}
//		
//		return files;
//	}
	
	private static synchronized Collection<ITempFileUser> getTempFileUsers()
	{
		if(tempFileUsers_ != null)
		{
			return tempFileUsers_;
		}
		
		tempFileUsers_ = new ArrayList<ITempFileUser>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CommonPlugin.ID, ITempFileUser.EXTENSION_POINT_ID);
		IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
		if(elements == null)
		{
			return tempFileUsers_;
		}
		
		for(IConfigurationElement element : elements)
		{
			if(element == null)
			{
				continue;
			}
			
			try {
				Object user = element.createExecutableExtension("class");
				if(user!=null && (user instanceof ITempFileUser))
				{
					tempFileUsers_.add((ITempFileUser) user);
				}
			} catch(CoreException ce) {
				CommonPlugin.getDefault().log(ce.getStatus());
			}
		}
		
		return tempFileUsers_;
	}
}
