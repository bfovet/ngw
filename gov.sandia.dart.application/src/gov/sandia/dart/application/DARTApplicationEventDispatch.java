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
 * Created by mjgibso on Oct 23, 2013 at 1:34:07 PM
 */
package gov.sandia.dart.application;

import gov.sandia.dart.common.core.listeners.ListenersHandler;
import gov.sandia.dart.common.core.listeners.LoggingListenerNotifyHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.codec.binary.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * TODO could add static hooks to let users add/remove listeners directly via code instead of just the extension point.
 * 
 * @author mjgibso
 *
 */
public class DARTApplicationEventDispatch
{
	private static final DARTApplicationEventDispatch _instance = new DARTApplicationEventDispatch();
	
	private final ListenersHandler<IDARTApplicationListener, EventWrapper> _listeners;
	
	/**
	 * 
	 */
	private DARTApplicationEventDispatch()
	{
		this._listeners = new ListenersHandler<IDARTApplicationListener, EventWrapper>(new NotifyHandler());
		
		initListeners();
	}
	
	private void initListeners()
	{
		readRegisterredListeners().forEach(this._listeners::addListener);
	}
	
	public boolean addListener(IDARTApplicationListener listener)
	{
		return _listeners.addListener(listener);
	}
	
	public boolean removeListener(IDARTApplicationListener listener)
	{
		return _listeners.removeListener(listener);
	}
	
	private static Collection<ListenerReference> readRegisterredListeners()
	{
		Collection<ListenerReference> listeners = new ArrayList<>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(DARTApplicationBundleActivator.PLUGIN_ID, IDARTApplicationListener.EXTENSION_POINT_ID);
		IConfigurationElement[] extensions = extensionPoint.getConfigurationElements();
		
		if(extensions==null || extensions.length<1)
		{
			return listeners;
		}
		
		Arrays.asList(extensions).stream().filter(Objects::nonNull).forEach(e -> listeners.add(new ListenerReference(e)));
		
		return listeners;
	}
	
	public static DARTApplicationEventDispatch getInstance()
	{
		return _instance;
	}
	
	/**
	 * Intended for internal package use only
	 */
	static void preNotify(DARTApplicationEvent event)
	{
		getInstance()._listeners.notifyListeners(new EventWrapper(event, EventWrapper.TYPE.pre));
	}
	
	/**
	 * Intended for internal package use only
	 */
	static void postNotify(DARTApplicationEvent event)
	{
		getInstance()._listeners.notifyListeners(new EventWrapper(event, EventWrapper.TYPE.post));
	}
	
	private static class NotifyHandler extends LoggingListenerNotifyHandler<IDARTApplicationListener, EventWrapper>
	{
		/**
		 * 
		 */
		public NotifyHandler()
		{
			super("Error notifying DART Application listener of an application event");
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.Common.IListenerNotifyHandler#notifyListener(java.lang.Object, java.lang.Object)
		 */
		@Override
		public void notifyListener(IDARTApplicationListener listener, EventWrapper event)
		{
			switch(event.type)
			{
				case pre: listener.preApplicationEvent(event.event); break;
				case post: listener.postApplicationEvent(event.event); break;
			}
		}
	}
	
	private static class EventWrapper
	{
		enum TYPE {
			pre,
			post
		}
		
		final TYPE type;
		final DARTApplicationEvent event;
		
		EventWrapper(DARTApplicationEvent event, TYPE type)
		{
			this.event = event;
			this.type = type;
		}
	}
	
	private static class ListenerReference implements IDARTApplicationListener
	{
		private enum PRE_POST
		{
			PRE("PRE: "),
			POST("POST: "),
			;
			
			private final String prefix;
			
			private PRE_POST(String prefix)
			{
				this.prefix = prefix;
			}
		}
		
		private static final String CLASS_ATTRIBUTE = "listener";
		private static final String EVENT_ELEMENT = "Event";
		private static final String EVENT_ATTRIBUTE = "Event";
		private static final String MATCH_ALL = "* (All events)";
		
		private final IConfigurationElement _extension;
		
		private IDARTApplicationListener _listener;
		private final Object _listenerLock = new Object();
		
		/**
		 * 
		 */
		private ListenerReference(IConfigurationElement extension)
		{
			// TODO if we wanted to load listeners even earlier in the case of one listening for '*', we could force load such a listener here.
			this._extension = extension;
		}

		/* (non-Javadoc)
		 * @see gov.sandia.dart.application.IDARTApplicationListener#preApplicationEvent(gov.sandia.dart.application.DARTApplicationEvent)
		 */
		@Override
		public void preApplicationEvent(DARTApplicationEvent event)
		{
			sendEvent(event, PRE_POST.PRE);
		}

		/* (non-Javadoc)
		 * @see gov.sandia.dart.application.IDARTApplicationListener#postApplicationEvent(gov.sandia.dart.application.DARTApplicationEvent)
		 */
		@Override
		public void postApplicationEvent(DARTApplicationEvent event)
		{
			sendEvent(event, PRE_POST.POST);
		}
		
		private void sendEvent(DARTApplicationEvent event, PRE_POST prePost)
		{
			if(isListening(event, prePost))
			{
				getListener().ifPresent(l -> {
					switch(prePost)
					{
						case PRE:
							l.preApplicationEvent(event);
							break;
						case POST:
							l.postApplicationEvent(event);
							break;
					}
				});
			}
		}
		
		private boolean isListening(DARTApplicationEvent event, PRE_POST prePost)
		{
			IConfigurationElement[] events = _extension.getChildren(EVENT_ELEMENT);
			if(events==null || events.length<1)
			{
				return false;
			}
			
			return Arrays.asList(events).stream().filter(Objects::nonNull).anyMatch(e -> match(e, event, prePost));
		}
		
		private boolean match(IConfigurationElement eventElement, DARTApplicationEvent event, PRE_POST prePost)
		{
			String eventString = prePost.prefix + event.name();
			String elementString = eventElement.getAttribute(EVENT_ATTRIBUTE);
			return MATCH_ALL.equals(elementString) || StringUtils.equals(eventString, eventElement.getAttribute(EVENT_ATTRIBUTE));
		}
		
		private Optional<IDARTApplicationListener> getListener()
		{
			// check first to avoid the time of synchronizing if we're already loaded
			if(_listener == null)
			{
				synchronized(_listenerLock)
				{
					// since two or more threads could be waiting on the lock, have to check
					// again in the locked bit of code to make sure we don't load more than once
					if(_listener == null)
					{
						_listener = loadListener();
					}
				}
			}
			
			return Optional.ofNullable(_listener);
		}
		
		private IDARTApplicationListener loadListener()
		{
			try
			{
				Object listener = this._extension.createExecutableExtension(CLASS_ATTRIBUTE);
				if(listener instanceof IDARTApplicationListener)
				{
					return (IDARTApplicationListener) listener;
				}
			} catch (CoreException e) {
				String clazz = this._extension.getAttribute(CLASS_ATTRIBUTE);
				DARTApplicationBundleActivator.getDefault().logError(
						"Error loading DARTApplicationListener: "+clazz+".  Listener will not be notified of events.", e);
			}
			
			return null;
		}
	}
}
