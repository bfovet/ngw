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
 *  kholson on Jun 16, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

/**
 * <p>Represents an application that has remote sites associated with
 * it.</p>
 * @author kholson
 *
 */
public interface IRemoteApplication
{

  /**
   * <p>Returns the name of the application.</p>
   * @return The name of the application; may be empty, but not null
   */
  public String getApplicationName();
 
  
  /**
   * Checks to see if the current Application matches the specified Application;
   * in general, the two applications must have the same
   * setting, unless one or the other is a wildcard (*). However, the
   * specific parameters are implementation specific.
   * @param env The environment to check
   * @return True if the environments match, false otherwise
   * @author kholson
   * <p>Initial Javadoc date: Jun 17, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public boolean matches(final IRemoteApplication remoteApp);  
}
