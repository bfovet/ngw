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
 * Created by mjgibso on Apr 16, 2013 at 1:55:58 PM
 */
package gov.sandia.dart.common.core.listeners;


/**
 * @author mjgibso
 *
 */
public class ListenersHandler<L, E> extends AbstractListenersHandler<L, E>
{
	private final IListenerNotifyHandler<L, E> notifyHandler_;
	
	/**
	 * 
	 */
	public ListenersHandler(IListenerNotifyHandler<L, E> notifyHandler)
	{
		this.notifyHandler_ = notifyHandler;
		
		if(notifyHandler instanceof IBatchListenerNotifyHandler)
		{
			((IBatchListenerNotifyHandler<L, E>) notifyHandler).setListenerProvider(this);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.AbstractListenersHandler#notifyListeners(java.lang.Object)
	 */
	@Override
	public void notifyListeners(E event)
	{
		if(notifyHandler_ instanceof IBatchListenerNotifyHandler)
		{
			((IBatchListenerNotifyHandler<L, E>) notifyHandler_).notifyListeners(event);
		} else {
			super.notifyListeners(event);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.AbstractListenersHandler#notifyListener(L listener, E event)
	 */
	@Override
	public void notifyListener(L listener, E event)
	{
		this.notifyHandler_.notifyListener(listener, event);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.AbstractListenersHandler#handleNotifyListenerError(java.lang.Throwable)
	 */
	@Override
	public void handleNotifyListenerError(Throwable t)
	{
		this.notifyHandler_.handleNotifyListenerError(t);
	}
}
