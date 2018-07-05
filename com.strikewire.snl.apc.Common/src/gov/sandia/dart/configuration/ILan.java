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
 * <p>
 * Represents a LAN (such as srn, local, scn, restricted, etc.) upon which the
 * application is running.
 * </p>
 * <p>
 * The LAN is used in several configuration places to specify settings such as:
 * <ul>
 * <li>The URI to which the server will connect for data management</li>
 * <li>The URI for posting metrics</li>
 * <li>The machines that are available</li>
 * <li>Kerberos login configurations</li>
 * </ul>
 * </p>
 * 
 * Checks to see if the current LAN matches the specified LAN; in general, the
 * two LANs must have the same value, unless one or the other is a
 * wildcard (*). However, the specific parameters are implementation specific.
 * 
 * @author kholson
 *
 */
public interface ILan extends IExecEnvInfo, Predicate<ILan>
{

  /**
   * @return The value of the LAN as a String; must not return null.
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
  public String getLan();

}
