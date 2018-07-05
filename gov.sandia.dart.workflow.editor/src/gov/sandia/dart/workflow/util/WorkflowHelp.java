/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.util;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class WorkflowHelp {

	public static  void openDocumentationWebPage(String nodeType) {
		try {
			URL url = new URL("https://dart.sandia.gov/wiki/display/WRKFLW/" + nodeType);			
			IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
			IWebBrowser browser = browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_VIEW | IWorkbenchBrowserSupport.LOCATION_BAR
					| IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.STATUS | IWorkbenchBrowserSupport.PERSISTENT,
					"workflow.help.browser", "Web Browser", "Web Browser");
			browser.openURL(url);
	
		} catch (PartInitException | MalformedURLException e1) {
			WorkflowEditorPlugin.getDefault().logError("Error creating help browser", e1);
		}
	}

}
