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
 * Created by mjgibso on Mar 6, 2014 at 4:28:04 AM
 */
package gov.sandia.dart.metrics;

import gov.sandia.dart.MetricsInfo;
import gov.sandia.dart.application.DARTApplicationAdapter;
import gov.sandia.dart.application.DARTApplicationEvent;
import gov.sandia.dart.application.IDARTApplicationListener;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.AllWindowListener;
import com.strikewire.snl.apc.osgi.util.OSGIUtils;

/**
 * @author mjgibso
 *
 */
public class PerspectiveMetricRecorder extends DARTApplicationAdapter implements IDARTApplicationListener
{
	private final IPerspectiveListener _perspectiveListener = new PerspectiveAdapter() {
		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
			logPerspectiveActivated(perspective);
		}
	};
	
	private static void logPerspectiveActivated()
	{
		IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for(IWorkbenchWindow window : windows)
		{
			IWorkbenchPage[] pages = window.getPages();
			for(IWorkbenchPage page : pages)
			{
				IPerspectiveDescriptor desc = page.getPerspective();
				logPerspectiveActivated(desc);
			}
		}
	}
	
	private static void logPerspectiveActivated(IPerspectiveDescriptor perspective)
	{
		if(perspective != null)
		{
			OSGIUtils.postMetricEvent(CommonPlugin.class, new MetricsInfo()
			.setPlugin(CommonPlugin.ID)
			.setCapability("perspective_activated")
			.setData(perspective.getId()));
		}
	}
	
	/**
	 * 
	 */
	public PerspectiveMetricRecorder()
	{}

	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.application.IDARTApplicationListener#preApplicationEvent(com.strikewire.snl.apc.application.DARTApplicationEvent)
	 */
	@Override
	public void preApplicationEvent(DARTApplicationEvent event)
	{
		switch (event)
		{
			case WORKBENCH_ADVISOR_INITIALIZE:
				hookWindowListener();
				break;
			default:
				break;
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.application.DARTApplicationAdapter#postApplicationEvent(gov.sandia.dart.application.DARTApplicationEvent)
	 */
	@Override
	public void postApplicationEvent(DARTApplicationEvent event)
	{
		switch(event)
		{
			case WINDOW_ADVISOR_POST_WINDOW_OPEN:
				logPerspectiveActivated();
				break;
			default:
				break;
		}
	}

	private void hookWindowListener()
	{
		new AllWindowListener() {
			
			@Override
			protected void registerWindow(IWorkbenchWindow window) {
				window.addPerspectiveListener(_perspectiveListener);
			}
			
			@Override
			protected void deregisterWindow(IWorkbenchWindow window) {
				window.removePerspectiveListener(_perspectiveListener);
			}
		};
	}
}
