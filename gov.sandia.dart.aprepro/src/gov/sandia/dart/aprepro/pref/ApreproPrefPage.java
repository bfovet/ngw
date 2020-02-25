/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.pref;

import gov.sandia.dart.aprepro.ApreproPlugin;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ApreproPrefPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	private RadioGroupFieldEditor apreproOrdeprepro;
	
	public ApreproPrefPage() {
		super(GRID);
		final IPreferenceStore store = ApreproPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(store);
		setTitle("Aprepro or Deprepro?");
	}	
	
	@Override
	protected void createFieldEditors() {
		
//		apreproOrdeprepro = new RadioGroupFieldEditor(ApreproConstants.APREPRO_ID, ApreproConstants.PROMPT_STR, 2, 
//				 new String[][] {{ ApreproConstants.APREPRO_SELECTION_STR, ApreproConstants.APREPRO_USAGE_ID },
//							     { ApreproConstants.DEPREPRO_SELECTION_STR, ApreproConstants.DEPREPRO_USAGE_ID }}, 
//				 getFieldEditorParent(), true);
//		addField(apreproOrdeprepro);
	}

	@Override
	public boolean performOk() {
		apreproOrdeprepro.store();
		
		return super.performOk();
	}

	@Override
	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub
		
	}

}
