/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.monitoring;

/**
 * Incredibly crude. Just an early prototype.
 * @author ejfried
 */

public interface IWorkflowListener {
	void nodeEntered(String name, String workflow);
	void nodeExited(String name, String workflow);
	void nodeAborted(String name, String workflow, Throwable t);
	void workflowStarted(String workflow);
	void workflowStopped(String workflow);
}
