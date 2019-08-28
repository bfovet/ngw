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

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import com.strikewire.snl.apc.reporting.AbsReportingUIPlugin;

import gov.sandia.dart.workflow.phase3.embedded.tools.ClasspathTool;

/**
 * The activator class controls the plug-in life cycle
 */
public class EmbeddedWorkflowPlugin extends AbsReportingUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.sandia.dart.workflow.phase3.embedded"; //$NON-NLS-1$
	public static final String KILLED = "icons/stage-killed.gif";
	public static final String RUNNING = "icons/stage-running.gif";
	public static final String ERROR = "icons/state-error.gif";
	public static final String SUCCESSFUL = "icons/status-successful.gif";
	public static final String RUN_FOLDER = "icons/runfolder.png";

	// The shared instance
	private static EmbeddedWorkflowPlugin plugin;
	
	private IPreferenceStore preferenceStore;

	private static final String[] imageKeys_ = {
			KILLED, RUNNING, ERROR, SUCCESSFUL, RUN_FOLDER
	};
	
	public static final String EMBEDDED_PREFERENCE_NAME = "embedded_info";
	
	public EmbeddedWorkflowPlugin() {
		super(PLUGIN_ID);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		createStandaloneWorkflowScript();
	}

	private void createStandaloneWorkflowScript() {
		try {
			
			boolean isUnix = System.getProperty("os.name").toLowerCase().indexOf("win") < 0;
			String scriptName = isUnix ? "runNgw.sh" : "runNgw.bat";				
			File file = new File(System.getProperty("user.home") + File.separator + ".dart", scriptName);
			try (FileWriter f = new FileWriter(file)) {
				IOUtils.write(isUnix ? ClasspathTool.generateBashScript() : ClasspathTool.generateBatchFile(), f);
				file.setExecutable(true, false);
			}
			
		} catch (Exception e) {				
			EmbeddedWorkflowPlugin.getDefault().logError("Error creating standalone workflow script", e);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
	
	public static EmbeddedWorkflowPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path)
	{
		ImageDescriptor descriptor = getDefault().getImageRegistry().getDescriptor(path);
		return descriptor!=null ? descriptor : imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#initializeImageRegistry(org.eclipse.jface.resource.ImageRegistry)
	 */
	@Override
	protected void initializeImageRegistry(ImageRegistry reg)
	{
		for(String imageKey : imageKeys_)
		{
			reg.put(imageKey, imageDescriptorFromPlugin(PLUGIN_ID, imageKey));
		}
	}
	
	@Override
	public IPreferenceStore getPreferenceStore() {
        // Create the preference store lazily.
        if (preferenceStore == null) {
            preferenceStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, getBundle().getSymbolicName());
        }
        return preferenceStore;
	}
}
