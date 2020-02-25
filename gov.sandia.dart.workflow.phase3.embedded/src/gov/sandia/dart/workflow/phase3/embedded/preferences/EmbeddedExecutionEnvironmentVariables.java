/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.preferences;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.Bundle;

import com.strikewire.snl.apc.properties.BundlePropertiesSource;
import com.strikewire.snl.apc.properties.DefaultPropertiesSource;
import com.strikewire.snl.apc.properties.IPropertiesInstance;
import com.strikewire.snl.apc.properties.MutablePropertiesInstance;
import com.strikewire.snl.apc.properties.PropertiesStore;

import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;

public class EmbeddedExecutionEnvironmentVariables extends PropertiesStore<EmbeddedExecutionEnvironmentVariable> {
	public static final String ENV_VARS_FOLDER_NAME = "embeddedWorkflowEnvironmentVars";
  
	/**
	 * The instance of this Singleton; thread safe initialization
	 * by creating at the point of class access
	 */
	private static final EmbeddedExecutionEnvironmentVariables instance_ = new EmbeddedExecutionEnvironmentVariables();  
	  
	private EmbeddedExecutionEnvironmentVariables()
	{
		super();
	}

	public static EmbeddedExecutionEnvironmentVariables getInstance()
	{
		return instance_;
	}

	@Override
	public String getUserPropertiesFolderName() {
		return ENV_VARS_FOLDER_NAME;
	}

	@Override
	public String getPropertiesDisplayName() {
		return "Embedded workflow execution environment variable";
	}

	@Override
	protected Plugin getPlugin() {
		return EmbeddedWorkflowPlugin.getDefault();
	}

	@Override
	protected IPropertiesInstance<EmbeddedExecutionEnvironmentVariable>
		createDefaultProperties(File file) throws Exception {
		return new EmbeddedExecutionEnvironmentVariable(file, this);
	}

	@Override
	protected MutablePropertiesInstance<EmbeddedExecutionEnvironmentVariable>
		createUserProperties(File file) throws Exception {
		return new MutableEmbeddedExecutionEnvironmentVariable(this, file);
	}

	@Override
	public MutablePropertiesInstance<EmbeddedExecutionEnvironmentVariable> createNewProperties() {
		return new MutableEmbeddedExecutionEnvironmentVariable(this);
	}

	@Override
	public MutablePropertiesInstance<EmbeddedExecutionEnvironmentVariable>
		createNewProperties(File file) throws Exception {
		return new MutableEmbeddedExecutionEnvironmentVariable(this, file);
	}
	
	@Override
	protected DefaultPropertiesSource[] initializeDefaultSource()
	{
		return new DefaultPropertiesSource[] {new BundlePropertiesSource() {

			@Override
			protected Bundle getBundle()
			{
				return getPlugin().getBundle();
			}

			@Override
			protected IPath getPropertiesFolderPath()
			{
				return new Path(getUserPropertiesFolderName());
			}
			
		}};
	}

}
