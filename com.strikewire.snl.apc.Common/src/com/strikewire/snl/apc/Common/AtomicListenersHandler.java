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
 * Created by mjgibso on Jun 26, 2015 at 6:38:12 AM
 */
package com.strikewire.snl.apc.Common;

import gov.sandia.dart.common.core.listeners.IListenerNotifyHandler;
import gov.sandia.dart.common.core.listeners.ListenersHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author mjgibso
 *
 */
public class AtomicListenersHandler<L, E> extends ListenersHandler<L, E> implements IAtomicListenersHandler
{
	private ConcurrentMap<Thread, Collection<E>> _atomicOperations = new ConcurrentHashMap<Thread, Collection<E>>();
	
	/**
	 * 
	 */
	public AtomicListenersHandler(IListenerNotifyHandler<L, E> notifyHandler)
	{
		super(notifyHandler);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IAtomicListenerHandler#beginAtomicOperation()
	 */
	@Override
	public boolean beginAtomicOperation()
	{
		boolean added = _atomicOperations.putIfAbsent(Thread.currentThread(), new ArrayList<E>()) == null;
		if(!added)
		{
			// TODO should this throw an exception instead?
			CommonPlugin.getDefault().logWarning("Begining an atomic operation that is already registered."
					+ "  Potential nested operation begin/end", new Exception());
		}
		return added;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IAtomicListenerHandler#endAtomicOperation()
	 */
	@Override
	public boolean endAtomicOperation()
	{
		Collection<E> events = _atomicOperations.remove(Thread.currentThread());
		boolean removed = events != null;
		if(!removed)
		{
			// TODO should this throw an exception instead?
			CommonPlugin.getDefault().logWarning("Ended an atomic operation that wasn't registered."
					+ "  Potential nested operation begin/end", new Exception());
		}
		if(events != null)
		{
			events.forEach(e -> notifyListeners(e));
		}
		return removed;
	}
	
	public void notifyListeners(E event)
	{
		Collection<E> events = _atomicOperations.get(Thread.currentThread());
		if(events != null)
		{
			events.add(event);
		} else {
			super.notifyListeners(event);
		}
	}
}
