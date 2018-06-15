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
