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
 *  kholson on Jul 10, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.ISite;

/**
 * <p>
 * A basic implmentation of a Site for an Execution Environment, where a Site
 * represents a unique physical deployment area. The Site is used to configure
 * settings where settings differ by the physical site.
 * </p>
 * 
 * @author kholson
 *
 */
public class ExecEnvSite extends AbsExecEnv implements ISite
{

  /**
   * @param value
   */
  public ExecEnvSite(String value)
  {
    super(value);
  }




  @Override
  public String getSite()
  {
    return getValue();
  }




  @Override
  public boolean test(ISite site)
  {
    return super.test(site.getSite());
  }




  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof ISite)) {
      return false;
    }

    ISite ees = (ISite) obj;

    return getValue().equals(ees.getSite());
  }

}
