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
 * Created by mjgibso on Aug 2, 2016 at 1:49:06 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class TableColumnSorter extends AbstractViewerColumnSorter<TableViewer, Table, TableColumn>
{
	/**
	 * 
	 */
	public TableColumnSorter(TableViewer viewer, String prefix)
	{
		super(new TableViewerWrapper(viewer), prefix);
	}
}
