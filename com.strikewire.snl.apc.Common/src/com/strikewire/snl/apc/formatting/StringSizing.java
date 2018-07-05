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
 *  kholson on Nov 4, 2015
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.formatting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;


/**
 * <p>A class that provides information about the size of String to
 * use in GUI sizing, such as setting the size of a control.
 * @author kholson
 *
 */
public class StringSizing
{
  /**
   * _log -- A Logger instance for StringSizing
   */
  private static final Logger _log = LogManager.getLogger(StringSizing.class);
  
  /**
   * 
   */
  private StringSizing()
  {
  }

  
  /**
   * Based upon the control, calculate the size of the String, and
   * return a Point with the x as the width of the String, and the
   * y as the height based upon the font. May return null if
   * the GC has an issue.
   * @param A control from which the string extent may be calculated. If
   * the control is null, will attempt to calculate based upon
   * the font metrics acquired fromt the active shell
   * @param str The string for which the width should be calculated
   * @return A point with the width of the string and the height of the
   * font; may be null if unable to obtain a GC
   */
  public static Point getStringExtent(final Control ctrl,
                                      final String str)
  {
    Point point = null;
    
    if (str == null) {
      throw new IllegalArgumentException("Null parameters");
    }
    
    GC gc = null;
    int width, height;
    
    try {
      if (ctrl != null) {
        gc = new GC(ctrl);
        
        Point p = gc.stringExtent(str);
        
        width = p.x;
        height = gc.getFontMetrics().getHeight();

        point = new Point(width, height);        
      }
      else {
        point = getMinimumStringSize(str);
      }
    }
    finally {
      if (gc != null) {
        gc.dispose();
      }
    }    
    
    return point;
  }
  
  
  /**
   * Calculates the likely minimum size of a string based upon
   * average character width of the default font from the shell. May
   * return null if cannot obtain a GC
   * @param str A String to obtain a width for
   * @return A Point with x as the width, and y as the height
   * 
   */
  public static Point getMinimumStringSize(final String str)
  {
    Point point = null;
    
    GC gc = null;
    
    _log.debug("Attempting to use default shell for control");
    try {
      IWorkbench wb = PlatformUI.getWorkbench();
      if (wb != null) {
        Shell shell = wb.getDisplay().getActiveShell();
        
        if (shell == null) {
          Display.getDefault().getActiveShell();
        }
        
        if (shell == null) {
          return null;
        }
        
        gc = new GC(shell);
        
        FontMetrics fontMetrics = gc.getFontMetrics();
        
        final int strLen = (str != null ? str.length() : 0);
        
        int width = fontMetrics.getAverageCharWidth() * strLen;
        int height = fontMetrics.getHeight();
        
        point = new Point(width, height);
      }
    }
    catch (Exception e) {
      _log.error(e);
    }
    finally {
      if (gc != null) {
        gc.dispose();
      }
    }  
    
    return point;
  }
  
  
  /**
   * Returns the FontMetrics based upon the specified control
   */
  public static FontMetrics getFontMetrics(final Control ctrl)
  {
    FontMetrics ret = null;
    
    if (ctrl == null) {
      throw new IllegalArgumentException("Null parameters");
    }
    
    GC gc = null;
    
    try {
      gc = new GC(ctrl);
      
      ret = gc.getFontMetrics();
    }
    finally {
      if (gc != null) {
        gc.dispose();
      }
    }
    
    return ret;
  }
  
  
  
  
}
