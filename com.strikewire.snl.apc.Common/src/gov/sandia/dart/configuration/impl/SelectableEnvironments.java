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

import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.ISelEnvs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * <p>
 * A simple implementation of the ISelEnvs interface
 * </p>
 * 
 * @author kholson
 *
 */
public class SelectableEnvironments implements ISelEnvs
{
  private final IEnv _env;
  private final ILan _lan;

  private final List<IEnv> _selEnvs = new ArrayList<>();




  /**
   * 
   */
  public SelectableEnvironments(IEnv env, ILan lan)
  {
    _env = env;
    _lan = lan;
  }




  /**
   * @see gov.sandia.dart.configuration.ISelEnvs#getEnv()
   */
  @Override
  public IEnv getEnv()
  {
    return _env;
  }




  /**
   * @see gov.sandia.dart.configuration.ISelEnvs#getLan()
   */
  @Override
  public ILan getLan()
  {
    return _lan;
  }




  /**
   * @see gov.sandia.dart.configuration.ISelEnvs#getEnvironments()
   */
  @Override
  public List<IEnv> getEnvironments()
  {
    return _selEnvs;
  }




  public void addSelEnv(IEnv env)
  {
    if (env != null) {
      _selEnvs.add(env);
    }
  }




  public void addSelEnvs(Collection<IEnv> envs)
  {
    if (envs != null) {
      _selEnvs.addAll(envs);
    }
  }

}
