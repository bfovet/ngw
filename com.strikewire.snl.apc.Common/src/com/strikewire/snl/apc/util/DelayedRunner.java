/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
///*******************************************************************************
// * Sandia Analysis Workbench Integration Framework (SAW)
// * Copyright 2016 Sandia Corporation. Under the terms of Contract
// * DE-AC04-94AL85000 with Sandia Corporation, the U.S. Government
// * retains certain rights in this software.
// * 
// * This software is distributed under the Eclipse Public License.
// * For more information see the files copyright.txt and license.txt
// * included with the software.
// ******************************************************************************/
///*
// * Created by Marcus Gibson
// * On Apr 20, 2006 at 12:40:22 PM
// */
//package com.strikewire.snl.apc.util;
//
//
//class DelayedRunner implements IDelayedRunner
//{
//	private Thread thread_;
//	
//	private final Runnable target_;
//	private final long delay_;
//	private long delayStartTimeStamp_;
//	private boolean stop_ = true;
//	private volatile boolean running_ = false;
//	
//	/**
//	 * @param target - the target to be run after the given delay
//	 * @param delay - the delay (in milliseconds) to wait prior to running the given target
//	 */
//	DelayedRunner(Runnable target, long delay)
//	{
//		this.target_ = target;
//		this.delay_ = delay;
//	}
//
//	public synchronized void schedule()
//	{
//		delayStartTimeStamp_ = System.currentTimeMillis();
//		this.stop_ = false;
//		
//		if(thread_ == null)
//		{
//			thread_ = new Thread(new Waiter(), "DelayedRunner: "+target_);
//			thread_.start();
//		}
//	}
//	
//	public synchronized void stop()
//	{
//		this.stop_ = true;
//		
//		if(thread_ != null)
//		{
//			thread_.interrupt();
//		}
//	}
//	
//	private class Waiter implements Runnable
//	{
//		/* (non-Javadoc)
//		 * @see java.lang.Runnable#run()
//		 */
//		@Override
//		public void run()
//		{
//			while(!tryShutdownThread())
//			{
//				long timeLeft = getTimeRemaining();
//				if(timeLeft > 0)
//				{
//					try {
//						Thread.sleep(timeLeft);
//					} catch (InterruptedException e) {
//						Thread.interrupted();
//					}
//				}
//				
//				try {
//					if(shouldRunTarget())
//					{
//							target_.run();
//					}
//				} finally {
//					running_ = false;
//				}
//			}
//		}
//	}
//	
//	/* (non-Javadoc)
//	 * @see com.strikewire.snl.apc.util.IDelayedRunner#isScheduledOrRunning()
//	 */
//	@Override
//	public synchronized boolean isScheduledOrRunning()
//	{
//		return thread_!=null || running_;
//	}
//	
//	private synchronized boolean tryShutdownThread()
//	{
//		if(stop_)
//		{
//			thread_ = null;
//			return true;
//		} else {
//			return false;
//		}
//	}
//	
//	private synchronized long getTimeRemaining()
//	{ return delay_ - (System.currentTimeMillis() - delayStartTimeStamp_); }
//	
//	private synchronized boolean shouldRunTarget()
//	{
//		// We should run if we're not supposed to stop, and we've elapsed enough time
//		if(!stop_ && getTimeRemaining()<=0)
//		{
//			// if we're going to say 'yes', which will kick off a run,
//			// also set 'stop' so it will be done after that.  This provided
//			// another call to schedule isn't made prior to finishing
//			// the run that will result from the 'yes' answer.  This would
//			// result in another run being scheduled to occur after the
//			// first run completes or the delay has been reached, which
//			// ever comes later.
//			
//			stop_ = true;
//			
//			// since we're going to for sure run as a result of this 'true'
//			// return, set running now while we're still synchronized.
//			running_ = true;
//			
//			return true;
//		} else {
//			return false;
//		}
//	}
//}
