/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.strikewire.snl.apc.Common.CommonPlugin;

public class ModelCreatorManager {
	
	public static IModelCreator getModelCreator()
	{		
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CommonPlugin.ID, IModelCreator.EXTENSION_POINT_ID);
		IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
		if(elements == null)
		{
			return null;
		}
		
		for(IConfigurationElement element : elements)
		{
			if(element == null)
			{
				continue;
			}
			
			try {
				Object user = element.createExecutableExtension("class");
				if(user!=null && (user instanceof IModelCreator))
				{
					 return (IModelCreator) user;
				}
			} catch(CoreException ce) {
				CommonPlugin.getDefault().log(ce.getStatus());
			}
		}
		
		return null;
	}
}
