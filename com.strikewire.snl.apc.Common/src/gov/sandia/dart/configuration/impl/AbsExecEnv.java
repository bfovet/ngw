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
 *  Copyright (C) 2015
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Jun 17, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.IExecEnvInfo;

/**
 * <p>
 * An abstract class providing some functionality for the implementation classes
 * of the IEnv, ILan, and IMode.
 * </p>
 * 
 * @author kholson
 *
 */
public abstract class AbsExecEnv implements IExecEnvInfo
{
  private final String _execEnvValue;

  private String _initializedVia = "";


  /**
   * 
   */
  public AbsExecEnv(final String value)
  {
    if (value != null) {
      _execEnvValue = value;
    }
    else {
      throw new IllegalArgumentException("May not instantiate "
          + this.getClass().getName() + " with null parameter");
    }
  }

  
  @Override
  public String getInitBy()
  {
    return _initializedVia;
  }
  
  
  public void setInitBy(final String via)
  {
    if (via != null) {
      _initializedVia = via;
    }
  }
  
  
  /**
   * Returns true if the values match, or if either one
   * is a wildcard
   */
  protected boolean test(final String chkValue)
  {
    if (chkValue == null) {
      return false;
    }
    
    //
    // see if either one is a wildcard
    //
    if ("*".equals(getValue()) || "*".equals(chkValue)) {
      return true;
    }
    
    //
    // see if the values match
    //
    if (chkValue.equals(getValue())) {
      return true;
    }

    
    return false;
  }


  /**
   * @return The value for this ExecEnv object
   */
  protected String getValue()
  {
    return _execEnvValue;
  }




  @Override
  public int hashCode()
  {
    return _execEnvValue.hashCode();
  }




  @Override
  public String toString()
  {
    return _execEnvValue;
  }

}
