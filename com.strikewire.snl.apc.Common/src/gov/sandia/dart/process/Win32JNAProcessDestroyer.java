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

import java.lang.reflect.Field;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinNT;

public class Win32JNAProcessDestroyer implements ProcessDestroyer {

	@Override
	public void destroy(Process p) {
		int pid = getPid(p);
		if (pid != -1) {
	        try {
				Process pp = Runtime.getRuntime().exec(String.format("taskkill /pid %d /t /f", pid));
				new Thread(new Squirter(pp.getInputStream(), System.out), "Process Destroyer stdout").start();
				new Thread(new Squirter(pp.getErrorStream(), System.out), "Process Destroyer stdin").start();
				pp.waitFor();
			} catch (Exception e) {
				CommonPlugin.getDefault().logError("Failed to kill process", e);
			}	
		}
		// Failsafe
		p.destroyForcibly();
	}
	
	private static int getPid(Process p) {
		Field f;

		try {
			f = p.getClass().getDeclaredField("handle");
			f.setAccessible(true);
			long handle = (Long) f.get(p);
			if (handle != -1) {
				// TODO Hand-wrap this method so we don't need the whole JNA platform library!
				int pid = Kernel32.INSTANCE.GetProcessId(new WinNT.HANDLE(new Pointer(handle)));
				return pid;
			}
		} catch (Exception ex) {
			CommonPlugin.getDefault().logError("Failed to get process id", ex);
		}

		return -1;
	}
}
