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

import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;

import java.io.File;

import com.strikewire.snl.apc.properties.PropertiesInstance;
import com.strikewire.snl.apc.properties.PropertiesStore;

/**
 * Cloning this from gov.sandia.dart.workflow.editor.UserDefinedType, because it gives
 * us a table of names and values which can also be imported or exported.
 * 
 * @author mrglick
 *
 */
public class EmbeddedExecutionEnvironmentVariable extends PropertiesInstance<EmbeddedExecutionEnvironmentVariable>{
	protected static final String VALUE = "embedded_workflow_environment_variable_value";

	public EmbeddedExecutionEnvironmentVariable(File file,
			PropertiesStore<EmbeddedExecutionEnvironmentVariable> parent)
			throws Exception {
		super(file, parent);
	}
	
	public EmbeddedExecutionEnvironmentVariable(PropertiesStore<EmbeddedExecutionEnvironmentVariable> parent) {
		super(parent);
	}

	@Override
	protected String getCurrentBuildVersion() {
		return EmbeddedWorkflowPlugin.getDefault().getBundle().getVersion().toString();
	}

	public String getValue() {
		return getProperty(VALUE);
	}
}
