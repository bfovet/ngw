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
 *  kholson on May 27, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.mgr;

import gov.sandia.dart.configuration.IExecutionEnvironment;
import gov.sandia.dart.configuration.IUpdateSite;
import gov.sandia.dart.configuration.IUpdateSites;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.strikewire.snl.apc.Common.CommonPlugin;


/**
 * <p>A singleton that will collect all contributed update sites
 * (via the UpdateSiteContributor extension point), and return a complete
 * collection of all potential update sites.</p>
 * @author kholson
 *
 */
public class UpdateSitesMgr extends AbsExtensionMgr
{
  /**
   * EXT_ID - The extension point identifier for contributing update sites
   */
  private static final String EXT_ID = "UpdateSiteContributor";
  
  /**
   * _cfg - The configuration of this extension manager
   */
  private static final ExtMgrCfg _cfg = initCfg();  
  
  /**
   * _this - The single instance; thread safe initialization 
   */
  private static final UpdateSitesMgr _this = new UpdateSitesMgr();
  

  /**
   * 
   */
  private UpdateSitesMgr()
  {
    super(_cfg);
  }
  
  
  private static ExtMgrCfg initCfg()
  {
    ExtMgrCfg cfg = new ExtMgrCfg();

    cfg.extensionPointId(EXT_ID);

    return cfg;
  }  
  
  /**
   * @return The singleton instance
   * @author kholson
   * <p>Initial Javadoc date: May 27, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public static UpdateSitesMgr getInstance()
  {
    return _this;
  }
  

  
  public Collection<IUpdateSite> getUpdateSites()
  {
    Collection<IUpdateSite> sites = new ArrayList<>();
    
    IConfigurationElement[] eles = getExtensionElements();    

    
    final IExecutionEnvironment execEnv = getExecutionEnv();
    
    for (IConfigurationElement ele : eles) {
      try {
        Object ext = instantiateElement(ele);

        // make sure it is of the correct type
        if (ext instanceof IUpdateSites) {
          IUpdateSites updateSites = (IUpdateSites)ext;
          
          // now get the contributed updates sites and add to our
          // return collection
          Collection<IUpdateSite> contribSites = updateSites.getSites(execEnv);
          
          // the updateSites is not supposed to return null, but
          // just check anyway; if we have something, add
          if (contribSites != null && ! contribSites.isEmpty()) {
            sites.addAll(contribSites);
          }
        }        
      } // try
      catch (CoreException e) {
        CommonPlugin.getDefault().logError(e);
      }    
    } // for: process  all of the contributed elements
    
    
    return sites;

  }

}
