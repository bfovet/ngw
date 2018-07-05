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
 * Created by mjgibso on Apr 16, 2013 at 2:57:35 PM
 */
package gov.sandia.dart.common.core.listeners;

import gov.sandia.dart.common.core.CommonCoreActivator;


/**
 * @author mjgibso
 *
 */
public abstract class LoggingListenerNotifyHandler<L, E> implements IListenerNotifyHandler<L, E>
{
	public static final String DEFAULT_LOG_MESSAGE = "Error notifying listeners.";
	
	protected final String logMessage_;
	
	protected LoggingListenerNotifyHandler()
	{
		this(DEFAULT_LOG_MESSAGE);
	}
	
	protected LoggingListenerNotifyHandler(String logMessage)
	{
		this.logMessage_ = logMessage;
	}
	
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.Common.IListenerNotifyHandler#handleNotifyListenerError(java.lang.Throwable)
	 */
	@Override
	public void handleNotifyListenerError(Throwable t)
	{
		CommonCoreActivator.getDefault().logError(logMessage_, t);
	}
}
