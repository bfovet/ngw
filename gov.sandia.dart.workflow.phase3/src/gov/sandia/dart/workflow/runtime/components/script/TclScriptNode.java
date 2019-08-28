/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.script;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;

/**
 * 
 * Node for running Tcl scripts.
 *
 * Prepends variable definitions for ports and properties to the script code (specified via the "script" property).
 * 
 * @author mrglick
 *
 */

public class TclScriptNode extends AbstractExternalScriptNode {

	public TclScriptNode() throws IOException {}

	@Override
	protected
	PrintWriter initializeScript(File workDir, List<String> commandArgs, RuntimeData runtime) throws IOException {
		// TODO Need to offer Wish as an option.
		addInterpreterArguments("TCL", runtime, "tclsh", commandArgs );
		String scriptName = getFilenameRoot() + ".tcl";				
		File scriptFile = new File(workDir, scriptName);
		commandArgs.add(scriptName);
		PrintWriter fos = new PrintWriter(scriptFile);
		fos.println("######################################################################");
		fos.println("##### DO NOT EDIT - WILL BE OVERWRITTEN ON EACH SCRIPT EXECUTION #####");
		fos.println("######################################################################");
		fos.println();
		fos.println("# beginning of workflow node variable definitions for ports and properties");
		fos.println();
		return fos;
	}

	@Override
	protected
	void addInputPortToScript(PrintWriter scriptStream, String portName, String value) throws IOException {
		scriptStream.println("set " + portName + " \"" + value + "\"");		
	}

	@Override
	protected
	void addOutputPortToScript(PrintWriter scriptStream, String portName, String fileName) throws IOException {
		scriptStream.println("set fp [open \"" + portName + "\" w+]");
		scriptStream.println("puts $fp $" + portName);
		scriptStream.println("close $fp");
	}

	@Override
	protected
	void addPropertyToScript(PrintWriter scriptStream, String name, String value) throws IOException {
		scriptStream.println("set " + name + " \"" + value + "\"");		
	}

	@Override
	protected
	void finalizeScript(PrintWriter scriptStream) throws IOException {
		scriptStream.println("exit");
		scriptStream.close();
	}
	
	@Override
	protected
	void addScriptBody(PrintWriter scriptStream, String script) throws IOException {
		scriptStream.println();
		scriptStream.println(script);
	}
	
	@Override
	protected
	void addComment(PrintWriter scriptStream, String comment) throws IOException {
		scriptStream.print("# ");
		scriptStream.println(comment);
	}
	
	@Override
	protected String escapeString(String unescaped) {
		return unescaped.replaceAll("\"", "\\\\\"");
	}
}
