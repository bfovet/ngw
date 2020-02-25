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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public abstract class PropertiesInstance<E extends PropertiesInstance<E>> extends PlatformObject implements PropertiesConstants, IPropertiesInstance<E>
{
	protected final PropertiesStore<E> parent_;
  protected final Properties properties_ = new Properties();
  
  protected String name_;

	
  /**
   * Constructor that initializes this Object with the
   * specified parent; this method is the minimum
   * constructor.
   * @param parent May not be null
   */
	protected PropertiesInstance(PropertiesStore<E> parent)
	{
		if(parent == null)
			throw new IllegalArgumentException("The JSProperties parent cannot be null.");
		this.parent_ = parent;
	}
	
	/**
	 * Constructor that will initialize the object by reading
	 * the properties from the specified file.
	 */
	protected PropertiesInstance(File file, PropertiesStore<E> parent) throws Exception
	{
		this(parent);
		
		if(file == null)
			throw new IllegalArgumentException("The file for the "+parent_.getPropertiesDisplayName()+" cannot be null.");
		
		if(!file.getName().endsWith(DOT_PROPERTIES))
			throw new IllegalArgumentException("The file for the "+parent_.getPropertiesDisplayName()+" must end with \""+DOT_PROPERTIES+"\".");
		
		if(file.getName().length() <= DOT_PROPERTIES.length())
			throw new IllegalArgumentException("The file for the "+parent_.getPropertiesDisplayName()+" must have a name consisting of more than just \""+DOT_PROPERTIES+"\".");
		
		this.name_ = file.getName().substring(0, file.getName().length()-DOT_PROPERTIES.length());
		
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			properties_.load(fis);
		} finally {
			if(fis != null)
				fis.close();
		}
	}
	
	@Override
  public String getName()
	{ return this.name_; }
	
	/**
	 * Method returns the current build version of the implementors source.
	 * Typically, this will be the implementor's containing plugin's version.
	 * This value is written into the underlying properties file when it is
	 * saved to disk to be able to later determine what version of the
	 * implementor generated the file.
	 */
	protected abstract String getCurrentBuildVersion();
	
	@Override
  public void saveAs(File file)
	{
		properties_.setProperty(BUILD_VERSION_KEY, getCurrentBuildVersion());
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file, false);
			properties_.store(fos, null /* comments */);
		} catch (IOException ioe) {
			System.err.println("Error saving "+parent_.getPropertiesDisplayName()+": "+name_);
			ioe.printStackTrace();
		} finally {
			if(fos != null)
			{
				try {
					fos.flush();
				} catch (IOException e) {
					CommonPlugin.getDefault().logError(e);
				}
				try {
					fos.close();
				} catch (IOException e) {
					CommonPlugin.getDefault().logError(e);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.apc.jobsubmission.properties.IJSPropertiesInstance#asBaseType()
	 */
	@Override
  @SuppressWarnings("unchecked")
	public E asBaseType()
	{ return (E) this; }
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public MutablePropertiesInstance<E> clone()
	{
		MutablePropertiesInstance<E> mp = parent_.createNewProperties();
		mp.asBaseType().name_ = this.name_;
		mp.asBaseType().properties_.putAll(this.properties_);
		return mp;
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.apc.jobsubmission.properties.IJSPropertiesInstance#getProperty(java.lang.String)
	 */
	@Override
  public String getProperty(String key)
	{ return properties_.getProperty(key); }
	
	@Override
  public Map<String, String> getProperties()
	{
		Map<String, String> props = new HashMap<String, String>(this.properties_.size());
		Enumeration<?> propNames = this.properties_.propertyNames();
		while(propNames.hasMoreElements())
		{
			String name = (String) propNames.nextElement();
			props.put(name, this.properties_.getProperty(name));
		}
		return props;
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.apc.jobsubmission.properties.IJSPropertiesInstance#getParent()
	 */
	@Override
  public PropertiesStore<E> getParent()
	{ return parent_; }
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.properties.IPropertiesInstance#dispose()
	 */
	@Override
	public void dispose()
	{}
	
	
	/**
	 * From the specified selection provider, returns the Properties Instance
	 * if it is the first element in the selection; otherwise returns null.
	 * @param sp A selection provider (which includes Viewers, etc.)
	 * @return The properties instance, or null
	 */
  public static PropertiesInstance<?> getFirstSelectedPropObj(ISelectionProvider sp)
  {
    ISelection sel = (sp != null ? sp.getSelection() : null);

    if (sel instanceof IStructuredSelection) {
      Object firstObj = ((IStructuredSelection) sel).getFirstElement();
      if (firstObj instanceof PropertiesInstance) {
        return (PropertiesInstance<?>) firstObj;
      }
    }

    return null;
  }
}
