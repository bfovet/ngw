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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A class which implements the IAssociableData interface, and may
 * be used as an internal storage class, allowing another class
 * which implements the IAssociableData interface to simply pass
 * calls through to an instance of this class.
 * @author kholson
 *
 */
public class AssociableData implements Serializable, IAssociableData
{
  /**
   * serialVersionUID - 
   */
  private static final long serialVersionUID = 5326447492741629423L;

  private final Map<String,Object> _mAdditionalData = 
      new HashMap<String,Object>();
  
  private final String UUID_KEY = UUID.randomUUID().toString();
      
  /**
   * 
   */
  public AssociableData()
  {
  }




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.Common.data.IAssociableData#setData(java.lang.Object)
   */
  @Override
  public Object setData(Object object)
  {
    Object oldData = _mAdditionalData.remove(UUID_KEY);
    
    if (object != null) {
      _mAdditionalData.put(UUID_KEY, object);
    }
    
    return oldData;
  }




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.Common.data.IAssociableData#getData()
   */
  @Override
  public Object getData()
  {
    return _mAdditionalData.get(UUID_KEY);
  }




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.Common.data.IAssociableData#setData(java.lang.String, java.lang.Object)
   */
  @Override
  public Object setData(String key, Object object)
  {
    Object oldData = _mAdditionalData.remove(key);
    if (object != null) {
      _mAdditionalData.put(key, object);
    }

    return oldData;
  }




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.Common.data.IAssociableData#getData(java.lang.String)
   */
  @Override
  public Object getData(String key)
  {
    return _mAdditionalData.get(key);
  }

}
