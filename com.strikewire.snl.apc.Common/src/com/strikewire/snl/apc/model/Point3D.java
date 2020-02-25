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
 *  kholson on Jan 15, 2015
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.model;

/**
 * <p>
 * A class that represents a 3D coordinate in space; it is conceptually similar
 * to the JavaFX Point3D, and should be replaced by it when everything moves to
 * Java 8.
 * 
 * @author kholson
 *
 */
public class Point3D
{
  private final Double x;
  private final Double y;
  private final Double z;

  private final int hash;

  /**
   * ZERO - The origin (0, 0, 0)
   */
  public static Point3D ZERO = new Point3D(0, 0, 0);




  /**
   * 
   */
  public Point3D(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;

    hash = calcHash();
  }
  
  


  public double getX()
  {
    return this.x;
  }




  public double getY()
  {
    return this.y;
  }




  public double getZ()
  {
    return this.z;
  }




  @Override
  public boolean equals(Object obj)
  {
    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Point3D)) {
      return false;
    }

    Point3D p3d = (Point3D) obj;

    return (p3d.x.equals(this.x) && p3d.y.equals(this.y) && p3d.z.equals(this.z));
  }




  private int calcHash()
  {
    long bits = 7L;
    int hash;
    bits = 31L * bits + Double.doubleToLongBits(getX());
    bits = 31L * bits + Double.doubleToLongBits(getY());
    bits = 31L * bits + Double.doubleToLongBits(getZ());
    hash = (int) (bits ^ (bits >> 32));

    return hash;
  }




  @Override
  public int hashCode()
  {

    return hash;
  }
  
  @Override
  public String toString()
  {
    return String.format("[ %f, %f, %f]",
        this.x, this.y, this.z);
  }

}
