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

import com.strikewire.snl.apc.model.Point3D;

/**
 * <p>Represents the center of gravity. Provides access to the 
 * X,Y,Z in a Point3D, as well as the Mass (if calculated).</p>
 * @author kholson
 *
 */
public interface ICenterOfGravity
{

  /**
   * Returns the center of gravity as a point; if it has not been
   * set should return Point3D.ZERO;
   */
  public Point3D getCenterOfGravity();
  
  /**
   * Returns the total mass of the object, as it is frequently calculated
   * during the calculation of the center of gravity. The returned value
   * may be 0 if the total mass was not set/calculated when the CoG 
   * was calculated
   */ 
  public IMass getTotalMass();
  
  
  public void setCenterOfGravity(Point3D cog);
  
  public void setCenterOfGravity(Double x, Double y, Double z);
  
  public void setCenterOfGravity(IMass x, IMass y, IMass z);
  
  public void setTotalMass(IMass mass);
  

  
}
