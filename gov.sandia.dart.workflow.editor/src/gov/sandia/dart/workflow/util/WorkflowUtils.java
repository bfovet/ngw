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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import gov.sandia.dart.workflow.editor.DatedPath;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class WorkflowUtils {

	private static final String MARKER = "gov.sandia.dart.workflow.editor.runLocation";
	private static final String PATH_ATTRIBUTE = "path";
	private static final String TIMESTAMP_ATTRIBUTE = "timestamp";

	
	// TODO Only keep N newest?
	public static void updateRunLocationMarker(final IFile workflowFile, final File folder) {
		final String path = folder.getAbsolutePath();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRunnable task = new IWorkspaceRunnable() {
			@Override public void run(IProgressMonitor monitor) throws CoreException {
				IMarker theMarker = null;
				IMarker[] markers = workflowFile.findMarkers(MARKER, true, IResource.DEPTH_ZERO);
				for (IMarker marker: markers) {
					String oldPath = marker.getAttribute(PATH_ATTRIBUTE, "");
					if (path.equals(oldPath)) {
						theMarker = marker;
						break;
					}					
				}
				if (theMarker == null)
					theMarker = workflowFile.createMarker(MARKER);
				theMarker.setAttribute(PATH_ATTRIBUTE, path);
				theMarker.setAttribute(TIMESTAMP_ATTRIBUTE, String.valueOf(System.currentTimeMillis()));
			}
		};
		try {
			workspace.run(task, workflowFile, IWorkspace.AVOID_UPDATE, null);
		} catch (CoreException e) {
			WorkflowEditorPlugin.getDefault().logError("Problem updating run location marker", e);
		}
	}
	
	public static List<DatedPath> getRunLocationMarkers(IFile file) throws CoreException {
		List<DatedPath> content = new ArrayList<>();
		IMarker[] markers = file.findMarkers(MARKER, true, IResource.DEPTH_ZERO);
		for (IMarker marker: markers) {
			if (marker.exists()) {
				String path = marker.getAttribute(PATH_ATTRIBUTE, "");
				String timestamp = marker.getAttribute(TIMESTAMP_ATTRIBUTE, "");
				if (StringUtils.isNotEmpty(path) && StringUtils.isNotEmpty(timestamp)) {
					content.add(new DatedPath(path, Long.parseLong(timestamp)));
				}
			}
		}
		Collections.sort(content);
		return content;
	}


}
