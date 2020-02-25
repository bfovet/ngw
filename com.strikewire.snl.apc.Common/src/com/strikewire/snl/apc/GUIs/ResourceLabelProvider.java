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
 * Created by mjgibso on Aug 22, 2013 at 4:21:38 PM
 */
package com.strikewire.snl.apc.GUIs;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.LabelProvider;

/**
 * @author mjgibso
 *
 */
public abstract class ResourceLabelProvider extends LabelProvider
{
	
	private ResourceManager resourceManager_;
	
	protected ResourceLabelProvider()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		if(resourceManager_ != null)
		{
			resourceManager_.dispose();
		}
    	resourceManager_ = null;
    	
		super.dispose();
	}
	
	/**
	 * Lazy load the resource manager
	 * 
	 * @return The resource manager, create one if necessary
	 */
	protected ResourceManager getResourceManager()
	{
		if(resourceManager_ == null)
		{
			resourceManager_ = new LocalResourceManager(JFaceResources.getResources());
		}

		return resourceManager_;
	}
}
