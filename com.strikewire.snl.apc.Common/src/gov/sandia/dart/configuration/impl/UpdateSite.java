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

package gov.sandia.dart.configuration.impl;

import gov.sandia.dart.configuration.IUpdateSite;

import java.net.URI;

/**
 * <p>A default implementation of an update site.</p>
 * @author kholson
 *
 */
public class UpdateSite implements IUpdateSite
{
  private String _name = "";
  private URI _uriMetadata = null;
  private URI _uriArtifact = null;

  /**
   * 
   */
  public UpdateSite(final String name,
                    final URI metadataURI,
                    final URI artifactURI)
  {
    setName(name);
    setMetadataRepository(metadataURI);
    setArtifactRepository(artifactURI);
  }


  public UpdateSite setName(final String n)
  {
    _name = n;
    return this;
  }
  
  public UpdateSite setMetadataRepository(final URI uri)
  {
    _uriMetadata = uri;
    return this;
  }
  
  public UpdateSite setArtifactRepository(final URI uri)
  {
    _uriArtifact = uri;
    return this;
  }
  
  


  /**
   * @see gov.sandia.dart.configuration.IUpdateSite#getMetadataRepository()
   */
  @Override
  public URI getMetadataRepository()
  {
    return _uriMetadata;
  }




  /**
   * @see gov.sandia.dart.configuration.IUpdateSite#getArtifactRepository()
   */
  @Override
  public URI getArtifactRepository()
  {
    return _uriArtifact;
  }




  /**
   * @see gov.sandia.dart.configuration.IUpdateSite#getName()
   */
  @Override
  public String getName()
  {
    return _name;
  }
  
  
  @Override
  public String toString()
  {
    return _name + " : " + String.valueOf(_uriMetadata) 
        + " : " + String.valueOf(_uriArtifact);
  }

}
