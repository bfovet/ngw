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
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.Utils;

/**
 * @author mjgibso
 *
 */
public abstract class TransferFilesJob<K extends ICancelable, V> extends Job
{
	private final Map<K, V> queuedFiles_ = new HashMap<>();
	
	private IStatus status_ = Status.OK_STATUS;
	
	protected TransferFilesJob(String title)
	{
		super(title);
	}
	
	@Override
	protected final IStatus run(IProgressMonitor monitor)
	{
		monitor = TimedProgressMonitor.getTimedProgressMonitor(monitor);
		
		try {
			queueJobs(monitor, new JobChangeAdapter() {
				@Override
				public void done(IJobChangeEvent event) {
					jobFinished(event);
				}
			});
		} catch (CoreException e) {
			return e.getStatus();
		}
		
		monitor.setTaskName(getName()+"...");
		
		// now that they're all queued up, wait until they're all done
		// we need to do this for two reasons:
		// 1. we want to be able to return the complete merged status from all jobs
		// 2. if we cancel the overall monitor, the individually running jobs need to be able to call the
		//    isCancelled() method on the monitor and have it return true.  If we return, and this job
		//    completes, then subsequent calls to this monitor's isCancelled method will return false.
		while(getNumQueuedFiles() > 0)
		{
//			System.out.println("Waiting on queued jobs: "+getNumQueuedFiles());
			if(monitor.isCanceled())
			{
				monitor.subTask("Cancelling...");
				for(K k : new ArrayList<>(queuedFiles_.keySet()))
				{
					if(k.isCancelled())
					{
						removeQueuedFile(k);
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// shouldn't get here
				CommonPlugin.getDefault().logError("Transfer job interrupted: "+e.getMessage(), e);
			}
		}
		
		if(monitor.isCanceled())
		{
			mergeStatus(Status.CANCEL_STATUS);
		}
		
		return status_;
	}
	
	protected abstract void queueJobs(IProgressMonitor monitor, IJobChangeListener listener) throws CoreException;
	
	protected void createAndQueueFile(QueuedFileCreator creator, V file) throws CoreException
	{
		synchronized (queuedFiles_) {
			queuedFiles_.put(creator.createAndQueueObject(), file);
		}
	}
	
//	protected void addQueuedFile(K k, V file)
//	{
//		synchronized (queuedFiles_) {
//			queuedFiles_.put(k, file);
//		}
//	}
	
	protected V removeQueuedFile(K k)
	{
		synchronized (queuedFiles_) {
			return queuedFiles_.remove(k);
		}
	}
	
	protected V getQueuedFile(K k)
	{
		synchronized (queuedFiles_) {
			return queuedFiles_.get(k);
		}
	}
	
	protected int getNumQueuedFiles()
	{
		synchronized (queuedFiles_) {
			return queuedFiles_.size();
		}
	}
	
	protected abstract void jobFinished(IJobChangeEvent event);
	
	protected abstract String getPluginID();
	
	protected void mergeStatus(IStatus status)
	{
		this.status_ = Utils.mergeStatus(status_, status, getPluginID(), "Multiple errors or warnings "+getName()+".");
	}
	
	protected abstract class QueuedFileCreator
	{
		public abstract K createAndQueueObject() throws CoreException;
	}
}
