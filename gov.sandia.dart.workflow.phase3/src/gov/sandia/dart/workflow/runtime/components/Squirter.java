/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class Squirter implements Runnable {
	private static int BUFSIZ = 512; // or, like whatever
	private InputStream s;
	private PrintStream out;

	public Squirter(InputStream s, PrintStream out) {
		this.s = s;
		this.out = out;
	}

	private int bytesToRead() throws IOException {
		int nReady = s.available();
		if (nReady <= 1)
			return 1;
		if (nReady > BUFSIZ)
			return BUFSIZ;
		return nReady;
	}
	
	@Override
	public void run() {
		int c;
		byte[] buffer = new byte[BUFSIZ];
		try {
			while (!Thread.interrupted() && (c = s.read(buffer, 0, bytesToRead())) != -1) {
				//out.print((char) c);
				out.write(buffer, 0, c);
				out.flush();
			}
		} catch (IOException e) {
			// Ignore
		}
	}
}
