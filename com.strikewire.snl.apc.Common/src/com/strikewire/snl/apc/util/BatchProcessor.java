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
 * Created by mjgibso on Jul 31, 2014 at 3:47:02 PM
 */
package com.strikewire.snl.apc.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public abstract class BatchProcessor<E>
{
	// TODO would some implementors not want the 'set' implementation, and instead prefer a queue?  Maybe they'd want a priority queue instead of fifo or even lifo etc?
	
	// TODO should we be using a synchronous queue or something instead of doing the synchronization ourselves?
	
	private final long _delay;
	
	private final Job _job;
	
	private final Set<E> _elements = new LinkedHashSet<E>();
	
	/**
	 * 
	 */
	public BatchProcessor(String jobName, int delay)
	{
		this._job = new InternalJob(jobName);
		this._delay = delay;
	}
	
	public void queue(E element)
	{
		List<E> list = new ArrayList<E>(1);
		list.add(element);
		queue(list);
	}
	
	public void queue(Collection<E> elements)
	{
		synchronized (_elements) {
			_elements.addAll(elements);
			schedule();
		}
	}
	
	protected void schedule()
	{
		_job.schedule(_delay);
	}
	
	protected abstract void process(Collection<E> elements, IProgressMonitor monitor);
	
	protected Collection<E> drainElements()
	{
		synchronized (_elements) {
			List<E> ret = new ArrayList<E>(_elements);
			_elements.clear();
			return ret;
		}
	}
	
	private class InternalJob extends Job
	{
		/**
		 * 
		 */
		public InternalJob(String name)
		{
			super(name);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor)
		{
			// TODO better error handling and status reporting
			try {
				process(drainElements(), monitor);
			} catch(Throwable t) {
				CommonPlugin.getDefault().logError(t);
			}
			return Status.OK_STATUS;
			
		}
	}
}
