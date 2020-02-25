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
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.table.utils;

import org.eclipse.swt.SWT;

/**
 * The standard ViewerComparator from Eclipse is a class, not an interface,
 * which makes writing a re-usable component more difficult. Two things
 * which seem to be necessary to allow for the comparator to work, at least
 * as we've implemented them thus far, is for the comparator to know
 * what the last sorted column was and the direction of the sort. The
 * standard class from Eclipse has categories, and a very simplistic
 * compare method (which mosty needs to be overridden anyway).
 * @author kholson
 *
 */
public interface IViewerComparator
{
  /**
   * Definitions for the sorting direction, ascending or descending. Maps
   * these to SWT.UP and SWT.DOWN respectively. Provides an inversion
   * method.
   * @author kholson
   *
   */
  public enum ESortDirection {
    /**
     * ASCENDING - smallest to largest; going up
     */
    ASCENDING(SWT.UP),
    
    /**
     * DESCENDING - largest to smallest; going down 
     */
    DESCENDING(SWT.DOWN),
    ;
    
    private final int _equiv;
    
    private ESortDirection(int eq)
    {
      _equiv = eq;
    }
    
    /**
     * Returns the SWT.UP or SWT.DOWN equivalent of the sort direction
     */
    public int getSWTEquiv()
    {
      return _equiv;
    }
    
    public static ESortDirection invert(ESortDirection cur)
    {
      ESortDirection ret = ASCENDING;
      switch (cur) {
        case ASCENDING:
          ret = DESCENDING;
          break;
        case DESCENDING:
          ret = ASCENDING;
          break;
      }
      
      return ret;
    }
  };
  
  /**
   * Sets the current sort column to the specified column. In general,
   * implementing methods should track the existing column, and reverse
   * the sort direction if the same column is passed that is already being
   * used for sorting.
   */
  public void setColumn(ITableColumn column);
  
  /**
   * Returns the current column upon which sorting is being performed.
   * @return The current sort column
   */
  public ITableColumn getColumn();
  
  
  /**
   * <p>Returns the current sort direction, which should be either
   * SWT.DOWN or SWT.UP. Unfortunately, Eclipse pre-dates the widespread
   * use of enums, so it utilizes ints.</p>
   * <ul>
   * <li>SWT.DOWN : 1<<10</li>
   * <li>SWT.UP : 1<<7</li>
   * </ul>
   * @return The current sort direction
   */
  public ESortDirection getDirection();
  
  
  
}
