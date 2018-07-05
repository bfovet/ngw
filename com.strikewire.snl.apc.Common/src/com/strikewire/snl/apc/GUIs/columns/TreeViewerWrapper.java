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
 * Created by mjgibso on Aug 2, 2016 at 1:48:00 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.strikewire.snl.apc.GUIs.GuiUtils;

public class TreeViewerWrapper extends AbstractViewerWrapper<TreeViewer, Tree, TreeColumn>
{
	public TreeViewerWrapper(TreeViewer viewer)
	{
		super(viewer);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#getWidget()
	 */
	@Override
	protected Tree getWidget()
	{
		return getViewer().getTree();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#setSortColumn(com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.ColumnWrapper)
	 */
	@Override
	public void setSortColumn(TreeColumn column)
	{
		getWidget().setSortColumn(column);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#refresh()
	 */
	@Override
	public void refresh()
	{
		GuiUtils.refreshTreeViewer(getViewer());
	}
	
	public void setSortDirection(int direction)
	{
		getWidget().setSortDirection(direction);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#getColumns()
	 */
	@Override
	protected TreeColumn[] getColumns()
	{
		return getWidget().getColumns();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.util.ColumnViewerUtils.ColumnSorter.AbstractViewerWrapper#newColumnWrapper(org.eclipse.swt.widgets.Item)
	 */
	@Override
	protected AbstractViewerColumnWrapper<TreeColumn> newColumnWrapper(TreeColumn column)
	{
		return new TreeColumnWrapper(column);
	}
}
