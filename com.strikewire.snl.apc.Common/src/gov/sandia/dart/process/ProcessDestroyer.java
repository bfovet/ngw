/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.process;

import gov.sandia.dart.common.core.env.OS;

public interface ProcessDestroyer {
	public static ProcessDestroyer get() {
		if (OS.isLinux() || OS.isMac()) {
			return new UnixProcessDestroyer();	
		} else if (OS.isWindows()) {
			return new Win32JNAProcessDestroyer();
		} else {
			return new DefaultProcessDestroyer();
		}
	}
	
	public abstract void destroy(Process p);
	
}
