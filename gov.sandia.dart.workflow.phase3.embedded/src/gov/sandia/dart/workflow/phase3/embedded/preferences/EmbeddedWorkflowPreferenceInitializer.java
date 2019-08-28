/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.preferences;
import static gov.sandia.dart.workflow.phase3.embedded.preferences.IEmbeddedExecutionPreferenceConstants.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import gov.sandia.dart.workflow.phase3.embedded.EmbeddedWorkflowPlugin;

public class EmbeddedWorkflowPreferenceInitializer extends AbstractPreferenceInitializer {

	public EmbeddedWorkflowPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = EmbeddedWorkflowPlugin.getDefault().getPreferenceStore();
		store.setDefault(VALIDATE_UNDEFINED, false);		
		store.setDefault(CLEAR_CONSOLE, true);
	}
	
}
