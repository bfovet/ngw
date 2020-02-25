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
 *    kholson on Sep 5, 2013
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.gui.propertydescriptors;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author kholson
 *
 */
public class MultiSelectLongAdditiveLabelProvider extends LabelProvider
{

  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.gui.propertydescriptors.IMultiValueLableProvider#getTextForValues(java.util.Collection)
   */
  @Override
  public String getText(Object object)
  {
	 if (! (object instanceof Collection))
		 object = Collections.singletonList(object);
	 
	 Collection<Object> values = (Collection<Object>) object;
	  
    String defRet = "NaN";
    String ret = defRet;
    
    long lTotal = 0L;
    long lTmp;
    
    if (values == null || values.isEmpty()) {
      return "";
    }
    
    for (Object obj : values) {
      if (obj instanceof Integer) {
        int iTmp = ((Integer)obj).intValue();
        lTotal += iTmp;
      }
      else if (obj instanceof Long) {
        lTotal += ((Long)obj).longValue();
      }
      else if (obj instanceof String) {
        try {
          lTmp = Long.parseLong((String)obj);
          lTotal += lTmp;
        }
        catch (NumberFormatException noop) {
          return defRet;
        }
      }
    } //for : process all of the objects
    
    ret = Long.toString(lTotal);
    

    return ret;
  }

}
