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
 * Created on Aug 24, 2007 at 10:18:17 AM
 */
package com.strikewire.snl.apc.properties;

import java.io.File;
import java.util.Map;

/**
 * @author mjgibso
 *
 */
public interface IPropertiesInstance<E extends PropertiesInstance<E>>
{
	public String getName();
	public void saveAs(File file);
	public E asBaseType();
	public MutablePropertiesInstance<E> clone();
	public String getProperty(String key);
	public PropertiesStore<E> getParent();
	public Map<String, String> getProperties();
	public void dispose();
}
