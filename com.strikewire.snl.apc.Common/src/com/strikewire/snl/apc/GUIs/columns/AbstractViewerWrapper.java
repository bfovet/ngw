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
 * Created by mjgibso on Aug 2, 2016 at 1:47:51 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

public abstract class AbstractViewerWrapper<V extends ColumnViewer, W extends Control, C extends Item>
{
	private V _viewer;
	
	public AbstractViewerWrapper(V viewer)
	{
		this._viewer = viewer;
	}
	
	protected V getViewer()
	{
		return _viewer;
	}
	
	protected abstract W getWidget();
	
	public int getColumnCount()
	{
		C[] cols = getColumns();
		return cols!=null ? cols.length : 0;
	}
	
	public AbstractViewerColumnWrapper<C> getColumn(int i)
	{
		return newColumnWrapper(getColumns()[i]);
	}
	
	public void setSortColumn(AbstractColumnWrapper column)
	{
		AbstractViewerColumnWrapper<C> columnWrapper = column!=null ? getColumnByName(column.getText()) : null;
		C c = columnWrapper!=null ? columnWrapper.getColumn() : null;
		setSortColumn(c);
	}
	
	public void updateViewer(V viewer)
	{
		this._viewer = viewer;
	}
	
	protected abstract AbstractViewerColumnWrapper<C> newColumnWrapper(C column);
	
	public abstract void setSortColumn(C column);
	
	public abstract void setSortDirection(int direction);
	
	public abstract void refresh();
	
	protected abstract C[] getColumns();
	
	public synchronized AbstractViewerColumnWrapper<C> getColumnByName(String name)
	{
		for(C c : getColumns())
		{
			if(StringUtils.equals(name, c.getText()))
			{
				return newColumnWrapper(c);
			}
		}
		
		return null;
	}
}
