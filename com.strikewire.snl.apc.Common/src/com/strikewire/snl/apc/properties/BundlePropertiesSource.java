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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.Bundle;

/**
 * @author mjgibso
 *
 */
public abstract class BundlePropertiesSource implements DefaultPropertiesSource
{
	@Override
	public File getPropertiesFolder() throws IOException
	{
		URL propertiesFolderURL = FileLocator.find(getBundle(), getPropertiesFolderPath(), null);
		if (propertiesFolderURL == null) {
			throw new FileNotFoundException("Cannot find folder " + getPropertiesFolderPath() + " in bundle " + getBundle().getSymbolicName());
		}
		propertiesFolderURL = FileLocator.toFileURL(propertiesFolderURL);
		return new File(propertiesFolderURL.getPath());
	}
	
	protected abstract Bundle getBundle();
	
	protected abstract IPath getPropertiesFolderPath();
}
