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
 * Created by Marcus Gibson
 * On Jan 31, 2006 at 8:51:03 AM
 */
package com.strikewire.snl.apc.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.ContainerGenerator;
import org.eclipse.ui.internal.editors.text.WorkspaceOperationRunner;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.osgi.framework.Bundle;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.natures.APCNatureHandler;
import com.strikewire.snl.apc.resources.ICommonContainer;
import com.strikewire.snl.apc.resources.ICommonFile;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;

/**
 * @author Marcus Gibson
 *
 */
public class ResourceUtils
{
	public static final String METADATA_FILE_NAME = com.strikewire.snl.apc.util.Messages.getString("metadata.filename");

	public static final String DART_HIDDEN_PROJECT = "DART-Hidden-Default-Project";

	public static final String DART_HIDDEN_PROJECT_LINKS_ROOT = "LINKED_RESOURCES";

	public static final String DART_HIDDEN_PROJECT_TEMP_DIR = "TEMP";

	// Default directory to initialize file browsers to
	private static final String DEFAULT_DIR = "gov.sandia.dart.sierraui.vtkGraphicsViewer.defaultBrowseDir";

	/*
	 * This method was ripped off from the createNewFile() method in the class WizardNewFileCreationPage
	 */
    public static IFile createNewFile(ICommonContainer location, String fileName, String contents) throws CoreException
    {
    	// TODO progress monitor?
    	if(location == null)
    	{
    		throw new IllegalArgumentException("The container must not be null");
    	}
    	if(fileName == null)
    	{
    		throw new IllegalArgumentException("The fileName must not be null");
    	}

    	// make sure the file doesn't already exist
    	if(location.exists(new Path(fileName)))
    	{
			throw new IllegalArgumentException("The given fileName already exists.");
    	}

    	
        ICommonFile icf = location.getFile(new Path(fileName));
        final IFile newFileHandle = icf.asIFile();

        // create rule
        ISchedulingRule sRule = null;
        IResource resource = newFileHandle;
		IResource parent = resource.getParent();
    	while(parent != null)
    	{
    		if(parent.exists())
    		{
    			sRule = resource.getWorkspace().getRuleFactory().createRule(resource);
    			break;
    		}
    		resource = parent;
    		parent = parent.getParent();
    	}
    	if(sRule == null)
    	{
    		sRule = resource.getWorkspace().getRoot();
    	}

		// TODO use the getBytes() method that takes a charset
		byte[] buf = contents.getBytes();
		final ByteArrayInputStream initialContents = new ByteArrayInputStream(buf);

        WorkspaceModifyOperation op = new WorkspaceModifyOperation(sRule) {
            @Override
			protected void execute(IProgressMonitor monitor)
                    throws CoreException, InvocationTargetException {
                try {
                    monitor.beginTask(IDEWorkbenchMessages.WizardNewFileCreationPage_progress, 2000);
                    ContainerGenerator generator = new ContainerGenerator(newFileHandle.getParent().getFullPath());
                    generator.generateContainer(new SubProgressMonitor(monitor,
                            1000));
//                        createFile(newFileHandle, initialContents,
//                                new SubProgressMonitor(monitor, 1000));
                    IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1000);
                    InputStream contents = initialContents;

                    try {
                    	setContents(newFileHandle, initialContents);

                        // Create a new file resource in the workspace
//                        IPath path = newFileHandle.getFullPath();
//                        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
//                        int numSegments= path.segmentCount();
//                        if (numSegments > 2 && !root.getFolder(path.removeLastSegments(1)).exists()) {
//                            // If the direct parent of the path doesn't exist, try to create the
//                            // necessary directories.
//                            for (int i= numSegments - 2; i > 0; i--) {
//                                IFolder folder = root.getFolder(path.removeLastSegments(i));
//                                if (!folder.exists()) {
//                                    folder.create(false, true, subMonitor);
//                                }
//                            }
//                        }
//                    	// if we're a linked file to a path that doesn't really exist on the FS
//                        if(newFileHandle.exists() && !newFileHandle.getLocation().toFile().exists())
//                        {
//                        	// we can't call create, or it will throw an exception that it already exists, and we can't call
//                        	// setContents, because it will complain that it doesn't exist.
//                        	// TODO there's got to be a better way to do this...
//                        	try {
//
//								File newFile = newFileHandle.getLocation().toFile();
//								File parentFolder = newFile.getParentFile();
//								if(!parentFolder.exists())
//								{
//									parentFolder.mkdirs();
//								}
//								newFile.createNewFile();
//							} catch (IOException e) {
//								throw new InvocationTargetException(e);
//							}
//                        	newFileHandle.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor()); // TODO breakup the sub monitors and include here
//                        	newFileHandle.setContents(contents, false, false, subMonitor);
//                        } else {
//                            newFileHandle.create(contents, false, subMonitor);
//                        }
                    } catch (CoreException e) {
                        // If the file already existed locally, just refresh to get contents
                        if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED)
                        	newFileHandle.refreshLocal(IResource.DEPTH_ZERO, null);
                        else
                            throw e;
					}

                    if (subMonitor.isCanceled())
                        throw new OperationCanceledException();
                } finally {
                    monitor.done();
                }
            }
        };

        try {
        	IRunnableContext runner = new WorkspaceOperationRunner();
            runner.run(true, true, op);
        } catch (InterruptedException e) {
            return null;
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof CoreException) {
                ErrorDialog
                        .openError(null
                        		, // Was Utilities.getFocusShell()
                                IDEWorkbenchMessages.WizardNewFileCreationPage_errorTitle,
                                null, // no special message
                                ((CoreException) e.getTargetException())
                                        .getStatus());
            } else {
                // CoreExceptions are handled above, but unexpected runtime exceptions and errors may still occur.
                IDEWorkbenchPlugin.log(ResourceUtils.class,
                        "createNewFile()", e.getTargetException()); //$NON-NLS-1$
                MessageDialog
                        .openError(
                        		null,
                                IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorTitle, NLS.bind(IDEWorkbenchMessages.WizardNewFileCreationPage_internalErrorMessage, e.getTargetException().getMessage()));
            }
            return null;
        }

        return newFileHandle;
    }

	public static void recursiveDelete(File file)
	{
		if(file!=null && file.exists())
		{
			if(file.isDirectory())
			{
				for(File child : file.listFiles())
				{
					recursiveDelete(child);
				}
			}

			file.delete();
		}
	}

	/** This version of recursiveDelete deletes folders in both the file system and the workspace.  This is useful in cases
	 * where we are working with linked resources (e.g. stand-alone Jaguar, SierraEditor, or CompSimUI.  Needs to be tested in
	 * Full DART Workbench application.
	 *
	 * @param folderPath the Path to the folder that will be deleted along with its contents.
	 * @throws CoreException
	 */
	public static void recursiveDelete(IPath folderPath) throws CoreException
	{
		File ioFolder = folderPath.toFile();
		if(ioFolder.exists())
			recursiveDelete(ioFolder);

		IFile iFolder = getHiddenProject().getFile(folderPath);
		if(iFolder != null)
		{
			iFolder.delete(true, new NullProgressMonitor());
		}
	}

	public static void delete(IFile file) throws CoreException
	{

		if(file.isLinked())
			file.getLocation().toFile().delete();

		file.delete(false, new NullProgressMonitor());
	}


	public static boolean isArtifactFolder(IResource folder)
	{
		if(folder != null)
		{
			// get local resources
			IWorkbenchAdapter adapter = (IWorkbenchAdapter) folder.getAdapter(IWorkbenchAdapter.class);
			if (adapter != null)
			{
				Object[] kids = adapter.getChildren(folder);

				for(int i=0; i<kids.length; i++)
					if(((IResource) kids[i]).getName().equals(METADATA_FILE_NAME))
						return true;
			}
		}

		return false;
	}

	/**
	 * Rebuilds all projects in the workspace in a job thread.  All associated builders are invoked.
	 */
	public static void rebuildProjects()
	{ rebuildProjects(null); }

	/**
	 * Rebuilds all projects in the workspace in a job thread.
	 *
	 * @param builderID - the specific builder to that should rebuild.  If null, all builders will be invoked.
	 */
	public static void rebuildProjects(final String builderID)
	{
		new Job("Rebuilding workspace...") {
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
				monitor.beginTask("Rebuilding workspace...", projects.length*100);
				IStatus retStatus = Status.OK_STATUS;
				for(IProject project : projects)
				{
					monitor.subTask(project.getName());
					IProgressMonitor subMon = new SubProgressMonitor(monitor, 100, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK);
					try {
						if(builderID != null)
							project.build(IncrementalProjectBuilder.FULL_BUILD, builderID, null, subMon);
						else
							project.build(IncrementalProjectBuilder.FULL_BUILD, subMon);
					} catch (CoreException ce) {
						retStatus = CommonPlugin.getDefault().mergeStatus(retStatus, ce.getStatus());
					}
				}

				return retStatus;
			}
		}.schedule();
	}

	/**
	 * Rebuilds the given project in a job thread.  All associated builders are invoked.
	 *
	 * @param project - the project to rebuild
	 */
	public static void rebuildProject(IProject project)
	{ rebuildProject(project, null, null); }

	/**
	 * Rebuilds the given project using the given builder in a job thread.
	 *
	 * @param project - the project to rebuild
	 * @param builderID - the specific builder to that should rebuild.  If null, all associated builders will be invoked.
	 * @param builderArgs - an optional map of builder specific arguments, may be null
	 */
	public static void rebuildProject(final IProject project, final String builderID, final Map<String,String> builderArgs)
	{
		new Job("Rebuilding "+project.getName()+"...") {
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					if(builderID != null)
						project.build(IncrementalProjectBuilder.FULL_BUILD, builderID, builderArgs, monitor);
					else
						project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
					return Status.OK_STATUS;
				} catch (CoreException ce) {
					return ce.getStatus();
				}
			}
		}.schedule();
	}

	public static IFile getFileForLocation(String absFilePath, boolean allowMissing) throws CoreException
	{
		return getFileForLocation(getHiddenProject(), absFilePath, allowMissing);
	}

	public static IFile getFileForLocation(String absFilePath) throws CoreException
	{
		return getFileForLocation(getHiddenProject(), absFilePath);
	}

	public static IFile getFileForLocation(IProject project, String absFilePath) throws CoreException
	{
		return getFileForLocation(project, absFilePath, false);
	}

	/**
	 *
	 * @param project
	 * @param absFilePath
	 * @param allowMissing - if true, this method still returns an IFile handle to the absFilePath specified, even
	 * if there is no existing file at the referenced location.  If false, this method returns null if referenced
	 * file does not exist.
	 * @return
	 * @throws CoreException
	 */
	public static IFile getFileForLocation(IProject project, String absFilePath, boolean allowMissing) throws CoreException
	{
		IPath path = new Path(absFilePath);

		// First see if the file already exists in the workspace.
		// This handles cases where files are in projects that are in the workspace.
		// They can be retrieved
		// based on their absolute path.
		IFile theFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(absFilePath));

		// if it's not null, it was already a WS-based location
		if(theFile != null)
		{
			return theFile;
		}

		// If not, get a hidden link to it
		theFile = getLinkedFileHandle(project, path);

		// if we are not allowing missing files, and we cannot find an IFile in a project, AND the java.io.File
		// does not exist, bail out
		if(!allowMissing && !path.toFile().exists() && !theFile.exists())
			return null;

		// Next, check to see if the resource exists. If not, then create a linked resource
		// to the file outside of the workspace.
		if(!theFile.exists() && project.getName().equals(DART_HIDDEN_PROJECT))
		{
			try {
				// create the parent folder.  The parent resource must exist to create a linked file under its path
				createFolders(theFile.getParent());

				// Append a '/' to the path to make it a true uri absolute path
				// if one does not already exist. This solves issues with
				// Windows uris
				if(!absFilePath.startsWith("/"))
				{
					absFilePath = "/" + absFilePath;
				}
				URI uri = new URI("file", null, absFilePath, null);

				int flags = IResource.REPLACE;
				if(allowMissing)
				{
					flags |= IResource.ALLOW_MISSING_LOCAL;
				}
				theFile.createLink(uri, flags, null);
			} catch (URISyntaxException e) {
				CommonPlugin.getDefault().throwError("Error creating linked file: "+e.getMessage(), e);
			}
		}

		return theFile;
	}

