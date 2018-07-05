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

import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.IRemoteApplication;
import gov.sandia.dart.configuration.IRemoteSite;
import gov.sandia.dart.configuration.impl.RemoteSite;

import java.net.URI;
import java.security.Principal;
import java.util.UUID;

/**
 * <p>
 * A singleton factory that can make instances conforming to the IRemoteSite
 * interface.
 * 
 * @author kholson
 *
 */
public class SimpleRemoteSiteFactory
{


  /**
   * _this - The singelton instance
   */
  private static final SimpleRemoteSiteFactory _this =
      new SimpleRemoteSiteFactory();




  /**
   * 
   */
  private SimpleRemoteSiteFactory()
  {
  }




  public static SimpleRemoteSiteFactory getInstance()
  {
    return _this;
  }
  
  
  /**
   * <p>Creates a basic object conforming to the IRemoteSite interface.</p>
   */
  public IRemoteSite makeRemoteSite(IRemoteApplication app,
                                    UUID key,
                                    IEnv env,
                                    ILan lan,
                                    Principal principal,
                                    URI uri,
                                    String description)
  {
    RemoteSite rs = new RemoteSite();
    rs.setKey(key);
    rs.setApplication(app);
    rs.setEnvironment(env);
    rs.setLan(lan);
    rs.setRemotePrincipal(principal);
    rs.setURI(uri);
    rs.setDescription(description);
    
    return rs;
  }


}
