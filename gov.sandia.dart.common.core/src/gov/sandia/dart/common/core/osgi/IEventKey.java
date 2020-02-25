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
 * Created by mjgibso on Mar 20, 2013 at 10:18:53 AM
 */
package gov.sandia.dart.common.core.osgi;

import org.osgi.service.event.Event;

/**
 * @author mjgibso
 *
 */
public interface IEventKey
{
  /**
   * Returns the name for the key
   */
	String name();
	
	
	/**
	 * From the specified event, obtain the object associated with the key.
	 * For example, if the key is "oid", the getProperty(Event) would return
	 * the OID object associated with the key.
	 */
	<T extends Object> T getProperty(Event event);
}
