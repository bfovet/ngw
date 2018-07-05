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
 * Created by mjgibso on Apr 1, 2015 at 2:13:03 PM
 */
package com.strikewire.snl.apc.GUIs;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.listeneradapters.PartAdapter;
import com.strikewire.snl.apc.osgi.util.OSGIUtils;

/**
 * @author mjgibso
 *
 */
public abstract class AbsPersistingView extends ViewPart
{
	private static final Logger LOG = LogManager.getLogger(AbsPersistingView.class);
	
	private ServiceRegistration<EventHandler> _shutdownRegistration = null;
	
	private IPartListener _closeListener = null;
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite)
	 */
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);
		
		registerPersistListeners(site);
	}

	/**
	 * @param site
	 */
	protected void registerPersistListeners(IViewSite site) {
		_shutdownRegistration = OSGIUtils.registerShutdownEventHandler(getClass(), new EventHandler() {
			
			@Override
			public void handleEvent(Event event) {
				persistStateWrapper();
			}
		});
		
		_closeListener = new PartAdapter() {
			@Override
			public void partClosed(IWorkbenchPart part) {
				persistStateWrapper();
			};
		};
		site.getPage().addPartListener(_closeListener);
	}
	
	protected void persistStateWrapper()
	{
		LOG.debug("persisting view state");

		// the below mechanism

		XMLMemento memento = getBlankMemento();
		persistState(memento);
		StringWriter writer = new StringWriter();
		try {
			memento.save(writer);

			MPart mpart = (MPart) getSite().getService(MPart.class);
			if(mpart != null)
			{
				mpart.getPersistedState().put("memento",  writer.toString());
			} else {
				// EJFH Cubit ModelView throws one of these every time
				// CommonPlugin.log("Got a null MPart trying to save view state.  "+getClass().getName(), new Exception());
			}
		} catch (IOException e) {
			CommonPlugin.getDefault().logError(e);
		}
	}
	
	protected XMLMemento getBlankMemento()
	{
		XMLMemento root = XMLMemento.createWriteRoot("view");
		return root;
	}
	
	protected abstract void persistState(IMemento memento);
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
	 */
	@Override
	public void saveState(IMemento memento)
	{
		// do nothing
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose()
	{
		unregisterPersistListeners();
		
		super.dispose();
	}

	/**
	 * 
	 */
	protected void unregisterPersistListeners()
	{
		if(_closeListener != null)
		{
			getSite().getPage().removePartListener(_closeListener);
			_closeListener = null;
		}
		
		OSGIUtils.unregisterEventHandler(getClass(), _shutdownRegistration);
		_shutdownRegistration = null;
	}

}
