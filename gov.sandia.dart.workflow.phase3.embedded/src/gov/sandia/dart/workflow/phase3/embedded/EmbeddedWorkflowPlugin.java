/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import com.strikewire.snl.apc.reporting.AbsReportingUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class EmbeddedWorkflowPlugin extends AbsReportingUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.sandia.dart.workflow.phase3.embedded"; //$NON-NLS-1$

	// The shared instance
	private static EmbeddedWorkflowPlugin plugin;
	
	private IPreferenceStore preferenceStore;
	
	public static final String EMBEDDED_PREFERENCE_NAME = "embedded_info";
	
	public EmbeddedWorkflowPlugin() {
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
	
	public static EmbeddedWorkflowPlugin getDefault() {
		return plugin;
	}
	
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	} 
	
	public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, getBundle().getSymbolicName());
        }
        return preferenceStore;
	}
}
