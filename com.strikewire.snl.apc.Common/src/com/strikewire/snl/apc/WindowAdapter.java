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
 * Created by mjgibso on Mar 14, 2013 at 11:56:51 AM
 */
package com.strikewire.snl.apc;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchWindow;

/**
 * @author mjgibso
 *
 */
public abstract class WindowAdapter implements IWindowListener
{

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowActivated(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void windowActivated(IWorkbenchWindow window) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowDeactivated(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void windowDeactivated(IWorkbenchWindow window) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowClosed(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void windowClosed(IWorkbenchWindow window) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWindowListener#windowOpened(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	public void windowOpened(IWorkbenchWindow window) {}

}
