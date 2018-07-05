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

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Provides a display for multiple selections by putting the values into
 * a display where a given entity only appears once
 * @author kholson
 *
 */
public class MultiSelectUniqueSetLabelProvider extends AbsMultiConcatLabelProvider 
{

  /**
   * 
   */
  public MultiSelectUniqueSetLabelProvider()
  {
  }


  private void addToMap(String s, LabelByCount map)
  {
    Integer count = map.get(s);
    
    if (count == null) {
      count = new Integer(1);
      map.put(s, count);
    }
    else {
      count++;
      map.put(s, count);
    }
    
  }


  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.gui.propertydescriptors.IMultiValueLableProvider#getTextForValues(java.util.Collection)
   */
  @Override
  public String getText(Object object)
  {
	  
	if (! (object instanceof Collection))
		object = Collections.singletonList(object);
	  
	Collection<Object> values = (Collection<Object>) object;
		  
    Comparator<String> comp = new SortedStringComparator();
//    Map<String,Integer> map = new TreeMap<String, Integer>(comp);
    LabelByCount map = new LabelByCount(comp);
    
    for (Object obj : values) {
      String s = String.valueOf(obj);
      
      addToMap(s, map);
    }
    
    // we have the map, and it should be sorted
    Iterator<Map.Entry<String,Integer>> it;
    
    it = map.entrySet().iterator();
    
    StringBuilder sb = new StringBuilder();
    while (it.hasNext()) {
      Map.Entry<String, Integer> entry = it.next();
      
      String s = entry.getKey() + "(" + entry.getValue() + ")";
      concatToBuffer(s, sb);
      
    } //while
    
    return sb.toString();
  }

  
  private static class LabelByCount extends TreeMap<String,Integer>
  {

    /**
     * serialVersionUID - 
     */
    private static final long serialVersionUID = -3173924639461666318L;
    
    public LabelByCount(Comparator<String> comparator) {
      super(comparator);
    }
  }
  
  
  private static class SortedStringComparator implements Comparator<String>
  {

    @Override
    public int compare(String o1, String o2)
    {
      int retComp = 0;
      
      if (o1 == null) {
        if (o2 != null) {
          retComp = 1;
        }
        else {
          retComp = 0;
        }
      }
      else {
        retComp = o1.compareTo(o2);
      }
      return retComp;      
    } //compare

  } //inner class SortedStringComparator
  
}
