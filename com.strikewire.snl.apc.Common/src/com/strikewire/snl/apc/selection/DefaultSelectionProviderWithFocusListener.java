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
 * Created by mjgibso on Mar 27, 2013 at 6:45:32 AM
 */
package com.strikewire.snl.apc.selection;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Control;

/**
 * @author mjgibso
 *
 */
public class DefaultSelectionProviderWithFocusListener extends AbstractSelectionProviderPassthroughWithFocusListener implements ISelectionProviderWithFocusListener
{
	private final Control control_;
	
	public DefaultSelectionProviderWithFocusListener(ISelectionProvider provider, Control control)
	{
		super(provider);
		
		this.control_ = control;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.AbstractSelectionProviderWithFocusProvider#getControl()
	 */
	@Override
	public Control getControl()
	{
		return control_;
	}
}
