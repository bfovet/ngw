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

package com.strikewire.snl.apc.Common.data;

/**
 * An Interface that indicates the Object supports arbitrary associations of
 * data to the object. Patterned after an Eclipse idea that allows attaching
 * data onto, e.g., a TableItem. 
 * @author kholson
 *
 */
public interface IAssociableData
{
  /**
   * Allows attaching additional information to the
   * object, such as the originating widget. If the specified
   * object is null, then the associated data is cleared.
   * @return Any previous data; may be null
   */
  public Object setData(Object object);
  
  /**
   * Gets an additional information associated with the
   * object; may return null if not previously set.
   */
  public Object getData();
  
  
  /**
   * Adds additional data to the object, under
   * the specified key. If the specified object is null, then
   * the associated data under the key is cleared.
   * @return Any previous data under this key; may be null
   */
  public Object setData(String key, Object object);

  
  
  /**
   * Returns additional associated data for the specified key; may
   * return null if the specified key was not previously added.
   */
  public Object getData(String key);
  

} //interface
