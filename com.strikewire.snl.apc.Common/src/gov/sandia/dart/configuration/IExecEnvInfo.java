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
 *  kholson on Aug 17, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

/**
 * An interface that other interfaces in the Execution Environment
 * system extend and provides methods for describing basic information
 * about the particular aspect of the execution environment.
 * @author kholson
 *
 */
public interface IExecEnvInfo
{
  /**
   * @return A String indicating how the value was initialized; may return
   * an empty value, but not null. Used only for debuggin purposes.
   * @author kholson
   * <p>Initial Javadoc date: Aug 17, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public String getInitBy();
}
