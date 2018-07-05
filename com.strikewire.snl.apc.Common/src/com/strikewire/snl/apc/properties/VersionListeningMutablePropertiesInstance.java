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

/**
 * @author mjgibso
 *
 */
public interface VersionListeningMutablePropertiesInstance<E extends PropertiesInstance<E>> extends MutablePropertiesInstance<E>
{
	/**
	 * Method is called following construction if the current build version is different from the version
	 * this properties instance was written with (based on the version stored in it).  Implementors can
	 * perform any necessary migration of properties based on the supplied version changes.
	 * 
	 * @param oldVersion - The build version found in the file defining this properties instance.  That is,
	 * 					   the build version this properties instance was written with.
	 * @param newVersion - The current build version.  Same as what would be returned by
	 * 					   {@link PropertiesInstance#getCurrentBuildVersion()}.
	 */
	public void buildVersionChanged(String oldVersion, String newVersion);
}
