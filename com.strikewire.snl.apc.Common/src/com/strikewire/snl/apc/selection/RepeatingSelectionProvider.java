/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.selection;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;

import com.strikewire.snl.apc.GUIs.GuiUtils;

/**
 * @author mjgibso
 *
 */
public class RepeatingSelectionProvider implements ISelectionProvider, ISelectionChangedListener
{
	private List<ISelectionChangedListener> listeners_ = null;
	
	private ISelection selection_;
	
	private boolean broadcastChanges_ = true;
	
	private IWorkbenchPage workbenchPage_;
	
	public RepeatingSelectionProvider(ISelectionProvider baseProvider)
	{
		baseProvider.addSelectionChangedListener(this);
	}

	public RepeatingSelectionProvider(ISelectionProvider baseProvider, IWorkbenchPage workbenchPage)
	{
		baseProvider.addSelectionChangedListener(this);
		
		setEditorLinking(workbenchPage);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public synchronized void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		if(listener == null)
		{
			return;
		}
		
		if(this.listeners_ == null)
		{
			this.listeners_ = new ArrayList<ISelectionChangedListener>();
		}
		
		this.listeners_.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	public ISelection getSelection()
	{ return this.selection_; }

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	public synchronized void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		if(listener==null || this.listeners_==null)
		{
			return;
		}
		
		listeners_.remove(listener);
		
		if(this.listeners_.size() < 1)
		{
			this.listeners_ = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	public void setSelection(ISelection selection)
	{ this.selection_ = selection; }

	public void selectionChanged(SelectionChangedEvent event)
	{
		if(broadcastChanges_)
		{
			setSelection(event.getSelection());
			
			broadcastChange(event);
		}
		
		IWorkbenchPage page = workbenchPage_;
		if(page != null)
		{
			GuiUtils.linkToEditor(page, event.getSelection());
		}
	}
	
	public void setBroadcasting(boolean broadcast)
	{
		this.broadcastChanges_ = broadcast;
	}
	
	public void stopEditorLinking()
	{
		this.workbenchPage_ = null;
	}
	
	public void setEditorLinking(IWorkbenchPage workbenchPage)
	{
		this.workbenchPage_ = workbenchPage;
	}
	
	private void broadcastChange(SelectionChangedEvent event)
	{
		List<ISelectionChangedListener> listeners = getListeners();
		
		if(listeners == null)
		{
			return;
		}
		
		for(ISelectionChangedListener listener : listeners)
		{
			listener.selectionChanged(event);
		}
	}
	
	private synchronized List<ISelectionChangedListener> getListeners()
	{
		if(this.listeners_ == null)
		{
			return null;
		} else {
			return new ArrayList<ISelectionChangedListener>(this.listeners_);
		}
	}
}
