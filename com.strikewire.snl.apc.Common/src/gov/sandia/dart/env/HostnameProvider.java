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
 *  Copyright (C) 2017
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Jan 4, 2017
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.env;

import gov.sandia.dart.common.core.localhostname.AbsHostnameInput;
import gov.sandia.dart.common.core.localhostname.EmptyHostnameInput;
import gov.sandia.dart.common.core.localhostname.HostnameInput;
import gov.sandia.dart.common.core.localhostname.IHostnameStrategy;
import gov.sandia.dart.common.preferences.localhost.ILocalHostnamePreferences;
import gov.sandia.dart.common.preferences.localhost.LocalHostnamePreferences;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p>
 * Provides access to obtaining the hostname based upon
 * operating system and preferences.
 * </p>
 * <p>
 * The available means for resolving a hostname for the local machine
 * are defined by a series of strategies. The particular strategy
 * in use is obtained from the preferences, and the range of
 * available strategies is O/S dependent.
 * </p>
 * @author kholson
 *
 */
public class HostnameProvider
{
  /**
   * _log -- A Logger instance for HostnameProvider
   */
  private static final Logger _log =
      LogManager.getLogger(HostnameProvider.class);
  
  /**
   * _prefs - The preferences that are in use
   */
  private static final ConcurrentMap<String, ILocalHostnamePreferences>
    _prefs = new ConcurrentHashMap<>();

  private static final String KEY = "lalaland";

  

  
  /**
   * Returns the hostname of the local machine based upon the
   * operating system and preferences. The returned
   * URI is an opaque URI, with the scheme set to "host", and
   * the host in the URI being set to the hostname.
 * @throws IOException 
   */
  public static URI getLocalHostname() throws IOException
  {
    //
    // get the preferences
    //
    ILocalHostnamePreferences prefs = 
        _prefs.computeIfAbsent(KEY, e -> defaultPrefs());
    
    IHostnameStrategy strategy = prefs.getSelected();
    
    AbsHostnameInput input = strategy.needsInput() ? new HostnameInput(prefs.getData(strategy)) : new EmptyHostnameInput();
    
    URI ret = strategy.resolve(input);
    
    return ret;
  }
  
  
  /**
   * Allows setting the preferences that will be utilized
   */
  public static void setPreferences(ILocalHostnamePreferences prefs)
  {

    if (prefs == null) {
      _prefs.remove(KEY);
      _log.debug("Cleared preferences");
    }
    else {
      _prefs.put(KEY, prefs);
      _log.debug("Updated preferences");    
      }
  }
  

  
  private static ILocalHostnamePreferences defaultPrefs()
  {
    return LocalHostnamePreferences.getInstance();
  }  
}
