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

package gov.sandia.dart.configuration;


import org.eclipse.jface.preference.IPreferenceStore;

/**
 * <p>A configuration for the ResourceFinder object. Several plugins
 * store settings in a file, usually under the PLUGIN_DIR/resources
 * directory. This class creates the configuration that is
 * used by the ResourceFinder class.</p>
 * @author kholson
 *
 */
public class ResourceFinderCfg
{

  private IPreferenceStore _store = null;
  private String _storeKey = "";
  private String _defFile = "";
  private String _pluginId = "";



  /**
   * Sets the preference store; allows for searching (using
   * the set storeKey) for a desired location for a preference
   * file that would searching for the default file in 
   * the defaultFile location.
   */
  public ResourceFinderCfg store(IPreferenceStore store)
  {
    _store = store;
    return this;
  }




  public ResourceFinderCfg storeKey(String key)
  {
    if (key != null) {
      _storeKey = key;
    }

    return this;
  }



  /**
   * The name of the file that contains the defaults; used if
   * a preference in the specified store under the storeKey
   * is not found
   */
  public ResourceFinderCfg defaultFile(String file)
  {
    if (file != null) {
      _defFile = file;
    }

    return this;
  }



  /**
   * The id of the plugin in which to search for the defaultFile
   */
  public ResourceFinderCfg pluginId(String id)
  {
    if (id != null) {
      _pluginId = id;
    }

    return this;
  }




  /**
   * @return The preference store as set; may return null
   */
  public IPreferenceStore getStore()
  {
    return _store;
  }




  /**
   * @return The key to search for in the preference store; may return empty
   *         string but not null
   */
  public String getStoreKey()
  {
    return _storeKey;
  }




  /**
   * @return The default filename; may return an empty String if not previously
   *         set
   */
  public String getDefaultFile()
  {
    return _defFile;
  }




  /**
   * @return The plugin/bundle id; may return an empty String if not previously
   *         set
   */
  public String getPluginId()
  {
    return _pluginId;
  }
} // class ResourceFinderCfg


