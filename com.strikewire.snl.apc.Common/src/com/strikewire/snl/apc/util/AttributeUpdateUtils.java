/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.util;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventHandler;

import com.strikewire.snl.apc.osgi.util.OSGIUtils;

public class AttributeUpdateUtils {
	
  private AttributeUpdateUtils()
  {
  }
  
	/**
	 * Key used for attribute update events
	 */
	private static final String ATTRIBUTE_UPDATE_KEY = "workbench/update";
	
	  /**
	   * Post an attribute update event with the given EventAdmin.
	   * 
	   * Used to notify listeners that attributes on the given object have been updated
	   */
	  public static void postAttributeUpdateEvent(Class<?> bundleClass, Object object) {
	    OSGIUtils.postEvent(ATTRIBUTE_UPDATE_KEY,
	        bundleClass,
	        object);
	  }
	  
	  /**
	   * Register an EventHandler to listen for attribute updates
	   * 
	   */
	  public static ServiceRegistration<EventHandler> registerEventHandlerForAttributeUpdates(
			  Class<?> bundleClass, EventHandler handler) {
		  return OSGIUtils.registerEventHandler(bundleClass, handler, ATTRIBUTE_UPDATE_KEY);
	  }
	  

}
