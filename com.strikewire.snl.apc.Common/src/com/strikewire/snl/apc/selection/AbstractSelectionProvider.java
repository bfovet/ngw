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
 * Created by mjgibso on Mar 27, 2013 at 6:54:30 AM
 */
package com.strikewire.snl.apc.selection;

import gov.sandia.dart.common.core.listeners.BaseListenersHandler;

import java.util.Collection;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * @author mjgibso
 *
 */
public abstract class AbstractSelectionProvider implements ISelectionProvider
{
	private final BaseListenersHandler<ISelectionChangedListener> listeners_ = new BaseListenersHandler<ISelectionChangedListener>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners_.addListener(listener);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		listeners_.removeListener(listener);
	}
	
	protected void fireSelectionEvent(SelectionChangedEvent event)
	{
//		System.out.println("Fire selection event: "+event+", selection: "+event.getSelection());
		
		for(ISelectionChangedListener listener : getListeners())
		{
			listener.selectionChanged(event);
		}
	}
	
	protected Collection<ISelectionChangedListener> getListeners()
	{
		return listeners_.getListeners();
	}
}
