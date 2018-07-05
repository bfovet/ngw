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
 * Created by mjgibso on Mar 27, 2013 at 11:05:31 AM
 */
package com.strikewire.snl.apc.selection;

import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;


/**
 * @author mjgibso
 *
 */
public abstract class AbstractSelectionProviderWithFocusListener extends AbstractSelectionProvider implements ISelectionProviderWithFocusListener
{
	public abstract Control getControl();

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.ISelectionProviderWithFocusProvider#addFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	@Override
	public void addFocusListener(FocusListener listener)
	{
		Control control = getControl();
		if(control!=null && !control.isDisposed())
		{
			control.addFocusListener(listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.ISelectionProviderWithFocusProvider#removeFocusListener(org.eclipse.swt.events.FocusListener)
	 */
	@Override
	public void removeFocusListener(FocusListener listener)
	{
		Control control = getControl();
		if(control!=null && !control.isDisposed())
		{
			control.removeFocusListener(listener);
		}
	}
}
