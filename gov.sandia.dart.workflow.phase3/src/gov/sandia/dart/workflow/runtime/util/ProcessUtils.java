/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;

public class ProcessUtils {
	private volatile static Destroyer destroyer = new Destroyer() {
		@Override
		public void destroy(Process process) {
			if (process != null) {
				try {
					process.destroy();
					if (!process.waitFor(2, TimeUnit.SECONDS))
						process.destroyForcibly();
				} catch (InterruptedException e) {
					process.destroyForcibly();
				}
			}
		}
	};

	/**
	 * Creates a ProcessBuilder whose environment has been scrubbed of nontrivial SAW contamination.
	 * Using this ensures that your launched process will work from an embedded workflow the same way it works
	 * from the command line. You should almost certainly use this any time you want to launch an external process.
	 * 
	 * Environment variables from RuntimeData.getenv() override any in the original environment.
	 * @param runtime TODO
	 */
	public static ProcessBuilder createProcess(RuntimeData runtime) {
		ProcessBuilder builder = new ProcessBuilder();
		Map<String, String> environment = builder.environment();
		
		if (isEmbeddedRun()) {
			environment.remove("LD_PRELOAD");
			environment.remove("DYLD_INSERT_LIBRARIES");
			environment.remove("DYLD_FORCE_FLAT_NAMESPACE");
			environment.remove("LD_LIBRARY_PATH");
			environment.remove("DYLD_LIBRARY_PATH");


			Map<String, String> replacements = new HashMap<>();
			for (Map.Entry<String, String> entry: environment.entrySet()) {
				if (entry.getKey().startsWith("SAW_PSV_")) {
					replacements.put(entry.getKey().substring("SAW_PSV_".length()), entry.getValue());				
				}
			}

			environment.putAll(replacements);
		}
		environment.putAll(runtime.getenv());
		return builder;
	}
	
	public static boolean isEmbeddedRun() {		
		return System.getProperties().containsKey("eclipse.launcher");
	}

	/**
	 * For now, this is only going to work on Linux. In Java 9, there is a method to call.
	 */
	public static int getpid() {
		File file = new File("/proc/self");
		if (file.exists()) {
			try {
				return Integer.parseInt(file.getCanonicalFile().getName());
			} catch (NumberFormatException | IOException e) {
				// FALL THROUGH
			}
		}
	    return -1;	
	}
	
	public static void destroyProcess(Process process) {
		destroyer.destroy(process);
	}
	
	public static String gethost() {
		try {
			return InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException ioe) {
			return "localhost";
		}
	}
	
	public interface Destroyer {
		void destroy(Process p);
	}
	
	public static void setDestroyer(Destroyer d) {
		if (destroyer != null)
			destroyer = d;
	}
	
}
