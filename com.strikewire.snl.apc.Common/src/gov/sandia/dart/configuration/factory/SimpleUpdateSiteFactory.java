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
 *  kholson on Oct 15, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration.factory;

import gov.sandia.dart.configuration.IRemoteSite;
import gov.sandia.dart.configuration.IUpdateSite;
import gov.sandia.dart.configuration.impl.UpdateSite;

/**
 * @author kholson
 *
 */
public class SimpleUpdateSiteFactory
{
  /**
   * _this - The instance
   */
  private static final SimpleUpdateSiteFactory _this =
      new SimpleUpdateSiteFactory();

  /**
   * 
   */
  private SimpleUpdateSiteFactory()
  {
  }
  
  
  public static SimpleUpdateSiteFactory getInstance()
  {
    return _this;
  }
  
  
  /**
   * Makes a simple implementation of an update site.
   * @param metadata
   * @param artifact
   * @return
   * @author kholson
   * <p>Initial Javadoc date: Oct 15, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public IUpdateSite makeUpdateSite(final IRemoteSite metadata,
                                    final IRemoteSite artifact)
  {
    if (metadata == null || artifact == null) {
      throw new IllegalArgumentException("null parameter(s)");
    }
    
    UpdateSite us = new UpdateSite(metadata.getDescription(),
        metadata.getURI(),
        artifact.getURI());
    
    return us;
  }

}
