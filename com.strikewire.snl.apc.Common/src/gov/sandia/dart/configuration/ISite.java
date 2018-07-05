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
 *  kholson on Jul 10, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

import java.util.function.Predicate;

/**
 * <p>Represents a deployment site.</p>
 * 
 * Checks to see if the current Site matches the specified Site; in general, the
 * two Sites must have the same value, unless one or the other is a
 * wildcard (*). However, the specific parameters are implementation specific.
 * 
 * @author kholson
 *
 */
public interface ISite extends IExecEnvInfo, Predicate<ISite>
{


  /**
   * @return The value of the Site as a String; must not return null.
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
  public String getSite();
}
