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
 * Create on Mar 21, 2012 at 4:51:45 PM by mjgibso
 */
package com.strikewire.snl.apc.GUIs;

import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author mjgibso
 * 
 * Ripped off from: http://wiki.eclipse.org/FAQ_How_to_decorate_a_TableViewer_or_TreeViewer_with_Columns%3F
 *
 */

/**
 * @author Annamalai Chockalingam
 * 
 */
public class TableDecoratingLabelProvider extends DecoratingLabelProvider implements ITableLabelProvider
{
	ITableLabelProvider provider;
	ILabelDecorator decorator;

	/**
	 * @param provider
	 * @param decorator
	 */
	public TableDecoratingLabelProvider(ILabelProvider provider, ILabelDecorator decorator)
	{
		super(provider, decorator);
		this.provider = (ITableLabelProvider) provider;
		this.decorator = decorator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang
	 * .Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex)
	{
		Image image = provider.getColumnImage(element, columnIndex);
		if(decorator != null)
		{
			Image decorated = decorator.decorateImage(image, element);
			if(decorated != null)
			{
				return decorated;
			}
		}
		return image;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang
	 * .Object, int)
	 */
	public String getColumnText(Object element, int columnIndex)
	{
		String text = provider.getColumnText(element, columnIndex);
		if(decorator != null)
		{
			String decorated = decorator.decorateText(text, element);
			if(decorated != null)
			{
				return decorated;
			}
		}
		return text;
	}
}
