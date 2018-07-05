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
 * Created by mjgibso on Aug 12, 2014 at 8:23:30 AM
 */
package com.strikewire.snl.apc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class ExtensionPointUtils
{
	private ExtensionPointUtils()
	{}
	
	public static IExtensionPoint getExtensionPoint(String pluginID, String extensionID)
	{
		return Platform.getExtensionRegistry().getExtensionPoint(pluginID, extensionID);
	}
	
	public static List<IExtension> getExtensions(IExtensionPoint extensionPoint)
	{
		if(extensionPoint == null)
		{
			return Collections.emptyList();
		}
		
		IExtension[] extensionsArr = extensionPoint.getExtensions();
		if(extensionsArr==null || extensionsArr.length<1)
		{
			return Collections.emptyList();
		}
		
		return Arrays.asList(extensionsArr);
	}
	
	public static List<IExtension> getExtensions(String pluginID, String extensionID)
	{
		return getExtensions(getExtensionPoint(pluginID, extensionID));
	}
	
	public static List<IConfigurationElement> getConfigurationElements(List<IExtension> extensions)
	{
		return getConfigurationElements(extensions, null);
	}
	
	public static List<IConfigurationElement> getConfigurationElements(List<IExtension> extensions, String elementName)
	{
		if(extensions==null || extensions.isEmpty())
		{
			return Collections.emptyList();
		}
		
		List<IConfigurationElement> allElements = new ArrayList<IConfigurationElement>();
		for(IExtension extension : extensions)
		{
			if(extension == null)
			{
				continue;
			}
			
			IConfigurationElement[] elements = extension.getConfigurationElements();
			if(elements==null || elements.length<1)
			{
				continue;
			}
			
			for(IConfigurationElement element : elements)
			{
				if(element!=null && (elementName==null || elementName.equals(element.getName())))
				{
					allElements.add(element);
				}
			}
		}
		
		return allElements;
	}
	
	public static List<IConfigurationElement> getConfigurationElements(IExtensionPoint extensionPoint)
	{
		return getConfigurationElements(getExtensions(extensionPoint));
	}
	
	public static List<IConfigurationElement> getConfigurationElements(IExtensionPoint extensionPoint, String elementName)
	{
		return getConfigurationElements(getExtensions(extensionPoint), elementName);
	}
	
	public static List<IConfigurationElement> getConfigurationElements(String pluginID, String extensionID)
	{
		return getConfigurationElements(getExtensionPoint(pluginID, extensionID));
	}

	public static List<IConfigurationElement> getConfigurationElements(String pluginID, String extensionID, String elementName)
	{
		return getConfigurationElements(getExtensionPoint(pluginID, extensionID), elementName);
	}
	
	public static <A> List<A> getExtensionInstances(String pluginId, String extensionId, String elementName)
	{
		return getExtensionInstances(pluginId, extensionId, elementName, "class");
	}
	
	public static <A> List<A> getExtensionInstances(String pluginId, String extensionId, String elementName, String classAttributeName)
	{
		List<IConfigurationElement> elements = getConfigurationElements(pluginId, extensionId, elementName);
		List<A> contributors = new ArrayList<>();
		elements.forEach(element -> {
			try {
				@SuppressWarnings("unchecked")
				A instance = (A) element.createExecutableExtension(classAttributeName);
				contributors.add(instance);
			} catch (Exception e) {
				StringBuilder sb = new StringBuilder();
				sb.append("Can't create executable extension instance:");
				sb.append("\n\tPlugin ID: ");
				sb.append(pluginId);
				sb.append("\n\tExtension ID: ");
				sb.append(extensionId);
				sb.append("\n\tElement Name: ");
				sb.append(elementName);
				sb.append("\n\tClass Attribute Name: ");
				sb.append(classAttributeName);
				CommonPlugin.getDefault().logError(sb.toString(), e);
			}
		});
		
		return contributors;
	}
}
