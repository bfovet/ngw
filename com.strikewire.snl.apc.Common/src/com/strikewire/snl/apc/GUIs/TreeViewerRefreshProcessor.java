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
 * Created by mjgibso on Jul 31, 2014 at 4:49:19 PM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Control;

/**
 * @author mjgibso
 *
 */
public abstract class TreeViewerRefreshProcessor<E> extends StructuredViewerRefreshProcessor<E>
{
	/**
	 * 
	 */
	public TreeViewerRefreshProcessor(AbstractTreeViewer viewer, String jobName, int delay)
	{
		super(viewer, jobName, delay);
	}
	
	protected abstract boolean isParent(E parent, E child);
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.GUIs.StructuredViewerRefreshProcessor#process(java.util.Collection, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected void process(Collection<Request> elements, IProgressMonitor monitor)
	{
		// TODO use progress monitor
		
		// TODO respect request settings for updateLabels and properties, but still process in batch
		// will have to look into what all this would mean.  i.e. for updates, would we just still batch them
		// all together, and then send the union of all properties?  Or would we batch together groups of updates
		// with unique properties collections?  What about a refresh that does NOT request update labels?  Would that
		// mean that any nested updates should not be removed, as the refresh would NOT take care of them?
		
		final Collection<E> refreshElements = new ArrayList<E>();
		final Collection<E> updateElements = new ArrayList<E>();
		boolean full = false;
		for(Request r : elements)
		{
			if(r instanceof StructuredViewerRefreshProcessor.RefreshRequest)
			{
				if(r._element == null)
				{
					full = true;
					break;
				}
				refreshElements.add(r._element);
			} else if(r instanceof StructuredViewerRefreshProcessor.UpdateRequest) {
				updateElements.add(r._element);
			}
		}
		
		if(!full)
		{
			// optimize the lists
			
			// remove any nested items within the refresh list
			refreshElements.removeAll(getNestedChildren(refreshElements));
			
			// don't remove any nested items within the update list, updates aren't recursive
			
			// remove any items in the update list that are already captured in the refresh list
			updateElements.removeAll(getNestedChildren(refreshElements, updateElements, true));
		}
		
		final boolean ffull = full;
		Control ctrl = _viewer.getControl();
		if(ctrl!=null && !ctrl.isDisposed())
		{
			ctrl.getDisplay().syncExec(new Runnable() {
				
				@Override
				public void run() {
					if(_viewer.getControl().isDisposed())
					{
						return;
					}
					
					try {
						// TODO do we really want to turn off redrawing?  Couldn't that actually be more UI flickering?
						
						Object[] oldState = ((TreeViewer) _viewer).getExpandedElements();
						_viewer.getControl().setRedraw(false);
						
						if(ffull)
						{
							_viewer.refresh(true);
						} else {
							if(!updateElements.isEmpty())
							{
								_viewer.update(updateElements.toArray(), null);
							}
							
							for(E element : refreshElements)
							{
								_viewer.refresh(element, true);
							}
						}
						
						((TreeViewer) _viewer).setExpandedElements(oldState);
					} catch (SWTException ex) {
						// Something was disposed. These things happen. Just ignore.
						// CommonPlugin.getDefault().logError("Error expanding tree", ex);
						
					} finally {
						_viewer.getControl().setRedraw(true);
						_viewer.getControl().redraw();
						postProcess();
					}
				}
			});
		}
	}
	
	protected void postProcess()
	{
	}
	
	protected Collection<E> getNestedChildren(Collection<E> elements)
	{ return getNestedChildren(elements, elements, false); }
	
	protected Collection<E> getNestedChildren(Collection<E> parents, Collection<E> children, boolean countObjectMatches)
	{
		List<E> nestedChildren = new ArrayList<E>();
		for(E parent : parents)
		{
			if(parent == null)
			{
				continue;
			}
			
			for(E child : children)
			{
				if(child == null)
				{
					continue;
				}
				
				// skip if we're on the same object
				if(parent == child)
				{
					if(countObjectMatches)
					{
						nestedChildren.add(child);
					}
					
					continue;
				}
				
				if(isParent(parent, child))
				{
					nestedChildren.add(child);
				}
			}
		}
		return nestedChildren;
	}
}
