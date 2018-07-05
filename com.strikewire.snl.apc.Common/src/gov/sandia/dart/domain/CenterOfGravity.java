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
 *  kholson on Feb 5, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.domain;

import com.strikewire.snl.apc.model.Point3D;

/**
 * Represents the center of gravity for an object; the actual CoG is
 * in a Point3D, and the total mass, which is frequently calculated
 * at the same time, is also available.
 * @author kholson
 *
 */
public class CenterOfGravity implements ICenterOfGravity
{
  private Point3D _cog = Point3D.ZERO;
  private IMass _totalMass = Mass.ZERO;
  

  /**
   * 
   */
  public CenterOfGravity()
  {
  }




  /**
   * @see gov.sandia.dart.domain.ICenterOfGravity#getCenterOfGravity()
   */
  @Override
  public Point3D getCenterOfGravity()
  {
    return _cog;
  }




  /**
   * @see gov.sandia.dart.domain.ICenterOfGravity#getTotalMass()
   */
  @Override
  public IMass getTotalMass()
  {
    return _totalMass;
  }




  /**
   * @see gov.sandia.dart.domain.ICenterOfGravity#setCenterOfGravity(com.strikewire.snl.apc.model.Point3D)
   */
  @Override
  public void setCenterOfGravity(Point3D cog)
  {
    if (cog != null) {
      _cog = cog;
    }
  }




  /**
   * @see gov.sandia.dart.domain.ICenterOfGravity#setCenterOfGravity(java.lang.Double, java.lang.Double, java.lang.Double)
   */
  @Override
  public void setCenterOfGravity(Double x, Double y, Double z)
  {
    _cog = new Point3D(x, y, z);
  }




  /**
   * @see gov.sandia.dart.domain.ICenterOfGravity#setCenterOfGravity(gov.sandia.dart.domain.IMass, gov.sandia.dart.domain.IMass, gov.sandia.dart.domain.IMass)
   */
  @Override
  public void setCenterOfGravity(IMass x, IMass y, IMass z)
  {
    setCenterOfGravity(x.getMass(), y.getMass(), z.getMass());
  }




  /**
   * @see gov.sandia.dart.domain.ICenterOfGravity#setTotalMass(gov.sandia.dart.domain.IMass)
   */
  @Override
  public void setTotalMass(IMass mass)
  {
    if (mass != null) {
      _totalMass = mass;
    }
  }

  
  @Override
  public String toString()
  {
    return _cog.toString() + " : " + _totalMass.toString();
  }
}
