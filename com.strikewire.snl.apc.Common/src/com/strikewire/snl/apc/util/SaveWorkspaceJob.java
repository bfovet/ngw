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
 * Created by mjgibso on Jan 19, 2010 at 6:30:41 AM
 */
package com.strikewire.snl.apc.util;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author mjgibso
 *
 */
public class SaveWorkspaceJob extends Job
{	
	/** 60 seconds */
	private static final int SAVE_INTERVAL = 60 * 1000;
	
	private static final SaveWorkspaceJob instance_ = new SaveWorkspaceJob();
	
	private SaveWorkspaceJob()
	{
		super("Saving Workspace...");
		
		setUser(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor)
	{
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			return workspace.save(true, monitor);
		} catch (CoreException ce) {
			return ce.getStatus();
		}
	}
	
	public static synchronized void scheduleSave()
	{ getInstance().schedule(SAVE_INTERVAL); }
	
	private static SaveWorkspaceJob getInstance()
	{
		return instance_;
	}
}
