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
 *  Copyright (C) 2012
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.properties.filters;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IFilter;

/**
 * Indicates whether the selection is an IResource Container. Containers
 * are projects and folders.
 * @author kholson
 *
 */
public class IsIResourceContainer implements IFilter
{

  /**
   * 
   */
  public IsIResourceContainer()
  {
  }




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.projectexplorer.properties.filters.AbsTreeObjectFilter#select(java.lang.Object)
   */
  @Override
  public boolean select(Object toTest)
  {
    boolean bRet = false;
    
    if (toTest instanceof IAdaptable) {
      IAdaptable adapt = (IAdaptable)toTest;
      
      Object oAdaptable = adapt.getAdapter(IResource.class);
      
      bRet = (oAdaptable instanceof IContainer);
      
    }
    
    return bRet;
  }

}
