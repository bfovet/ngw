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
package com.strikewire.snl.apc.resources;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.internal.events.ResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;

import gov.sandia.dart.application.DARTApplicationAdapter;
import gov.sandia.dart.application.DARTApplicationEvent;
import gov.sandia.dart.application.IDARTApplicationListener;

// TODO a better implementation would probably be to let clients register for change events THROUGH this class,
// wherein after registering, we'll first feed them all the events they missed, and then start just passing through
// events we receive after that.

// TODO, also just realized this is kind of dangerous the way it's implemented.  If we change the code so that nobody
// calls this thing, it will save up events forever.  Presumably until we run out of memory from our saved events list
// getting so huge?

/**
 * @author mjgibso
 *
 */
public class PreWorkbenchResourceEventListener extends DARTApplicationAdapter implements IDARTApplicationListener
{
	private static final Logger _log = LogManager.getLogger(PreWorkbenchResourceEventListener.class);

	/**
	 * 
	 */
	public PreWorkbenchResourceEventListener() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void preApplicationEvent(DARTApplicationEvent event)
	{
		if(DARTApplicationEvent.WORKBENCH_ADVISOR_CONSTRUCT == event)
		{
			EventSaver.getInstance().registerListener();
		}
	}
	
	public static List<IResourceChangeEvent> takeEventsAndShutdown()
	{
		return EventSaver.getInstance().takeEventsAndShutdown();
	}
	
	private static class EventSaver implements IResourceChangeListener
	{
		private static final EventSaver _instance = new EventSaver();
		
		private final List<IResourceChangeEvent> _savedEvents = new ArrayList<>();
		
		private volatile boolean _shutdown = false;
		
		private EventSaver()
		{}
		
		public static EventSaver getInstance()
		{
			return _instance;
		}
		
		
		@Override
		public void resourceChanged(IResourceChangeEvent event)
		{
			_log.debug("PreWorkbenchResourceChangeEvent: "+event);
			_log.debug(((ResourceChangeEvent) event).toDebugString());
			
			if(!_shutdown)
			{
				saveEvent(event);
			}
		}
		
		private synchronized void saveEvent(IResourceChangeEvent event)
		{
			if(_shutdown)
			{
				return;
			}
			
			_savedEvents.add(event);
		}
		
		private void registerListener()
		{
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			ws.addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
			_log.debug("Listener attached!!!");
		}
		
		private synchronized List<IResourceChangeEvent> takeEventsAndShutdown()
		{
			if(_shutdown)
			{
				return Collections.emptyList();
			}
			
			List<IResourceChangeEvent> savedEvents = new ArrayList<>(_savedEvents);
			_savedEvents.clear();
			_shutdown = true;
			
			IWorkspace ws = ResourcesPlugin.getWorkspace();
			ws.removeResourceChangeListener(this);
			
			return savedEvents;
		}
	}

}
