/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.aprepro;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.components.Squirter;
import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

public class ApreproUtil {
	private static final int UNSET = 1234567890;

	static File createApreproParamsFile(File file, String commentChar, Map<String, String> properties) {
		try (PrintWriter pw = new PrintWriter(file)) {
			for (String prop: properties.keySet()) {
				pw.print(commentChar);
				pw.println(constructApreproString(prop, properties.get(prop)));
			}
			return file;
		} catch (Exception e) {
			throw new SAWWorkflowException("Aprepro: problem writing parameters file", e);
		}
	}
	
	static String constructApreproString(String name, String value) {
		if (!isNumeric(value))
			value = new StringBuilder("'").append(value).append("'").toString();
			
		return new StringBuilder("{").append(name).append(" = ").append(value).append("}").toString();	
	}
	
	private static boolean isNumeric(String num) {
		try {
			Double.parseDouble(num);
			return true;
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}
	}

	static int doTransform(File paramsFile, File definitionFile, File outputFile, File workingDir, String commentChar, RuntimeData runtime) throws IOException, InterruptedException {

		ProcessBuilder builder = ProcessUtils.createProcess(runtime).directory(workingDir);
		Map<String, String> environment = builder.environment();

		List<String> commands = new ArrayList<>();	
		String apreproCommand = environment.get("APREPRO_PATH");
		if (apreproCommand == null)
			apreproCommand = "aprepro";
		commands.add(apreproCommand);
		
		// Don't emit header
		commands.add("-q");
		
		// comment character
		String commentCmd = "-c" + commentChar;
		commands.add(commentCmd);
		
		// immutable flag
		commands.add("--immutable");
		
		// params file
		commands.add("--include");
		commands.add(paramsFile.getAbsolutePath());
		
		commands.add(definitionFile.getAbsolutePath());
		commands.add(outputFile.getAbsolutePath());
		
		builder.command(commands);
		Process process = builder.start();
		ICancelationListener listener = () -> ProcessUtils.destroyProcess(process);
		runtime.addCancelationListener(listener);
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			PrintStream log = new PrintStream(out);
			Thread errGobbler = new Thread(new Squirter(process.getErrorStream(), log));	
			Thread outGobbler = new Thread(new Squirter(process.getInputStream(), log));
			errGobbler.start();
			outGobbler.start();
			int exitStatus = UNSET;
			while (exitStatus == UNSET && !runtime.isCancelled()) {
				try {
					exitStatus = process.waitFor();
					break;
				} catch (InterruptedException ex) {
					// May be a spurious wakeup. Check for cancellation, and go check exit status again.
				}
			}
			errGobbler.join(1000);
			outGobbler.join(1000);
			File logFile = new File(workingDir, "aprepro.log");
			log.flush();		
			FileUtils.write(logFile, out.toString());
			return exitStatus;
		} finally {
			runtime.removeCancelationListener(listener);
		}
	}

}
