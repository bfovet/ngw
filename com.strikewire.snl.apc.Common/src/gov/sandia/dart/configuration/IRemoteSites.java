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

package gov.sandia.dart.configuration;

import java.util.Collection;

/**
 * <p>A collection of remote sites that are registered against
 * an extension point.</p>
 * @author kholson
 *
 */
public interface IRemoteSites
{

  
  /**
   * @return All available remote sites
   * @author kholson
   * <p>Initial Javadoc date: Jun 16, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public Collection<IRemoteSite> getSites();
  
  
  
  /**
   * Returns the environments that are specified as selectable given
   * the specified environment and lan. Generally these selections
   * are defined in the configuration files. The selections
   * allow a development environment, for example, to support 
   * selecting a connection from multiple options, but a 
   * production environment might only allow a single selection.
   * @param env
   * @param lan
   * @return
   * @author kholson
   * <p>Initial Javadoc date: Jun 17, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public Collection<ISelEnvs> getSelectableEnvironments();
}
