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
 * Created by mjgibso on Sep 15, 2016 at 12:06:06 AM
 */
package com.strikewire.snl.apc.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author mjgibso
 *
 */
public class FairCountingProcessCountLimiter extends CountingProcessCountLimiter
{
	private final Queue<Thread> requestOrder = new ConcurrentLinkedQueue<>();
	
	/**
	 * @param maxProcessCount
	 */
	public FairCountingProcessCountLimiter(int maxProcessCount)
	{
		super(maxProcessCount);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.CountingProcessCountLimiter#getProcessRight()
	 */
	@Override
	public void getProcessRight() throws InterruptedException
	{
		synchronized(_runningCount)
		{
			requestOrder.add(Thread.currentThread());
			super.getProcessRight();
			while(requestOrder.peek() != Thread.currentThread())
			{
//				System.out.println("  Wasn't next to run: "+Thread.currentThread().getName());
				returnProcessRight();
				_runningCount.wait();
				super.getProcessRight();
			}
			requestOrder.poll();
		}
	}
}
