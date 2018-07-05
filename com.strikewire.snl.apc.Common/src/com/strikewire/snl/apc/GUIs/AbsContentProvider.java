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
 * Created by mjgibso on Feb 28, 2014 at 5:59:13 AM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author mjgibso
 *
 */
public abstract class AbsContentProvider implements IContentProvider
{
	private final Map<Viewer, Object> _viewersAndInputs = new HashMap<Viewer, Object>();
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public final void dispose()
	{
		if(_viewersAndInputs.isEmpty())
		{
			doDispose();
		}
	}
	
	protected abstract void doDispose();

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public final void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
		if(viewer != null)
		{
			if(newInput != null)
			{
				_viewersAndInputs.put(viewer, newInput);
			} else {
				_viewersAndInputs.remove(viewer);
			}
		}
		
		doInputChanged(viewer, oldInput, newInput);
	}
	
	protected abstract void doInputChanged(Viewer viewer, Object oldInput, Object newInput);
	
	protected Map<Viewer, Object> getViewersAndInputs()
	{
		return Collections.unmodifiableMap(_viewersAndInputs);
	}
}
