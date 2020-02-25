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
 *  kholson on Apr 29, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.factory;

import gov.sandia.dart.configuration.IEnv;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.IMode;
import gov.sandia.dart.configuration.ISite;
import gov.sandia.dart.configuration.impl.ExecEnvEnv;
import gov.sandia.dart.configuration.impl.ExecEnvLan;
import gov.sandia.dart.configuration.impl.ExecEnvMode;
import gov.sandia.dart.configuration.impl.ExecEnvSite;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * A simple factory that will take a String and produce various components of
 * the IExecutionEnvironment.
 * </p>
 * 
 * @author kholson
 *
 */
public class SimpleExecEnvFactory
{
  /**
   * _this - The instance, via thread safe initialization
   */
  private static SimpleExecEnvFactory _this = new SimpleExecEnvFactory();




  /**
   * 
   */
  private SimpleExecEnvFactory()
  {

  }




  public static SimpleExecEnvFactory getInstance()
  {
    return _this;
  }




  /**
   * <p>
   * Instantiates a simple implementation of ILan that is suitable for most
   * purposes.
   * </p>
   * 
   * @param lan
   *          A non-null/non-empty String that describes the LAN
   * @return An instantiation of an ILan
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Apr 29, 2015
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
  public ILan makeLan(final String lan)
  {
    return makeLan(lan, null);
  }




  /**
   * <p>
   * Instantiates a simple implementation of ILan that is suitable for most
   * purposes.
   * </p>
   * 
   * @param lan
   *          A non-null/non-empty String that describes the LAN
   * @param initVia
   *          The way the value was initialized; if null, then the value is an
   *          empty String
   * @return An instantiation of an ILan
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Aug 17, 2015
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
  public ILan makeLan(final String lan, final String initVia)
  {
    if (StringUtils.isBlank(lan)) {
      throw new IllegalArgumentException("null/empty lan parameter");
    }

    ExecEnvLan retLan = new ExecEnvLan(lan);

    if (initVia != null) {
      retLan.setInitBy(initVia);
    }

    return retLan;
  }




  /**
   * <p>
   * Instantiates a simple implementation of IEnv that is suitable for most
   * purposes.
   * </p>
   * 
   * @param env
   *          A non-null/non-empty String that describes the environment
   *          (quality, production, development, etc.)
   * @return A class implementing IEnv
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 12, 2015
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
  public IEnv makeEnv(final String env)
  {
    return makeEnv(env, null);
  }




  /**
   * <p>
   * Instantiates a simple implementation of IEnv that is suitable for most
   * purposes.
   * </p>
   * 
   * @param env
   *          A non-null/non-empty String that describes the environment
   *          (quality, production, development, etc.)
   * @param initVia
   *          The way the value was initialized; if null, then the value is an
   *          empty String
   * @return A class implementing IEnv
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 12, 2015
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
  public IEnv makeEnv(final String env, final String initVia)
  {
    if (StringUtils.isBlank(env)) {
      throw new IllegalArgumentException("null/empty env parameter");
    }

    ExecEnvEnv retEnv = new ExecEnvEnv(env);

    if (initVia != null) {
      retEnv.setInitBy(initVia);
    }

    return retEnv;
  }




  /**
   * <p>
   * Instantiates a simple implementation of IMode that is suitable for most
   * purposes.
   * </p>
   * 
   * @param mode
   *          A non-null/non-empty String that describes the mode (e.g., shared,
   *          standalone)
   * @return An instantiated IMode
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 12, 2015
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
  public IMode makeMode(final String mode)
  {
    return makeMode(mode, null);
  }




  /**
   * <p>
   * Instantiates a simple implementation of IMode that is suitable for most
   * purposes.
   * </p>
   * 
   * @param mode
   *          A non-null/non-empty String that describes the mode (e.g., shared,
   *          standalone)
   * @param initVia
   *          The way the value was initialized; if null, then the value is an
   *          empty String
   * @return An instantiated IMode
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jun 12, 2015
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
  public IMode makeMode(final String mode, final String initVia)
  {
    if (StringUtils.isBlank(mode)) {
      throw new IllegalArgumentException("null/empty mode parameter");
    }

    ExecEnvMode retMode = new ExecEnvMode(mode);

    if (initVia != null) {
      retMode.setInitBy(initVia);
    }

    return retMode;
  }




  /**
   * <p>
   * Instantiates a simple site
   * </p>
   * 
   * @param site
   *          The site; may not be null or empty
   * @return An instantiated ISite
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jul 10, 2015
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
  public ISite makeSite(final String site)
  {
    return makeSite(site, null);
  }




  /**
   * <p>
   * Instantiates a simple site
   * </p>
   * 
   * @param site
   *          The site; may not be null or empty
   * @param initVia
   *          The way the value was initialized; if null, then the value is an
   *          empty String
   * @return An instantiated ISite
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Jul 10, 2015
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
  public ISite makeSite(final String site, final String initVia)
  {
    if (StringUtils.isBlank(site)) {
      throw new IllegalArgumentException("null/empty site parameter");
    }

    ExecEnvSite retSite = new ExecEnvSite(site);

    if (initVia != null) {
      retSite.setInitBy(initVia);
    }

    return retSite;
  }
  
  

  /**
   * EMPTY_ENV - A default IEnv, which has an empty String as the value
   */
  public static final IEnv EMPTY_ENV = new ExecEnvEnv("");


  /**
   * WILDCARD_ENV - A wildcard env, useful when searching
   */
  public static final IEnv WILDCARD_ENV = new ExecEnvEnv("*");


  /**
   * EMPTY_MODE - A default IMode, which has an empty String as the value
   */
  public static final IMode EMPTY_MODE = new ExecEnvMode("");

  /**
   * WILDCARD_MODE - A wildcard mode, useful when searching
   */
  public static final IMode WILDCARD_MODE = new ExecEnvMode("*");


  /**
   * EMPTY_LAN - A default ILan, which has an empty String as the value
   */
  public static final ILan EMPTY_LAN = new ExecEnvLan("");

  /**
   * WILDCARD_LAN - A wildcard lan, useful when searching
   */
  public static final ILan WILDCARD_LAN = new ExecEnvLan("*");


  /**
   * EMPTY_SITE - A default ISite, which has an empty String as the value
   */
  public static final ISite EMPTY_SITE = new ExecEnvSite("");

  /**
   * WILDCARD_SITE - A wildcard site, useful when searching
   */
  public static final ISite WILDCARD_SITE = new ExecEnvSite("*");

}
