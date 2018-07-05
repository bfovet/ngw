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
 *  Copyright (C) 2014
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Feb 24, 2014
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart;

import java.util.Optional;

/**
 * Interface for information that is posted by an IMetricsPoster
 * 
 * @author kholson
 * 
 */
public interface IMetricsInfo
{
  /**
   * @return the plugin identifier; may return an empty String but not null
   */
  public String getPlugin();




  /**
   * @return the capability that is being represented by this posting event; may
   *         return an empty String, but not null
   */
  public String getCapability();




  /**
   * @return any additional data that should be sent
   */
  public Optional<String> getData();
}
