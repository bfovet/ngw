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

import org.eclipse.ui.IWorkbench;

import com.strikewire.snl.apc.properties.MutablePropertiesInstance;
import com.strikewire.snl.apc.properties.PropertiesPreferencePage;

public class EmbeddedWorkflowPreferencePage extends PropertiesPreferencePage<EmbeddedExecutionEnvironmentVariable>
{

	public EmbeddedWorkflowPreferencePage() {
		super(EmbeddedExecutionEnvironmentVariables.getInstance());
	}

	@Override
	public void init(IWorkbench workbench) {
		setDescription("Define additional environment variables that will be visible to external processes created when running workflows embedded in the workbench.");
	}

	@Override
	protected int showEditDialog(MutablePropertiesInstance<EmbeddedExecutionEnvironmentVariable> mp) {
		MutableEmbeddedExecutionEnvironmentVariable type = (MutableEmbeddedExecutionEnvironmentVariable) mp;
  	    
	    return EmbeddedExecutionEnvironmentVariableDialog.showDialog(getShell(), type);
	}
}
