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
 *  kholson on Jun 11, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.IEnv;


/**
 * <p>
 * A basic implementation of an Environment (dev, qual, prod), for
 * an Execution Environment.
 * 
 * @author kholson
 *
 */
public class ExecEnvEnv extends AbsExecEnv implements IEnv
{


  /**
   * 
   */
  public ExecEnvEnv(final String env)
  {
    super(env);
  }




  @Override
  public String getEnv()
  {
    return getValue();
  }

  
  @Override
  public boolean test(IEnv env)
  {
    if (env == null) {
      return false;
    }
    
    return super.test(env.getEnv());
    
  }



  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof IEnv)) {
      return false;
    }

    IEnv eel = (IEnv) obj;

    return getValue().equals(eel.getEnv());
  } // equals()





}
