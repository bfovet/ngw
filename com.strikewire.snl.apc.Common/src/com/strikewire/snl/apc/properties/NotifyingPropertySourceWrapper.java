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
 * Created by mjgibso on Feb 17, 2014 at 1:08:41 PM
 */
package com.strikewire.snl.apc.properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

import com.strikewire.snl.apc.GUIs.settings.IStatusPropertySource;

/**
 * @author mjgibso
 *
 */
public class NotifyingPropertySourceWrapper extends AbsNotifyingPropertySource
{
	private final IPropertySource _source;
	
	/**
	 * 
	 */
	public NotifyingPropertySourceWrapper(IPropertySource source)
	{
		this._source = source;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.properties.AbsNotifyingPropertySource#notifyPropertyChanged()
	 */
	@Override
	public void notifyPropertyChanged()
	{
		super.notifyPropertyChanged();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors()
	{
		return this._source.getPropertyDescriptors();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object id)
	{
		return this._source.getPropertyValue(id);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.properties.AbsPropertySource#getEditableValue()
	 */
	@Override
	public Object getEditableValue()
	{
		return this._source.getEditableValue();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.properties.AbsPropertySource#isPropertySet(java.lang.Object)
	 */
	@Override
	public boolean isPropertySet(Object id)
	{
		return this._source.isPropertySet(id);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.properties.AbsPropertySource#resetPropertyValue(java.lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id)
	{
		this._source.resetPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value)
	{
		this._source.setPropertyValue(id, value);
	}
	
	@Override
	public IStatus getStatus(){
		if(_source instanceof IStatusPropertySource){
			return ((IStatusPropertySource)this._source).getStatus();
		}else{
			return super.getStatus();
		}
	}
	
	@Override 
	public void setStatus(IStatus status){
		if(_source instanceof IStatusPropertySource){
			((IStatusPropertySource)this._source).setStatus(status);
		}else{
			super.setStatus(status);
		}
	}
}
