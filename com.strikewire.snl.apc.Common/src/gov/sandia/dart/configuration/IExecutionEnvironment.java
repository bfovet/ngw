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
 *  kholson on Apr 28, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

import gov.sandia.dart.common.core.env.OS.EOperatingSystem;

/**
 * <p>
 * Provides access to the execution environment for the application, including
 * the LAN, Environment, and Mode in which the application is deployed.
 * Implementing classes should provide the relevant values based upon local
 * conditions.
 * </p>
 * <p>
 * Example: Sandia National Laboratories has two primary LANs, three
 * environments (development, quality, production), and two modes (shared,
 * stand-alone). The values returned here would indicate where it is running.
 * </p>
 * 
 * @author kholson
 *
 */
public interface IExecutionEnvironment
{

  /**
   * <p>
   * Obtains the LAN (e.g., srn, scn) where the application is running
   * </p>
   * 
   * @return The LAN where the application is currently running.
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Apr 28, 2015
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
  ILan getLan();




  /**
   * @return The environment (e.g., dev, qual, prod) where the application is
   *         running
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Apr 28, 2015
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
  IEnv getEnv();




  /**
   * @return The mode in which the application is running.
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Apr 28, 2015
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
  IMode getMode();


  /**
   * @return The site for the current application
   * @author kholson
   * <p>Initial Javadoc date: Jul 10, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  ISite getSite();
  
  
  /**
   * @return The operating system for the current environment
   * @author kholson
   * <p>Initial Javadoc date: Oct 6, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  EOperatingSystem getOS();

}
