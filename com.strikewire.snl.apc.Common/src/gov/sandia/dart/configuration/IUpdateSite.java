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
 *  kholson on May 27, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.configuration;

import java.net.URI;

/**
 * <p>Represents a contributed update site that will be added 
 * programatically.</p>
 * @author kholson
 *
 */
public interface IUpdateSite
{

  /**
   * Obtains the URI to the metadata repository for this update site;
   * frequently though not necessarily the same as the artifact
   * repository
   * @return A valid URI to a metadata repository
   * @author kholson
   * <p>Initial Javadoc date: Jun 17, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public URI getMetadataRepository();
  
  /**
   * Obtains the URI to the artifact repository for this update site;
   * frequently though not necessarily the same as the metadata
   * repository
   * @return A valid URI to an artifact repository
   * @author kholson
   * <p>Initial Javadoc date: Jun 17, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public URI getArtifactRepository();
  
  /**
   * Returns the name of this Update Site
   * @return A non-null, non-empty String that identifies this Update Site
   */
  public String getName();
}
