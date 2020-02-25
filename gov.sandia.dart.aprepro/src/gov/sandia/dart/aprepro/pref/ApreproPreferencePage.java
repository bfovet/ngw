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

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.strikewire.snl.apc.GUIs.DefaultFieldEditorPreferencePage;

import gov.sandia.dart.aprepro.ApreproPlugin;


public class ApreproPreferencePage extends DefaultFieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	public ApreproPreferencePage() 
	{
		super(GRID);
		setPreferenceStore( ApreproPlugin.getDefault().getPreferenceStore() );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors()
	 */
	@Override
	protected void createFieldEditors()
	{
		addField( new FileFieldEditor(
				ApreproConstants.APREPRO_EXECUTABLE,
				"APREPRO executable",
				getFieldEditorParent() ) );
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench)
	{}

}
