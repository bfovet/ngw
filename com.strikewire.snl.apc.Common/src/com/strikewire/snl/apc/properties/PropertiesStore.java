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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.Utils;

import gov.sandia.dart.common.core.listeners.BaseListenersHandler;
import gov.sandia.dart.configuration.IExecutionEnvironment;
import gov.sandia.dart.configuration.ILan;
import gov.sandia.dart.configuration.factory.SimpleExecEnvFactory;
import gov.sandia.dart.configuration.mgr.ExecutionEnvironmentMgr;

/**
 * @author mjgibso
 *
 */
public abstract class PropertiesStore<E extends PropertiesInstance<E>> 
{
  /**
   * _log -- A Logger instance for PropertiesStore
   */
  private static final Logger _log =
      LogManager.getLogger(PropertiesStore.class);
  

	private static final DefaultPropertiesSource[] NO_SOURCES = new DefaultPropertiesSource[0];

	public enum Type { Default, User }
	
	protected final ConcurrentMap<String, IPropertiesInstance<E>> defaultProperties_ =
	    initDefaultProperties();
	protected final Map<String, MutablePropertiesInstance<E>> userProperties_ =
	    initUserProperties();
	
	private DefaultPropertiesSource[] defaultSource_;
	private boolean sourceInitialized_ = false;
	private final Object sourceLock_ = new Object();
	
	private final Comparator<IPropertiesInstance<E>> JSPropComparator_ = new Comparator<IPropertiesInstance<E>>() {
		
		@Override
		public int compare(IPropertiesInstance<E> p1, IPropertiesInstance<E> p2) {
			return p1.getName().compareToIgnoreCase(p2.getName());
		}
	
	};
	
	protected boolean modified_ = false;
	
	protected BaseListenersHandler<IPropertyChangeListener> listenersHandler_ = new BaseListenersHandler<>();
	
	protected PropertiesStore()
	{
		initialize();
	}
	
	protected Map<String, MutablePropertiesInstance<E>> initUserProperties()
	{
	  return new TreeMap<>();
	}
	
	protected ConcurrentMap<String, IPropertiesInstance<E>> initDefaultProperties()
	{
	  return new ConcurrentSkipListMap<>();
	}
	
	
	public void initialize()
	{
	  _log.entry();
		try {
			clearProperties(this.userProperties_);
			clearProperties(this.defaultProperties_);
			initProperties(Type.Default, getDefaultPropertiesFolder());
			initProperties(Type.User, getUserPropertiesFolder());
			this.modified_ = false;
		} catch (Exception e) {
			_log.error(e);
			IStatus status;
			if(e instanceof CoreException)
			{
				status = ((CoreException) e).getStatus();
			} else {
				status = CommonPlugin.getDefault().newErrorStatus("Error initializing "+getPropertiesDisplayName()+"s list", e);
			}
			CommonPlugin.getDefault().log(status);
		}
		finally {
		  _log.exit();
		}
	}
	
	protected void clearProperties(Map<String, ? extends IPropertiesInstance<E>> properties)
	{
		Collection<? extends IPropertiesInstance<E>> origValues = new ArrayList<IPropertiesInstance<E>>(properties.values());
		properties.clear();
		for(IPropertiesInstance<E> origValue : origValues)
		{
			origValue.dispose();
		}
	}
	
	public final DefaultPropertiesSource[] getDefaultPropertiesSource()
	{
		synchronized (sourceLock_) {
			if(!sourceInitialized_)
			{
				defaultSource_ = initializeDefaultSource();
			}
		}
		
		return defaultSource_;
	}
	
	/**
	 * Implementors can override this method to return the ID of a provided extension point that
	 * allows an extender to contribute a {@link DefaultPropertiesSource} class.
	 * 
	 * Note, there can only be one contributor to the extension point.  If multiple contributors
	 * are found, only one will be used, and which one is arbitrary.  An an error will be logged
	 * if multiple are found.
	 */
	protected String getContributorExtensionID()
	{ return null; }
	
