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
 * Created by mjgibso on Feb 17, 2014 at 6:06:43 AM
 */
package com.strikewire.snl.apc.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.views.properties.IPropertySource;

import com.strikewire.snl.apc.GUIs.settings.IStatusPropertySource;

/**
 * @author mjgibso
 *
 */
public abstract class AbsPropertySource implements IStatusPropertySource
{

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang.Object)
	 */
	@Override
	public boolean isPropertySet(Object id)
	{
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java.lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id)
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object id, Object value)
	{
	}
	
	private IStatus status; 
	public void setStatus(IStatus status){
		this.status = status;
	}
	
	public IStatus getStatus(){
		if(status == null){
			return Status.OK_STATUS;
		}		
		return status;
	}



}
