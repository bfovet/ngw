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
 * Created by mjgibso on Jun 26, 2015 at 6:50:28 AM
 */
package com.strikewire.snl.apc.Common;

import gov.sandia.dart.common.core.listeners.IListenerNotifyHandler;
import gov.sandia.dart.common.core.listeners.ListenersHandler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.strikewire.snl.apc.util.Tuple;

/**
 * @author mjgibso
 *
 */
public class AtomicTreeListenersHandler<L, E extends ITreeElementEvent> extends ListenersHandler<L, E> implements IAtomicTreeListenersHandler
{
	// TODO merry this class and AtomicListenersHandler in such a way that there could be one handler that would
	// support both atomic by thread, and by tree branch simultaneously.
	
	private Set<ThreadElementTuple> _atomicOperations = new HashSet<ThreadElementTuple>();
	
	// keep just one event queue to preserve order and potentially block event dispatching as desired corresponding to 'locked' branches
	// of the tree.  i.e, even if an operation is ended which would free some events in the queue to be dispatched, if there
	// are other events in the queue ahead of them that are not yet allowed to be dispatched, don't dispatch the free ones yet,
	// if specified, potentially then dispatching events out of order.
	private final Queue<E> _eventQueue = new ConcurrentLinkedQueue<E>();
	
	private final boolean _preserveOrder;
	
	public static final boolean DEFAULT_PRESERVE_ORDER = false;
	
	/**
	 * Equivalent to calling {@link #AtomicTreeListenersHandler(IListenerNotifyHandler, boolean)} and passing
	 * {@link #DEFAULT_PRESERVE_ORDER} for the <b>preserveOrder</b> argument.
	 */
	public AtomicTreeListenersHandler(IListenerNotifyHandler<L, E> notifyHandler)
	{
		this(notifyHandler, DEFAULT_PRESERVE_ORDER);
	}
	
	/**
	 * Consider the following tree:
	 * <code>
	 * </BR>A
	 * </BR>|-B
	 * </BR>&nbsp&nbsp|-C
	 * </code>
	 * </BR>
	 * Consider the following scenario: B & C are locked (have 'atomic' operations running for them).  An event
	 * comes in that is for C, then an event comes in that is for B.  Both events are queued up as they're for
	 * 'locked', or 'atomic' branches of the tree.  Now {@link #endAtomicOperation(ITreeElement)} is called for
	 * B.  If <b>preserveOrder</b> is true, then the event for B won't be fired, as it occurred after the event
	 * for C, which is not yet free.  So it won't be dispatched until the 'lock'/'atomic operation' is completed
	 * for C.  Alternatively if <b>preserveOrder</b> is false, then as soon as {@link #endAtomicOperation(ITreeElement)}
	 * for B is called, the event for B will be broadcast, and the event for C won't be broadcast until
	 * {@link #endAtomicOperation(ITreeElement)} is called for C, thus potentially dispatching events out of order.
	 */
	public AtomicTreeListenersHandler(IListenerNotifyHandler<L, E> notifyHandler, boolean preserveOrder)
	{
		super(notifyHandler);
		
		this._preserveOrder = preserveOrder;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IAtomicTreeListenersHandler#beginAtomicOperation(com.strikewire.snl.apc.Common.ITreeElement)
	 */
	@Override
	public boolean beginAtomicOperation(ITreeElement element)
	{
		synchronized (_eventQueue) {
			boolean added = _atomicOperations.add(new ThreadElementTuple(Thread.currentThread(), element));
			if(!added)
			{
				// TODO should this throw an exception instead?
				CommonPlugin.getDefault().logWarning("Beginning an atomic operation that is already registered.", new Exception());
			}
			return added;
		}
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IAtomicTreeListenersHandler#endAtomicOperation(com.strikewire.snl.apc.Common.ITreeElement)
	 */
	@Override
	public boolean endAtomicOperation(ITreeElement element)
	{
		synchronized (_eventQueue) {
			boolean removed = _atomicOperations.remove(new ThreadElementTuple(Thread.currentThread(), element));
			
			if(!removed)
			{
				// TODO should this throw an exception instead?
				CommonPlugin.getDefault().logWarning("Ended an atomic operation that wasn't registered.", new Exception());
			}
			
			
			Iterator<E> iter = _eventQueue.iterator();
			while(iter.hasNext())
			{
				E event = iter.next();
				// See if this event can be dispatched or not...
				if(isBlocked(event))
				{
					// This event can't be dispatched yet.  If we're to preserve order, we can't dispatch any of
					// the other events in the queue, as that would break order, so bail out.  Otherwise we can
					// continue on to the next in case there are others later in the queue which are now released.
					if(_preserveOrder)
					{
						break;
					}
				} else {
					// This event is now free to be dispatched, so take it and queue it to be dispatched.  Note
					// we're extending the ThreadedListenerHandler, so this shouldn't be a long-running, or
					// potentially thread-blocking call, which we wouldn't want to have given we're currently
					// synchronized on our event queue.
					iter.remove();
					super.notifyListeners(event);
				}
			} // end while
			
			return removed;
		} // end synchronization
	}
	
	protected boolean addToQueue(E event)
	{
		synchronized (_eventQueue) {
			// If we're to preserve order, and there are events in the queue now, we have to put this
			// event in the queue behind what's there, even if this event wouldn't be blocked by an
			// existing atomic operation.
			if(_preserveOrder && !_eventQueue.isEmpty())
			{
				_eventQueue.add(event);
				return true;
			}
			
			// Otherwise, we don't care about order, or we don't have any events queued up yet, so only
			// queue it up if it's an event that is for a branch of the tree that is current locked performing
			// an atomic operation.
			if(isBlocked(event))
			{
				_eventQueue.add(event);
				return true;
			}
			
			// didn't get added to the queue...
			return false;
		}
	}
	
	protected boolean isBlocked(E event)
	{
		ITreeElement element = event.getTreeElement();
		synchronized(_eventQueue)
		{
			for(ThreadElementTuple atomic : _atomicOperations)
			{
				// This event is blocked if we're currently blocking all events (we have a null atomic operation
				// element), or this event is for a currently 'locked' node of the tree, or is under a currently
				// 'locked' node of the tree.
				if(atomic.getRight()==null || ITreeElement.isEqualOrDescendant(atomic.getRight(), element))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	public void notifyListeners(E event)
	{
		if(!addToQueue(event))
		{
			super.notifyListeners(event);
		}
	}
	
	private static class ThreadElementTuple extends Tuple<Thread, ITreeElement>
	{
		/**
		 * @param left
		 * @param right
		 */
		public ThreadElementTuple(Thread left, ITreeElement right)
		{
			super(left, right);
		}
	}
}
