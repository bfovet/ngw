/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *  Copyright (C) 2015
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Feb 19, 2015
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.osgi.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * A utility class that assists with the new Eclipse 4 Event system.
 * 
 * @author kholson
 *
 */
public class EventUtils
{
  /**
   * _log -- A Logger instance for EventUtils
   */
  private static final Logger _log = LogManager.getLogger(EventUtils.class);




  /**
   * 
   */
  private EventUtils()
  {
  }




  /**
   * Returns the model object 'window', which will be an active window. May
   * return null.
   * 
   * @return The current MWindow, obtained from the service; may be null
   * 
   */
  public static MWindow getEclipseModelWindow()
  {
    MWindow mWindow = null;


    try {
      if (PlatformUI.isWorkbenchRunning()) {
        mWindow = (MWindow) PlatformUI.getWorkbench().getService(MWindow.class);

        if (mWindow == null) {
          IWorkbenchWindow window =
              PlatformUI.getWorkbench().getActiveWorkbenchWindow();

          if (window != null) {
            mWindow = (MWindow) window.getService(MWindow.class);
          }
        }
      } // if isrunning
      else {
        _log.error("Workbench is not yet running");
      }
    }
    catch (IllegalStateException e) {
      _log.error("Workbench is not available");
    }

    return mWindow;
  }
  
  public static MWindow getEclipseModelWindow(IWorkbenchWindow window)
  {
    return window!=null ? (MWindow) window.getService(MWindow.class) : null;
  }
  
  
  /**
   * Returns the e4 Eclipse context from a getService for MWindow call; depends
   * upon an active workbench window to be present. A full e4 app can use
   * dependency injection, but without DI, this call can be utilized. May return
   * null.
   * 
   * @return The Eclipe context, or null if
   */
  public static IEclipseContext getContext()
  {
    IEclipseContext retCtx = null;

    MWindow mWindow = getEclipseModelWindow();

    if (mWindow != null) {
      retCtx = mWindow.getContext();
    }

    return retCtx;
  }




  /**
   * Returns the e4 Event broker; may return null if unable to find the broker
   */
  public static IEventBroker getEventBroker()
  {
    IEventBroker retBroker = null;

    IEclipseContext ctx = getContext();
    
    if (ctx != null) {
      retBroker = ctx.get(IEventBroker.class);
    }
    
    return retBroker;
  }
  
  public static IEventBroker getEventBroker(IWorkbenchWindow window)
  {
    IEventBroker retBroker = null;
    
    IEclipseContext ctx = getContext(window);
    
    if (ctx != null) {
      retBroker = ctx.get(IEventBroker.class);
    }

    return retBroker;
  }
  
  public static IEclipseContext getContext(IWorkbenchWindow window)
  {
    IEclipseContext retCtx = null;
    
    MWindow mWindow = getEclipseModelWindow(window);
    
    if (mWindow != null) {
      retCtx = mWindow.getContext();
    }
    
    return retCtx;
  }
  
  
  /**
   * Registers the specified event handler for invocation for the
   * specified topic
   * @param topic The topic (e.g., org/eclipse/blah; wildcard at end OK)
   * @param handler The handler that will be called
   * @return True if the registration worked; false otherwise 
   */
  public static boolean registerForEvent(final String topic,
      final EventHandler handler)
  {
    return registerForEvent(topic, handler, getEventBroker());
  }




  /**
   * Registers the specified event handler for invocation for the
   * specified topic
   * @param topic The topic (e.g., org/eclipse/blah; wildcard at end OK)
   * @param handler The handler that will be called
   * @return True if the registration worked; false otherwise 
   */
  public static boolean registerForEvent(final String topic,
      final EventHandler handler, final IWorkbenchWindow window)
  {
    return registerForEvent(topic, handler, getEventBroker(window));
  }
      
  /**
   * Registers the specified event handler for invocation for the
   * specified topic
   * @param topic The topic (e.g., org/eclipse/blah; wildcard at end OK)
   * @param handler The handler that will be called
   * @return True if the registration worked; false otherwise 
   */
  public static boolean registerForEvent(final String topic,
      final EventHandler handler, final IEventBroker broker)
  {
    boolean bRet = false;
    
    if (broker != null) {
      bRet = broker.subscribe(topic, handler);
    }

    return bRet;
  }




  /**
   * Unregisters the specified event handler from the event system; since there
   * is no topic specified, it is not possible to stop listening for just
   * certain topics.
   */
  public static boolean unregisterEventHandler(final EventHandler eventHandler)
  {
    return unregisterEventHandler(eventHandler, getEventBroker());
  }
  
  /**
   * Unregisters the specified event handler from the event system; since
   * there is no topic specified, it is not possible to stop listening for just
   * certain topics.
   */
  public static boolean unregisterEventHandler(final EventHandler eventHandler, IWorkbenchWindow window)
  {
    return unregisterEventHandler(eventHandler, getEventBroker(window));
  }
  
  /**
   * Unregisters the specified event handler from the event system; since
   * there is no topic specified, it is not possible to stop listening for just
   * certain topics.
   */
  public static boolean unregisterEventHandler(final EventHandler eventHandler, final IEventBroker broker)
  {
    boolean bRet = false;
    
    if (broker != null) {
      bRet = broker.unsubscribe(eventHandler);
    }


    return bRet;
  }
  
  public static String verbose(Event event)
  {
		StringBuilder sb = new StringBuilder();
		sb.append("event: ");
		sb.append(event);
		sb.append(" : ");
		String[] propNames = event.getPropertyNames();
		sb.append(ArrayUtils.toString(propNames));
		for(String propName : propNames)
		{
			sb.append("\n  ");
			sb.append(propName);
			sb.append(" | ");
			sb.append(event.getProperty(propName));
		}
		return sb.toString();
  }

}
