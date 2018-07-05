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

package com.strikewire.snl.apc.GUIs;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.osgi.framework.Bundle;

/**
 * <p>Extends the AbstractPreferenceInitializer to provide methods
 * to locate resources in the plugin. For example, setting a default
 * file location.</p>
 * @author kholson
 *
 */
public abstract class AbsResourcePreferenceInitializer extends
    AbstractPreferenceInitializer
{

  /**
   * Searches the specified bundle for the specified resource, using
   * the FileLocator.find(...) method against the bundle.
   * @param pluginId
   * @param resourceName
   * @return
   * @author kholson
   * <p>Initial Javadoc date: Jun 15, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  protected IPath getPathToResourceFile(final String pluginId,
                                        final String resourceName)
  {
    IPath ret = null;
    
    Bundle bundle = Platform.getBundle(pluginId);
    IPath loginConf = new Path(resourceName);
    URL u = null;
    
    try {
      u = FileLocator.find(bundle, loginConf, null);
    }
    catch (Exception noop) {
    }
    
    if (u != null) {
      try {
        u = FileLocator.resolve(u);
        ret = new Path(new File(u.getFile()).getAbsolutePath());
      }
      catch (IOException noop) {
      }
    }
    return ret;
  }  
}
