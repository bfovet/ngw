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
 * Created by mjgibso on Apr 4, 2013 at 6:51:37 AM
 */
package com.strikewire.snl.apc.GUIs;

import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.WindowAdapter;

/**
 * @author mjgibso
 *
 */
public abstract class AllWindowListener
{
	private class WindowListener extends WindowAdapter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * com.strikewire.snl.apc.WindowAdapter#windowOpened(org.eclipse.ui.
		 * IWorkbenchWindow)
		 */
		@Override
		public void windowOpened(IWorkbenchWindow window) {
			registerWindow(window);
		}
	}
	
	private final IWindowListener windowListener_ = new WindowListener();

	
	/**
	 * 
	 */
	public AllWindowListener()
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.addWindowListener(windowListener_);

		for(IWorkbenchWindow window : workbench.getWorkbenchWindows())
		{
			registerWindow(window);
		}
	}
	
	public void dispose()
	{
		IWorkbench workbench = PlatformUI.getWorkbench();
		
		workbench.removeWindowListener(windowListener_);
		
		for(IWorkbenchWindow window : workbench.getWorkbenchWindows())
		{
			deregisterWindow(window);
		}
	}
	
	protected abstract void registerWindow(IWorkbenchWindow window);
	protected abstract void deregisterWindow(IWorkbenchWindow window);
}
