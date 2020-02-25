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
 * Created by mjgibso on Sep 17, 2013 at 11:02:20 AM
 */
package com.strikewire.snl.apc.selection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

/**
 * I DON'T THINK THIS CLASS SHOULD BE USED.
 * 
 * I created it because we were doing this in several views, and I wanted to consolidate the code.
 * However, I think this is the wrong model for updating selection.  In most cases, true selection
 * providers should be viewers (which are already selection providers).  In cases where views don't
 * have a simple single viewer in them that deals with sending and receiving selections, using
 * a {@link MultiControlSelectionProvider} may be the best approach.
 * 
 * References like getSite().getSelectionProvider().setSelection() I believe are flawed.  Many
 * places that is done, there is an assumption that the site has a selection provider like this
 * installed that will re-broadcast the selection.  That is a forced, and not natural way to do things.
 * Furthermore, it is inconsistent with what that would normally mean.  Usually, that would really
 * mean something more along the lines of getting the viewer in the view, and requesting to set
 * a given selection in the viewer, which is somewhat backwards to attempting to broadcast a
 * selection through the eclipse selection framework.
 * 
 * Another flaw with this system is that it introduces an extra layer between the selection
 * framework and the actual viewer of the data model.  i.e. it relies on yet another layer exist
 * to ensure the cached selection herein is updated when it changes on the underlying model viewer.
 * In practice, this sometimes fails, and leads to a stale selection cache which natural leads
 * to inaccurate selection behavior.
 * 
 * Another undesired behavior of using a selection provider like this is that if it's properly
 * connected to a real viewer as described above, that can potentially lead to duplicate selection
 * events, which can have undesirable outcomes with registered selection listeners, and can
 * potentially lead to recursive view activation issues.
 * 
 * @author mjgibso
 * @deprecated
 * @see MultiControlSelectionProvider
 */
public class CachingSelectionProvider extends AbstractSelectionProvider
{
	protected ISelection selection_;
	
	private boolean broadcast_ = false;
	
	/**
	 * 
	 */
	public CachingSelectionProvider()
	{
		this(false);
	}
	
	/**
	 * 
	 */
	public CachingSelectionProvider(boolean broadcast)
	{
		setBroadcasting(broadcast);
	}
	
	public boolean getBroadcasting()
	{
		return this.broadcast_;
	}
	
	public boolean setBroadcasting(boolean broadcast)
	{
		boolean oldBroadcast = this.broadcast_;
		this.broadcast_ = broadcast;
		return oldBroadcast;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection()
	{
		return selection_;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection)
	{
		this.selection_ = selection;
		
		if(broadcast_)
		{
			fireSelectionEvent(new SelectionChangedEvent(this, selection_));
		}
	}
}
