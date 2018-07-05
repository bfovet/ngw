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
 * Created by mjgibso on Jan 19, 2010 at 6:41:28 AM
 */
package com.strikewire.snl.apc.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * @author mjgibso
 *
 */
public abstract class SharedWorkspaceListener
{
	private final int eventTypes_;
	
	private IResourceChangeListener listener_;
	
	private List<Object> saveRequesters_;
	
	protected SharedWorkspaceListener(int eventTypes)
	{
		this.eventTypes_ = eventTypes;
	}
	
	protected synchronized void addRequester(Object requester)
	{
		boolean firstRequester = false;
		if(saveRequesters_ == null)
		{
			saveRequesters_ = new ArrayList<Object>();
			firstRequester = true;
		}
		
		saveRequesters_.add(requester);
		
		if(firstRequester)
		{
			installListener();
		}
	}
	
	protected synchronized void removeRequester(Object requester)
	{
		if(saveRequesters_ == null)
		{
			return;
		}
		
		saveRequesters_.remove(requester);
		
		if(saveRequesters_.size() < 1)
		{
			saveRequesters_ = null;
			uninstallListener();
		}
	}
	
	protected abstract IResourceChangeListener createListener();
	
	private synchronized IResourceChangeListener getListener()
	{
		if(this.listener_ == null)
		{
			this.listener_ = createListener();
		}
		
		return this.listener_;
	}
	
	private void installListener()
	{ ResourcesPlugin.getWorkspace().addResourceChangeListener(getListener(), eventTypes_); }
	
	private void uninstallListener()
	{ ResourcesPlugin.getWorkspace().removeResourceChangeListener(getListener()); }
}
