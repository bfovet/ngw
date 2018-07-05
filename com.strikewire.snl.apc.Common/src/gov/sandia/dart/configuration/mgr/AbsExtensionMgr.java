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

import gov.sandia.dart.configuration.IExecutionEnvironment;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>
 * An abstract class that provides some basic functionality to support classes
 * that work with gathering extension point information. Leverages a few other
 * classes (e.g., ExtMgrCfg) to provide a consistent approach to collecting
 * extension points.
 * </p>
 * 
 * @author kholson
 *
 */
public abstract class AbsExtensionMgr
{
  /**
   * _log -- A Logger instance for AbsExtensionMgr
   */
  private static final Logger _log =
      LogManager.getLogger(AbsExtensionMgr.class);
  
  private final ExtMgrCfg _cfg;




  /**
   * @param pluginId
   *          The plugin where the extension point is defined
   * @param extId
   *          The id of the extension point
   * @param exeExtName
   *          The definition that is used for the
   *          <code>createExecutableExtension</code>
   */
  public AbsExtensionMgr(final ExtMgrCfg cfg)
  {
    _cfg = cfg;
  }

  /**
   * Returns the extension point id (name) for which the class
   * looks for contributors.
   */
  public String getExtensionId()
  {
    return _cfg.getExentionPointId();
  }
  
  
  /**
   * Returns the Id of the plugin in which the exentions point
   * is to be found
   */
  public String getPluginId()
  {
    return _cfg.getPluginId();
  }
  
  

  /**
   * @return The registered extensions for the extension point defined in the
   *         configuration; may return an empty array, but not null.
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
  protected IConfigurationElement[] getExtensionElements()
  {
    IConfigurationElement[] eles = null;

    // get the class from the extension point
    IExtensionRegistry reg = Platform.getExtensionRegistry();
    IExtensionPoint extPoint =
        reg.getExtensionPoint(_cfg.getPluginId(), _cfg.getExentionPointId());

    if (extPoint != null) {
      eles = extPoint.getConfigurationElements();
    }
    else {
      String msg =
          "No registered contributors to the " + "extension point "
              + _cfg.getExentionPointId();
      IStatus status = CommonPlugin.getDefault().newWarningStatus(msg);
      CommonPlugin.getDefault().log(status);
    }


    if (eles == null) {
      eles = new IConfigurationElement[0];
    }

    return eles;
  }




  /**
   * @param ele
   *          A configuration element; may be null
   * @return An instantiated object from the executionExtention name defined in
   *         the configuration; may return null
   * @throws CoreException
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
  protected Object instantiateElement(final IConfigurationElement ele)
    throws CoreException
  {
    Object ext = null;

    if (ele != null) {
      ext = ele.createExecutableExtension(_cfg.getExecutableExtension());
    }

    return ext;
  }




  protected IExecutionEnvironment getExecutionEnv()
  {
    return ExecutionEnvironmentMgr.getInstance().getExecutionEnv();
  }
  
  
  /**
   * Returns the configuration element where the
   * implementor (a Java class name) matches the
   * specified String; may return null.
   */
  protected IConfigurationElement getElementFor(String cls)
  {
    IConfigurationElement retEle = null;
    
    IConfigurationElement[] eles = getExtensionElements();
    
    for (IConfigurationElement ele : eles) {
      String imp = ele.getAttribute("implementor"); 
      if (StringUtils.isBlank(imp)) {
        continue;
      }
      
      if (imp.equals(cls)) {
        retEle = ele;
        break;
      }
    }
    
    return retEle;
  }  
  
  
  /**
   * Returns the configuration element based upon the value in
   * the preference store
   */
  protected IConfigurationElement getElementByPreference(String storeKey,
                                                         IPreferenceStore store)
  {
    String cls = store.getString(storeKey);
    
    if (StringUtils.isBlank(cls)) {
      _log.error("No class obtained for preference {}", storeKey);
      return null;
    }
    
    _log.debug("Attempting to find element for {}", cls);
    IConfigurationElement ele = getElementFor(cls);
    
    if (ele == null) {
      _log.error("Unable to find element for {}", cls);
      CommonPlugin.getDefault().logError("Unable to find Element " +
          cls, new Exception());
      return null;
    }

    return ele;
  }
  
  
  

  /**
   * <p>
   * Configuration for the extension point mgr.
   * </p>
   * <p>
   * Defaults
   * </p>
   * <ul>
   * <li>pluginId : CommonPlugin.ID</li>
   * <li>executableExtension: "implementor"</li>
   * </ul>
   * 
   * @author kholson
   *
   */
  public static class ExtMgrCfg
  {
    private String _pluginId = CommonPlugin.ID;
    private String _extId = "";
    private String _exeExtName = "implementor";




    /**
     * The plugin in which the extension point resides
     */
    public ExtMgrCfg pluginId(String id)
    {
      _pluginId = id;
      return this;
    }




    /**
     * The name of the extension point, such as MachineDefintionContributor
     */
    public ExtMgrCfg extensionPointId(String id)
    {
      _extId = id;

      return this;
    }




    /**
     * The name of the attribute that is used to instantiate the contributor
     */
    public ExtMgrCfg executableExtension(String name)
    {
      _exeExtName = name;

      return this;
    }




    public String getPluginId()
    {
      return _pluginId;
    }




    public String getExentionPointId()
    {
      return _extId;
    }




    public String getExecutableExtension()
    {
      return _exeExtName;
    }

  } // inner class ExtMgrCfg



} // class
