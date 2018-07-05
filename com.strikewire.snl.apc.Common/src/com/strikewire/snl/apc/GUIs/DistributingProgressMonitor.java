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
 * Created on Oct 9, 2008 at 8:48:59 AM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author mjgibso
 *
 * AJR - 11/30/16 - This Class NEEDS to make all of it's calls to the internal
 * ProgressMonitors synchronously because the internal eclipse ProgressManager
 * expects synchronous operation. If the calls were done asynchronously, then 
 * there is the potential for a memory leak due to race conditions, most noticeably 
 * when the done() method for this class gets called.
 */
public class DistributingProgressMonitor implements IProgressMonitor
{
	private final List<IProgressMonitor> monitors_;
	
	public DistributingProgressMonitor(IProgressMonitor monitor)
	{
		if(monitor == null)
		{
			throw new IllegalArgumentException("Cannot accept a null progress monitor");
		}
		
		this.monitors_ = new ArrayList<IProgressMonitor>();
		this.monitors_.add(monitor);
	}
	
	public DistributingProgressMonitor(List<IProgressMonitor> monitors)
	{
		if(monitors == null)
		{
			throw new IllegalArgumentException("Cannot accept a null list of progress monitors");
		}
		
		this.monitors_ = new ArrayList<IProgressMonitor>(monitors);
	}
	
	public void addProgressMonitor(IProgressMonitor monitor)
	{ this.monitors_.add(monitor); }
	
	public void removeProgressMonitor(IProgressMonitor monitor)
	{ this.monitors_.remove(monitor); }

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
	 */
	public void beginTask(final String name, final int totalWork)
	{
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.beginTask(name, totalWork);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#done()
	 */
	public void done()
	{
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.done();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
	 */
	public void internalWorked(final double work)
	{
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.internalWorked(work);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
	 */
	public boolean isCanceled()
	{
		return isCanceledInternal();
	}
	
	private boolean isCanceledInternal()
	{
		for(IProgressMonitor monitor : monitors_)
		{
			if(monitor.isCanceled())
			{
				return true;
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
	 */
	public void setCanceled(final boolean value)
	{
		System.out.println("set cancelled");
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.setCanceled(value);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
	 */
	public void setTaskName(final String name)
	{
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.setTaskName(name);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
	 */
	public void subTask(final String name)
	{
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.subTask(name);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
	 */
	public void worked(final int work)
	{
		for(IProgressMonitor monitor : monitors_)
		{
			monitor.worked(work);
		}
	}
}
