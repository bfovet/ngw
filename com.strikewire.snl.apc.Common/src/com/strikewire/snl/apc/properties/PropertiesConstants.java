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
 * Created on Aug 27, 2008 at 4:34:24 PM
 */
package com.strikewire.snl.apc.properties;

/**
 * @author mjgibso
 *
 */
public interface PropertiesConstants
{
	public static final String DOT_PROPERTIES = ".properties";
	public static final String BUILD_VERSION_KEY = "build.version";
	public static final String LAN = "lan";
	
	/**
	 * @Deprecated - no longer used.  Do not use except in migration code
	 */
	public static final String NAMESPACE_SEPARATOR_CHAR = "~";
}
