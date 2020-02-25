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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Used to read runtime exec output in a separate thread. StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), ConsoleDisplayMgr.MSG_ERROR);
 * StreamGobbler inputGobbler = new StreamGobbler(proc.getInputStream(), ConsoleDisplayMgr.MSG_INFORMATION); errorGobbler.start(); inputGobbler.start(); try {
 * final int outputCode = proc.waitFor(); while (inputGobbler.isAlive()) { //wait for the stream to finish processing Display.getDefault().asyncExec(new
 * Runnable() { public void run() { ConsoleDisplayMgr.getDefault().println( "Process has ended, but waiting to finish capturing all output",
 * ConsoleDisplayMgr.MSG_INFORMATION); } }); Thread.sleep(500); }
 *
 * @author echan
 */
public class StreamGobbler extends Thread {
	InputStream is;
	StringBuffer str = new StringBuffer();
	int type = -1;
	IStreamGobblerCallback callback;

	String buffer = "";

	/**
	 * @param is
	 * @param type
	 *            like ConsoleDisplayMgr.MSG_INFORMATION, but -1 if do not want to write out
	 */
	public StreamGobbler(final InputStream is, final int type) {
		this(is, type, null);
	}

	/**
	 * @param is
	 * @param type
	 *            like ConsoleDisplayMgr.MSG_INFORMATION, but -1 if do not want to write out
	 * @param _callback
	 *            when a line is read, run the callback method
	 */
	public StreamGobbler(final InputStream is, final int type, IStreamGobblerCallback _callback) {
		this.is = is;
		this.type = type;
		this.setName("Stream Gobbler");
		this.callback = _callback;
		buffer = "";

	}

	@Override
	public void run() {
		try {
			final InputStreamReader isr = new InputStreamReader(is);
			final BufferedReader br = new BufferedReader(isr);
			String line = null;

			while ((line = br.readLine()) != null) {

				str.append(line).append('\n');
				final String line2 = line;

				buffer += line + '\n';

				if (callback != null) {
					callback.run(line2); // run callback
				}

//				Display.getDefault().asyncExec(new Runnable() {
//					public void run() {
//						if (type == 1) { // information
//							ConsoleDisplayMgr.getDefault().println(/* "stdout: " + */line2, type);
//						} else if (type == 2) { // error
//							ConsoleDisplayMgr.getDefault().println(/* "stderr: " + */line2, type);
//						} else {
//							// do not print if no type
//							// ConsoleDisplayMgr.getDefault().println("std???: " + line2, type);
//						}
//
//					}
//				});
			}
			br.close();
			isr.close();
		} catch (final IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Get output string captured from this Stream Gobbler!
	 */
	@Override
	public String toString() {
		return str.toString();
	}

	/**
	 * Convenience method to clear console
	 */
	public void clearConsole() {

//		Display.getDefault().asyncExec(new Runnable() {
//			public void run() {
//				ConsoleDisplayMgr.getDefault().clear();
//			}
//		});
	}

}
