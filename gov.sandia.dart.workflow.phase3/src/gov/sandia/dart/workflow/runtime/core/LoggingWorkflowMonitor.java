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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is supposed to take care of logging workflow status to a database,
 * so we can check the database from a client. Right now this stub implementation just logs.
 */

public class LoggingWorkflowMonitor implements IWorkflowMonitor, AutoCloseable{
	public static final String DEFAULT_NAME = "workflow.status.log";
	private PrintWriter  statusLog;

	public LoggingWorkflowMonitor(File logFile) throws IOException {
		statusLog = new PrintWriter(new FileWriter(logFile, false), true);
	}
	
	@Override
	public synchronized void breakpointHit(SAWCustomNode node, RuntimeData runtime) {
		runtime.log().info("Sample {0}, Hit breakpoint at node {1}", runtime.getSampleId(), node.getName());
		statusLog.println("BREAK: " + node.getName());
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
		statusLog.println("STOP");
		statusLog.close();
	}

	public synchronized void logMessage(String message) {
		statusLog.println("MESSAGE: " + message);
	}

	@Override
	public void terminated(RuntimeData runtime, Throwable t) {
		runtime.log().info("Sample {0}, Terminated: {1}", runtime.getSampleId(), t.getMessage());
		terminated(t);
	}
	
	public void terminated(Throwable t) {
		statusLog.println("ABORT: Workflow terminated.");
	}

	
	@Override
	public void status(SAWCustomNode node, RuntimeData runtime, String status) {
		runtime.log().info("Sample {0}, Status for node  {1}: {2}", runtime.getSampleId(), node.getName(), String.valueOf(status));
		statusLog.println("STATUS: " + node.getName() + " . " + status);
	}

	@Override
	public void close(RuntimeData runtime) throws Exception {
		close();
	}

	@Override
	public void workflowStarted(RuntimeData runtime, Collection<String> startNodes) {
		statusLog.println("START: " + StringUtils.join(startNodes, ","));
		
	}
}
