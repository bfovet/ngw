/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.preferences.settings;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SettingsViewPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	  
	  public SettingsViewPreferencePage() {
	    super("Settings View", GRID);
	    setPreferenceStore(CommonPreferencesPlugin.getDefault().getPreferenceStore());
	    setDescription("Settings view preferences");
	  }

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		BooleanFieldEditor editor = new BooleanFieldEditor(ISettingsViewPreferences.DRAW_BORDERS, "Draw widget borders", getFieldEditorParent());
		addField(editor);
	}

}
