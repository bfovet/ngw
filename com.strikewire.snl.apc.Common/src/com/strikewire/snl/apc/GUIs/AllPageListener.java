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
 * Created by mjgibso on Apr 4, 2013 at 6:57:38 AM
 */
package com.strikewire.snl.apc.GUIs;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import com.strikewire.snl.apc.listeneradapters.PageAdapter;

/**
 * @author mjgibso
 *
 */
public abstract class AllPageListener extends AllWindowListener
{
	private class PageListener extends PageAdapter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.strikewire.snl.apc.PageAdapter#pageOpened(org.eclipse.ui.
		 * IWorkbenchPage )
		 */
		@Override
		public void pageOpened(IWorkbenchPage page) {
			registerPage(page);
		}
	}
	
	private IPageListener pageListener_;
	
	/**
	 * 
	 */
	public AllPageListener()
	{
		super();
	}
	
	protected abstract void registerPage(IWorkbenchPage page);
	protected abstract void deregisterPage(IWorkbenchPage page);
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.AllWindowListener#registerWindow(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	protected void registerWindow(IWorkbenchWindow window)
	{
		window.addPageListener(getPageListener(true));

		for(IWorkbenchPage page : window.getPages())
		{
			registerPage(page);
		}
	}
	
	private synchronized IPageListener getPageListener(boolean init)
	{
		if(this.pageListener_==null && init)
		{
			this.pageListener_ = new PageListener();
		}
		
		return this.pageListener_;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.AllWindowListener#deregisterWindow(org.eclipse.ui.IWorkbenchWindow)
	 */
	@Override
	protected void deregisterWindow(IWorkbenchWindow window)
	{
		IPageListener pageListener = getPageListener(false);
		if(pageListener != null)
		{
			window.removePageListener(pageListener);
		}
		
		for(IWorkbenchPage page: window.getPages())
		{
			deregisterPage(page);
		}
	}
}
