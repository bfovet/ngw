/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.util;

import java.io.PrintWriter;
import java.io.Writer;

public class Indenter {
	public static final String SPC = "                                                                                                                                                                                  ";
	private int indent = 0;
	private PrintWriter writer;

	public Indenter(Writer w) {
		writer = new PrintWriter(w);
	}
	
	public void close() {
		if (writer != null)
			writer.close();
	}
	public void printAndIndent(String string) {
		writer.print(SPC.substring(0, indent*2));
		writer.println(string);
		++ indent;
	}
	
	public void unindentAndPrint(String string) {
		-- indent;
		writer.print(SPC.substring(0, indent*2));
		writer.println(string);
	}
	
	public void unindent() {
		-- indent;
	}
	
	public void printIndented(String string) {
		writer.print(SPC.substring(0, indent*2));
		writer.println(string);
	}

	public PrintWriter getPrintWriter() {
		return writer;
	}

	public String asTag(String text) {
		return String.format("<%s>", text);
	}
	public String asEndTag(String text) {
		return String.format("</%s>", text);
	}

	// TODO Need XML escaping here!
	public void printIndentedAsElement(String tag, String content) {
		printIndented(String.format("<%s>%s</%s>", tag, content, tag));
	}
}
