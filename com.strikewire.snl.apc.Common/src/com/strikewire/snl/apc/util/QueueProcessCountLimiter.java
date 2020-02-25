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
 * Created by mjgibso on Sep 14, 2016 at 8:29:46 PM
 */
package com.strikewire.snl.apc.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author mjgibso
 *
 */
public class QueueProcessCountLimiter implements IProcessCountLimiter
{
	private final BlockingQueue<Object> _rightQueue;
	
	/**
	 * 
	 */
	public QueueProcessCountLimiter(int processCountLimit)
	{
		this._rightQueue = new LinkedBlockingQueue<Object>(processCountLimit);
		while(this._rightQueue.offer(new Object()));
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.IProcessCountLimiter#getProcessRight()
	 */
	@Override
	public void getProcessRight() throws InterruptedException
	{
		_rightQueue.take();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.IProcessCountLimiter#returnProcessRight()
	 */
	@Override
	public void returnProcessRight()
	{
		_rightQueue.add(new Object());
	}
}
