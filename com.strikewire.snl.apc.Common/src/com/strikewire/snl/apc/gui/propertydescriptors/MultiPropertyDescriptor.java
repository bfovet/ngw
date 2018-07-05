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
 *  Copyright (C) 2013
 *  Sandia National Laboratories
 *
 *  File originated by:
 *    kholson on Aug 23, 2013
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.gui.propertydescriptors;

import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * This class provides a base for implementing a property descriptor that
 * works with multiple selections
 * @author kholson
 *
 */
public class MultiPropertyDescriptor extends PropertyDescriptor 
implements IMultiSelectPropertyDescriptor
{

  public MultiPropertyDescriptor(Object id, String displayName)
  {
    super(id, displayName);
  }

  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.gui.propertydescriptors.IMultiSelectPropertyDescriptor#supportsMultiEditing()
   */
  @Override
  public boolean supportsMultiEditing()
  {
    return false;
  }

}
