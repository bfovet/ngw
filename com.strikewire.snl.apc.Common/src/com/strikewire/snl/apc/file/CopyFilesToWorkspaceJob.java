/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *
 * Copyright (C) 2005,2006
 *    
 *  All Rights Reserved
 *
 *  StrikeWire, LLC
 *  368 South McCaslin Blvd., #115
 *  Louisville, CO 80027
 *  (720) 890-8591
 *  support@strikewire.com
 *
 *  COMPANY PROPRIETARY
 *
 */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/*
 *
 *  $Author$
 *  $Date$
 *  
 * FILE: 
 *  $Source$
 *
 *
 * Description ($Revision$):
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.CommonMessages;
import com.strikewire.snl.apc.CommonMessages.eOverwriteValues;
import com.strikewire.snl.apc.Common.CommonPlugin;
import com.twmacinta.util.MD5OutputStream;

/**
 * A Job that copies files into the Workspace. We are using a Job because the
 * CopyFilesAndFoldersOperation.copyFiles presents a modal dialog (with
 * inaccurate information), and thus the copy stuff needs to be in a Job.
 * <p/>
 * In the future, this Job should do more of the actual copying, and not rely
 * upon the ultimate issue of the ImportOperation class.
 * 
 * @author kholson
 * 
 */
public class CopyFilesToWorkspaceJob extends WorkspaceJob
{
	
	protected Map<File, String> _srcFiles = new LinkedHashMap<>();


  /**
   * _resourcesToUpdate - IResources to update
   */
  protected Collection<IResource> _resourcesToUpdate = new HashSet<IResource>();

  /**
   * _destination - The destination folder
   */
  protected IContainer _destination;

  /**
   * _shell - The parent shell
   */
  protected Shell _shell;

  // TODO: make this from a preference
  /**
   * buffer - A buffer into which the bytes are passed during the copy operation
   */
  private byte[] buffer;

  /**
   * _bOverwriteAll - If true, then will not prompt for overwriting a
   * destination file.
   */
  boolean _bOverwriteAll = false;
  /**
   * @param filenamesWithPaths
   *          The files to copy, full path information
   * @param resourcesToUpdate
   *          IResource for a filename, if appropriate. There should be only
   *          one IResource for a given filename. If no IResource for the
   *          name exists, then later code will not attempt to update. This
   *          parameter may be null or empty. There need not be the same number
   *          of IResources as filenamesWithPaths, as copies may have both
   *          non-existent destinations as well as existing destinations.
   */
  public CopyFilesToWorkspaceJob(final String[] filenamesWithPaths,
                                 final IContainer destination)
  {
    this(null, filenamesWithPaths, new HashSet<IResource>(), destination);
  }

  /**
   * @param shell
   *          The calling shell
   * @param filenamesWithPaths
   *          The files to copy, full path information
   * @param resourcesToUpdate
   *          IResource for a filename, if appropriate. There should be only
   *          one IResource for a given filename. If no IResource for the
   *          name exists, then later code will not attempt to update. This
   *          parameter may be null or empty. There need not be the same number
   *          of IResources as filenamesWithPaths, as copies may have both
   *          non-existent destinations as well as existing destinations.
   */
  public CopyFilesToWorkspaceJob(final Shell shell,
                                 final String[] filenamesWithPaths,
                                 final IContainer destination)
  {
    this(shell, filenamesWithPaths, new HashSet<IResource>(), destination);
  }


  /**
   * @param shell
   *          The calling shell
   * @param filenamesWithPaths
   *          The files to copy, full path information
   * @param resourcesToUpdate
   *          IResource for a filename, if appropriate. There should be only
   *          one IResource for a given filename. If no IResource for the
   *          name exists, then later code will not attempt to update. This
   *          parameter may be null or empty. There need not be the same number
   *          of IResources as filenamesWithPaths, as copies may have both
   *          non-existent destinations as well as existing destinations.
   */
  public CopyFilesToWorkspaceJob(final Shell shell,
                                 final String[] filenamesWithPaths,
                                 final Collection<IResource> resourcesToUpdate,
                                 final IContainer destination)
  {
	  this(shell, getSrcFilesAndDestNames(filenamesWithPaths), resourcesToUpdate, destination);
  }
  
