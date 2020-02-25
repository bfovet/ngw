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
package com.strikewire.snl.apc.GUIs.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.settings.SettingsExtensionManager.EmptySelectionEditorPerspectiveBinding.Instance;

/**
 * @author mjgibso
 *
 */
public class SettingsExtensionManager
{
	public static final String EXTENSION_POINT_ID = "SettingsEditor";
	
	private static SettingsExtensionManager _instance = null;
	private static final Object _instanceLock = new Object();
	
	private final EmptySelectionEditor _emptySelectionEditor;
	private final EmptySelectionEditorPerspectiveBinding _emptySelectionEditorPerspectiveBinding;
	
	private SettingsExtensionManager()
	{
		_emptySelectionEditor = new EmptySelectionEditor();
		_emptySelectionEditorPerspectiveBinding = new EmptySelectionEditorPerspectiveBinding();
		
		Collection<IExtensionElement<? extends IInstance>> elements = new ArrayList<>();
		elements.add(_emptySelectionEditor);
		elements.add(_emptySelectionEditorPerspectiveBinding);
		
		Map<String, IExtensionElement<? extends IInstance>> elementsByName = new HashMap<>();
		for(IExtensionElement<? extends IInstance> element : elements)
		{
			elementsByName.put(element.getElementName(), element);
		}
		
		init(elementsByName);
	}
	
	public static SettingsExtensionManager getInstance()
	{		
		synchronized (_instanceLock)
		{
			if(_instance == null)
			{
				_instance = new SettingsExtensionManager();
			}
			return _instance;
		}
	}
	
	private void init(Map<String, IExtensionElement<? extends IInstance>> elementsByName)
	{
		readExtensionRegistry(elementsByName);
	}
	
	private void readExtensionRegistry(Map<String, IExtensionElement<? extends IInstance>> elementsByName)
	{
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(CommonPlugin.ID, EXTENSION_POINT_ID);
		IConfigurationElement[] extensions = extensionPoint.getConfigurationElements();
		
		if(extensions==null || extensions.length<1)
		{
			return;
		}
		
		for(IConfigurationElement extension : extensions)
		{
			if(extension == null)
			{
				continue;
			}
			
			String elementName = extension.getName();
			IExtensionElement<? extends IInstance> element = elementsByName.get(elementName);
			if(element == null)
			{
				IStatus warn = CommonPlugin.getDefault().newWarningStatus("Unrecognized SeettingsExtension element: "+elementName, new Exception());
				CommonPlugin.getDefault().log(warn);
				continue;
			}
			
			element.read(extension);
		}
	}
	
	private static interface IExtensionElement<T extends IInstance>
	{
		String getElementName();
		
		T read(IConfigurationElement extension);
		
		T newInstance(IConfigurationElement extension);
	}
	
	static interface IInstance
	{
		String getID();
	}
	
	private static abstract class AbstractElement<T extends IInstance> implements IExtensionElement<T>
	{
		private final Map<String, T> _instances = new HashMap<>();
		
		@Override
		public T read(IConfigurationElement extension)
		{
			T instance = null;
			try {
				instance = newInstance(extension);
				if(instance == null)
				{
					throw new Exception("null instance returned");
				}
			} catch(Throwable t) {
				CommonPlugin.getDefault().logError("Error reading SettingsExtension element: "+extension, t);
			}
			
			if(instance != null)
			{
				String id = instance.getID();
				T existingInstance = this._instances.put(id, instance);
				if(existingInstance != null)
				{
					CommonPlugin.getDefault().logError("duplicate empty selection editor extension definition of: "+getElementName()
							+ ".  ID: "+id, new Exception());
				}
			}
			
			return instance;
		}
		
		public T getInstanceByID(String id)
		{
			return _instances.get(id);
		}
	}
	
	static class EmptySelectionEditor extends AbstractElement<EmptySelectionEditor.Instance> implements IExtensionElement<EmptySelectionEditor.Instance>
	{
		private static final String NAME = EmptySelectionEditor.class.getSimpleName();
		
