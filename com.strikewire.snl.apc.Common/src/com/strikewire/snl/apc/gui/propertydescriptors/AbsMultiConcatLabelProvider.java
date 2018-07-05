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
 *    kholson on Sep 12, 2013
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.gui.propertydescriptors;

import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author kholson
 *
 */
public abstract class AbsMultiConcatLabelProvider extends LabelProvider
{
  protected String _separator = "; ";
  

  
  /**
   * @return The current separator
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 12, 2013
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public String getSeparator()
  {
    return _separator;
  }
  
  /**
   * Sets the separator that is placed between multiple instances, for
   * example a space " ", or a dividing character an a space "; "
   */
  public void setSeparator(final String sep)
  {
    if (sep != null) {
      _separator = sep;
    }
  }
  
  /**
   * Concatenates the String representation of the object into the specified
   * buffer, separated by the separator character; if the specified obj
   * is null, then simply returns
   * @param obj An object whose String representation should be added
   * @param sb A StringBuilder into which the concatentation should be build
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 12, 2013
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  protected void concatToBuffer(Object obj, StringBuilder sb)
  {
    if (obj == null) {
      return;
    }
    
    String s;
    
    if (! (obj instanceof String)) {
      s = String.valueOf(obj);
    }
    else {
      s = (String)obj;
    }
    
    if (sb.length() > 0) {
      sb.append(_separator);
    }
    
    sb.append(s);    
  }

}