  protected static Map<File, String> getSrcFilesAndDestNames(String[] fileNames)
  {
	  return Arrays.asList(fileNames).stream().collect(Collectors.toMap(File::new, fn -> new File(fn).getName()));
  }


  /**
   * @param shell
   *          The calling shell
   * @param srcFilesAndDestNames
   *          Keys: The files to copy, full path information, Values: The destination file names to use
   * @param resourcesToUpdate
   *          IResource for a filename, if appropriate. There should be only
   *          one IResource for a given filename. If no IResource for the
   *          name exists, then later code will not attempt to update. This
   *          parameter may be null or empty. There need not be the same number
   *          of IResources as filenamesWithPaths, as copies may have both
   *          non-existent destinations as well as existing destinations.
   */
  public CopyFilesToWorkspaceJob(final Shell shell,
                                 final Map<File, String> srcFilesAndDestNames,
                                 final Collection<IResource> resourcesToUpdate,
                                 final IContainer destination)
  {
	    super("");

	    _shell = shell;

	    _destination = destination;
	    
	    this._srcFiles = new LinkedHashMap<File, String>(srcFilesAndDestNames);

	    if (resourcesToUpdate != null) {
	      _resourcesToUpdate.addAll(resourcesToUpdate);
	    }

	    buffer = new byte[512 * 1024];

  }


  /**
   * Report that a file info could not be found.
   * 
   * @param fileName
   */
  private void reportFileInfoNotFound(final String fileName)
  {

    _shell.getDisplay().syncExec(new Runnable() {
      @Override
      public void run()
      {
        ErrorDialog.openError(_shell, "Copy failed", "Unable to locate file "
            + fileName, null);
      }
    });
  }




  /**
   * Sets for overwriting all destination files
   */
  public void setOverwriteAll(boolean overwriteAll)
  {
    _bOverwriteAll = overwriteAll;
  }




  /**
   * Obtains whether all destination files should be overwritten
   */
  protected boolean getOverwriteAll()
  {
    return _bOverwriteAll;
  }




  /**
   * Displays the overwrite query dialog, and sets the appropriate values for
   * over write.
   * 
   * @return The Overwrite selection of yes/yes to all/no/cancel
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Apr 23, 2012
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  private eOverwriteValues overwriteFileQuery(final String filename)
  {
    eOverwriteValues eReturn =
        CommonMessages.showOverwriteMessageDialog(_shell,
            "Overwrite file?",
            filename);

    setOverwriteAll((eReturn == eOverwriteValues.YesToAll));

    return eReturn;
  }




	private List<IFileStore> buildFileStores() throws CoreException
	{
		List<IFileStore> stores = new ArrayList<>();
		
		for(File f : _srcFiles.keySet())
		{
			IFileStore store = EFS.getStore(f.toURI());
			
			if(store == null)
			{
				reportFileInfoNotFound(f.getAbsolutePath());
				return null;
			}
			
			stores.add(store);
		}

		return stores;
	}


//  /**
//   * Display the supplied status in an error dialog.
//   * 
//   * @param status
//   *          The status to display
//   */
//  private void displayError(final IStatus status)
//  {
//    _shell.getDisplay().syncExec(new Runnable() {
//      public void run()
//      {
//        ErrorDialog.openError(_shell, "Issues with File Copy", null, status);
//      }
//    });
//  }



	/**
	 * Checks whether the infos exist.
	 * 
	 * @param stores
	 *            the file infos to test
	 * @return Multi status with one error message for each missing file.
	 */
	IStatus checkExist(List<IFileStore> stores)
	{
		MultiStatus multiStatus = new MultiStatus(PlatformUI.PLUGIN_ID,
				IStatus.OK, "Issues with File Copy", null);
		
		stores.stream().filter(CopyFilesToWorkspaceJob::notExists).map(
				CopyFilesToWorkspaceJob::notFoundStatus).forEach(multiStatus::add);
		
		return multiStatus;
	}
	
	private static boolean notExists(IFileStore store)
	{
		return !store.fetchInfo().exists();
	}
	
	private static IStatus notFoundStatus(IFileStore store)
	{
		String message = "Resource was not found: " + store.getName();
		return new Status(IStatus.ERROR, PlatformUI.PLUGIN_ID, Status.OK, message, null);
	}

