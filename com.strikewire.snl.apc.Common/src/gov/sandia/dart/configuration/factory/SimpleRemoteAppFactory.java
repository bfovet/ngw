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

package gov.sandia.dart.configuration.factory;

import gov.sandia.dart.configuration.IRemoteApplication;
import gov.sandia.dart.configuration.impl.RemoteApplication;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>Singleton factory for making IRemoteApplication objects;
 * each remote application is stored, and if it already has
 * been created, the existing object is returned.</p>
 * @author kholson
 *
 */
public class SimpleRemoteAppFactory
{
  private final ConcurrentMap<String, IRemoteApplication> _remoteAppsByName =
      new ConcurrentHashMap<>();
  
  /**
   * _this - Singleton instance
   */
  private static final SimpleRemoteAppFactory _this = 
      new SimpleRemoteAppFactory();

  /**
   * 
   */
  private SimpleRemoteAppFactory()
  {
  }
  
  public static SimpleRemoteAppFactory getInstance()
  {
    return _this;
  }
  
  
  public IRemoteApplication makeApplication(String app)
  {
    IRemoteApplication retApp;
    
    IRemoteApplication remoteApp = new RemoteApplication(app);

    // will put if not there; if it is there, we get back the
    // previous value, which we will then want to use
    retApp = _remoteAppsByName.putIfAbsent(app, remoteApp);
    
    // did not get a previous value, so use the one that was just added
    if (retApp == null) {
      retApp = remoteApp;
    }

    return retApp;
  }

  
  
  /**
   * EMPTY_APPLICATION - An empty/default application
   */
  public static final IRemoteApplication EMPTY_APPLICATION =
      new RemoteApplication("");
  
  /**
   * WILDCARD_APPLICATION - A wildcard app; useful when searching
   */
  public static final IRemoteApplication WILDCARD_APPLICATION =
      new RemoteApplication("*");
  
}
