/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.util;

import java.net.URL;

import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.osgi.framework.Bundle;

import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class WorkflowHelp {

	public static  void openDocumentationWebPage(String nodeType) {
		if (tryOpenDocumentationWebPage(nodeType, "gov.sandia.dart.workflow.help"))
			return;
		IExtensionPoint ep = ExtensionPointUtils.getExtensionPoint(WorkflowEditorPlugin.PLUGIN_ID, "nodeTypeContributor");
		for (IExtension ext: ep.getExtensions()) {
			String bundle = ext.getContributor().getName();
			if (tryOpenDocumentationWebPage(nodeType, bundle))
				return;
		}
	}
	
	private static boolean tryOpenDocumentationWebPage(String nodeType, String bundle) {
		String href = null;
		Bundle bndlSierraPlugin_ = Platform.getBundle(bundle);
		final URL entry = bndlSierraPlugin_.getEntry("/components/" + nodeType + ".html");
		if (entry == null)
			return false;
		href = "/" + bundle + "/components/" + nodeType + ".html";
		
		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		helpSystem.displayHelpResource(href);
		return true;
	}

}
