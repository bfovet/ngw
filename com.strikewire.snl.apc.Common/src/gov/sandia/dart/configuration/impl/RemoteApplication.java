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
 *  kholson on Jun 16, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.IRemoteApplication;

/**
 * @author kholson
 *
 */
public class RemoteApplication extends AbsExecEnv implements IRemoteApplication
{

  /**
   * 
   */
  public RemoteApplication(final String app)
  {
    super(app);
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteApplication#getApplicationName()
   */
  @Override
  public String getApplicationName()
  {
    return getValue();
  }




  @Override
  public boolean matches(IRemoteApplication remoteApp)
  {
    if (remoteApp == null) {
      return false;
    }

    return super.test(remoteApp.getApplicationName());
  }




  @Override
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof IRemoteApplication)) {
      return false;
    }

    return ((IRemoteApplication) obj).getApplicationName().equals(getValue());
  }



}
