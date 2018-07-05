/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.core.osgi;

import gov.sandia.dart.IMetricsInfo;
import gov.sandia.dart.metrics.MetricsEventKeys;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
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
   * 
   */
  private OSGIUtils()
  {
  }




  /**
   * Returns true if the topic in the specified event matches the specified
   * topic
   */
  public static boolean topicsEqual(IOSGIEvent topic, Event event)
  {
    if (topic == null || event == null) {
      return false;
    }

    String sTopic = topic.getTopic();
    String eventTopic = event.getTopic();

    if (StringUtils.isBlank(sTopic) || StringUtils.isBlank(eventTopic)) {
      return false;
    }

    boolean bEqual = sTopic.equals(eventTopic);

    return bEqual;
  }




  public static Object getEventObject(Event event)
  {
    String[] propNames = event.getPropertyNames();
    if (propNames.length > 0) {
      return event.getProperty(propNames[0]);
    }
    else {
      return null;
    }
  }




  public static void postEvent(IOSGIEvent topic,
                               Class<?> bundleClass,
                               Object object)
  {
    postEvent(topic.getTopic(), bundleClass, object);
  }




  /**
   * posts an event without any associated properties
   */
  public static void postEvent(Event event, Class<?> bundleClass)
  {
    EventAdmin eventAdmin = OSGIUtils.getEventAdmin(bundleClass);

    if (eventAdmin != null) {
      eventAdmin.postEvent(event);
    }
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
   * 
   */
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               Object object)
  {
    Map<String, Object> props = new WeakHashMap<String, Object>();
    props.put(object.getClass().getCanonicalName(), object);

    doEventPosting(eventTopic, bundleClass, props);
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
   */
  public static void postEvent(IOSGIEvent topic,
                               Class<?> bundleClass,
                               Map<String, Object> properties)
  {
    doEventPosting(topic.getTopic(), bundleClass, properties);
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
   */
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               String... properties)
  {
    Map<String, Object> props = new WeakHashMap<String, Object>();
    for (int i = 0; i < properties.length - 1; i += 2) {
      props.put(properties[i], properties[i + 1]);
    }

    doEventPosting(eventTopic, bundleClass, props);
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
   */
  public static void postMetricEvent(Class<?> bundleClass, IMetricsInfo info)
  {
    String eventTopic = MetricsEventKeys.Add.getKey();
    Map<String, Object> props = new WeakHashMap<String, Object>();

    props.put(eventTopic, info);

    postEvent(eventTopic, bundleClass, props);
  }




  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               Map<String, Object> properties)
  {
    doEventPosting(eventTopic, bundleClass, properties);
  }




  private static void doEventPosting(String eventTopic,
                                     Class<?> bundleClass,
                                     Map<String, Object> properties)
  {
    Event event = new Event(eventTopic, properties);

    EventAdmin eventAdmin = OSGIUtils.getEventAdmin(bundleClass);

    if (eventAdmin != null) {
      eventAdmin.postEvent(event);
    }

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
   * 
   */
  public static void postEvent(String eventTopic,
                               Class<?> bundleClass,
                               String property,
                               Object object)
  {
    Map<String, Object> props = new WeakHashMap<String, Object>();
    props.put(property, object);

    doEventPosting(eventTopic, bundleClass, props);
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
   */
  public static EventAdmin getEventAdmin(final Class<?> bundleClass)
  {
    EventAdmin eventAdmin = null;

    try {
      if (bundleClass == null) {
        throw new IllegalArgumentException("Null bundleClass argument");
      }

      BundleContext bndlCtx =
          FrameworkUtil.getBundle(bundleClass).getBundleContext();

      if (bndlCtx == null) {
        return eventAdmin;
      }

      ServiceReference<EventAdmin> ref =
          bndlCtx.getServiceReference(EventAdmin.class);

      if (ref == null) {
        return eventAdmin;
      }

      eventAdmin = bndlCtx.getService(ref);
    } // try
    finally {
    }

    return eventAdmin;
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
   */
  public static ServiceRegistration<EventHandler> registerEventHandler(final Class<?> bundleClass,
                                                                       EventHandler handler,
                                                                       IOSGIEvent topic)
  {
    return registerEventHandler(bundleClass, handler, topic.getTopic());
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
   * 
   */
  public static Collection<ServiceRegistration<EventHandler>> registerEventHandler(final Class<?> bundleClass,
                                                                                   EventHandler handler,
                                                                                   Collection<? extends IOSGIEvent> events)
  {
    ArrayList<ServiceRegistration<EventHandler>> registrations =
        new ArrayList<ServiceRegistration<EventHandler>>();
    for (IOSGIEvent event : events)
      registrations.add(registerEventHandler(bundleClass,
          handler,
          event.getTopic()));
    return registrations;
  }




  /**
   * Registers for the EventHandler in the service with the specified event
   * handler, listening for the specified event topic, returning the
   * ServiceRegistration for the event (which may be null if unable to
   * register).
   * @return The Service Registration; may be null if unable to register
   */
  public static ServiceRegistration<EventHandler> registerEventHandler(final Class<?> bundleClass,
                                                                       EventHandler handler,
                                                                       String eventTopic)
  {
    try {
      if (bundleClass == null) {
        throw new IllegalArgumentException("Null bundleClass argument");
      }


      BundleContext bndlCtx =
          FrameworkUtil.getBundle(bundleClass).getBundleContext();

      
      return registerEventHandler(bndlCtx, handler, eventTopic);
    } // try
    finally {
    }

  }



  /**
   * Allows registratation using the bundle context;
   * may return null if cannot register
   * @return The Service Registration; may be null if unable to register
   */
  public static ServiceRegistration<EventHandler> registerEventHandler(final BundleContext bndlCtx,
                                                                       EventHandler handler,
                                                                       String eventTopic)
  {
    ServiceRegistration<EventHandler> srvcReg = null;
    
    if (bndlCtx == null) {
      _log.warn("Null BundleContext specified; cannot register event handler!");
      return srvcReg;
    }

    ServiceReference<EventAdmin> ref =
        bndlCtx.getServiceReference(EventAdmin.class);

    if (ref == null) {
      _log.warn("Failed to obtain service reference for EventAdmin class"
          + " from BundleContext; cannot register event handler");
      return srvcReg;
    }

    Dictionary<String, String> props = new Hashtable<String, String>();
    props.put(EventConstants.EVENT_TOPIC, eventTopic);

    srvcReg = bndlCtx.registerService(EventHandler.class, handler, props);
    _log.debug("Registered EventHandler for {}", eventTopic);
    
    return srvcReg;
  }




  /**
   * Unregisters a previously registered event handler. The bundle class must be
   * the same as the one used for the registration.
   */
  public static void unregisterEventHandler(final Class<?> bundleClass,
                                            final ServiceRegistration<EventHandler> registration)
  {
    // do not proceed if we might have NPE
    if (bundleClass == null || registration == null) {
      return;
    }

    BundleContext bndlCtx =
        FrameworkUtil.getBundle(bundleClass).getBundleContext();

    try {
      // not clear why this call is or is not needed
      // bndlCtx.getServiceReference(EventHandler.class);
      bndlCtx.ungetService(registration.getReference());
      registration.unregister();
    }
    catch (Exception e) {
      _log.warn("Issue with unregister for " + bundleClass.getName(), e);
      // This really doesn't matter to us.
    }
  }


}
