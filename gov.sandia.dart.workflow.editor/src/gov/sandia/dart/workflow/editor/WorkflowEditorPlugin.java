/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.strikewire.snl.apc.reporting.AbsReportingUIPlugin;

import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class WorkflowEditorPlugin extends AbsReportingUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.sandia.dart.workflow.editor"; //$NON-NLS-1$

	// The shared instance
	private static WorkflowEditorPlugin plugin;
	
	private Map<String, Font> registry = new HashMap<>();
	
	public WorkflowEditorPlugin() {
		super(PLUGIN_ID);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static WorkflowEditorPlugin getDefault() {
		return plugin;
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	} 
	
	public Font getDiagramFont() {
		String fontString = getPreferenceStore().getString(IWorkflowEditorPreferences.FONT);
		if (registry.get(fontString) == null) {
			FontData[] fontData = PreferenceConverter.getFontDataArray(getPreferenceStore(), IWorkflowEditorPreferences.FONT);	
			Font f = new Font(Display.getDefault(), fontData);
			registry.put(fontString, f);
		}
		return registry.get(fontString);
	}
	
	public Font getNotesFont() {
		String fontString = getPreferenceStore().getString(IWorkflowEditorPreferences.NOTES_FONT);
		if (registry.get(fontString) == null) {
			FontData[] fontData = PreferenceConverter.getFontDataArray(getPreferenceStore(), IWorkflowEditorPreferences.NOTES_FONT);	
			Font f = new Font(Display.getDefault(), fontData);
			registry.put(fontString, f);
		}
		return registry.get(fontString);
	}
	
//	public Font getImagesFont() {
//		String fontString = getPreferenceStore().getString(IWorkflowEditorPreferences.IMAGES_FONT);
//		if (registry.get(fontString) == null) {
//			FontData[] fontData = PreferenceConverter.getFontDataArray(getPreferenceStore(), IWorkflowEditorPreferences.NOTES_FONT);	
//			Font f = new Font(Display.getDefault(), fontData);
//			registry.put(fontString, f);
//		}
//		return registry.get(fontString);
//	}
	
	public Font getEditorAreaFont() {
		String fontString = getPreferenceStore().getString(IWorkflowEditorPreferences.EDITOR_FONT);
		if (registry.get(fontString) == null) {
			FontData[] fontData = PreferenceConverter.getFontDataArray(getPreferenceStore(), IWorkflowEditorPreferences.EDITOR_FONT);	
			Font f = new Font(Display.getDefault(), fontData);
			registry.put(fontString, f);
		}
		return registry.get(fontString);
	}

}
