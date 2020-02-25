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

package com.strikewire.snl.apc.file;

import org.eclipse.core.resources.IResource;

/**
 * @author kholson
 * @author mjgibso
 */
public class FixedTypeLocalResourceMarkersHelper extends LocalResourceMarkersHelper
{
  private final String _markerTypeID;
  
  /**
   *  
   */
  public FixedTypeLocalResourceMarkersHelper(String markerTypeID)
  {
	  this._markerTypeID = markerTypeID;
  }
  
	/**
	 * Sets the value for the key on the specified resource.
	 * 
	 * @return true if the value was changed
	 * @since 2.10
	 */
	public boolean setAttribute(IResource res, String key, String value)
	{
		return super.setAttribute(res, _markerTypeID, key, value);
	}

	/**
	 * For the specified resource, obtain the value of the marker for the
	 * specified key. May return null.
	 * 
	 * @since 2.10
	 */
	public String getAttribute(IResource res, String key)
	{
		return super.getAttribute(res, _markerTypeID, key);
	}

}
