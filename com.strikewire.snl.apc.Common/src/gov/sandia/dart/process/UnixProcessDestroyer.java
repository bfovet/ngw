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
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.osgi.framework.Bundle;

import com.strikewire.snl.apc.Common.CommonPlugin;

class UnixProcessDestroyer implements ProcessDestroyer {

	@Override
	public void destroy(Process p) {
		// Check for UNIXProcess
		int pid = pid(p);		
		if (pid != -1) {
			try {
				Bundle b = CommonPlugin.getDefault().getBundle();
				URL fileURL = FileLocator.find(b, new Path("scripts/killer"), null);
				fileURL = FileLocator.toFileURL(fileURL);
				String script = fileURL.getFile();
				ProcessBuilder builder = new ProcessBuilder("/bin/ksh", script, String.valueOf(pid));
				Process pp = builder.start();
				new Thread(new Squirter(pp.getInputStream(), System.out), "Process Destroyer stdout").start();
				new Thread(new Squirter(pp.getErrorStream(), System.out), "Process Destroyer stdin").start();
				pp.waitFor();
			} catch (Exception e) {
				// FALL THROUGH
				e.printStackTrace();
			}
		}
		// For good measure
		p.destroy();		
	}
	
	private int pid(Process process) {
		Class<?> clazz = process.getClass();
		try {
			if (clazz.getName().equals("java.lang.UNIXProcess")) {
				Field pidField = clazz.getDeclaredField("pid");
				pidField.setAccessible(true);
				Object value = pidField.get(process);
				if (value instanceof Integer) {
					return ((Integer) value).intValue();
				}
			}
		} catch (Exception sx) {
			// FALL THROUGH
		}
		return -1;
	}

}
