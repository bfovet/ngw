/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import java.util.Collection;

/**
 * This class is supposed to take care of logging workflow status to a database,
 * so we can check the database from a client. 
 */
public interface IWorkflowMonitor  {	
	void workflowStarted(RuntimeData runtime, Collection<String> startNodes);
	void breakpointHit(SAWCustomNode node, RuntimeData runtime);
	void enterNode(SAWCustomNode node, RuntimeData runtime); 
	void exitNode(SAWCustomNode node, RuntimeData runtime);
	void abortNode(SAWCustomNode node, RuntimeData runtime, Throwable t);
	void terminated(RuntimeData runtime, Throwable t);
	void status(SAWCustomNode node, RuntimeData runtime, String status);
	void close(RuntimeData runtime) throws Exception;
}
