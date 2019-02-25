/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.Common;

import java.io.PrintStream;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.Preferences;

import com.strikewire.snl.apc.reporting.AbsReportingUIPlugin;

/**
 * The main plugin class to be used in the desktop.
 */
public class CommonPlugin extends AbsReportingUIPlugin
{
	/**
   * _log -- A Logger instance for CommonPlugin
   */
  private static final Logger _log = LogManager.getLogger(CommonPlugin.class);
  
	/** The ID String of this plugin */
	public static final String ID = "com.strikewire.snl.apc.Common";

	/** String locations of image files*/
	public static final String IMAGE_UP_ARROW = "icons/event_prev.gif";
	public static final String IMAGE_DOWN_ARROW = "icons/event_next.gif";
	public static final String IMAGE_LEFT_ARROW = "icons/e_back.gif";
	public static final String IMAGE_RIGHT_ARROW = "icons/e_forward.gif";
	public static final String IMAGE_PIN_EDITOR = "icons/pin_editor.png";
	public static final String IMAGE_PIN_VIEW = "icons/pin_view.gif";
	
	//The shared instance.
	private static CommonPlugin plugin;
	
	//Resource bundle.
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public CommonPlugin()
	{
		super(ID);
	}
	
	@Override
	public void start(BundleContext context) throws Exception
	{
	  super.start(context);

	  plugin = this;
	  
	  initSystemExplorerDefault();
	  
	  _log.trace("{}.start()", this.getClass().getName());
	}
	
	/**
	 * This method is called when the plug-in is stopped
	 */
	@Override
  public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		plugin = null;
		resourceBundle = null;
    _log.trace("{}.stop()", this.getClass().getName());
	}
	

	/**
	 * Returns the shared instance.
	 */
	public static CommonPlugin getDefault()
	{
		return plugin;
	}
	
	@Override
	public IPreferenceStore getPreferenceStore()
	{
		// The common plugin (due to several dependencies) is loaded VERY early, before workspace selection in fact, and thus
		// preferences are initialized prior to the workspace being selected, and thus are initialized incorrectly.  Please
		// store preferences for this plugin in the common.preferences plugin instead.
		throw new UnsupportedOperationException("Preferences for the CommonPlugin are stored in the CommonPreferencesPlugin");
	}


	
	public static void printStatus(IStatus status, PrintStream stream)
	{
		if(stream == null)
		{
			System.err.println("null stream");
			return;
		}
		
		if(status == null)
		{
			stream.println("null status");
			return;
		}
		
		stream.println(getSeverityText(status).toUpperCase()+" Status:");
		stream.println("  Message: "+status.getMessage());
		stream.println("  Plugin: "+status.getPlugin());
		stream.println("  Code: "+status.getCode());
		Throwable t = status.getException();
		if(t == null)
		{
			stream.println("    Exception: null");
		} else {
			stream.print("    Exception: ");
			t.printStackTrace(stream);
		}
		
		IStatus[] children = status.getChildren();
		if(children != null)
		{
			if(children.length > 0)
			{
				stream.println("Child status:");
			}
			
			for(IStatus child : children)
			{
				printStatus(child, stream);
			}
		}
	}
	
	public static String getSeverityText(IStatus status)
	{
		if(status == null)
		{
			return "null";
		}
		
		switch(status.getSeverity())
		{
			case IStatus.CANCEL: return "Cancel";
			case IStatus.ERROR: return "Error";
			case IStatus.INFO: return "Info";
			case IStatus.OK: return "Ok";
			case IStatus.WARNING: return "Warning";
			default: return "Unknown"; // shouldn't ever get here...
		}
	}
	
	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = CommonPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			if (resourceBundle == null)
				resourceBundle = ResourceBundle.getBundle("com.strikewire.snl.apc.Common.CommonPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}
	
	
  /**
   * Returns an image descriptor for the image file at the given
   * plug-in relative path
   *
   * @param path the path
   * @return the image descriptor
   */
  public static ImageDescriptor getImageDescriptor(String path) {
    return imageDescriptorFromPlugin(ID, path);
  }	
  
  
  private void initSystemExplorerDefault()
  {
    if(gov.sandia.dart.common.core.env.OS.isLinux()){
  	  Preferences prefs = DefaultScope.INSTANCE.getNode("org.eclipse.ui.ide");
  	  prefs.put("SYSTEM_EXPLORER", "nautilus \"${selected_resource_parent_loc}\"");
    }	  
  }  
  
}
