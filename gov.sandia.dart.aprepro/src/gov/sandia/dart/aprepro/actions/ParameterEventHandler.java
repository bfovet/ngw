/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package gov.sandia.dart.aprepro.actions;

import gov.sandia.dart.common.core.listeners.IListenerNotifyHandler;
import gov.sandia.dart.common.core.listeners.ListenersHandler;
import gov.sandia.dart.common.core.listeners.LoggingListenerNotifyHandler;

/**
 * @author mjgibso
 *
 */
public class ParameterEventHandler extends
		LoggingListenerNotifyHandler<IParameterListener, ParameterEvent> implements
		IListenerNotifyHandler<IParameterListener, ParameterEvent>
{
	private final ListenersHandler<IParameterListener, ParameterEvent> _listeners;
	
	private static final ParameterEventHandler _instance = new ParameterEventHandler();
	
	private ParameterEventHandler()
	{
		_listeners = new ListenersHandler<IParameterListener, ParameterEvent>(this);
	}
	
	public static ParameterEventHandler getInstance()
	{
		return _instance;
	}
	
	@Override
	public void notifyListener(IParameterListener listener, ParameterEvent event) {
		listener.parameterChange(event);
	}
	
	public void notifyListeners(ParameterEvent event)
	{
		_listeners.notifyListeners(event);
	}
	
	public boolean addListener(IParameterListener listener)
	{
		return _listeners.addListener(listener);
	}
	
	public boolean removeListener(IParameterListener listener)
	{
		return _listeners.removeListener(listener);
	}

}
