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
 * Created by mjgibso on Aug 2, 2016 at 1:42:44 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;

/**
 * Column sorter class for tree viewers.  Clients may wish to define their own extensions of this class.
 */
public abstract class AbstractViewerColumnSorter<V extends ColumnViewer, W extends Control, C extends Item> extends ColumnSorter
{
	private final AbstractViewerWrapper<V, W, C> _viewerWrapper;
	
	protected AbstractViewerColumnSorter(AbstractViewerWrapper<V, W, C> viewerWrapper, String prefix)
	{
		super(prefix);
		
		if(viewerWrapper == null)
		{
			throw new IllegalArgumentException("Viewer cannot be null");
		}
		
		this._viewerWrapper = viewerWrapper;
	}
	
	public synchronized void updateViewer(V viewer)
	{
		_viewerWrapper.updateViewer(viewer);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.columns.AbstractColumnSorter#initSorting(java.lang.String)
	 */
	@Override
	public synchronized void initSorting(String sortInfo)
	{
		super.initSorting(sortInfo);
		
		if(StringUtils.isBlank(sortInfo))
		{
			int columnCount = _viewerWrapper.getColumnCount();
			if(columnCount > 0)
			{
				setSortColumn(_viewerWrapper.getColumn(0), 0);
			}
			
			return;
		}
		
		updateSortColumn();
	}
	
	synchronized void updateSortColumn()
	{
		List<SortColumn> sortColumns = getSortColumns();
		if(!sortColumns.isEmpty())
		{
			updateSortColumn(sortColumns.iterator().next());
		} else {
			_viewerWrapper.setSortColumn((C) null);
		}
	}
	
	protected void updateSortColumn(SortColumn sortCol)
	{
		_viewerWrapper.setSortColumn(_viewerWrapper.getColumnByName(sortCol.columnName_));
		_viewerWrapper.setSortDirection(sortCol.direction_);
	}

	public synchronized void setSortColumn(C col)
	{
		setSortColumn(_viewerWrapper.newColumnWrapper(col), 0);
	}
	
	public synchronized void setSortColumn(C col, int index)
	{
		setSortColumn(_viewerWrapper.newColumnWrapper(col), index);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.columns.AbstractColumnSorter#setSortColumn(java.lang.String, int)
	 */
	@Override
	public synchronized void setSortColumn(String colName, int index)
	{
		super.setSortColumn(colName, index);
		
		updateSortColumn();
		
		_viewerWrapper.getViewer().refresh();
	}
}
