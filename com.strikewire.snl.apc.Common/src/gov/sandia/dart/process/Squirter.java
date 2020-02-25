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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

class Squirter implements Runnable {
	private InputStream s;
	private PrintStream out;
	Squirter(InputStream s, PrintStream out) {
		this.s = s;
		this.out = out;
	}
	@Override
	public void run() {
		int c;
		try {
			while (!Thread.interrupted() && (c = s.read()) != -1) {
				out.print((char) c);
				out.flush();
			}
		} catch (IOException e) {
			// Ignore
		}
	}
}
