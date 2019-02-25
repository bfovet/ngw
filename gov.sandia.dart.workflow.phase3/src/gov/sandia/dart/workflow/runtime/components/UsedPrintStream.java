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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

class UsedPrintStream extends java.io.PrintStream {

	private static class UsedFileOutputStream extends FileOutputStream {
		private volatile boolean used;
		public UsedFileOutputStream(File arg0) throws FileNotFoundException {
			super(arg0);
		}
		
		public boolean isUsed() {
			return used;
		}
		
		@Override
		public void write(byte[] b) throws IOException {
			super.write(b);
			used = true;
		}
		
		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			super.write(b, off, len);
			used = true;
		}
		
		@Override
		public void write(int b) throws IOException {
			super.write(b);
			used = true;
		}
	}
	
	public UsedPrintStream(File file) throws FileNotFoundException {
		super(new UsedFileOutputStream(file), true);
	}

	boolean isUsed() {
		return ((UsedFileOutputStream) out).isUsed();
	}
}
