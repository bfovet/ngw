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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import com.strikewire.snl.apc.util.ResourceUtils;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class OpenResourceNode extends SAWCustomNode {
	@Override
	public Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		
		String path = properties.get("filename");
		if (StringUtils.isEmpty(path)) {
			path = (String) runtime.getInput(getName(), "filename", String.class);
			if (StringUtils.isEmpty(path)) {
				throw new SAWWorkflowException("filename must be defined by either a propery or a port.");
			}
		}
			
		try {
			File file = new File(path);
			if (!file.isAbsolute()) {
				file = new File(runtime.getHomeDir(), path);
			}
			IFile iFile = ResourceUtils.getFileForLocation(file.getAbsolutePath(), false);
			if (iFile != null) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						try {
							iFile.refreshLocal(IFile.DEPTH_ZERO, null);
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							IDE.openEditor(page, iFile);
						} catch (Exception e) {
							throw new SAWWorkflowException("Can't open file", e);
						}					
					}				
				});
			} else {
				throw new SAWWorkflowException(String.format("File %s doesn't exist", file.getAbsolutePath()));
			}
			return Collections.singletonMap("f", file.getAbsolutePath());

		} catch (CoreException e) {
			throw new SAWWorkflowException(String.format("Error finding file %s in workspace", path), e);	
		}
				
	}

	@Override public List<String> getDefaultOutputNames() { return Collections.singletonList("f"); }
	@Override public List<String> getDefaultInputNames() { return Collections.singletonList("filename"); }
	@Override public List<String> getDefaultProperties() { return Arrays.asList("filename"); }



}
