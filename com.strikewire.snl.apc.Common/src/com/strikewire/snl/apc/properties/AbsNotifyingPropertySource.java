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
 * Created by mjgibso on Feb 17, 2014 at 6:06:11 AM
 */
package com.strikewire.snl.apc.properties;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertySheet;
import org.eclipse.ui.views.properties.PropertySheetPage;

import com.strikewire.snl.apc.Common.CommonPlugin;


/**
 * @author mjgibso
 *
 */
public abstract class AbsNotifyingPropertySource extends AbsPropertySource implements INotifyingPropertySource
{
	private static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";
	
	private IPropertyChangeListener _listener;
	
	private boolean _forcePropertyViewRefresh = false;
	
	protected void notifyPropertyChanged()
	{
		notifyPropertyChanged(new PropertyChangeEvent(this, "", null, null));
	}
	
	protected void notifyPropertyChanged(final PropertyChangeEvent event)
	{
		final IPropertyChangeListener listener = getPropertyChangeListener();
		final boolean forceRefresh = getForcePropertyViewRefresh();
		
		Display display = Display.getDefault();
		if(display.getThread() == Thread.currentThread())
		{
			doNotifyPropertyChanged(listener, event, forceRefresh);
		} else {
			display.asyncExec(new Runnable() {
				
				@Override
				public void run() {
					try {
						doNotifyPropertyChanged(listener, event, forceRefresh);
					} catch (Throwable t) {
						CommonPlugin.getDefault().logError("Error updating property sheet", t);
					}
				}
			});
		}
	}
	
	private static void doNotifyPropertyChanged(IPropertyChangeListener listener, PropertyChangeEvent event, boolean forcePropertyViewRefresh)
	{
		if(listener != null)
		{
			listener.propertyChange(event);
		}
		
		if(forcePropertyViewRefresh)
		{
			forcePropertyViewRefresh();
		}
	}
	
	private static void forcePropertyViewRefresh()
	{
		for(IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
		{
			for(IWorkbenchPage page : window.getPages())
			{
				IViewReference ref = page.findViewReference(PROPERTIES_VIEW_ID);
				if(ref != null)
				{
					IViewPart part = ref.getView(false);
					if(part != null)
					{
						if(part instanceof PropertySheet)
						{
							PropertySheet pView = (PropertySheet) part;
							IPage ipage = pView.getCurrentPage();
							if(ipage instanceof IPropertySheetPage)
							{
								IPropertySheetPage propertyPage = (IPropertySheetPage) ipage;
								if(propertyPage instanceof PropertySheetPage)
								{
									PropertySheetPage propPage = (PropertySheetPage) propertyPage;
									propPage.refresh();
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * If this is set true, then when a call is made to {@link #notifyPropertyChanged(PropertyChangeEvent)},
	 * the implementation will attempt to find an active eclipse Properties view and refresh it in
	 * addition to calling {@link IPropertyChangeListener#propertyChange(PropertyChangeEvent)} on the
	 * current {@link #_listener}.  It should be noted however, that due to the architecture of the
	 * eclipse {@link IPropertySource} infrastructure, the existing {@link IPropertySource} instance
	 * (this) will not be used to provide new descriptors or property values, but rather a new
	 * {@link IPropertySource} instance will be constructed via the adaptor framework method for currently
	 * selected object.  Therefore, it is important that the adaptor or adaptor factory that is responsible
	 * for constructing the {@link IPropertySource} instance have the new information embodying the reason
	 * for a given change event.  Specifically then, it is not helpful to compute and/or store any state
	 * information within a given {@link IPropertySource} instance with the expectation that it will be
	 * available subsequent to a call to {@link #notifyPropertyChanged(PropertyChangeEvent)}.  This could
	 * lead to a cyclic refresh.
	 * 
	 * @param forceRefresh
	 */
	public void setForcePropertyViewRefresh(boolean forceRefresh)
	{
		this._forcePropertyViewRefresh = forceRefresh;
	}
	
	public boolean getForcePropertyViewRefresh()
	{
		return this._forcePropertyViewRefresh;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.properties.INotifyingPropertySource#setPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
	 */
	@Override
	public void setPropertyChangeListener(IPropertyChangeListener listener)
	{
		this._listener = listener;
	}
	
	public IPropertyChangeListener getPropertyChangeListener()
	{
		return this._listener;
	}
}
