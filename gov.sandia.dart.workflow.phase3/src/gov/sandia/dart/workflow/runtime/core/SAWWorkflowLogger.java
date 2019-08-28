/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;

public class SAWWorkflowLogger implements AutoCloseable {

	private PrintWriter log;
	private boolean isFile;
	
	public SAWWorkflowLogger(File file) throws IOException {
		log = new PrintWriter(new FileWriter(file), true);
		isFile = true;
	}
	
	public SAWWorkflowLogger() {
		log = new PrintWriter(System.out, true);
		isFile = false;
	}

	@Override
	public void close() {
		if (isFile && log != null)
			log.close();		
	}

	private void emitLine(String level, String format, Object... args) {
		try {
			StringBuilder builder = new StringBuilder(level).append(" ");
			builder.append(DateFormatUtils.format(new Date(), "MM/dd/yy HH:mm:ss,SSS")).append(" ");
			builder.append(MessageFormat.format(format, args));
			log.println(builder.toString());
		} catch (RuntimeException e) {
			error("Logging error", e);
		}
	}
	
	
	public void error(String message, Throwable t) {
		StringBuilder builder = new StringBuilder("ERROR").append(" ");		
		builder.append(DateFormatUtils.format(new Date(), "MM/dd/yy HH:mm:ss,SSS")).append(" ");
		builder.append(message);
		log.println(builder.toString());
		t.printStackTrace(log);
	}
	
	public void rawDebug(String message) {
		StringBuilder builder = new StringBuilder("DEBUG").append(" ");		
		builder.append(DateFormatUtils.format(new Date(), "MM/dd/yy HH:mm:ss,SSS")).append(" ");
		builder.append(message);
		log.println(builder.toString());
	}
	
	public void debug(String format, Object... args) {
		emitLine("DEBUG", format, args);
	}
	
	public void info(String format, Object... args) {
		emitLine("INFO ", format, args);		
	}

	public void trace(String format) {
		emitLine("TRACE", format);				
	}

	public void warn(String format, Object... args) {
		emitLine("WARN ", format, args);				
	}
}
