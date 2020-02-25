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
 *  kholson on Jun 15, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.osgi.framework.Bundle;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>
 * A class to assist with finding a resource in a plugin, with the specific goal
 * of finding an xml file that will be used by a parser. It looks for a
 * preference value, and for a file location.
 * </p>
 * 
 * @author kholson
 *
 */
public class ResourceFinder
{
  /**
   * _log -- A Logger instance for ResourceFinder
   */
  private static final Logger _log = LogManager.getLogger(ResourceFinder.class);

  /**
   * _config - The configuration that provides the input for the search
   */
  private final ResourceFinderCfg _config;




  /**
   * 
   */
  public ResourceFinder(ResourceFinderCfg cfg)
  {
    if (cfg != null) {
      _config = cfg;
    }
    else {
      throw new IllegalArgumentException("null cfg");
    }
  }




  /**
   * @return the location, based upon the search algorithm; looks first for a
   *         setting in the preference store, if a store and key have been set.
   *         Attempts to obtain from the plugin bundle the entry if there is not
   *         a file path obtained from the preference store. May return
   *         null. Throws a MissingResourceException if unable to find
   *         the configuration. Uses the values previously set
   *         in the ResourceFinderCfg.
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 15, 2015
   *         </p>
   *         <p>
   *         Permission Checks:
   *         </p>
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         </p>
   */
  public URL getLocation() throws MissingResourceException
  {
    URL urlLoc = null;

    // make sure we can try to do something
    validateSomeActionPossible();

    String prefStoreLoc = getResourceLocationFromPreferenceStore(_config);

    //
    // if we did not get from the preference store, then will need
    // to potentially search the bundle
    if (StringUtils.isBlank(prefStoreLoc)) {
      _log.debug("Attempting to find resource in bundle");
      urlLoc = getURLFromBundle(_config, _config.getDefaultFile());
      
    } // if : we did not have a preference store location
    else {
      _log.debug("Using location from preferences: {}", prefStoreLoc);
      urlLoc = getURLFromFileLocation(prefStoreLoc);
    } // else : we had a potential preference store location

    
    if (urlLoc == null) {
      final String msg = "Failed to obtain a URL to the resource";
      _log.error(msg);
      CommonPlugin.getDefault().logError(msg, new Exception());
      throw new MissingResourceException(msg,
          this.getClass().getName(),
          _config.getDefaultFile());
    }    

    return urlLoc;
  }




  /**
   * @return The URL for the resource in the bundle; may return null if the
   *         resource is not found, or the bundle configuration was not properly
   *         specified
   */
  private URL getURLFromBundle(final ResourceFinderCfg cfg,
                               final String resourceFilename)
  {
    URL urlDefaultsXML = null;

    if (isBundleSearchPossible()) {
      Bundle bndl = getActivatorBundle(cfg);
      if (bndl != null) {
        urlDefaultsXML = bndl.getEntry(resourceFilename);
      }
      else {
        _log.error("Failed to obtain the bundle");
        CommonPlugin.getDefault().logError("Null bundle", new Exception());
      }
    }

    return urlDefaultsXML;
  }




  /**
   * @param The
   *          file location, as a full path
   * @return The URL as created from a java.io.File converted to a URL
   */
  private URL getURLFromFileLocation(String defXML)
  {
    URL urlDefaultsXML = null;

    _log.debug("Setting default settings file to {}", defXML);
    try {
      urlDefaultsXML = new File(defXML).toURI().toURL();
    }
    catch (MalformedURLException e) {
      _log.error("URL issue converting defaults file {}: {}",
          defXML,
          e.toString());
      CommonPlugin.getDefault().logError("URL issue with defaults file: " + defXML, e);
    }
    return urlDefaultsXML;
  }




  /**
   * Returns the bundle for the pluginId in the cfg; may return null
   */
  private Bundle getActivatorBundle(final ResourceFinderCfg cfg)
  {
    Bundle bndl = null;

    if (StringUtils.isNotBlank(cfg.getPluginId())) {
      bndl = Platform.getBundle(cfg.getPluginId());
    }

    return bndl;
  }




  /**
   * Returns the value from the preference store for the key
   * 
   * @return The value; may be null or blank
   */
  private String getResourceLocationFromPreferenceStore(ResourceFinderCfg cfg)
  {
    String retVal = "";

    if (isStoreSearchPossible()) {
      IPreferenceStore store = cfg.getStore();
      String key = cfg.getStoreKey();

      retVal = store.getString(key);
    }

    return retVal;
  }




  private void validateSomeActionPossible() throws MissingResourceException
  {
    if (isStoreSearchPossible() || isBundleSearchPossible()) {
      return;
    }

    final String msg =
        "No search for an resource file is possible; "
            + "as neither a PreferenceStore nor a Bundle was properly "
            + "specified";

    throw new MissingResourceException(msg,
        this.getClass().getName(),
        "<no key>");
  }




  private boolean isStoreSearchPossible()
  {
    final IPreferenceStore store = _config.getStore();
    final String storeKey = _config.getStoreKey();

    return (store != null && StringUtils.isNotBlank(storeKey));
  }




  private boolean isBundleSearchPossible()
  {
    final String defFile = _config.getDefaultFile();
    final String pluginId = _config.getPluginId();

    return (StringUtils.isNotBlank(defFile) && StringUtils.isNotBlank(pluginId));
  }



 


} // class
