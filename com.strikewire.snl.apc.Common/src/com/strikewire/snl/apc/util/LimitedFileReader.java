/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.util;

import java.io.*;

public class LimitedFileReader extends InputStreamReader {
	private int limit;

	public LimitedFileReader(File file, int limit) throws FileNotFoundException {
		super(new FileInputStream(file));
		this.limit = limit;
	}

	@Override
	public int read() throws IOException {
		if (limit <= 0) {
			return -1;
		} else {			
			--limit;
			return super.read();
		}
	}

	@Override
	public int read(char[] cbuf, int offset, int length) throws IOException {
		if (limit <= 0) {
			return -1;
		} else {
			length = Math.min(length, limit);
			limit -= length;
			return super.read(cbuf, offset, length);
		}
	}

	@Override
	public boolean ready() throws IOException {
		if (limit <=0)
			return false;
		return super.ready();
	}
	
	
	
}
