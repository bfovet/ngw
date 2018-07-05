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
 * Created by mjgibso on Aug 2, 2016 at 1:48:08 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableViewerWrapper extends AbstractViewerWrapper<TableViewer, Table, TableColumn>
{
	/**
	 * 
	 */
	public TableViewerWrapper(TableViewer viewer)
	{
		super(viewer);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#getWidget()
	 */
	@Override
	protected Table getWidget()
	{
		return getViewer().getTable();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#setSortColumn(com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.ColumnWrapper)
	 */
	@Override
	public void setSortColumn(TableColumn column)
	{
		getWidget().setSortColumn(column);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#refresh()
	 */
	@Override
	public void refresh()
	{
		TableViewer tableViewer = getViewer();
		tableViewer.getControl().setRedraw(false);
		tableViewer.refresh();
		tableViewer.getControl().setRedraw(true);
	}
	
	public void setSortDirection(int direction)
	{
		getWidget().setSortDirection(direction);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#getColumns()
	 */
	@Override
	protected TableColumn[] getColumns()
	{
		return getWidget().getColumns();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#newColumnWrapper(org.eclipse.swt.widgets.Item)
	 */
	@Override
	protected AbstractViewerColumnWrapper<TableColumn> newColumnWrapper(TableColumn column)
	{
		return new TableColumnWrapper(column);
	}
	
	
}
