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
 * Created by mjgibso on Apr 4, 2013 at 7:16:25 AM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.listeneradapters.PartListener2Adapter;
import com.strikewire.snl.apc.listeneradapters.PerspectiveListener2Adapter;

/**
 * This is a singleton class used to hide the editor area when the last
 * editor is closed to avoid leaving a hole where the editor was.  This
 * is done on a perspective basis.  That is, a user of this class can
 * obtain an instance and add/remove perspectives (by ID) that this
 * behavior should happen for.  So the editor area will be hidden when
 * the last editor is closed in a window which has an active perspective
 * matching one that has been specified via the
 * {@link #addPerspective(String)} method.
 * 
 * Once initialized via reference, this class registers against all
 * workbench windows (including new ones as they are opened) to be
 * notified when editors are closed in a given window.
 * 
 * TODO would be cool if we set up an extension point to register these,
 * so one could through an extension bind a perspective to this listener.
 * 
 * @author mjgibso
 *
 */
public class HideEditorAreaListener
{
	public static final String EXTENSION_POINT_ID = "HideEditor";
	
	private final IPartListener2 _partListener = new PartListener2Adapter() {
		
		@Override
		public void partClosed(IWorkbenchPartReference partRef) {
			IWorkbenchPage page = partRef.getPage();
			checkHideEditorArea(page, page.getPerspective());
		}
		
	};
	
	private final IPerspectiveListener _perspectiveListener = new PerspectiveListener2Adapter()
	{
		
		@Override
		public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective)
		{
			checkHideEditorArea(page, perspective);
		}

	};
	
	private void checkHideEditorArea(IWorkbenchPage page, IPerspectiveDescriptor perspective)
	{
		String perspectiveID = perspective!=null ? perspective.getId() : null;
		if(StringUtils.isBlank(perspectiveID) || !perspectiveIDs_.contains(perspectiveID))
		{
			return;
		}
		
		if(page==null || !page.isEditorAreaVisible())
		{
			return;
		}
		
		IEditorReference[] editors = page.getEditorReferences();
		if(editors == null)
		{
			return;
		}
		
		if(editors.length < 1)
		{
			page.setEditorAreaVisible(false);
		}
	}
	
	private final Set<String> perspectiveIDs_ = new HashSet<String>();
	
	private static final HideEditorAreaListener instance_ = new HideEditorAreaListener();
	
	private HideEditorAreaListener()
	{
		perspectiveIDs_.addAll(readRegisterredPerspectives());
		
		new AllPageListener() {
			
			@Override
			protected void registerWindow(IWorkbenchWindow window) {
				window.addPerspectiveListener(_perspectiveListener);
				
				super.registerWindow(window);
			}
			
			@Override
			protected void deregisterWindow(IWorkbenchWindow window) {
				window.removePerspectiveListener(_perspectiveListener);
				
				super.deregisterWindow(window);
			}
			
			@Override
			protected void registerPage(IWorkbenchPage page) {
				page.addPartListener(_partListener);
			}
			
			@Override
			protected void deregisterPage(IWorkbenchPage page) {
				page.removePartListener(_partListener);
			}
		};
	}
	
	public static HideEditorAreaListener getInstance()
	{
		return instance_;
	}
	
	private static Set<String> readRegisterredPerspectives()
	{
		Set<String> perspectives = new HashSet<String>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(CommonPlugin.ID, EXTENSION_POINT_ID);
		IConfigurationElement[] extensions = extensionPoint.getConfigurationElements();
		
		if(extensions==null || extensions.length<1)
		{
			return perspectives;
		}
		
		for(IConfigurationElement extension : extensions)
		{
			if(extension == null)
			{
				continue;
			}
			
			if(!"perspective".equals(extension.getName()))
			{
				continue;
			}
			
			String perspId = extension.getAttribute("perspectiveId");
			if(StringUtils.isNotBlank(perspId))
			{
				perspectives.add(perspId);
			}
		}
		
		return perspectives;
	}
	
	public boolean addPerspective(String perspectiveID)
	{
		if(StringUtils.isBlank(perspectiveID))
		{
			return false;
		}
		
		return this.perspectiveIDs_.add(perspectiveID);
	}
	
	public boolean removePerspective(String perspectiveID)
	{
		if(StringUtils.isBlank(perspectiveID))
		{
			return false;
		}
		
		return this.perspectiveIDs_.remove(perspectiveID);
	}
	
	public static class HideEditorRegistryInitializer implements IStartup
	{
		@Override
		public void earlyStartup()
		{
			getInstance();
		}
	}
}
