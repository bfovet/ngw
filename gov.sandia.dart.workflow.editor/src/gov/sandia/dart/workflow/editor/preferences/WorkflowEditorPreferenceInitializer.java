/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class WorkflowEditorPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public WorkflowEditorPreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = WorkflowEditorPlugin.getDefault().getPreferenceStore();
		
		FontData[] fontData = getDefaultDiagramFont();
		PreferenceConverter.setDefault(store, FONT, fontData);
		
		fontData[0].setStyle(SWT.ITALIC);
		PreferenceConverter.setDefault(store, NOTES_FONT, fontData);

		fontData = JFaceResources.getTextFont().getFontData();
		fontData[0].setHeight(9);
		PreferenceConverter.setDefault(store, EDITOR_FONT, fontData);

		store.setDefault(PALETTE_FILE_DIR, new File(System.getProperty("user.home")).getAbsolutePath());		
		store.setDefault(MANHATTAN_CONNECTIONS, false);
		store.setDefault(PORT_LABELS, true);
		store.setDefault(SKIP_SINGLETON_PORT_LABELS, true);
		store.setDefault(REQUIRE_CTRL_FOR_POPUP, false);
		store.setDefault(CONNECTIONS_BEHIND, true);
		store.setDefault(TRANSLUCENT_COMPONENTS, true);
		store.setDefault(RENDER_NESTED, true);
		store.setDefault(RENDER_NESTED_INDENTED, true);
		store.setDefault(SHOW_MIN_MAX_ICON, false);

	}
	
	private FontData[] getDefaultDiagramFont() {
		// These are values for default 11-point font on Mac.
		final int X = 36, Y = 13;
		FontData[] fontData = Display.getDefault().getSystemFont().getFontData();
		GC gc= new GC(Display.getDefault());		
		FontData fd = fontData[0];
		double lsq = Integer.MAX_VALUE;
		int best = 5;
		for (int i=5; i<13; i++) {
			fd.setHeight(i);
			Font test = new Font(Display.getDefault(), fd);
			gc.setFont(test);
			Point extent = gc.stringExtent("HELLO");
			double distance = Math.pow(X-extent.x, 2) + Math.pow(Y-extent.y, 2);
			if (distance < lsq) {
				lsq = distance;
				best = i;
			}
			test.dispose();
		}
		fd.setHeight(best);
		return fontData;
	}

}
