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
 * Created by mjgibso on Jul 30, 2013 at 2:33:47 PM
 */
package com.strikewire.snl.apc.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.util.SWdebug;

/**
 * @author mjgibso
 * 
 * @deprecated probably preferable to use the java Executor service.
 *
 */
public abstract class AbstractProducerConsumerManager<E>
{
	public static enum Order
	{
		FIFO,
		LIFO,
	}
	
	protected final String processName_;
	
	protected final BlockingQueue<E> queue_;
	protected int tasksToProcess_;
	
	protected final List<Consumer> consumers_ = new ArrayList<Consumer>();
	
	protected final Order order_;
	
	protected final IPreferenceStore prefStore_;
	protected final String prefKey_;
	
	/**
	 * Convenience constructor for {@link #AbstractProducerConsumerManager(String, int, Order)}, offering FIFO for the order
	 */
	protected AbstractProducerConsumerManager(String processName, int consumerCount)
	{
		this(processName, consumerCount, Order.FIFO);
	}
	
	protected AbstractProducerConsumerManager(String processName, int consumerCount, Order order)
	{
		this(processName, consumerCount, order, null);
	}
	
	protected AbstractProducerConsumerManager(String processName, int consumerCount, Comparator<E> comparator)
	{
		this(processName, consumerCount, null, comparator);
	}
	
	private AbstractProducerConsumerManager(String processName, int consumerCount, Order order, Comparator<E> comparator)
	{
		this(processName, order, comparator, null, null);
		
		adjustConsumerCount(consumerCount);
	}
	
	protected AbstractProducerConsumerManager(String processName, Order order, IPreferenceStore prefStore, String prefKey)
	{
		this(processName, order, null, prefStore, prefKey);
	}
	
	protected AbstractProducerConsumerManager(String processName, Comparator<E> comparator, IPreferenceStore prefStore, String prefKey)
	{
		this(processName, null, comparator, prefStore, prefKey);
	}
	
	private AbstractProducerConsumerManager(String processName, Order order, Comparator<E> comparator, IPreferenceStore prefStore, String prefKey)
	{
		this.processName_ = processName;
		
		if(comparator != null)
		{
			// use default initial size
			this.queue_ = new PriorityBlockingQueue<E>(11, comparator);
			this.order_ = null;
		} else {
			this.queue_ = new LinkedBlockingDeque<E>();
			this.order_ = order;
		}
		
		this.prefStore_ = prefStore;
		this.prefKey_ = prefKey;
		
		if(prefStore!=null && prefKey!=null)
		{
			hookPrefListener();
			
			initConsumerThreads();
		}
	}
	
	protected void hookPrefListener()
	{
		this.prefStore_.addPropertyChangeListener(new IPropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				String pref = event.getProperty();
				if(StringUtils.equals(pref, prefKey_))
				{
					initConsumerThreads();
				}
			}
		});
	}
	
	protected void initConsumerThreads()
	{
		int numThreads = this.prefStore_.getInt(prefKey_);
		adjustConsumerCount(numThreads);
	}
	
	public boolean queue(E element)
	{
		tasksToProcess_++;
		
		boolean queued = false;
		try {
			queued = queueInternal(element);
		} finally {
			if(!queued)
			{
				tasksToProcess_--;
			}
		}
		
		return queued;
	}
	
	private boolean queueInternal(E element)
	{
		// don't put it in the queue if it's already in there
		// TODO is there any reason any implementor of this wouldn't want us to check for this?  i.e., should we offer
		// an option here or as a configuration (at constructor, or later specified) to control this behavior.
		if(queue_.contains(element))
		{
			return false;
		}
		
		if(Order.LIFO==order_ && queue_ instanceof BlockingDeque)
		{
			// if order is LIFO, need to cast to a deque and add at the other end
			return ((BlockingDeque<E>) queue_).offerFirst(element);
		} else {
			return queue_.offer(element);
		}
	}
	
//	public void waitForEmptyQueue()
//	{
//		while(!this.queue_.isEmpty())
//		{
//			this.queue_.wait();
//		}
//	}
	
	public boolean isProcessing()
	{
		return tasksToProcess_ > 0;
	}
	
	public void waitForIdle() throws InterruptedException
	{
		while(isProcessing())
		{
			Thread.sleep(100);
		}
	}
	
	public int getQueueSize()
	{
		return this.queue_.size();
	}
	
	public void adjustConsumerCount(int numConsumers)
	{
		synchronized (consumers_) {
			while(consumers_.size() != numConsumers)
			{
				if(consumers_.size() < numConsumers)
				{
					consumers_.add(new Consumer(consumers_.size() + 1));
				} else {
					consumers_.remove(consumers_.size()-1).die();
				}
			}
		}
	}
	
	protected abstract void process(E element);
	
	private class Consumer implements Runnable
	{
		private Thread thread_;
		private boolean die_ = false;
		
		private Consumer(int id)
		{
			thread_ = new Thread(this, processName_+' '+id);
			thread_.start();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run()
		{
			while(!this.die_)
			{
				E e = null;
				try {
					e = queue_.take();
					try {
						process(e);
					} finally {
						tasksToProcess_--;
					}
				} catch (Throwable t) {
					if(t instanceof InterruptedException)
					{
						String msg = thread_.getName()+" thread interrupted";
						SWdebug.msg(msg);
						CommonPlugin.getDefault().logError(msg, t);
						break;
					} else if(t instanceof CoreException) {
						CommonPlugin.getDefault().log(((CoreException) t).getStatus());
					} else {
						CommonPlugin.getDefault().logError(thread_.getName()+" Error processing "+e, t);
					}
				}
			}
		}
		
		private void die()
		{
			this.die_ = true;
			thread_.interrupt();
		}
	}
}
