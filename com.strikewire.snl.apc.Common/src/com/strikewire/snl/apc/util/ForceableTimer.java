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
 * Create on Mar 24, 2012 at 1:23:49 PM by mjgibso
 */
package com.strikewire.snl.apc.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IJobRunnable;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class ForceableTimer extends JobChangeAdapter
{
	private IDelayedRunner timer_;
	
	private Runnable timerAction_ = new Runnable() {
		
		@Override
		public void run()
		{
			runInternal(true);
		}
	};
	
	private final Job target_;
	
	private boolean running_ = false;
	private boolean reschedule_ = false;
	private boolean shutdown_ = false;
	
	public ForceableTimer(final IJobRunnable target, String jobName, long time)
	{
		this.target_ = new Job(jobName) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				return target.run(monitor);
			}
		};
		this.target_.addJobChangeListener(this);
		
		set(time);
	}
	
	public synchronized void shutdown()
	{
		shutdown_ = true;
		stop();
		cancel();
	}
	
	public boolean cancel()
	{
		return target_.cancel();
	}
	
	public synchronized void stop()
	{
		if(timer_ != null)
		{
			timer_.stop();
		}
	}
	
	public synchronized void start()
	{
		if(running_ || shutdown_)
		{
			return;
		}
		
		if(timer_ != null)
		{
			timer_.schedule();
		}
	}
	
	public synchronized void set(long time)
	{
		stop();
		
		if(time > 0)
		{
			timer_ = IDelayedRunner.newRunner(timerAction_, time);
		} else {
			timer_ = null;
		}
		
		start();
	}
	
	public synchronized void runInUIOrJoinMonitorDialog(Shell userRequestShell)
	{
		if(shutdown_)
		{
			return;
		}
		
		if(running_)
		{
			PlatformUI.getWorkbench().getProgressService().showInDialog(userRequestShell, target_);
		} else {
			target_.setUser(true);
			runInternal(false);
		}
	}
	
	public synchronized void run()
	{
		runInternal(false);
	}
	
	private synchronized void runInternal(boolean runByTimer)
	{
		if(shutdown_)
		{
			return;
		}
		
		// if we're running because the timer went off, but the timer was supposed to be stopped
		// (if it's supposed to be stopped, we would have called cancel on it, and
		// isScheduledOrRunning would return false)
		if(timer_!=null && runByTimer && !timer_.isScheduledOrRunning())
		{
			// don't run
			return;
		}
		
		if(running_)
		{
			reschedule_ = true;
		} else {
			doRun();
		}
	}
	
	private synchronized void doRun()
	{
		if(shutdown_)
		{
			return;
		}
		
		running_ = true;
		
		// stop the timer
		stop();
		
		// run the target
		try {
			target_.schedule();
		} catch (IllegalStateException ise) {
			// Ooops, the JobManager is shut down already. No harm done.
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.jobs.JobChangeAdapter#done(org.eclipse.core.runtime.jobs.IJobChangeEvent)
	 */
	@Override
	public synchronized void done(IJobChangeEvent event)
	{
		running_ = false;
		
		try {
			target_.setUser(false);
		} catch(Throwable th) {
			IStatus warn = CommonPlugin.getDefault().newWarningStatus("Unable to set job back to non-user: "+target_.getName(), th);
			CommonPlugin.getDefault().log(warn);
		}
		
		if(reschedule_)
		{
			doRun();
			reschedule_ = false;
			return;
		}
		
		// start the timer back up
		start();
	}
}
