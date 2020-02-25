/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.monitoring;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
public interface IWorkflowListener {
	void breakpointHit(String name, IFile workflow, File workDir);
	void nodeEntered(String name, IFile workflow, File workDir);
	void nodeExited(String name, IFile workflow, File workDir);
	void nodeAborted(String name, IFile workflow, File workDir, Throwable t);
	void workflowStarted(IFile workflow, File workDir, Collection<String> startNodes);
	void workflowStopped(IFile workflow, File workDir);
	void status(String name, IFile workflow, File workDir, String status);
}
