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
 * Created by mjgibso on Mar 18, 2013 at 6:45:48 AM
 */
package com.strikewire.snl.apc.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Control;

/**
 * @author mjgibso
 *
 */
public class MultiControlSelectionProvider extends AbstractSelectionProvider implements ISelectionProvider
{
	protected volatile ISelectionProviderWithFocusListener activeProvider_;
	
	protected final Map<ISelectionProviderWithFocusListener, MyFocusListener> providers_ = new HashMap<ISelectionProviderWithFocusListener, MyFocusListener>();
	
	protected class MyFocusListener implements FocusListener
	{
		protected final ISelectionProviderWithFocusListener provider_;
		
		protected MyFocusListener(ISelectionProviderWithFocusListener provider)
		{
			this.provider_ = provider;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
		 */
		@Override
		public void focusGained(FocusEvent e)
		{
//			System.out.println("gained: "+e);
			
			setActiveProvider(provider_);
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
		 */
		@Override
		public void focusLost(FocusEvent e)
		{
//			System.out.println("lost: "+e);
			
			// remove the associated provider as the active provider
//			removeActiveProvider(provider_);
		}
	}
	
//	private synchronized void removeActiveProvider(ISelectionProviderWithControl provider)
//	{
//		if(activeProvider_ == provider)
//		{
//			activeProvider_ = null;
//		}
//	}
	
	protected synchronized void setActiveProvider(ISelectionProviderWithFocusListener provider)
	{
		boolean providerChanging = activeProvider_ != provider;
		
		activeProvider_ = provider;
		
		if(providerChanging && provider!=null)
		{
			ISelection sel = provider.getSelection();
			SelectionChangedEvent event = new SelectionChangedEvent(provider, sel!=null ? sel : StructuredSelection.EMPTY);
			
			fireSelectionEvent(event);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public synchronized void addSelectionChangedListener(ISelectionChangedListener listener)
	{
//		System.out.println("add listener: "+listener);
		if(listener == null)
		{
			return;
		}
		
		super.addSelectionChangedListener(listener);
		
		for(ISelectionProvider provider : this.providers_.keySet())
		{
			provider.addSelectionChangedListener(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public synchronized void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
//		System.out.println("remove listener: "+listener);
		if(listener == null)
		{
			return;
		}
		
		super.removeSelectionChangedListener(listener);
		
		for(ISelectionProvider provider : this.providers_.keySet())
		{
			provider.removeSelectionChangedListener(listener);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public synchronized ISelection getSelection()
	{
		ISelection selection = activeProvider_!=null ? activeProvider_.getSelection() : new StructuredSelection();
//		System.out.println("get selection.  ActiveProvider: "+activeProvider_+" selection: "+selection);
		return selection;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public synchronized void setSelection(ISelection selection)
	{
//		System.out.println("set: "+selection);
		if(activeProvider_ != null)
		{
			activeProvider_.setSelection(selection);
		}
	}
	
	public synchronized boolean addSelectionProvider(ISelectionProviderWithFocusListener provider)
	{
//		System.out.println("add provider: "+provider);
		if(providers_.containsKey(provider))
		{
			return false;
		}
		
		MyFocusListener focusListener = new MyFocusListener(provider);
		providers_.put(provider, focusListener);
		
		provider.addFocusListener(focusListener);
		
		for(ISelectionChangedListener listener : getListeners())
		{
			provider.addSelectionChangedListener(listener);
		}
		
		return true;
	}
	
	public synchronized boolean removeSelectionProvider(ISelectionProviderWithFocusListener provider)
	{
//		System.out.println("remove provider: "+provider);
		
		MyFocusListener focusListener = this.providers_.remove(provider);
		
		if(focusListener == null)
		{
			return false;
		}
		
		provider.removeFocusListener(focusListener);
		
		for(ISelectionChangedListener listener : getListeners())
		{
			provider.removeSelectionChangedListener(listener);
		}
		
		if(provider == activeProvider_)
		{
			setActiveProvider(null);
		}
		
		return true;
	}
	
	public boolean addSelectionProvider(Viewer viewer)
	{
		if(viewer == null)
		{
			return false;
		}
		
		return addSelectionProvider(new ViewerSelectionProvider(viewer));
	}
	
	public boolean removeSelectionProvider(Viewer viewer)
	{
		if(viewer == null)
		{
			return false;
		}
		
		return removeSelectionProvider(new ViewerSelectionProvider(viewer));
	}
	
	protected static class ViewerSelectionProvider extends AbstractSelectionProviderPassthroughWithFocusListener implements ISelectionProviderWithFocusListener
	{
		protected final Viewer viewer_;
		
		protected ViewerSelectionProvider(Viewer viewer)
		{
			super(viewer);
			
			this.viewer_ = viewer;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.AbstractSelectionProviderWithFocusProvider#getControl()
		 */
		@Override
		public Control getControl()
		{
			return viewer_.getControl();
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if(!(obj instanceof ViewerSelectionProvider))
			{
				return false;
			}
			
			if(obj == this)
			{
				return true;
			}
			
			return ObjectUtils.equals(viewer_, ((ViewerSelectionProvider) obj).viewer_);
		}
		
		@Override
		public int hashCode()
		{
			return viewer_.hashCode();
		}
	}
	
	public synchronized void clear()
	{
		for(ISelectionProviderWithFocusListener provider : new ArrayList<ISelectionProviderWithFocusListener>(providers_.keySet()))
		{
			removeSelectionProvider(provider);
		}
		
		setActiveProvider(null);
	}
}
