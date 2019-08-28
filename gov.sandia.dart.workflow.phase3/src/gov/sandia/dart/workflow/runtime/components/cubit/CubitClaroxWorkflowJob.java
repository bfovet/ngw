/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.cubit;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import gov.sandia.dart.workflow.runtime.components.Squirter;
import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

class CubitClaroxWorkflowJob {

	private static final int UNSET = 1234567890;
	private String cubitInput;
	private String pythonInput;
	private Thread t1;
	private Thread t2;
	private Process process;
	private File workingDir;
	private ByteArrayOutputStream output = new ByteArrayOutputStream();
	private PrintStream log = new PrintStream(output);
	private Map<String, String> envVars;

	public CubitClaroxWorkflowJob(String cubitInput, String pythonInput, File workingDir, Map<String, String> envVars) {
		this.cubitInput = cubitInput;
		this.pythonInput = pythonInput;
		this.workingDir = workingDir;
		this.envVars = envVars;
	}

	protected void canceling() {
		ProcessUtils.destroyProcess(process);
		if (t1 != null)
			t1.interrupt();
		if (t2 != null)
			t2.interrupt();
	}	
	
	protected boolean run(RuntimeData runtime) {
		ICancelationListener listener = null;

		try {
			String cubitPath = getCubitPath();
			// TODO I think we're using the built-in SAW CUBIT, so we should inherit the fixed LD_LIBRARY_PATH?
			ProcessBuilder builder = new ProcessBuilder();
			builder.command(cubitPath, "-nographics", "-nojournal");
			builder.directory(workingDir);
			Map<String, String> environment = builder.environment();
			environment.remove("LD_PRELOAD");
			environment.remove("DYLD_INSERT_LIBRARIES");
			environment.remove("DYLD_FORCE_FLAT_NAMESPACE");
			environment.putAll(envVars);
			process = builder.start();
			listener = () -> ProcessUtils.destroyProcess(process);
			runtime.addCancelationListener(listener);
			
			(t1  = new Thread(new Squirter(process.getInputStream(), log), "CUBIT stdout")).start();
			(t2 = new Thread(new Squirter(process.getErrorStream(), log), "CUBIT stderr")).start();
			
			PrintWriter writer = new PrintWriter(process.getOutputStream());
			sendCubitInput(writer, cubitInput);
			sendPythonInput(writer, pythonInput);						
			writer.println("quit()");
			writer.close();
			
			int exitStatus = UNSET;
			while (exitStatus == UNSET && !runtime.isCancelled()) {
				try {
					exitStatus = process.waitFor();
					break;
				} catch (InterruptedException ex) {
					// May be a spurious wakeup. Check for cancellation, and go check exit status again.
				}
			}
			t1.join(1000);
			t2.join(1000);

			// TODO Error detection
			if (exitStatus != 0) {
				return false;
			}

		} catch (Throwable t) {
			throw new SAWWorkflowException("Error while running Cubit", t);
		} finally {
			if (listener != null)
				runtime.removeCancelationListener(listener);
		}
		return true;
	}

	String getOutput() {
		return output.toString();
	}
	
	private void sendPythonInput(PrintWriter writer, String python) {
		if (python != null) {
			String[] lines = python.split("\\r?\\n");
			for (String line: lines) {
				line = line.trim();
				writer.println(line);
			}
		}
		writer.flush();
	}

	private void sendCubitInput(PrintWriter writer, String cubit) {
		if (cubit != null) {
			String[] lines = cubit.split("[\\r\\n]+");
			for (String line: lines) {
				line = StringEscapeUtils.escapeJava(line);
				writer.println(new StringBuilder("cubit.cmd(\"").append(line).append("\")"));
			}
		}
		writer.flush();
	}

	private String getCubitPath() {
		File location = getCubitBaseLocation();
		String clarox = getClaroxPath();
		return new File(location, clarox).getAbsolutePath();
	}

	private String getClaroxPath() {
		
		if (isMac()) 				
			return "Cubit";
		// This one is meant to be launched this way
		else if (isWindows())
			return "claroxcon.exe";
		else if (isLinux())
			return "clarox";
		else
			return "clarox";
	}

	private File getCubitBaseLocation() {
		String path = System.getenv("CUBIT_BASE_PATH");
		if (path != null)
			return new File(path);
		throw new RuntimeException("Configuration error: CUBIT_BASE_PATH not defined");
	}


	private boolean isMac() {
		return System.getProperty("os.name").toLowerCase().startsWith("mac os");
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().startsWith("windows");
	}
	
	private boolean isLinux() {
		return System.getProperty("os.name").toLowerCase().startsWith("linux");
	}
	
}
