/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is supposed to take care of logging workflow status to a database,
 * so we can check the database from a client. Right now this stub implementation just logs.
 */

public class LoggingWorkflowMonitor implements IWorkflowMonitor, AutoCloseable{
	private PrintWriter  statusLog;

	public LoggingWorkflowMonitor(File logFile) throws IOException {
		statusLog = new PrintWriter(new FileWriter(logFile, false), true);
	}
	
	@Override
	public synchronized void enterNode(SAWCustomNode node, RuntimeData runtime) {
		runtime.log().info("Sample {0}, Entering node {1}", runtime.getSampleId(), node.getName());
		statusLog.println("ENTER: " + node.getName());
	} 
	
	@Override
	public synchronized void exitNode(SAWCustomNode  node, RuntimeData runtime) {
		runtime.log().info("Sample {0}, Exiting node {1}", runtime.getSampleId(), node.getName());
		statusLog.println("EXIT: " + node.getName());
	}
	
	@Override
	public synchronized void abortNode(SAWCustomNode  node, RuntimeData runtime, Throwable t) {
		runtime.log().info("Sample {0}, Aborting node {1}: {2}", runtime.getSampleId(), node.getName(), t.getMessage());
		statusLog.println("ABORT: " + node.getName());
	}
	
	@Override
	public synchronized void close() {
		statusLog.close();
	}
}