  /**
   * Closes a stream and ignores any resulting exception.
   */
  protected void safeClose(InputStream in)
  {
    try {
      if (in != null) in.close();
    }
    catch (IOException e) {
      // ignore
    }
  }




  /**
   * Closes a stream and ignores any resulting exception.
   */
  protected void safeClose(OutputStream out)
  {
    try {
      if (out != null) out.close();
    }
    catch (IOException e) {
      // ignore
    }
  }




  protected void error(int code, String message) throws CoreException
  {
    error(code, message, null);
  }




  protected void error(int code, String message, Throwable exception)
    throws CoreException
  {
    int severity = code == 0 ? 0 : 1 << (code % 100 / 33);
    throw new CoreException(new Status(severity,
        CommonPlugin.ID,
        code,
        message,
        exception));
  }




  /*
   * From Policy
   */
  protected IProgressMonitor monitorFor(IProgressMonitor monitor)
  {
    return monitor == null ? new NullProgressMonitor() : monitor;
  }




  /**
   * From Policy
   */
  protected IProgressMonitor subMonitorFor(IProgressMonitor monitor, int ticks)
  {
    if (monitor == null) return new NullProgressMonitor();
    if (monitor instanceof NullProgressMonitor) return monitor;
    return new SubProgressMonitor(monitor, ticks);
  }




  /**
   * Updates the attributes and last modified stamp on the destination object
   * based upon the source object
   */
  private void transferAttributes(IFileInfo sourceInfo, IFileStore destination)
    throws CoreException
  {
    int options = EFS.SET_ATTRIBUTES | EFS.SET_LAST_MODIFIED;
    destination.putInfo(sourceInfo, options, null);
  }




  /**
   * Transfers the contents of an input stream to an output stream, using a
   * large buffer.
   * 
   * @param source
   *          The input stream to transfer
   * @param destination
   *          The destination stream of the transfer
   * @param path
   *          A path representing the data being transferred for use in error
   *          messages.
   * @param monitor
   *          A progress monitor. The monitor is assumed to have already done
   *          beginWork with one unit of work allocated per buffer load of
   *          contents to be transferred.
   * @throws CoreException
   */
  private boolean transferStreams(InputStream source,
                                  OutputStream destination,
                                  String path,
                                  IProgressMonitor monitor)
    throws CoreException
  {
    monitor = monitorFor(monitor);
    boolean bCancelled = false;
    try {
      /*
       * Note: although synchronizing on the buffer is thread-safe, it may
       * result in slower performance in the future if we want to allow
       * concurrent writes.
       */
      while (!bCancelled && true) {
        int bytesRead = -1;
        try {
          bytesRead = source.read(buffer);
        }
        catch (IOException e) {
          String msg = "Failure during write on " + path;
          error(EFS.ERROR_READ, msg, e);
        }
        if (bytesRead == -1) break;
        try {
          destination.write(buffer, 0, bytesRead);
        }
        catch (IOException e) {
          String msg = "Unable to write to " + path;
          error(EFS.ERROR_WRITE, msg, e);
        }
        monitor.worked(1);

        // here is the big add: see if we are canceled
        if (monitor.isCanceled()) {
          bCancelled = true;
        }
      } // while
    }
    finally {
      safeClose(source);
      safeClose(destination);

    }

    return bCancelled;
  }




  /**
   * Copied from FileStore, to allow cancelling
   * 
   */
  protected void copyDirectory(IFileStore source,
                               IFileInfo sourceInfo,
                               IFileStore destination,
                               IProgressMonitor monitor) throws CoreException
  {
    try {
      IFileStore[] children = null;
      int opWork = 1;

      children = source.childStores(EFS.NONE, null);
      opWork += children.length;

      monitor.beginTask("", opWork); //$NON-NLS-1$
      monitor.subTask("Copying " + source.toString());
      // create directory
      destination.mkdir(EFS.NONE, subMonitorFor(monitor, 1));
      // copy attributes
      transferAttributes(sourceInfo, destination);


      // copy children
      for (int i = 0; i < children.length; i++) {
        children[i].copy(destination.getChild(children[i].getName()),
            EFS.NONE,
            subMonitorFor(monitor, 1));
      }
    }
    finally {
      monitor.done();
    }
  }




