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
 * Created by Marcus J. Gibson on Jan 29, 2007 at 2:14:19 PM
 */
package com.strikewire.snl.apc.natures;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class APCNatureHandler
{
	public static List<String> getContributedNatures()
	{
		List<String> natures = new ArrayList<String>();
		for(IAPCProjectNatureContributor contributor : getNatureContributors())
		{
			List<String> newNatures = contributor.getContributedNatures();
			if(newNatures != null)
				natures.addAll(newNatures);
		}
		
		return natures;
	}
	
	public static List<IAPCProjectNatureContributor> getNatureContributors()
	{
		List<IAPCProjectNatureContributor> natureContributors = new ArrayList<IAPCProjectNatureContributor>();
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(CommonPlugin.ID, IAPCProjectNatureContributor.EXTENSION_POINT_ID);
		IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
		if(elements == null)
			return natureContributors;
		for(IConfigurationElement element : elements)
		{
			if(element == null)
				continue;
			try {
				Object contributor = element.createExecutableExtension("class");
				if(contributor != null && (contributor instanceof IAPCProjectNatureContributor))
					natureContributors.add((IAPCProjectNatureContributor) contributor);
			} catch(CoreException ce) {
				CommonPlugin.getDefault().log(ce.getStatus());
			}
		}
		
		return natureContributors;
	}
}
