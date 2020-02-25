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
 * Created by mjgibso on Mar 8, 2017 at 11:03:37 AM
 */
package com.strikewire.snl.apc.GUIs.settings;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author mjgibso
 *
 */
public class SettingsSelection implements IStructuredSelection
{
	private final IStructuredSelection _selection;
	
	public SettingsSelection(IStructuredSelection selection)
	{
		this._selection = selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	@Override
	public boolean isEmpty()
	{
		return this._selection.isEmpty();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredSelection#getFirstElement()
	 */
	@Override
	public Object getFirstElement()
	{
		return this._selection.getFirstElement();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredSelection#iterator()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Iterator iterator()
	{
		return this._selection.iterator();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredSelection#size()
	 */
	@Override
	public int size()
	{
		return this._selection.size();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toArray()
	 */
	@Override
	public Object[] toArray()
	{
		return this._selection.toArray();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IStructuredSelection#toList()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List toList()
	{
		return this._selection.toList();
	}

}
