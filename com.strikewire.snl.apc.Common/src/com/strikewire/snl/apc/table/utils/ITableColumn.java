/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.table.utils;

/**
 * An interface which provides information about a table column. Frequently
 * implemented by an enum (though not required), it allows for passing a
 * class which implements this interface to the other table column utils.
 * @author kholson
 *
 */
public interface ITableColumn
{

  public int getWeight();
  
  public void setWeight(int weight);
  
  public String getTitle();
  
  public void setTitle(final String title);
}
