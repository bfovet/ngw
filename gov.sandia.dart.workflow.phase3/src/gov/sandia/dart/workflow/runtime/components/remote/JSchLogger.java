/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.remote;

import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.Logger;

public class JSchLogger implements Logger {
    private static Map<Integer, String> LABELS = new HashMap<>();
    static {
        LABELS.put(new Integer(DEBUG), "DEBUG: ");
        LABELS.put(new Integer(INFO), "INFO: ");
        LABELS.put(new Integer(WARN), "WARN: ");
        LABELS.put(new Integer(ERROR), "ERROR: ");
        LABELS.put(new Integer(FATAL), "FATAL: ");
    }
    
    private StringBuilder builder = new StringBuilder();
	private int logLevel;
   
    public JSchLogger(int level) {
    	logLevel = level;
    }
    
    @Override
	public boolean isEnabled(int level){
        return level >= logLevel;
    }
    @Override
	public void log(int level, String message){
        builder.append(LABELS.get(new Integer(level)));
        builder.append(message).append("\n");       
    }
    
    @Override
    public String toString() {
    	return builder.toString();
    }
    
    public void reset() {
    	builder.setLength(0);
    }
}
