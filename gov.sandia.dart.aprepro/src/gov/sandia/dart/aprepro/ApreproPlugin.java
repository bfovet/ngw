/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro;

import gov.sandia.dart.aprepro.actions.CreateParameterAction;
import gov.sandia.dart.aprepro.actions.InsertParameterAction;

import java.io.OutputStream;
import java.net.URL;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;
import org.osgi.framework.BundleContext;

import com.strikewire.snl.apc.reporting.AbsReportingUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class ApreproPlugin extends AbsReportingUIPlugin
{
	public static final String CUT_ICON = "icons/cut_edit.png";
	public static final String COPY_ICON = "icons/copy_edit.png";
	public static final String PASTE_ICON = "icons/paste_edit.png";
	// The plug-in ID
	public static final String PLUGIN_ID = "gov.sandia.dart.aprepro"; //$NON-NLS-1$

	// The shared instance
	private static ApreproPlugin plugin;

	private static final String CONSOLE_NAME = "Aprepro Output";	
	
			
	/**
	 * The constructor
	 */
	public ApreproPlugin()
	{
		super(PLUGIN_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ApreproPlugin getDefault() {		
		return plugin;
	}
			
	@Override
	protected void initializeImageRegistry(ImageRegistry reg) {		
		registerImage(reg, CreateParameterAction.CREATE_PARAMETER_ICON, CreateParameterAction.CREATE_PARAMETER_ICON);
		registerImage(reg, InsertParameterAction.INSERT_PARAMETER_ICON, InsertParameterAction.INSERT_PARAMETER_ICON);
		registerImage(reg, COPY_ICON, COPY_ICON);
		registerImage(reg, CUT_ICON, CUT_ICON);
		registerImage(reg, PASTE_ICON, PASTE_ICON);
	}

	private void registerImage(ImageRegistry registry, String key,
			String fileName) {
		try {
			IPath path = new Path(fileName);
			URL url = find(path);
			if (url!=null) {
				ImageDescriptor desc = ImageDescriptor.createFromURL(url);
				registry.put(key, desc);
			}
		} catch (Exception e) {
		}
	}
		
	public static MessageConsole getConsole() {			
		return findConsole(CONSOLE_NAME);		
	}	
	
	private static MessageConsole findConsole(String name)
	  {
	    ConsolePlugin plugin = ConsolePlugin.getDefault();
	    IConsoleManager conMan = plugin.getConsoleManager();
	    IConsole[] existing = conMan.getConsoles();
	    for (int i = 0; i < existing.length; i++)
	      if (name.equals(existing[i].getName())) {
	    	  MessageConsole myConsole = (MessageConsole) existing[i];
	    	  conMan.showConsoleView(myConsole);
	    	  return myConsole;
	      }
	    // no console found, so create a new one
	    MessageConsole myConsole = new MessageConsole(name, null);
	    conMan.addConsoles(new IConsole[] { myConsole });
	    conMan.showConsoleView(myConsole);
	    IConsoleView view = getConsoleView();
	    if (view != null) {
	      view.display(myConsole);
	    }
	    return myConsole;
	  }

	private static IConsoleView getConsoleView()
	  {
	    if (!PlatformUI.isWorkbenchRunning()) {
	      return null;
	    }

	    IWorkbench wb = PlatformUI.getWorkbench();
	    if (wb != null) {
	      IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
	      if (win != null) {
	        IWorkbenchPage page = win.getActivePage();
	        try {
	          return (IConsoleView) page.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
	        }
	        catch (PartInitException e) {
	          // We tried
	        }
	      }
	    }
	    return null;
	  }
	
}
