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
 *  Copyright (C) 2012
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.osgi.util;

import gov.sandia.dart.IMetricsInfo;
import gov.sandia.dart.common.core.osgi.IOSGIEvent;

import java.util.Collection;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

/**
 * @author kholson
 * 
 */
public class OSGIUtils
{
  /**
   * _log -- A Logger instance for OSGIUtils
   */
  private static final Logger _log = LogManager.getLogger(OSGIUtils.class);

  /**
   * TOPIC_STARTUP - The topic that indicates the application has started
   */
  private static final String TOPIC_STARTUP = UIEvents.UILifeCycle.TOPIC
      + UIEvents.TOPIC_SEP + "appStartupComplete";

  /**
   * TOPIC_SHUTDOWN - Indicates the application is shutting down
   */
  private static final String TOPIC_SHUTDOWN =
      UIEvents.UILifeCycle.APP_SHUTDOWN_STARTED;





  /**
   * Returns true if the topic in the specified event matches the specified
   * topic
   * 
   * @deprecated Use topicsEqual in common.core
   */
  @Deprecated
  public static boolean topicsEqual(IOSGIEvent topic, Event event)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.topicsEqual(topic, event);
  }




  /**
   * @deprecated Use getEventObject in common.core
   */
  @Deprecated
  public static Object getEventObject(Event event)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.getEventObject(event);
  }




  /**
   * @deprecated Use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(IOSGIEvent topic,
                               Class<?> bundleClass,
                               Object object)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(topic,
        bundleClass,
        object);
  }




  /**
   * posts an event without any associated properties
   * 
   * @deprecated Use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(Event event, Class<?> bundleClass)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(event, bundleClass);
  }




  /**
   * Post an event with the given topic. The canonical class name is used in the
   * properties map, with the object.
   * 
   * @param eventTopic
   *          The OSGI topic that is posted
   * @param bundleClass
   *          A class which has a bundle associated with it, such as a view or a
   *          plugin
   * @param Object
   *          the object, which may not be null, that should be posted along
   *          with the event.
   * 
   * @deprecated use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               Object object)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(eventTopic,
        bundleClass,
        object);
  }




  /**
   * Allows for the specification of the exact map, and uses the IOSGIEvent
   * 
   * @param topic
   *          The topic; .getTopic() will be called against it
   * @param bundleClass
   *          The bundle class; must be a class that has a bundle associated
   *          with it, such as a view or plugin
   * @param properties
   *          The map of the properties
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 21, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   * @deprecated use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(IOSGIEvent topic,
                               Class<?> bundleClass,
                               Map<String, Object> properties)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(topic,
        bundleClass,
        properties);
  }




  /**
   * Posts the event under the specified eventTopic, with the specified
   * properites
   * 
   * @param eventTopic
   *          The string topic
   * @param bundleClass
   *          The bundle class; must be a class that has a bundle associated
   *          with it, such as a view or plugin
   * @param properties
   *          The properties to post, in an array of Strings, with a key/value
   *          type arrangement "key1", "value1", "key2", "value2", etc.
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 21, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   * @deprecated use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               String... properties)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(eventTopic,
        bundleClass,
        properties);
  }




  /**
   * <p>
   * Posts an OSGI event for a "Metric"; the eventTopic is fixed to
   * EventKeys.METRICS. If any piece of information in the specified info is
   * null, an empty String is assigned.
   * </p>
   * 
   * <p>
   * This posting sends the IMetricsInfo in the properties, under the key of
   * EventKeys.METRICS
   * </p>
   * 
   * <p>
   * Can be done manually by:
   * </p>
   * 
   * <pre>
   * OSGIUtils.postEvent(EventKeys.METRICS,
   *     SimbaAppPlugin.class,
   *     &quot;plugin&quot;,
   *     SimbaAppPlugin.PLUGIN_ID,
   *     &quot;capability&quot;,
   *     &quot;open_project&quot;,
   *     &quot;data&quot;,
   *     &quot;someData&quot;);
   * </pre>
   * 
   * @deprecated use postMetricEvent in common.core
   */
  @Deprecated
  public static void postMetricEvent(Class<?> bundleClass, IMetricsInfo info)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postMetricEvent(bundleClass,
        info);
  }




  /**
   * @deprecated use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               Map<String, Object> properties)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(eventTopic,
        bundleClass,
        properties);
  }





  /**
   * Post an event with the given topic and property.
   * 
   * @param eventTopic
   *          The OSGI topic that is posted
   * @param bundleClass
   *          A class which has a bundle associated with it, such as a view or a
   *          plugin
   * @param property
   *          The property key for the object
   * @param Object
   *          the object, which may not be null, that should be posted along
   *          with the event.
   * 
   * @deprecated Use postEvent in common.core
   */
  @Deprecated
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               String property,
                               Object object)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.postEvent(eventTopic,
        bundleClass,
        property,
        object);
  }




  /**
   * @return The EventAdmin for the specified class; may return null in the case
   *         the server cannot be found.
   *         <p>
   *         Obtains the OSGI event admin for the specified class.
   *         </p>
   * 
   *         <pre>
   *      EventAdmin eventAdmin = getEventAdmin(ProjectExplorerView.class)
   * </pre>
   * @deprecated use getEventAdmin in common.core
   */
  @Deprecated
  public static EventAdmin getEventAdmin(final Class<?> bundleClass)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.getEventAdmin(bundleClass);
  } // getEventAdmin




  /**
   * Allows for registering an event handler against a specified IOSGIEvent
   * 
   * @param bundleClass
   *          A class which has a bundle associated with it, such as a plugin or
   *          a view
   * @param handler
   *          An event handler to deal with the callback
   * @param topic
   *          An IOSGIEvent object for which the registration should occur
   * @return The service registration which may be used to unregister, but note
   *         the same bundleClass must be used in the de-registration
   * @deprecated use registerEventHandler in common.core
   */
  @Deprecated
  public static ServiceRegistration<EventHandler> registerEventHandler(final Class<?> bundleClass,
                                                                       EventHandler handler,
                                                                       IOSGIEvent topic)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.registerEventHandler(bundleClass,
        handler,
        topic.getTopic());
  }




  /**
   * Allows for specifying a series of events for which a single event handler
   * should be registered
   * 
   * @param bundleClass
   *          A class which has a bundle associated with it, such as a plugin or
   *          a view
   * @param handler
   *          An event handler to deal with the callback
   * @param events
   *          A collection of events for which the specified handler should be
   *          registered
   * @return A collection of service registration which may be used to
   *         unregister, but note the same bundleClass must be used in the
   *         de-registration
   * @deprecated Use registerEventHander in common.core
   */
  @Deprecated
  public static Collection<ServiceRegistration<EventHandler>> registerEventHandler(final Class<?> bundleClass,
                                                                                   EventHandler handler,
                                                                                   Collection<? extends IOSGIEvent> events)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.registerEventHandler(bundleClass,
        handler,
        events);
  }




  /**
   * Registers for the EventHandler in the service with the specified event
   * handler, listening for the specified event topic, returning the
   * ServiceRegistration for the event (which may be null if unable to
   * register).
   * 
   * @deprecated use registerEventHandler in common.core
   */
  @Deprecated
  public static ServiceRegistration<EventHandler> registerEventHandler(final Class<?> bundleClass,
                                                                       EventHandler handler,
                                                                       String eventTopic)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.registerEventHandler(bundleClass,
        handler,
        eventTopic);
  }




  /**
   * Registers for the appStartupComplete event; may return null
   */
  public static ServiceRegistration<EventHandler> registerStartupEventHandler(final Class<?> bundleClass,
                                                                              EventHandler handler)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.registerEventHandler(bundleClass,
        handler,
        TOPIC_STARTUP);
  }




  /**
   * Registers for the appStartupComplete event; may return null
   */
  public static ServiceRegistration<EventHandler> registerShutdownEventHandler(final Class<?> bundleClass,
                                                                               EventHandler handler)
  {
    return gov.sandia.dart.common.core.osgi.OSGIUtils.registerEventHandler(bundleClass,
        handler,
        TOPIC_SHUTDOWN);
  }




  /**
   * Unregisters a previously registered event handler. The bundle class must be
   * the same as the one used for the registration.
   * 
   * @deprecated Use unregisterEventHandler in common.core
   */
  @Deprecated
  public static void unregisterEventHandler(final Class<?> bundleClass,
                                            final ServiceRegistration<EventHandler> registration)
  {
    gov.sandia.dart.common.core.osgi.OSGIUtils.unregisterEventHandler(bundleClass,
        registration);
  }


} // class OSGIUtils
