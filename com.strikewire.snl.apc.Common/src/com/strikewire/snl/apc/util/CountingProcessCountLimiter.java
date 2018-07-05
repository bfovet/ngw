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
 * Created by mjgibso on Sep 14, 2016 at 8:44:50 PM
 */
package com.strikewire.snl.apc.util;

import org.apache.commons.lang3.mutable.MutableInt;

/**
 * @author mjgibso
 *
 */
public class CountingProcessCountLimiter implements IProcessCountLimiter
{
	protected final MutableInt _runningCount = new MutableInt(0);
	
	private volatile int _maxProcessCount;
	
	/**
	 * 
	 */
	public CountingProcessCountLimiter(int maxProcessCount)
	{
		this._maxProcessCount = maxProcessCount;
	}
	
	public int getMaxProcessCount()
	{
		return _maxProcessCount;
	}
	
	public void setMaxProcessCount(int maxProcessCount)
	{
		this._maxProcessCount = maxProcessCount;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.IProcessCountLimiter#getProcessRight()
	 */
	@Override
	public void getProcessRight() throws InterruptedException
	{
		synchronized(_runningCount)
		{
			while(_runningCount.getValue() >= this._maxProcessCount)
			{
				_runningCount.wait();
			}
			
			_runningCount.increment();
		}
	}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.IProcessCountLimiter#returnProcessRight()
	 */
	@Override
	public void returnProcessRight()
	{
		synchronized(_runningCount)
		{
			_runningCount.decrement();
			_runningCount.notifyAll();
		}
	}

}
