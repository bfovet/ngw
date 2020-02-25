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
 * Created by mjgibso on Apr 16, 2013 at 1:58:26 PM
 */
package gov.sandia.dart.common.core.listeners;

/**
 * @author mjgibso
 *
 * @param <L>
 * @param <E>
 */
public interface IListenerNotifyHandler<L, E>
{
	public abstract void notifyListener(L listener, E event);

	public abstract void handleNotifyListenerError(Throwable t);

}
