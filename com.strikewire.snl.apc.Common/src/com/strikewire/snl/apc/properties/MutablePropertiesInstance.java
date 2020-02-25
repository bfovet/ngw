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
package com.strikewire.snl.apc.properties;

import java.io.IOException;


/**
 * @author mjgibso
 *
 */
public interface MutablePropertiesInstance<E extends PropertiesInstance<E>> extends IPropertiesInstance<E>
{
	public void setName(String newName);
	public void setProperty(String key, String value);
	public void saveChanges() throws IOException;
	public boolean isModified();
	public void setModified();
	public boolean isOverriding();
	public void setHolder(MutablePropertiesInstanceHolder<E> holder);
}