  /**
   * Imported from FileStore.java
   * <p/>
   * Copies a file as specified by
   * {@link IFileStore#copy(IFileStore, int, IProgressMonitor)}.
   * 
   * @param sourceInfo
   *          The current file information for the source of the move
   * @param destination
   *          The destination of the copy.
   * @param options
   *          bit-wise or of option flag constants ( {@link EFS#OVERWRITE} or
   *          {@link EFS#SHALLOW}).
   * @param monitor
   *          a progress monitor, or <code>null</code> if progress reporting and
   *          cancellation are not desired
   * @exception CoreException
   *              if this method fails. Reasons include:
   *              <ul>
   *              <li> This store does not exist.</li> <li> The <code>OVERWRITE
   *              </code> flag is not specified and a file of the same name
   *              already exists at the copy destination.</li> <li> A directory
   *              of the same name already exists at the copy destination.</li>
   *              </ul>
   */
  protected void copyFile(IFileStore source,
                          IFileInfo sourceInfo,
                          IFileStore destination,
                          IProgressMonitor monitor) throws CoreException
  {
    int options = (getOverwriteAll() == true ? EFS.OVERWRITE : EFS.NONE);

    InputStream in = null;
    OutputStream out = null;
    BufferedInputStream bis = null;
    BufferedOutputStream bos = null;
    MD5OutputStream mdOut = null;

    try {

      if (destination.fetchInfo().exists()) {
        if (!getOverwriteAll()) {
          // TODO: prompt
          eOverwriteValues choice = overwriteFileQuery(destination.getName());

          switch (choice) {
            case Yes:
              options = EFS.OVERWRITE;
              break;

            case YesToAll:
              options = EFS.OVERWRITE;
              setOverwriteAll(true);
              break;

            case No:
              return;

            case Cancel:
              monitor.setCanceled(true);
              return;
          }
        }

      } // if : destination file exists



      if ((options & EFS.OVERWRITE) == 0 && destination.fetchInfo().exists()) {
        IStatus status =
            new Status(Status.ERROR,
                CommonPlugin.ID,
                "File already exists: " + destination.getName(),
                new Exception());
        throw new CoreException(status);
      }

      long length = sourceInfo.getLength();
      int totalWork;
      if (length == -1) totalWork = IProgressMonitor.UNKNOWN;
      else
        totalWork = 1 + (int) (length / buffer.length);
      String sourcePath = source.toString();
      // monitor.beginTask(NLS.bind(Messages.copying, sourcePath), totalWork);
      monitor.beginTask("Copying " + sourcePath, totalWork);


      try {
        in = source.openInputStream(EFS.NONE, subMonitorFor(monitor, 0));
        out = destination.openOutputStream(EFS.NONE, subMonitorFor(monitor, 0));


        bis = new BufferedInputStream(in);
        bos = new BufferedOutputStream(out);
        mdOut = new MD5OutputStream(bos);

        boolean bTransferCanceled =
            transferStreams(bis, mdOut, sourcePath, monitor);

        if (bTransferCanceled) {
          try {
            destination.delete(EFS.NONE, null);
          }
          catch (Exception e) {
            String msg =
                "Failure on delete of " + source.getName() + " "
                    + "after cancel request";
            IStatus status = CommonPlugin.getDefault().newErrorStatus(msg, e);
            CommonPlugin.getDefault().log(status);
          }
          return;
        }

        transferAttributes(sourceInfo, destination);

        // Get the checksum; have to close for it to be accurate
        safeClose(mdOut);
        String newLocalChecksum = mdOut.getMD5().asHex();

        /* for each _resourceToUpdate                     */
        /*     if resource can be matched to sourceInfo   */
        /*         update the resource                    */
        refreshLocal(sourceInfo, monitor, newLocalChecksum);


        // TODO: need to call to.setVersionInfoInWorkspaceJob(...)
        // Job job = to.setVersion
        // job.join()
      }
      catch (CoreException e) {
        // if we failed to write, try to cleanup the half written file
        if (!destination.fetchInfo(0, null).exists()) destination.delete(EFS.NONE,
            null);
        throw e;
      }
    }
    finally {
      safeClose(bis);
      safeClose(mdOut);

      monitor.done();
    }
  }


  
  /**
   * Refresh any _resourcesToUpdate that can be matched up to sourceInfo.
   * <p>
   * <pre>
   * ALGORITHM:
   * for each _resourceToUpdate
   *     for resource can be matched to sourceInfo
   *         update the resources
   * </pre>
   * <p>
   * @param sourceInfo find all _resourcesToUpdate whose name
   * matches the name in sourceInfo.
   * @param monitor Display something on the screen.
   * @throws CoreException
   */
  protected void refreshLocal(
		  IFileInfo sourceInfo,
          IProgressMonitor monitor,
          String newLocalChecksum) 
          throws CoreException {
	  
      for (IResource localResource : _resourcesToUpdate) {
    	  
        String name = localResource.getName();
        String sourceName = sourceInfo.getName();

        
        if (!name.equals(sourceName)) {
          continue;
        }


        if (localResource != null) {
          localResource.refreshLocal(IResource.DEPTH_ZERO,
              new SubProgressMonitor(monitor,
                  30,
                  SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));

        } // if : we got a resource

      } // for
  }
  