	/**
	 * Implementors can override this method to directly provide a {@link DefaultPropertiesSource} class
	 * without having to go through an extension point.
	 * 
	 * Note, if this method is overridden, the return from it will be used for the default source, and
	 * any contributor to the provided extension ID will be ignored.
	 * 
	 * Note, this method will only be invoked once upon demand, and the result will be cached for the session.
	 */
	protected DefaultPropertiesSource[] initializeDefaultSource()
	{
		String extensionID = getContributorExtensionID();
		
		if(StringUtils.isBlank(extensionID))
		{
			return NO_SOURCES;
		}
		
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(extensionID);
		if(extensionPoint == null)
		{
			// TODO log a warning?
			return NO_SOURCES;
		}
		
		IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
		if(elements==null || elements.length<1)
		{
			return NO_SOURCES;
		}
		
		List<DefaultPropertiesSource> sources = new ArrayList<>();
		for (IConfigurationElement element: elements) {
			try {
				Object contributor = element.createExecutableExtension("class");
				if(contributor instanceof DefaultPropertiesSource)
				{
					sources.add((DefaultPropertiesSource) contributor);
				}

			} catch(CoreException ce) {
				CommonPlugin.getDefault().log(ce.getStatus());
			}
		}
		return sources
				.toArray(new DefaultPropertiesSource[sources.size()]);
	}
	
	public abstract String getUserPropertiesFolderName();
	public abstract String getPropertiesDisplayName();
	
	protected void initProperties(Type type, File... dirs) {
		for (File dir : dirs) {
			if (dir == null || !dir.exists() || !dir.isDirectory()) {
				continue;
			}

			for (String filename : dir.list()) {
				if (StringUtils.isBlank(filename))
					continue;

				if (!filename.endsWith(PropertiesConstants.DOT_PROPERTIES))
					continue;

				addPropertiesInstance(new File(dir, filename), type);
			}
		}
	}
	
	File[] getDefaultPropertiesFolder() throws IOException
	{
		DefaultPropertiesSource[] defaultSource = getDefaultPropertiesSource();
		List<File> folders = new ArrayList<>();
		if (defaultSource != null) {
			for (DefaultPropertiesSource source : defaultSource) {
				try {
					folders.add(source.getPropertiesFolder());
				} catch (Exception ex) {
				      CommonPlugin.getDefault().logError("Error loading property source", ex);
				}
			}
		}
			
		return folders.isEmpty() ?  new File[0] : (File[]) folders.toArray(new File[folders.size()]);
	}
	
	protected File getUserPropertiesFolder() throws IOException
	{
		IPath statePath = getPlugin().getStateLocation();
		IPath propertiesPath = statePath.append(getUserPropertiesFolderName());
		File propertiesFolder = propertiesPath.toFile();
		if(!propertiesFolder.exists())
		{
			propertiesFolder.mkdirs();
		}
		return propertiesFolder;
	}
	
	/**
	 * Returns the plugin with which this propertiesstore is associated.
	 */
	protected abstract Plugin getPlugin();
	
	protected abstract IPropertiesInstance<E> createDefaultProperties(File file) throws Exception;
	protected abstract MutablePropertiesInstance<E> createUserProperties(File file) throws Exception;
	
	public static String stripNamespace(String propertiesName)
	{
		if(propertiesName == null)
		{
			return null;
		}
		
		String origName = propertiesName;
		
		int index = propertiesName.lastIndexOf(PropertiesConstants.NAMESPACE_SEPARATOR_CHAR);
		if(index >= 0)
		{
			propertiesName = propertiesName.substring(index + 1);
		}
		
		if(StringUtils.equals(origName, propertiesName))
		{
			return origName;
		} else {
			return propertiesName;
		}
	}
	
	
	/**
	 * Adds to the default properties the specified instance
	 */
	protected void insertIntoDefaultProperties(IPropertiesInstance<E> props)
	{
    IPropertiesInstance<E> oldValue = defaultProperties_.putIfAbsent(props.getName(), props);
    if (oldValue != null) {
      CommonPlugin.getDefault().logError(String.format("Default property name collision for  %s: dropping duplicate instance %s", getPropertiesDisplayName(), props.getName()), new Exception());
    }
	  
	}
	
