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
 *  kholson on May 11, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * <p>An implementing object represents a potential connection point to
 * a remote site, where there is a definition of the remote site bound to
 * an IEnv (the running environment, such as development, quality, production,
 * etc.), and a display name.</p>
 * @author kholson
 *
 */
public interface IRemoteSite
{
  /**
   * <p>Returns a description about the remote site, such as 
   * "Production Repository".</p>
   * @return A description of this remote site
   * @author kholson
   * <p>Initial Javadoc date: May 11, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public String getDescription();
  
  
  /**
   * <p>Returns the environment, such as qual, prod, etc., for which this
   * remote site is applicable.</p>
   * @return The environment for which this remote site is applicable.
   * @author kholson
   * <p>Initial Javadoc date: May 11, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public IEnv getEnvironment();
  
  
  /**
   * <p>Returns the LAN, such as SRN, SCN, yellow, green, etc., for which
   * this remote site is applicable.</p>
   * @return The LAN for the remote site
   * @author kholson
   * <p>Initial Javadoc date: Jun 2, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public ILan getLan();
  
  
  /**
   * <p>Returns the Application to which this remote site is useful, such
   * as DART, ESAW, etc.</p>
   * 
   * @return The application for which this remote site is valid
   * @author kholson
   * <p>Initial Javadoc date: Jun 2, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public IRemoteApplication getApplication();
  
  
  /**
   * @return A remote principal, if appropriate, for connecting to the
   * remote site; may return null. In many environments, may actually
   * be <code>KerberosPrincipal</code>, which provides access to
   * getNameType(), which will provide the type of the Kerberos instance.
   * 
   * @author kholson
   * <p>Initial Javadoc date: May 11, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public Principal getRemotePrincipal();
  
  
  
  /**
   * @return The URI that specifies the connection to the remote machine.
   * @author kholson
   * <p>Initial Javadoc date: May 26, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public URI getURI();
  
  
  /**
   * @return Any additional properties for this site, stored in a 
   * key/value Map; return may be empty but not null
   * @author kholson
   * <p>Initial Javadoc date: Jun 2, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public Map<String, String> getProperties();
  
  
  /**
   * @return Each defined remote site should have a unique key associated
   * with it; this method returns that unique key
   * @author kholson
   * <p>Initial Javadoc date: Jun 18, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public UUID getKey();
  
}
