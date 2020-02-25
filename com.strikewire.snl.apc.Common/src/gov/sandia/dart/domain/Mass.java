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

/**
 * Implementation of the IMass interface, representing the mass of an object.
 * 
 * @author kholson
 *
 */
public class Mass implements IMass
{
  private Double _mass = 0.0;


  /**
   * 
   */
  public Mass()
  {
  }




  public Mass(IMass mass)
  {
    setMass(mass);
  }




  @Override
  public void setMass(IMass mass)
  {
    if (mass != null) {
      _mass = mass.getMass();
    }

  }




  @Override
  public void setMass(Double mass)
  {
    if (mass != null) {
      _mass = new Double(mass);
    }
  }




  @Override
  public IMass add(IMass mass)
  {
    if (mass != null) {
      _mass += mass.getMass();
    }
    return this;
  }




  @Override
  public IMass add(Double mass)
  {
    if (mass != null) {
      _mass += mass;
    }
    return this;
  }




  @Override
  public IMass divideBy(IMass mass)
  {
    if (mass != null && mass.isNotZero()) {
      _mass = _mass / mass.getMass();
    }
    
    return this;  
  }




  @Override
  public Double getMass()
  {
    return new Double(_mass);
  }




  @Override
  public boolean isNotZero()
  {
    return (_mass != 0.0);
  }
  
  
  @Override
  public String toString()
  {
    return _mass.toString();
  }
  
  
  /**
   * ZERO - An unmutable Mass object with a value of 0
   */
  public static final IMass ZERO = new IMass() {
    public final Double _mass = 0.0;
    
    @Override
    public void setMass(Double mass)
    {
    }
    
    
    
    
    @Override
    public void setMass(IMass mass)
    {
    }
    
    
    
    
    @Override
    public boolean isNotZero()
    {
      return false;
    }
    
    
    
    
    @Override
    public Double getMass()
    {
      return new Double(_mass);
    }
    
    
    
    
    @Override
    public IMass divideBy(IMass mass)
    {
      return this;
    }
    
    
    
    
    @Override
    public IMass add(Double mass)
    {
      return this;
    }
    
    
    
    
    @Override
    public IMass add(IMass mass)
    {
      return this;
    }
    
    
    @Override
    public String toString()
    {
      return _mass.toString();
    }    
  };
}
