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
package com.strikewire.snl.apc.properties;

import java.io.File;
import java.io.IOException;

import com.strikewire.snl.apc.util.Utils;

/**
 * @author mjgibso
 *
 */
public class MutablePropertiesInstanceHolder<E extends PropertiesInstance<E>> implements PropertiesConstants
{
	private final PropertiesStore<E> parent_;
	private final MutablePropertiesInstance<E> instance_;
	private boolean modified_ = false;
	
	public MutablePropertiesInstanceHolder(MutablePropertiesInstance<E> instance, PropertiesStore<E> parent)
	{
		if(instance == null)
			throw new IllegalArgumentException("The instance cannot be null.");
		this.instance_ = instance;
		if(parent == null)
			throw new IllegalArgumentException("The parent properties object cannot be null.");
		this.parent_ = parent;
		
		instance_.setHolder(this);
		
		// if the name is set, it's not a new blank instance, so check for a version change
		if(instance_.getName() != null)
		{
			checkForVersionChange();
		}
	}
	
	private void checkForVersionChange()
	{
		if(!(this.instance_ instanceof PropertiesInstance) || !(this.instance_ instanceof VersionListeningMutablePropertiesInstance))
		{
			return;
		}
		
		String oldVersion = this.instance_.getProperty(BUILD_VERSION_KEY);
		String newVersion = ((PropertiesInstance<?>) this.instance_).getCurrentBuildVersion();
		
		if(Utils.stringsEqual(oldVersion, newVersion))
		{
			return;
		}
		
		((VersionListeningMutablePropertiesInstance<E>) this.instance_).buildVersionChanged(oldVersion, newVersion);
	}
	
	public void setName(String name)
	{
		if(name==null || name.trim().equals(""))
			throw new IllegalArgumentException(parent_.getPropertiesDisplayName()+" names cannot be null or blank.");
		
		if(name.equals(instance_.getName()))
			return;
		
		// if this properties is in the map, we need to update its key
		String oldName = instance_.getName();
		instance_.asBaseType().name_ = name;
		
		if(oldName!=null && !oldName.trim().equals("") && parent_.userProperties_.get(oldName)==instance_)
		{
			parent_.userProperties_.remove(oldName);
			parent_.addProperties(instance_);
		}
		setModified();
	}
	
	public void setProperty(String key, String value)
	{
		// if the value is null, that means remove the property
		if(value == null)
		{
			Object oldValue = instance_.asBaseType().properties_.remove(key);
			if(oldValue != null)
				setModified();
		} else {
			Object oldValue = instance_.asBaseType().properties_.setProperty(key, value);
			if(!value.equals(oldValue))
				setModified();
		}
	}
	
	public boolean isModified()
	{ return this.modified_; }
	
	public boolean isOverriding()
	{ return this.parent_.getDefaultPropertiesInstance(this.instance_.getName()) != null; }
	
	public void setModified()
	{
		this.modified_ = true;
		if(parent_.userProperties_.containsValue(instance_))
			parent_.modified_ = true;
	}
	
	public void saveChanges() throws IOException
	{
		File propertiesFolder = parent_.getUserPropertiesFolder();
		File propertiesFile = new File(propertiesFolder, instance_.getName()+DOT_PROPERTIES);
		if(!propertiesFile.exists() || this.modified_)
		{
			instance_.saveAs(propertiesFile);
		}
		
		this.modified_ = false;
	}
}