	private void addPropertiesInstance(File file, Type type)
	{
		try {
			switch(type)
			{
				case Default:	
					IPropertiesInstance<E> props = createDefaultProperties(file);

					//FIXME: no filtering here!!
          if (filter(props)) {					
            insertIntoDefaultProperties(props);
					}
				break;
				case User:
					MutablePropertiesInstance<E> mProps = createUserProperties(file);
					if(!(mProps instanceof MutablePropertiesInstance))
					{
						System.err.println("User properties is not Mutable: "+mProps.getName());
					}
					userProperties_.put(mProps.getName(), mProps);
					break;
			}
		} catch (Exception e) {
			IStatus status;
			if(e instanceof CoreException)
			{
				status = ((CoreException) e).getStatus();
			} else {
				status = CommonPlugin.getDefault().newErrorStatus("Error initializing "+getPropertiesDisplayName()+" from file: "+file.getAbsolutePath(), e);
			}
			CommonPlugin.getDefault().log(status);
		}
	}
	
	/**
	 * <p>Override this method to get a chance to accept or reject default
	 * instances at load time.</p>
	 * 
	 * <p>Relies upon the current ExecutionEnvironment and matching the
	 * "lan" setting in the specified properties
	 * @param props a proposed instance
	 * @return true if the instance should be kept
	 * @deprecated Contributions should be filtered without needing this method
	 */
	@Deprecated
  public boolean filter(IPropertiesInstance<E> props) {
	  IExecutionEnvironment execEnv = 
	      ExecutionEnvironmentMgr.getInstance().getExecutionEnv();

    // get the properties' lan
    String lan = props.getProperty(PropertiesConstants.LAN);
    
    if (StringUtils.isBlank(lan)) {
      return true;
    }
    
    ILan propLan = SimpleExecEnvFactory.getInstance().makeLan(lan);
    
    return (execEnv.getLan().test(propLan));
	}

	public void addProperties(MutablePropertiesInstance<E> properties)
	{
		if(properties == null)
			throw new IllegalArgumentException(getPropertiesDisplayName()+" cannot be null");
		if(properties.getName()==null || properties.getName().trim().equals(""))
			throw new IllegalArgumentException(getPropertiesDisplayName()+" name cannot be null or blank");
		if(userProperties_.containsKey(properties.getName()))
			throw new IllegalArgumentException(getPropertiesDisplayName()+" already exists with the name \""+properties.getName()+"\", cannot replace.");
		if(userProperties_.put(properties.getName(), properties) != null)
			System.err.println("Overwriting "+getPropertiesDisplayName()+": "+properties.getName());
			
		notifyPropertyChangeListeners(properties, true);
		
		this.modified_ = true;
	}
	
	public void removeProperties(MutablePropertiesInstance<E> properties)
	{
		if(properties==null || properties.getName()==null || properties.getName().trim().equals(""))
			return;
		
		MutablePropertiesInstance<E> removedProperties = userProperties_.remove(properties.getName());
		if(removedProperties != null)
		{
			if(!removedProperties.equals(properties))
				System.err.println("Found and removed a different "+getPropertiesDisplayName()+" by name: "+properties.getName()+", than was requested to be removed.");
				
			notifyPropertyChangeListeners(removedProperties, false);
			
			modified_ = true;
		}
	}
	
