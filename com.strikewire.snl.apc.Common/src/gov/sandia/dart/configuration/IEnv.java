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

import java.util.function.Predicate;

/**
 * <p>Represents an &quot;environment&quot; where the application is
 * running, such as development, quality, production, testing, etc.</p>
 * <p>The env is used in several configuration places to specify settings
 * such as:
 * <ul>
 * <li>The URI to which the server will connect for data management</li>
 * <li>The URI for posting metrics</li>
 * </ul>
 * 
 * Checks to see if the current env matches the specified env;
 * in general, the two environments must have the same
 * setting, unless one or the other is a wildcard (*). However, the
 * specific parameters are implementation specific.
 * 
 * @author kholson
 *
 */
public interface IEnv extends IExecEnvInfo, Predicate<IEnv>
{

  
  /**
   * @return The String representation of an environment, such as
   * development, quality, production, testing, etc.
   * @author kholson
   * <p>Initial Javadoc date: Jun 11, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public String getEnv();
}
