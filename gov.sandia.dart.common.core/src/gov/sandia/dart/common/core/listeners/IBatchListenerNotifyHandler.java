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
 * Created by mjgibso on Jun 28, 2015 at 6:26:51 AM
 */
package gov.sandia.dart.common.core.listeners;


/**
 * @author mjgibso
 *
 */
public interface IBatchListenerNotifyHandler<L, E> extends IListenerNotifyHandler<L, E>
{
	void notifyListeners(E event);
	
	void setListenerProvider(IListenersProvider<L> listenerProvider);
}
