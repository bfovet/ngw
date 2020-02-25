/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Aug 2, 2016 at 1:46:29 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

public class SortColumn
{
	public final String columnName_;
	public int direction_;
	public int index;
	
	SortColumn(String columnName, int direction)
	{
		this.columnName_ = columnName;
		direction_ = direction;
	}
	
	SortColumn(String columnName, int direction, int index)
	{
		this(columnName, direction);
		this.index = index;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(columnName_);
		sb.append(' ');
		sb.append(direction_);
		return sb.toString();
	}
}
