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
 * Created by mjgibso on Mar 14, 2013 at 12:01:17 PM
 */
package com.strikewire.snl.apc.listeneradapters;

import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author mjgibso
 *
 */
public abstract class PageAdapter implements IPageListener
{

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPageListener#pageActivated(org.eclipse.ui.IWorkbenchPage)
	 */
	@Override
	public void pageActivated(IWorkbenchPage page) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPageListener#pageClosed(org.eclipse.ui.IWorkbenchPage)
	 */
	@Override
	public void pageClosed(IWorkbenchPage page) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IPageListener#pageOpened(org.eclipse.ui.IWorkbenchPage)
	 */
	@Override
	public void pageOpened(IWorkbenchPage page) {}

}
