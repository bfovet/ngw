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

import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.IRemoteApplication;
import gov.sandia.dart.configuration.IRemoteSite;
import gov.sandia.dart.configuration.factory.SimpleExecEnvFactory;
import gov.sandia.dart.configuration.factory.SimpleRemoteAppFactory;

import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.HashedMap;

/**
 * <p>
 * A default implementation of an IRemoteSite interface.
 * </p>
 * 
 * @author kholson
 *
 */
public class RemoteSite implements IRemoteSite
{
  private String _description = "";
  private IEnv _env = SimpleExecEnvFactory.EMPTY_ENV;
  private ILan _lan = SimpleExecEnvFactory.EMPTY_LAN;
  private Map<String, String> _properties = new HashedMap<>();
  private IRemoteApplication _app = SimpleRemoteAppFactory.EMPTY_APPLICATION;
  private Principal _principal = null;
  private URI _uri = null;
  private UUID _key = null;




  /**
   * 
   */
  public RemoteSite()
  {
  }




  @Override
  public UUID getKey()
  {
    return _key;
  }




  public RemoteSite setKey(UUID key)
  {
    _key = key;

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getDescription()
   */
  @Override
  public String getDescription()
  {
    return _description;
  }




  /**
   * Sets the description
   * 
   * @param description
   *          The description; may not be null
   */
  public RemoteSite setDescription(String description)
  {
    if (description != null) {
      this._description = description;
    }

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getEnvironment()
   */
  @Override
  public IEnv getEnvironment()
  {
    return _env;
  }




  /**
   * Sets the environment
   * 
   * @param env
   *          The environment in which this remote site is valid; may not be
   *          null
   */
  public RemoteSite setEnvironment(IEnv env)
  {
    if (env != null) {
      this._env = env;
    }

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getLan()
   */
  @Override
  public ILan getLan()
  {
    return _lan;
  }




  /**
   * Sets the LAN
   * 
   * @param lan
   *          The LAN in which this remote site is valid; may not be null
   */
  public RemoteSite setLan(ILan lan)
  {
    if (lan != null) {
      this._lan = lan;
    }

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getApplication()
   */
  @Override
  public IRemoteApplication getApplication()
  {
    return _app;
  }




  /**
   * Sets the application to which this remote site is bound
   * 
   * @param app
   *          The application; may not be null
   */
  public RemoteSite setApplication(IRemoteApplication app)
  {
    if (app != null) {
      _app = app;
    }

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getRemotePrincipal()
   */
  @Override
  public Principal getRemotePrincipal()
  {
    return _principal;
  }




  /**
   * Sets the remote principal for this remote site
   * 
   * @param principal
   *          The remote principal; may be null if no remote principal is
   *          applicable
   */
  public RemoteSite setRemotePrincipal(Principal principal)
  {
    _principal = principal;

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getURI()
   */
  @Override
  public URI getURI()
  {
    return _uri;
  }




  /**
   * Sets the URI associated with this remote site
   * 
   * @param uri
   *          The URI; generally should not be null as a remote site should have
   *          some location associated with it
   */
  public RemoteSite setURI(URI uri)
  {
    _uri = uri;

    return this;
  }




  /**
   * @see gov.sandia.dart.configuration.IRemoteSite#getProperties()
   */
  @Override
  public Map<String, String> getProperties()
  {
    return _properties;
  }




  /**
   * Adds a property for this remote site
   */
  public RemoteSite addProperty(String key, String value)
  {
    _properties.put(key, value);

    return this;
  }




  /**
   * Adds all of the specified properties to the remote site; the properties are
   * copied into the existing properties
   */
  public RemoteSite addProperties(Map<String, String> props)
  {
    if (props != null && !props.isEmpty()) {
      _properties.putAll(props);
    }

    return this;
  }




  /**
   * Clears all of the existing properties for this remote site
   */
  public RemoteSite clearProperties()
  {
    _properties.clear();

    return this;
  }




  @Override
  public String toString()
  {
    return _app.getApplicationName() + "/" + _env.getEnv() + "/"
        + _lan.getLan() + ":"
        + (_uri != null ? _uri.toASCIIString() : "<no uri>");
  }

}
