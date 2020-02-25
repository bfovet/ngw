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
 * Created by mjgibso on Nov 19, 2013 at 12:58:56 PM
 */
package gov.sandia.dart.metrics;

import gov.sandia.dart.application.DARTApplicationAdapter;
import gov.sandia.dart.application.DARTApplicationEvent;
import gov.sandia.dart.application.IDARTApplicationListener;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.AllPageListener;
import com.strikewire.snl.apc.listeneradapters.PartListener2Adapter;
import com.strikewire.snl.apc.osgi.util.EventKeys;
import com.strikewire.snl.apc.osgi.util.OSGIUtils;

/**
 * @author mjgibso
 *
 */
public class PartOpenedMetricRecorder extends DARTApplicationAdapter implements IDARTApplicationListener
{
	private static final IPartListener2 _partOpenedListener = new PartListener2Adapter() {
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.listeneradapters.PartListener2Adapter#partOpened(org.eclipse.ui.IWorkbenchPartReference)
		 */
		@Override
		public void partOpened(IWorkbenchPartReference partRef)
		{
			recordPartOpened(partRef);
		}
	};
	
	private static void recordPartOpened(IWorkbenchPartReference partRef)
	{
		String capability = partRef instanceof IEditorReference ? "editor_opened" : "view_opened";
		StringBuilder data = new StringBuilder(partRef.getId());
		if(partRef instanceof IViewReference)
		{
			String secondaryId = ((IViewReference) partRef).getSecondaryId();
			if(StringUtils.isNotBlank(secondaryId))
			{
				data.append(';');
				data.append(secondaryId);
			}
		}
		
		OSGIUtils.postEvent(EventKeys.METRICS, CommonPlugin.class,
				"plugin", CommonPlugin.ID, 
				"capability", capability,
				"data", data.toString());
	}
	
	private static class PageListener extends AllPageListener
	{
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.AllPageListener#registerPage(org.eclipse.ui.IWorkbenchPage)
		 */
		@Override
		protected void registerPage(IWorkbenchPage page)
		{
			// record the views that are already open since we won't get events for those
			Collection<IWorkbenchPartReference> alreadyOpenParts = new HashSet<IWorkbenchPartReference>();
			alreadyOpenParts.addAll(Arrays.asList(page.getEditorReferences()));
			alreadyOpenParts.addAll(Arrays.asList(page.getViewReferences()));
			for(IWorkbenchPartReference partRef : alreadyOpenParts)
			{
				// only get the parts that are already open, we'll
				// get events when a hidden (behind another view) view
				// is brought to the top and initialized.
				IWorkbenchPart part = partRef.getPart(false);
				if(part != null)
				{
					recordPartOpened(partRef);
				}
			}
			
			page.addPartListener(_partOpenedListener);
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.AllPageListener#deregisterPage(org.eclipse.ui.IWorkbenchPage)
		 */
		@Override
		protected void deregisterPage(IWorkbenchPage page)
		{
			page.removePartListener(_partOpenedListener);
		}
	}
	
	/**
	 * 
	 */
	public PartOpenedMetricRecorder()
	{
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.application.DARTApplicationAdapter#preApplicationEvent(com.strikewire.snl.apc.application.DARTApplicationEvent)
	 */
	@Override
	public void preApplicationEvent(DARTApplicationEvent event)
	{
		switch (event)
		{
			// register after the Workbench exists 
			case WORKBENCH_ADVISOR_INITIALIZE:
				registerPageListener();
				break;
			default:
				break;
		}
	}
	
	private void registerPageListener()
	{
		new PageListener();
	}
}
