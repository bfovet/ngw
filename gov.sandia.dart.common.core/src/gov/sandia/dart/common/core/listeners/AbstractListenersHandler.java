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
 * Created by mjgibso on Apr 16, 2013 at 1:43:40 PM
 */
package gov.sandia.dart.common.core.listeners;

/**
 * @author mjgibso
 *
 */
public abstract class AbstractListenersHandler<L, E> extends BaseListenersHandler<L> implements IListenerNotifyHandler<L, E>
{
	public void notifyListeners(E event)
	{
		notifyListeners(this, this, event);
	}
	
	public static <L, E> void notifyListeners(IListenersProvider<L> listenersProvider,
			IListenerNotifyHandler<L, E> notifyHandler, E event)
	{
		for(L listener : listenersProvider.getListeners())
		{
			try {
				notifyHandler.notifyListener(listener, event);
			} catch (Throwable t) {
				notifyHandler.handleNotifyListenerError(t);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IListenerNotifyHandler#notifyListener(L, E)
	 */
	@Override
	public abstract void notifyListener(L listener, E event);
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IListenerNotifyHandler#handleNotifyListenerError(java.lang.Throwable)
	 */
	@Override
	public abstract void handleNotifyListenerError(Throwable t);
}