  /**
   * Code lifted from FileStore.java
   */
  protected void copy(IFileStore source,
                      IFileStore destination,
                      IProgressMonitor monitor) throws CoreException
  {

    if (monitor.isCanceled()) {
      return;
    }

    final IFileInfo sourceInfo = source.fetchInfo(EFS.NONE, null);
    if (sourceInfo.isDirectory()) {
      copyDirectory(source, sourceInfo, destination, monitor);
    }
    else {
      copyFile(source, sourceInfo, destination, monitor);
    }
  }





	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.WorkspaceJob#runInWorkspace(org.eclipse.core
	 * .runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException
	{
		try
		{
			monitor.subTask("Building copy list");
			
			List<IFileStore> fileStores = buildFileStores();
			
			if(fileStores==null || fileStores.isEmpty())
			{
				return Status.CANCEL_STATUS;
			}
			
			IStatus fileStatus = checkExist(fileStores);
			if(fileStatus.getSeverity() != IStatus.OK)
			{
				throw new CoreException(fileStatus);
			}
			
			final IPath destinationPath = _destination.getLocation();
			IFileStore fsDestination = EFS.getStore(destinationPath.toFile().toURI());
			
			if(fsDestination == null)
			{
				IStatus status = new Status(Status.ERROR, CommonPlugin.ID,
						"Unable to find destination of " + destinationPath, new Exception());
				
				throw new CoreException(status);
			}
			
			String title = "Copy " + fileStores.size() + ((fileStores.size() == 1) ? " object" : " objects") +
					" " + "to " + _destination.getName();
			setName(title);
			
			// ensure the destination directory exists locally
			fsDestination.mkdir(EFS.NONE, monitor);
			
			// copy all of our stuff
			int iCurrent = 0;
			for(IFileStore fileStore : fileStores)
			{
				if(monitor.isCanceled())
				{
					return Status.CANCEL_STATUS;
				}
				
				++iCurrent;
				String subTaskDescription = "";
				if(fileStore.fetchInfo().isDirectory())
				{
					subTaskDescription += "directory ";
				} else {
					subTaskDescription += "file ";
				}
				subTaskDescription += iCurrent + " of " + fileStores.size() + " ";
				
				monitor.subTask(subTaskDescription);
				
				File srcFile = new File(fileStore.toURI());
				File destFolder = destinationPath.toFile();
				
				String destFileName = _srcFiles.get(srcFile);
				if(StringUtils.isBlank(destFileName))
				{
					destFileName = srcFile.getName();
				}
				
				File fDesintation = new File(destFolder, destFileName);
				
				IFileStore fullDest = EFS.getStore(fDesintation.toURI());
				
				// our copy, copied from FileStore, which should allow cancel
				// in the middle of a file
				copy(fileStore, fullDest, monitor);
			} // for

			// get the destination to refresh
			monitor.setCanceled(false);

			return Status.OK_STATUS;
		} // try
		finally {
			// have to check for aliases ourselves and refresh them first if
			// they don't exist. See DTA-8818
			IContainer[] locs = _destination.getWorkspace().getRoot().
					findContainersForLocationURI(_destination.getLocationURI(),
							IContainer.INCLUDE_HIDDEN);
			
			for(IContainer cont : locs)
			{
				if(!cont.exists())
				{
					cont.refreshLocal(IResource.DEPTH_ONE, monitor);
				}
			}
			_destination.refreshLocal(IResource.DEPTH_ONE, monitor);
		}
	} // runInWorkspace
}
