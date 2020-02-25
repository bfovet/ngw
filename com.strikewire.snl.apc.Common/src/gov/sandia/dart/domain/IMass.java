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
 *  kholson on Jan 23, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.domain;

/**
 * <p>Interface for representing Mass of an object</p> 
 * @author kholson
 *
 */
public interface IMass
{
  /**
   * @param mass The mass to set; will update the value of this
   * object to the value in the specified mass
   */
  public void setMass(IMass mass);
  
  /**
   * @param mass Set the value of this object to the specified value
   */
  public void setMass(Double mass);
  
  /**
   * Adds the specified mass to the current value of this object
   * @return the current Object
   */
  public IMass add(IMass mass);
  
  /**
   * Adds the specified mass to the current value of this object
   * @return the current Object
   */
  public IMass add(Double mass);
  
  
  /**
   * Divides the current object's mass by the specified mass
   * @return the current Object
   */
  public IMass divideBy(IMass mass);
  
  /**
   * Return the mass of the object as a floating point
   */
  public Double getMass();
  
  /**
   * Returns true if the mass is not 0; false otherwise
   */
  public boolean isNotZero();
  

}
