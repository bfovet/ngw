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
 * Created by mjgibso on Aug 2, 2016 at 1:48:15 PM
 */
package com.strikewire.snl.apc.GUIs.columns;

import org.eclipse.swt.widgets.Item;

public abstract class AbstractViewerColumnWrapper<C extends Item> extends AbstractColumnWrapper
{
	private final C _column;
	
	public AbstractViewerColumnWrapper(C column)
	{
		this._column = column;
	}
	
	public String getText()
	{
		return _column.getText();
	}
	
	public C getColumn()
	{
		return _column;
	}
}
