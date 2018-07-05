/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
///**
// * 
// */
//package com.strikewire.snl.apc.properties;
//
//import java.io.File;
//import java.io.IOException;
//
//import org.eclipse.core.internal.runtime.InternalPlatform;
//import org.eclipse.core.runtime.IPath;
//import org.osgi.framework.Bundle;
//
///**
// * @author mjgibso
// *
// */
//@SuppressWarnings("restriction")
//public abstract class LocalPropertiesSource implements IPropertiesSource
//{
//	public static final String NAMESPACE = "user.defined";
//	
//	public String getNamespace()
//	{
//		return NAMESPACE;
//	}
//	
//	/* (non-Javadoc)
//	 * @see com.strikewire.snl.apc.properties.IPropertiesSource#getPropertiesFolder()
//	 */
//	public File getPropertiesFolder() throws IOException
//	{
//		IPath statePath = InternalPlatform.getDefault().getStateLocation(getBundle(), true);
//		IPath propertiesPath = statePath.append(getPropertiesFolderPath());
//		File propertiesFolder = propertiesPath.toFile();
//		if(!propertiesFolder.exists())
//		{
//			propertiesFolder.mkdirs();
//		}
//		return propertiesFolder;
//	}
//	
//	protected abstract Bundle getBundle();
//	
//	protected abstract IPath getPropertiesFolderPath();
//
//	/* (non-Javadoc)
//	 * @see com.strikewire.snl.apc.properties.IPropertiesSource#isMutable()
//	 */
//	public boolean isMutable()
//	{
//		return true;
//	}
//
//	/* (non-Javadoc)
//	 * @see com.strikewire.snl.apc.properties.IPropertiesSource#filter(com.strikewire.snl.apc.properties.IPropertiesInstance)
//	 */
//	public boolean filter(IPropertiesInstance<?> instance)
//	{
//		return true;
//	}
//
//}
