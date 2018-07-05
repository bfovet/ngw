/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.preferences;

import static gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences.*;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class WorkflowEditorPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public WorkflowEditorPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = WorkflowEditorPlugin.getDefault().getPreferenceStore();
		FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
		fontData[0].setHeight(9);
		PreferenceConverter.setDefault(store, FONT, fontData);
		
		fontData = Display.getDefault().getSystemFont().getFontData();
		fontData[0].setHeight(9);
		fontData[0].setStyle(SWT.ITALIC);
		PreferenceConverter.setDefault(store, NOTES_FONT, fontData);

		fontData = JFaceResources.getTextFont().getFontData();
		fontData[0].setHeight(9);
		PreferenceConverter.setDefault(store, EDITOR_FONT, fontData);

		store.setDefault(PALETTE_FILE_DIR, new File(System.getProperty("user.home")).getAbsolutePath());		
		store.setDefault(MANHATTAN_CONNECTIONS, false);
		store.setDefault(PORT_LABELS, true);
	}

}
