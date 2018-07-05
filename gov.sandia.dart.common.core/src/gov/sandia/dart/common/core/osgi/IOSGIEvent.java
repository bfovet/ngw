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
 * Created by mjgibso on Mar 19, 2013 at 2:26:33 PM
 */
package gov.sandia.dart.common.core.osgi;

import java.util.Map;

import org.osgi.service.event.Event;


/**
 * @author mjgibso
 *
 */
public interface IOSGIEvent
{
  /**
   * Returns the topic associated with this event, such as
   * workbench/domain/refreshed
   */
	String getTopic();
	
	/**
	 * Returns the object that is in the property map for the specified
	 * key contained in the specified event
	 */
	<T extends Object> T getObject(Event event, IEventKey key);
	
	/**
	 * Creates a new event with the specified properties
	 */
	Event newEvent(Map<String, Object> properties);
	
	
	/**
	 * Some basic actions that are re-used, such as added, refreshed, etc. These
	 * actions are usually at the end of a topic
	 * @author kholson
	 *
	 */
	public enum EActions {
	  added,
	  created,
	  deleted,
	  linked,
	  refreshed,
	  removed,
	  retrieved,
	  updated,
	  ;
	};
	
	
	/**
	 * Some basic relationships that are re-used, such as parents, children,
	 * etc.
	 * @author kholson
	 *
	 */
	public enum ERelations {
	  parents,
	  children,
	  inputs,
	  outputs,
	  ;
	};
}
