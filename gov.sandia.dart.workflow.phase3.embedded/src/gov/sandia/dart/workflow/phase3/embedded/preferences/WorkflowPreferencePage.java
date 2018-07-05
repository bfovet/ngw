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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class WorkflowPreferencePage
	extends FieldEditorPreferencePage 
	implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
	    setDescription("Preferences category for workflow capabilities");
	}

	@Override
	protected void createFieldEditors() {
		// TODO Auto-generated method stub
		
	}

}

