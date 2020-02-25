/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.preferences.compare;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class CompareWithPreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage
{

  public CompareWithPreferencePage()
  {
    super("Compare Current Editor With...",GRID);
    setPreferenceStore(CommonPreferencesPlugin.getDefault().getPreferenceStore());
    setDescription("Preferences for Compare Editors.");
  }

  @Override
  public void init(IWorkbench workbench)
  {
  }

  @Override
  protected void createFieldEditors()
  {
	BooleanFieldEditor bfe =
	    new BooleanFieldEditor(EComparePrefs.OPEN_COMPARE_IN_NEW_TAB.getPrefKey(),
	        "Compare Editor will open in new tab, rather than replacing an " +
	        "existing compare editor",
	        getFieldEditorParent());
    addField(bfe);
  }

}