		static class Instance implements IInstance
		{
			public static final String ATTRIBUTE_ID = "id";
			public static final String ATTRIBUTE_CLASS = "class";
			
			public final String id;
			public final String clazz;
			private final IConfigurationElement _extension;
			private IEmptySelectionSettingsEditor<Object> _editor = null;
			private final Object _instanceLock = new Object();
			
			public Instance(IConfigurationElement extension)
			{
				this.id = extension.getAttribute(ATTRIBUTE_ID);
				this.clazz = extension.getAttribute(ATTRIBUTE_CLASS);
				this._extension = extension;
			}

			@SuppressWarnings("unchecked")
			public IEmptySelectionSettingsEditor<Object> getEditor() throws CoreException
			{
				if(_editor != null)
				{
					return _editor;
				}
				
				synchronized (_instanceLock) {
					Object editorInst = _extension.createExecutableExtension(ATTRIBUTE_CLASS);
					if(editorInst instanceof IEmptySelectionSettingsEditor)
					{
						this._editor = (IEmptySelectionSettingsEditor<Object>) editorInst;
					}
				}
				
				return this._editor;
			}
			
			@Override
			public String getID()
			{
				return id;
			}
		}
		
		@Override
    public String getElementName()
		{
			return NAME;
		}
		
		@Override
		public Instance newInstance(IConfigurationElement extension)
		{
			return new Instance(extension);
		}
		
		public IEmptySelectionSettingsEditor<Object> getSettingsEditor(String editorID) throws CoreException
		{
			Instance editorRef = getInstanceByID(editorID);
			if(editorRef == null)
			{
				return null;
			}
			
			return editorRef.getEditor();
		}
	}
	
	static class EmptySelectionEditorPerspectiveBinding extends AbstractElement<EmptySelectionEditorPerspectiveBinding.Instance> implements IExtensionElement<EmptySelectionEditorPerspectiveBinding.Instance>
	{
		private static final String NAME = EmptySelectionEditorPerspectiveBinding.class.getSimpleName();
		
		static class Instance implements IInstance
		{
			public static final String ATTRIBUTE_PERSPECTIVE = "perspective";
			public static final String ATTRIBUTE_EMPTY_SELECTION_EDITOR = "EmptySelectionEditor";
			
			public final String perspective;
			public final String editorID;
			
			public Instance(IConfigurationElement extension)
			{
				perspective = extension.getAttribute(ATTRIBUTE_PERSPECTIVE);
				editorID = extension.getAttribute(ATTRIBUTE_EMPTY_SELECTION_EDITOR);
			}
			
			@Override
			public String getID()
			{
				return perspective;
			}
		}
		
		@Override
    public String getElementName()
		{
			return NAME;
		}

		@Override
		public Instance newInstance(IConfigurationElement extension)
		{
			return new Instance(extension);
		}
		
		private Instance getBinding(String perspectiveID)
		{
			return getInstanceByID(perspectiveID);
		}
	}

	public IEmptySelectionSettingsEditor<Object> getEditorForCurrentPerspective() throws CoreException
	{
		return getEditorForPerspective(getCurrentPerspective());
	}
	
	public IEmptySelectionSettingsEditor<Object> getEditorForPerspective(String perspectiveID) throws CoreException
	{
		Instance binding = _emptySelectionEditorPerspectiveBinding.getBinding(perspectiveID);
		if(binding == null)
		{
			return null;
		}
		
		return _emptySelectionEditor.getSettingsEditor(binding.editorID);
	}
	
	public static String getCurrentPerspective()
	{
		IWorkbench wb = PlatformUI.getWorkbench();
		if(wb == null)
		{
			return null;
		}
		
		IWorkbenchWindow window = wb.getActiveWorkbenchWindow();
		if(window == null)
		{
			return null;
		}
		
		IWorkbenchPage page = window.getActivePage();
		if(page == null)
		{
			return null;
		}
		
		IPerspectiveDescriptor perspective = page.getPerspective();
		if(perspective == null)
		{
			return null;
		}
		
		return perspective.getId();
	}
}