//	/**
//	 * Determines if a file path is from the local filesystem,
//	 * if not it checks the workspace path and returns the appropriate IFile
//	 * If it is neither, it returns null
//	 * @param absFilePath the absolute file path
//	 * @return
//	 * @throws CoreException
//	 */
//	public static IFile getFileForLocationOrWorkspace(String absFilePath) throws CoreException
//	{
//		IFile file = ResourceUtils.getFileForLocation(absFilePath);
//		if(file == null)
//		{
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			IWorkspaceRoot root = workspace.getRoot();
//			file = root.getFile(new Path(absFilePath));
//		}
//
//		return file;
//	}



	public static IFile getLinkedFileHandle(IProject project, IPath absFilePath) throws CoreException
	{
		IPath path;

		if(project.equals(getHiddenProject()))
		{
			if (DART_HIDDEN_PROJECT_LINKS_ROOT.equals(absFilePath.segment(0))) {
				// This is already a linked path.
				path = absFilePath;
			} else {
				// put linked resources in a special folder to avoid conflicts with TEMP dir (issue DTA-5991)
				path = new Path(DART_HIDDEN_PROJECT_LINKS_ROOT);

				// Check the device to handle Windows C: vs E: drive (issue DTA-5991)
				String device = absFilePath.getDevice();
				if(StringUtils.isNotBlank(device))
				{
					String cleanDevice = device.replaceAll(":", "");
					path = path.append(cleanDevice);
				}
				path = path.append(absFilePath);
			}
		} else {
			path = absFilePath;
		}

		return project.getFile(path);
	}

	public static IFile getFileForLocation(IResource file, String absFilePath) throws CoreException
	{
		return getFileForLocation(file, absFilePath, false);
	}

	public static IFile getFileForLocation(IResource file, String absFilePath, boolean allowMissing) throws CoreException
	{
		return getFileForLocation(file.getProject(), absFilePath, allowMissing);
	}


	/**
	 * This method creates a local folder (IFolder).
	 *
	 * @param folder
	 * @throws CoreException
	 */
	public static void createFolders(IContainer folder) throws CoreException
	{
		IContainer container = folder;
		Stack<IFolder> folders = new Stack<IFolder>();
		while(container instanceof IFolder && !container.exists())
		{
			folders.push((IFolder) container);
			container = container.getParent();
		}

		while(!folders.isEmpty())
		{
			IFolder newFolder = folders.pop();
			if(!newFolder.exists())
			{
				try {
					newFolder.create(true, true, new NullProgressMonitor());
				} catch (Exception e) {
					if (!newFolder.exists())
						throw e;
				}
			}
		}
	}


	public static IProject getHiddenProject() throws CoreException
	{
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject project = root.getProject(DART_HIDDEN_PROJECT);
		if(project.exists() && project.isOpen())
		{
			return project;
		}

		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			@Override
			public void run(IProgressMonitor monitor)
			throws CoreException {
				boolean needToSetNatures = false;

				if(!project.exists())
				{
					project.create(null,IResource.HIDDEN,null);
					needToSetNatures = true;
				} else if(!new File(project.getLocation().toOSString()).exists()) {
					// Somehow hidden project directory was deleted on filesystem so
					// need to recreate project

					project.delete(true, null);
					project.create(null,IResource.HIDDEN,null);

					needToSetNatures = true;
				}

				if(!project.isOpen())
				{
					project.open(null);
					if(needToSetNatures)
					{
						setProjectNaturesWithRule(project);
					}
				}

				if(!project.isHidden())
				{
					project.setHidden(true);
				}

			}
		};

		ResourcesPlugin.getWorkspace().run(runnable, root, IWorkspace.AVOID_UPDATE, null);

		return project;
	}

	/**
	 * Method returns a TEMP folder guaranteed to exist.  The temp folder is common and shared by
	 * others, so existing content should not be modified.  All contents of the folder will be
	 * be deleted when the workbench exits.  To prevent this, the caller can extend the extension
	 * point TempFileUser to be given an opportunity to specify that a file is still in use and
	 * should not be deleted on workbench close.
	 */
	public static synchronized IFolder getTempDir() throws CoreException
	{
		IProject project = getHiddenProject();
		IFolder tempDir = project.getFolder(DART_HIDDEN_PROJECT_TEMP_DIR);
		if(!tempDir.exists())
		{
			tempDir.create(true, true, new NullProgressMonitor());
		}

		return tempDir;
	}

	public static void setProjectNaturesWithRule(final IProject project)
	{
		if(!project.isOpen())
		{
			return;
		}

		Job job = new Job("Setting Project Natures...") {
			/* (non-Javadoc)
			 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
			 */
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				try {
					setProjectNatures(project, true);
					return Status.OK_STATUS;
				} catch (CoreException ce) {
					return ce.getStatus();
				}
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRoot());
		job.schedule();
	}

	public static void setProjectNatures(IProject project, boolean on) throws CoreException
	{
		if(!project.isOpen())
		{
			return;
		}

		// Toggle the nature
		IProjectDescription description = project.getDescription();
		List<String> newIds = new ArrayList<String>();
		newIds.addAll(Arrays.asList(description.getNatureIds()));

		for(String nature : APCNatureHandler.getContributedNatures())
		{
			if(nature!=null && !newIds.contains(nature) && on)
			{
				newIds.add(nature);
			}
		}

		description.setNatureIds(newIds.toArray(new String[newIds.size()]));

		// Save the description
		project.setDescription(description, null);
	}

	public static String getDefaultDirectory()
	{
		return getDefaultDirectory(null);
	}

	public static String getDefaultDirectory(String context)
	{
		String prefKey = getDefaultDirectoryKey(context);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();

		String defaultDir = store.getString(prefKey);

		if(StringUtils.isBlank(defaultDir))
		{
			defaultDir = store.getString(DEFAULT_DIR);
		}

		return defaultDir;
	}

	public static void setDefaultDirectory(String dir)
	{
		setDefaultDirectory(null, dir);
	}

	public static String getDefaultDirectoryKey(String context)
	{
		String prefKey = DEFAULT_DIR;
		if(StringUtils.isNotBlank(context))
		{
			prefKey += '.'+context;
		}
		return prefKey;
	}

	public static void setDefaultDirectory(String context, String dir)
	{
		String prefKey = getDefaultDirectoryKey(context);
		CommonPreferencesPlugin.getDefault().getPreferenceStore().setValue(prefKey, dir);
	}

	public static boolean isFileWithExtension(IResource file, String extension)
	{
		if(StringUtils.isBlank(extension))
		{
			throw new IllegalArgumentException("Extension cannot be null or blank: "+extension);
		}

		if(file == null)
		{
			return false;
		}

		if(!file.exists())
		{
			return false;
		}

		if(!(file instanceof IFile))
		{
			return false;
		}

		return extension.equalsIgnoreCase(Utils.getExtension(file.getName()));
	}

	/**
	 * Method properly handles setting the contents of the specified IFile via the provided String (can be null
	 * or an empty string, indicating to set the file to have no contents, or be of zero bytes in length)
	 * regardless of if the file is linked, and regardless of if it, or its parent resources already exist.
	 * @throws CoreException
	 * @throws IOException
	 * @see #setContents(IFile, InputStream)
	 */
	public static void setContents(IFile file, String contents) throws CoreException
	{
		setContents(file, getStringAsInputStream(contents));
	}

	/**
	 * Method properly handles setting the contents of the specified IFile via the provided String (can be null
	 * or an empty string, indicating to set the file to have no contents, or be of zero bytes in length)
	 * regardless of if the file is linked, and regardless of if it, or its parent resources already exist.
	 * @throws CoreException
	 * @throws IOException
	 * @see #setContents(IFile, InputStream)
	 */
	public static void setContents(IFile file, String contents, int updateFlags) throws CoreException, IOException
	{
		setContents(file, getStringAsInputStream(contents), updateFlags);
	}

	public static InputStream getStringAsInputStream(String contents)
	{
		byte[] bytes = contents!=null ? contents.getBytes() : "".getBytes();
		return new ByteArrayInputStream(bytes);
	}

	/**
	 * Method properly handles setting the contents of the specified IFile via the provided InputStream
	 * regardless of if the file is linked, and regardless of if it, or its parent resources already exist.
	 * @throws CoreException
	 * @throws IOException
	 */
	public static void setContents(IFile file, InputStream in) throws CoreException
	{
		setContents(file, in, IResource.NONE);
	}

	/**
	 *
	 * Method properly handles setting the contents of the specified IFile via the provided InputStream
	 * regardless of if the file is linked, and regardless of if it, or its parent resources already exist.
	 * @throws CoreException
	 * @throws IOException
	 */
	public static void setContents(IFile file, InputStream in, int updateFlags) throws CoreException
	{
		// the dilemma is that if it's linked, but doesn't exist on the FS yet, you're stuck.  You can't call setContents,
		// because it will complain the actual file doesn't exist.  But you can't call create, as it will complain that
		// the IFile already exists.  Hence this method

		file.refreshLocal(IContainer.DEPTH_ZERO, new NullProgressMonitor());

		if(file.isLinked())
		{
			File ioFile = file.getLocation().toFile();
			File parentFolder = ioFile.getParentFile();
			if(!parentFolder.exists())
			{
				parentFolder.mkdirs();
			}
			if(!ioFile.exists())
			{
				try {
					ioFile.createNewFile();
				} catch (IOException e) {
					throw CommonPlugin.getDefault().newError(e);
				}
			}
			file.refreshLocal(IContainer.DEPTH_ZERO, new NullProgressMonitor());
			file.setContents(in, updateFlags, null);
		} else { // not linked, actually working in WS
			if(file.exists())
			{
				file.setContents(in, updateFlags, null);
			} else {
				ResourceUtils.createFolders(file.getParent());
				file.create(in, updateFlags, null);
			}
		}
	}
	
	
	/**
	 * Searches within the bundle of specified pluginId to locate
	 * a resource, and returns it. May return null.
	 * @throws IllegalArgumentException If the parameters are invalid
	 */
	public static IPath getPathToFile(final String pluginId,
	                                  final String resourceName)
	{
	  IPath ret = null;
	  
	  if (StringUtils.isBlank(pluginId) || StringUtils.isBlank(resourceName)) {
	    String msg = "Blank/null parameters; pluginId = " + pluginId +
	        "; resourceName = " + resourceName;
	    IllegalArgumentException e = new IllegalArgumentException(msg);
	    CommonPlugin.getDefault().logError(e);
	    throw e;
	  }
	    

    Bundle bundle = Platform.getBundle(pluginId);
    
    // if we cannot get the bundle, there is no reason to continue
    if (bundle == null) {
      String msg = "Unable to find bundle for plugin " + pluginId;
      CommonPlugin.getDefault().logError(msg, new FileNotFoundException());
      return null;
    }
    
    IPath loginConf = new Path(resourceName);
    URL u = FileLocator.find(bundle, loginConf, null);

    if (u != null) {
      try {
        u = FileLocator.resolve(u);
        ret = new Path(new File(u.getFile()).getAbsolutePath());
      } catch (IOException noop) {
      }
    }
    
    return ret;
    
	} //getPathToFile()
	
	
	/**
	 * Returns true if the specified execName is in the Path
	 * environment variable.
	 */
	public static boolean isExecutableInPath(final String execName)
	{
	  boolean exists = false;
	  
	  // algo from http://stackoverflow.com/questions/934191/how-to-check-existence-of-a-program-in-the-path
	  if (StringUtils.isNotBlank(execName)) {
	    exists = Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
	    .map(Paths::get)
	    .anyMatch(path -> Files.exists(path.resolve(execName)));
	  }
	  
	  return exists;
	}
	
	
	/**
	 * Returns the path to the specified executable if the executable is
	 * in the current Path (as obtained from the environment variable). If
	 * the executble is not found, throws an Exception
	 * @param execName The name of the executable to search for in the path
	 * @return The path to the executable
	 * @throws FileNotFoundException If the executable is not found in
	 * the existing path.
	 * @throws IllegalArgumentException The execName is invalid
	 */
	public static java.nio.file.Path getPathToExecutable(final String execName)
	  throws FileNotFoundException
	{
	  java.nio.file.Path retPth = null;
	  
	  if (StringUtils.isBlank(execName)) {
	    throw new IllegalArgumentException("null/empty execName");
	  }
	  
	  // algo from http://stackoverflow.com/questions/934191/how-to-check-existence-of-a-program-in-the-path
	  Optional<java.nio.file.Path> pth2Exe;
	  pth2Exe = Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
	      .map(Paths::get)
	      .filter(path -> Files.exists(path.resolve(execName))).findFirst();
	  
	  if (pth2Exe.isPresent()) {
	    retPth = pth2Exe.get();
	  }
	  else {
	    throw new FileNotFoundException("Unable to find " + execName + " in path");
	  }
	  
	  return retPth;
	}
	
}
