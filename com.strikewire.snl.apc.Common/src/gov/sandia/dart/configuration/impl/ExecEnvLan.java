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
 *  kholson on Apr 29, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.ILan;


/**
 * <p>
 * A basic implementation of a LAN for an Execution Environment, where a LAN
 * represents a network on which the application is running. The LAN is used to
 * configure what resources are available to the application based upon the
 * network. For example, the SDM servers, the Metric Servers, and the remote
 * compute clusters.
 * </p>
 * 
 * @author kholson
 *
 */
public class ExecEnvLan extends AbsExecEnv implements ILan
{


  /**
   * 
   */
  public ExecEnvLan(final String lan)
  {
    super(lan);
  }




  @Override
  public String getLan()
  {
    return getValue();
  }




  @Override
  public boolean test(ILan lan)
  {
    if (lan == null) {
      return false;
    }

    return super.test(lan.getLan());
  }




  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof ILan)) {
      return false;
    }

    ILan eel = (ILan) obj;

    return getValue().equals(eel.getLan());
  } // equals()

} // class ExecEnvLan
