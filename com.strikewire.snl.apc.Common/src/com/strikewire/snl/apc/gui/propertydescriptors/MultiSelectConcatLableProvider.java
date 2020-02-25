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

import java.util.Collection;
import java.util.Collections;

/**
 * A Label provider that supports multiple selections, and displays the 
 * multiple selections separated by a semi-color, or other specified
 * separator.
 * @author kholson
 *
 */
public class MultiSelectConcatLableProvider extends AbsMultiConcatLabelProvider 

{

  /**
   * 
   */
  public MultiSelectConcatLableProvider()
  {
  }



  /**
   * Appends the .toString() for each object to a list, separated by the
   * current separating character
   */
  @Override
  public String getText(Object object)
  {
    if (! (object instanceof Collection))
    	object = Collections.singletonList(object);
  
	Collection<Object> values = (Collection<Object>) object;

    if (values == null || values.isEmpty()) {
      return "";
    }
    
    StringBuilder sb = new StringBuilder();
    
    for (Object obj : values) {
      concatToBuffer(obj, sb);
    }
    
    return sb.toString();
  }

}
