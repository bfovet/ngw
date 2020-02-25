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
 * Created by mjgibso on Jun 7, 2017 at 11:32:31 AM
 */
package com.strikewire.snl.apc.util;

/**
 * This is a simple class that provides delayed single execution of a given runnable target.
 * When the run method is called, the target is executed after the given delay time has been
 * reached.  If the run method is called during the waiting period, the waiting period starts
 * over such that if multiple calls to the run method are made within the given delay time,
 * only one call will be made to the given target's run method, which will happen the specified
 * delay time after the last call to the run method was made.
 * 
 * @author Marcus Gibson
 */
public interface IDelayedRunner
{
	public static final long DEFAULT_DELAY = 2000;
	
	/**
	 * When the schedule method is called, the target is executed after the given delay time has been
	 * reached.  If the schedule method is called during the waiting period, the waiting period starts
	 * over such that if multiple calls to the run method are made within the given delay time,
	 * only one call will be made to the given target's run method, which will happen the specified
	 * delay time after the last call to the run method was made.
	 */
	void schedule();
	
	/**
	 * If called after a call to {@link #schedule()}, but before the specified delay has been reached,
	 * the provided runnable target will not be run.
	 * 
	 * If called while the provided runnable target is running, after it has been run (and before
	 * another call to {@link #schedule()} has been made), or before a call to {@link #schedule()} is
	 * ever made, has no effect.
	 */
	void stop();
	
	/**
	 * Returns true if the given target is waiting to be run or is running.
	 */
	boolean isScheduledOrRunning();
	
	/**
	 * Uses default delay time {@link #DEFAULT_DELAY}.
	 * 
	 * @param target - the target to be run after the given delay
	 */
	static IDelayedRunner newRunner(Runnable target)
	{
		return newRunner(target, DEFAULT_DELAY);
	}
	
	static IDelayedRunner newRunner(Runnable target, long delay)
	{
		return new DelayExec(target, delay);
	}
	
	static void run(Runnable target, long delay)
	{
		newRunner(target, delay).schedule();
	}
}
