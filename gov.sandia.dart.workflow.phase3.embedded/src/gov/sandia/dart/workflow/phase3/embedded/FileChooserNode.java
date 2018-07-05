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

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.GUIs.GuiUtils;

public class FileChooserNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		String[] result =  { "" };
		
		try {
			String title = properties.get("title");
			String initialChoice = properties.get("initialPath");
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					IPath initialPath = null;
					if (!StringUtils.isEmpty(initialChoice)) {
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(initialChoice));
						initialPath = file.getFullPath();
					}
					IPath chosenPath;
					if (StringUtils.isEmpty(title))
						chosenPath = GuiUtils.openFileBrowser(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(), initialPath);
					else
						chosenPath = GuiUtils.openFileBrowser(PlatformUI.getWorkbench().getWorkbenchWindows()[0].getShell(), initialPath, title);
					result[0] = chosenPath.makeAbsolute().toString();
				}			
			});
									
		} catch (Exception e) {
			throw new SAWWorkflowException("Error executing node", e);
		}

		runtime.log().info("File {0} was selected via FileChooserNode {1}", result[0], getName());
		return Collections.singletonMap("f", result[0]);			
	}

	public String getFormatString(Map<String, String> properties) {
		return properties.get("formatString");
	}

	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList("f"); }
	@Override public List<String> getDefaultInputNames() { return Collections.singletonList("x"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("initialPath"); }



}
