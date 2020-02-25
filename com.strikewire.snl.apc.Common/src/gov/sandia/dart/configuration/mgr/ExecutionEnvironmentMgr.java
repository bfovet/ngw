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
 *  kholson on May 5, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.mgr;

import gov.sandia.dart.common.core.env.OS.EOperatingSystem;
import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.IExecutionEnvironment;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.IMode;
import gov.sandia.dart.configuration.ISite;
import gov.sandia.dart.configuration.factory.SimpleExecEnvFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>
 * A singleton that allows obtaining the implementing class for the
 * IExecutionEnvironment
 * </p>
 * 
 * @author kholson
 *
 */
public class ExecutionEnvironmentMgr extends AbsExtensionMgr
{
  /**
   * _log -- A Logger instance for ExecutionEnvironmentMgr
   */
  private static final Logger _log =
      LogManager.getLogger(ExecutionEnvironmentMgr.class);
  
  /**
   * EXT_ID - The extension point identifier for those who contribute
   * an execution environment definition
   */
  private static final String EXT_ID = "ExecutionEnvContributor";
  
  /**
   * _cfg - The configuration of this extension manager
   */
  private static final ExtMgrCfg _cfg = initCfg();  

  /**
   * _this - The singleton instance; thread safe initialization
   */
  private static final ExecutionEnvironmentMgr _this =
      new ExecutionEnvironmentMgr();


  private IExecutionEnvironment _execEnv = null;


  /**
   * 
   */
  private ExecutionEnvironmentMgr()
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
   * @return The singleton instance of the manager.
   * @author kholson
   * <p>Initial Javadoc date: May 26, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public static ExecutionEnvironmentMgr getInstance()
  {
    return _this;
  }




  /**
   * <p>Obtains the current IExecutionEnvironment, as contributed. Will
   * return a default one if one is not registered.</p>
   * @return The current ExecutionEnvironment
   * @author kholson
   * <p>Initial Javadoc date: May 26, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  @Override
  public synchronized IExecutionEnvironment getExecutionEnv()
  {
    IExecutionEnvironment execEnv = null;
    
    if (_execEnv == null) {
      IConfigurationElement[] eles = getExtensionElements();    
      
      for (IConfigurationElement ele : eles) {
        try {              
          Object ext = instantiateElement(ele);
          
          if (ext instanceof IExecutionEnvironment) {
            execEnv = (IExecutionEnvironment) ext;
            _execEnv = execEnv;
          }
        } // try
        catch (CoreException e) {
          _log.error(e);
          CommonPlugin.getDefault().logError(e);
        }
              
      } // for: process all of the found elements
      
    }
    else {
      execEnv = _execEnv;
    }

    if (execEnv == null) {
      String msg = "Unable to find any IExecutionEnvironment providers!"; 
      _log.warn(msg);
      CommonPlugin.getDefault().logWarning(msg, null);
      execEnv = EMPTY_EXEC_ENV;
    }

    return execEnv;
  }

  
  /**
   * EMPTY_EXEC_ENV - A default IExecutionEnvironment that returns
   * empty String filled components
   */
  public static final IExecutionEnvironment EMPTY_EXEC_ENV = 
      new IExecutionEnvironment()
      {

        @Override
        public ILan getLan()
        {
          return SimpleExecEnvFactory.EMPTY_LAN;
        }

        @Override
        public IEnv getEnv()
        {
          return SimpleExecEnvFactory.EMPTY_ENV;
        }

        @Override
        public IMode getMode()
        {
          return SimpleExecEnvFactory.EMPTY_MODE;
        }
        
        @Override
        public ISite getSite() {
          return SimpleExecEnvFactory.EMPTY_SITE;
        };
        
        @Override
        public EOperatingSystem getOS() {
          return EOperatingSystem.Unknown;
        };
      };
}
