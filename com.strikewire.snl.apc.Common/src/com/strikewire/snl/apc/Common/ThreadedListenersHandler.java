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
 * Created by mjgibso on Apr 16, 2013 at 2:04:02 PM
 */
package com.strikewire.snl.apc.Common;

import gov.sandia.dart.common.core.listeners.AbstractListenersHandler;
import gov.sandia.dart.common.core.listeners.IBatchListenerNotifyHandler;
import gov.sandia.dart.common.core.listeners.IListenersProvider;
import gov.sandia.dart.common.core.listeners.LoggingListenerNotifyHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * @author mjgibso
 *
 */
public abstract class ThreadedListenersHandler<L, E> extends LoggingListenerNotifyHandler<L, E> implements
		IBatchListenerNotifyHandler<L, E>
{
	public static final String DEFAULT_JOB_NAME = "Notifying Listeners";
	
	private final Job _notifyJob;
	
	private final Queue<E> _eventQueue = new ConcurrentLinkedQueue<E>();
	
	private IListenersProvider<L> _listenersProvider = null;
	
	public ThreadedListenersHandler()
	{
		this(DEFAULT_JOB_NAME);
	}
	
	public ThreadedListenersHandler(String jobName)
	{
		this(jobName, false, true);
	}
	
	private static String getJobName(String jobName)
	{
		return StringUtils.isNotBlank(jobName) ? jobName : DEFAULT_JOB_NAME;
	}
	
	/**
	 * 
	 */
	public ThreadedListenersHandler(String jobName, boolean user, boolean system)
	{
		super("Error occurred: "+getJobName(jobName));
		
		_notifyJob = new Job(getJobName(jobName)) {
			
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				IListenersProvider<L> listenersProvider = _listenersProvider;
				if(listenersProvider == null)
				{
					throw new IllegalStateException("The listeners provider has not yet been initialized."
							+ "  Cannot broadcast events.");
				}
				
				E event;
				while((event = _eventQueue.poll()) != null)
				{
					AbstractListenersHandler.notifyListeners(listenersProvider, ThreadedListenersHandler.this, event);
				}
				return Status.OK_STATUS;
			}
		};
		_notifyJob.setUser(user);
		_notifyJob.setSystem(system);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IBatchListenerNotifyHandler#setListenerProvider(com.strikewire.snl.apc.Common.IListenersProvider)
	 */
	@Override
	public void setListenerProvider(IListenersProvider<L> listenerProvider)
	{
		this._listenersProvider = listenerProvider;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IBatchListenerNotifyHandler#notifyListeners(java.lang.Object)
	 */
	@Override
	public void notifyListeners(E event)
	{
		_eventQueue.add(event);
		
		_notifyJob.schedule();
	}
}
