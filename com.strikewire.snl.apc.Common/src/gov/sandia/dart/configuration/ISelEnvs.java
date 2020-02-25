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
 *  kholson on Jun 17, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

import java.util.List;

/**
 * <p>An interface that allows holding the selections that should
 * be displayed to a user based upon a particular environment and lan. For
 * example, when running in a development environment, one may wish to 
 * present the dev, qual, and production IRemoteSite options, whereas
 * with a production environment one may only have a single choice.</p>
 * @author kholson
 *
 */
public interface ISelEnvs
{
  /**
   * Return the environment for which the potential list of IRemoteSite 
   * objects are available.
   */
  public IEnv getEnv();
  
  /**
   * Return the lan for which the potential list of IRemoteSite
   * objects are available 
   */
  public ILan getLan();
  
  /**
   * Return the selected list of environments
   */
  public List<IEnv> getEnvironments();
}
