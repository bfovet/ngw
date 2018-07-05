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
 *  kholson on Jun 2, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.mgr;

import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.IExecutionEnvironment;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.IRemoteApplication;
import gov.sandia.dart.configuration.IRemoteSite;
import gov.sandia.dart.configuration.IRemoteSites;
import gov.sandia.dart.configuration.ISelEnvs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>
 * A singleton that will collect all the contributed remote sites (via the
 * RemoteSiteContributor extension point), and return a complete collection of
 * all the potential remote sites.
 * </p>
 * 
 * @author kholson
 *
 */
public class RemoteSitesMgr extends AbsExtensionMgr
{
  /**
   * EXT_ID - The extention point identifier for contributing remote sites
   */
  private static final String EXT_ID = "RemoteSiteContributor";

  /**
   * _cfg - The configuration of this extension manager
   */
  private static final ExtMgrCfg _cfg = initCfg();

  /**
   * _this - The singleton instance; thread safe initialization
   */
  private static final RemoteSitesMgr _this = new RemoteSitesMgr();


  /**
   * _allRemoteSites - All of the collected remote sites
   */
  private Collection<IRemoteSite> _allRemoteSites = new ArrayList<>();

  private Collection<ISelEnvs> _allSelEnvs = new ArrayList<>();


  private Map<IRemoteApplication, IRemoteSite> _currentlyUsedRemoteSite =
      new HashedMap<>();




  /**
   * 
   */
  private RemoteSitesMgr()
  {
    super(_cfg);

    gatherAllRemoteSites();
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
   *         <p>
   *         Initial Javadoc date: Jun 2, 2015
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
  public static RemoteSitesMgr getInstance()
  {
    return _this;
  }




  /**
   * Returns the currently used IRemoteSite as stored under a given application;
   * may return null if no remote site has been set for the application
   */
  public IRemoteSite getCurrentUsedRemoteSite(IRemoteApplication app)
  {
    return _currentlyUsedRemoteSite.get(app);
  }



  /**
   * Stores the specified remote site under the specified app; if
   * the remoteSite is null, and the app is not, then the remoteSite
   * for the specified app is removed from the map
   */
  public void setCurrentUsedRemotedSite(IRemoteApplication app,
                                        IRemoteSite remoteSite)
  {
    if (app != null) {
      if (remoteSite != null) {
        _currentlyUsedRemoteSite.put(app, remoteSite);
      }
      else {
        _currentlyUsedRemoteSite.remove(app);
      }
    }
  }




  private void gatherAllRemoteSites()
  {
    IConfigurationElement[] eles = getExtensionElements();

    for (IConfigurationElement ele : eles) {
      try {
        Object ext = instantiateElement(ele);

        if (ext instanceof IRemoteSites) {
          IRemoteSites remoteSites = (IRemoteSites) ext;

          //
          // gather all of the sites
          //
          Collection<IRemoteSite> contribSites = remoteSites.getSites();

          //
          // add all of the returned sites to the internal collection
          //
          if (contribSites != null && !contribSites.isEmpty()) {
            _allRemoteSites.addAll(contribSites);
          }

          //
          // get the specified selections
          //
          Collection<ISelEnvs> sels = remoteSites.getSelectableEnvironments();

          if (sels != null && !sels.isEmpty()) {
            _allSelEnvs.addAll(sels);
          }

        } // if : ext was an IRemoteSites
      } // try
      catch (CoreException e) {
        CommonPlugin.getDefault().logError(e);
      }

    } // for: process all of the contributed elements

  }




  /**
   * @return All of the potential remote sites for the current
   *         IExecutionEnvironment
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 2, 2015
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
  public Collection<IRemoteSite> getRemoteSitesForCurrentExecEnv()
  {
    Collection<IRemoteSite> sites = new ArrayList<>();

    final IExecutionEnvironment execEnv =
        ExecutionEnvironmentMgr.getInstance().getExecutionEnv();


    for (IRemoteSite rs : _allRemoteSites) {
      if (rs.getEnvironment().test(execEnv.getEnv())
          && rs.getLan().test(execEnv.getLan())) {

        sites.add(rs);
      }
    }


    return sites;
  }




  /**
   * @param app
   *          The application to limit the potential remote sites to
   * @param execEnv
   *          The execution environment
   * @return Possible remote sites
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 16, 2015
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
  public Collection<IRemoteSite> getRemoteSites(final IRemoteApplication app,
                                                final IEnv env,
                                                final ILan lan)
  {
    Collection<IRemoteSite> sites = new HashSet<>();


    for (IRemoteSite rs : _allRemoteSites) {
      if (rs.getEnvironment().test(env)
          && rs.getLan().test(lan)
          && rs.getApplication().matches(app)) {

        sites.add(rs);
      }
    }


    return sites;
  }


  /**
   * Returns the remote site associated with the given key; may return
   * null if no site is found
   */
  public IRemoteSite getRemoteSite(final UUID key)
  {
    IRemoteSite site = null;
    
    for (IRemoteSite rs : _allRemoteSites) {
      UUID remoteKey = rs.getKey();
      if (remoteKey == null) {
        continue;
      }
      
      if (remoteKey.equals(key)) {
        site = rs;
        break;
      }
    }
    
    return site;
  }


  /**
   * Returns the Remote Sites that match the application, any of the specified
   * environments, and the specified LAN. It is often used to present a
   * selection of remote sites
   * 
   * @param app
   *          The application to restrict the search to
   * @param envs
   *          A collection of environments, any of which are used to check for
   *          adding
   * @param lan
   *          The LAN to restrict the search to
   * @return A collection of remote sites that are limited to the specified app
   *         and lan, and match at least one of the envs; may return an empty
   *         collection, but not null
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 17, 2015
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
  public Collection<IRemoteSite> getRemoteSites(final IRemoteApplication app,
                                                final Collection<IEnv> envs,
                                                final ILan lan)
  {
    Collection<IRemoteSite> sites = new ArrayList<>();

    if (app == null || envs == null || envs.isEmpty() || lan == null) {
      return sites;
    }


    for (IRemoteSite rs : _allRemoteSites) {
      if (!rs.getApplication().matches(app)) {
        continue;
      }

      if (!rs.getLan().test(lan)) {
        continue;
      }

      for (IEnv potentialEnv : envs) {
        if (rs.getEnvironment().test(potentialEnv)) {
          sites.add(rs);
        }
      }
    }


    return sites;
  }




  public Collection<IEnv> getSelectableEnvs(IEnv env, ILan lan)
  {
    Collection<IEnv> envs = new ArrayList<>();

    for (ISelEnvs sels : _allSelEnvs) {
      if (sels.getEnv().test(env) && sels.getLan().test(lan)) {

        envs.addAll(sels.getEnvironments());
      }
    }

    return envs;
  }
  
  
  /**
   * Converts a server key in String form to a UUID; may return null
   */
  public static UUID makeServerKey(final String key)
  {
    UUID retUUID = null;
    if (StringUtils.isNotBlank(key)) {
      try {
        retUUID = UUID.fromString(key);
      }
      catch (Exception e) {
        retUUID = null;
      }
    }
    
    return retUUID;
  }

} // class
