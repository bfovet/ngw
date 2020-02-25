/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.util;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class DelayExec implements IDelayedRunner
{
	private final ScheduledExecutorService _pool;

	private final long _delayInMillis;
	private final Runnable _task;
	private final Object _lock = new Object();

	private ScheduledFuture<?> _scheduledTask = null;
	
	DelayExec(Runnable task, long delay)
	{
		_delayInMillis = delay;
		_task = task;
		
		_pool = makeThreadPool();
	}
	
	private static ScheduledExecutorService makeThreadPool()
	{
		// construct a ScheduledThreadPoolExeuctor directly ourselves instead of
		// calling Executors.newScheduledThreadPool(0), because we need to be able
		// to adjust the keepAliveTime (see below)
		
		
		// specify a corePoolSize of 0 so we kill off threads when they're not in use
		ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(0);
		
		// set the max pool size to 1 so we'll never create more than one thread.  This is
		// technically not necessary since the way we control scheduling, there'll never
		// be more than one thing scheduled, and I don't believe the executor will ever
		// construct more threads than there are things scheduled...
		pool.setMaximumPoolSize(1);
		
		// if we don't set keep alive to a reasonable value with a corePoolSize of 0, then
		// after scheduling a task, while it's waiting to run the task, it constantly polls
		// to see if there's a task or if it can kill off the thread.
		pool.setKeepAliveTime(5, TimeUnit.SECONDS);
		
		return pool;
	}
	
	public void schedule()
	{
		synchronized(_lock)
		{
			stop();
			_scheduledTask = _pool.schedule(_task, _delayInMillis, TimeUnit.MILLISECONDS);
		}
	}
	
	public void stop()
	{
		synchronized(_lock)
		{
			if(_scheduledTask != null)
			{
				_scheduledTask.cancel(false);
				_scheduledTask = null;
			}
		}
	}
	
	public boolean isScheduledOrRunning()
	{
		synchronized(_lock)
		{
			return _scheduledTask!=null ? !_scheduledTask.isDone() : false;
		}
	}
}