	public void saveChanges() throws IOException
	{
		if(!this.modified_)
			return;
		
		for(MutablePropertiesInstance<E> props : this.userProperties_.values())
		{
			if(props == null)
			{
				System.err.println("null "+getPropertiesDisplayName()+" in map.");
				continue;
			}
			props.saveChanges();
		}
		
		// now delete any properties files that aren't in our map
		try {
			File propertiesFolder = getUserPropertiesFolder();
			for(String propertiesFileName : propertiesFolder.list())
			{
				if(!propertiesFileName.endsWith(PropertiesConstants.DOT_PROPERTIES))
					continue;
				
				String propertiesName = propertiesFileName.substring(0, propertiesFileName.length()-PropertiesConstants.DOT_PROPERTIES.length());
				if(!userProperties_.keySet().contains(propertiesName))
				{
					File propertiesFile = new File(propertiesFolder, propertiesFileName);
					if(!propertiesFile.delete())
					{
						CommonPlugin.getDefault().logError("Error deleting removed "+getPropertiesDisplayName()+": "+propertiesFile, new Exception());
					}
				}
			}
		} catch (Exception e) {
			CommonPlugin.getDefault().logError("Error deleting removed "+getPropertiesDisplayName()+" properteis files.", e);
		}
		
		this.modified_ = false;
	}
	
	public List<String> getUserPropertiesNames()
	{ return new ArrayList<>(userProperties_.keySet()); }
	
	public List<String> getDefaultPropertiesNames()
	{ return new ArrayList<>(defaultProperties_.keySet()); }
	
	public E getDefaultPropertiesInstance(String propertiesName)
	{
		if(propertiesName == null)
			return null;
		
		if(defaultProperties_.containsKey(propertiesName))
			return defaultProperties_.get(propertiesName).asBaseType();
		
		return null;
	}
	
	public E getPropertiesInstance(String propertiesName)
	{ return getPropertiesInstance(propertiesName, false); }
	
	public E getPropertiesInstance(String propertiesName, boolean stripNamespace)
	{
		if(propertiesName == null)
			return null;
		
		if(stripNamespace)
		{
			propertiesName = stripNamespace(propertiesName);
		}
		
		if(userProperties_.containsKey(propertiesName))
			return userProperties_.get(propertiesName).asBaseType();
		
		if(defaultProperties_.containsKey(propertiesName))
			return defaultProperties_.get(propertiesName).asBaseType();
		
		return null;
	}
	
	public SortedSet<String> getAllPropertiesNames()
	{
		SortedSet<String> names = new TreeSet<>();
		names.addAll(defaultProperties_.keySet());
		names.addAll(userProperties_.keySet());
		return names;
	}
	
	public String getUniqueName(String baseName)
	{
		return Utils.getUniqueName(getAllPropertiesNames(), baseName);
	}
	
	
	
	/**
	 * Returns all of the properties in a new (modifiable) Collection
	 */
	public List<E> getAllProperties(boolean includeOverridden)
	{
	
		List<IPropertiesInstance<E>> allProps = new ArrayList<>();
		allProps.addAll(userProperties_.values());
		if(includeOverridden)
			allProps.addAll(defaultProperties_.values());
		else
			for(IPropertiesInstance<E> props : defaultProperties_.values())
				if(!userProperties_.containsKey(props.getName()))
					allProps.add(props);
		
		Collections.sort(allProps, JSPropComparator_);

		// 2018-04 (kho): change to use a stream
		List<E> baseProps = allProps.stream()
		  .map(p -> p.asBaseType())
		  .collect(Collectors.toList());

		return baseProps;
	}
	
	
	/**
	 * child classes must implement
	 */
	public abstract MutablePropertiesInstance<E> createNewProperties();
	
	
	
	/**
	 * child classes must implement
	 */
	public abstract MutablePropertiesInstance<E> createNewProperties(File file) throws Exception;
	
	

	private void notifyPropertyChangeListeners(IPropertiesInstance<E> properties, boolean added)
	{
		PropertyChangeEvent event = new PropertyChangeEvent(
				this, // source
				getPropertiesDisplayName(), // property
				added ? null : properties, // oldValue
				added ? properties : null // newValue
				);
		
		for(IPropertyChangeListener listener: listenersHandler_.getListeners())
		{
			listener.propertyChange(event);
		}
	}

	public void addPropertyListener(IPropertyChangeListener listener)
	{
		listenersHandler_.addListener(listener);
	}

	public void removePropertyChangeListener(IPropertyChangeListener listener)
	{
		listenersHandler_.removeListener(listener);
	}
}
