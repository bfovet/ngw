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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * This Class is a copy of methods from org.apache.commons.lang.StringEscapeUtils
 * modified to escape special shell characters in addition to java ones.
 * Additional characters may need to be added to those that need to be escaped
 *
 */
public class ShellStringEscapeUtils{
/**
 * _log -- A Logger instance for ShellStringEscapeUtils
 */
private static final Logger _log =
    LogManager.getLogger(ShellStringEscapeUtils.class);




    /**
     * This function escapes special characters in a given string
     * Note: This class does not respect strong quotes, nor does it perform parsing of any kind.
     * @param str A target string containing characters to be escaped
     * @return A string containing the result of applying the escape operation to the input string
     */
	public static String escape(String str) {
		try {
			return escapeString(str, false);
		} catch (IOException e) {
			CommonPlugin.getDefault().logError(e);
			return "";
		}
	}

	private static String escapeString(String str, boolean escapeSingleQuote) throws IOException {
		Writer out = new StringWriter();
		if (str == null) {
			return "";
		}
		int sz;
		sz = str.length();
		for (int i = 0; i < sz; i++) {
			char ch = str.charAt(i);
			// handle unicode
			if (ch > 0xfff) {
				out.write("\\u" + hex(ch));
			} else if (ch > 0xff) {
				out.write("\\u0" + hex(ch));
			} else if (ch > 0x7f) {
				out.write("\\u00" + hex(ch));
			} else if (ch < 32) {
				switch (ch) {
				case '\b':
					out.write('\\');
					out.write('b');
					break;
				case '\n':
					out.write('\\');
					out.write('n');
					break;
				case '\t':
					out.write('\\');
					out.write('t');
					break;
				case '\f':
					out.write('\\');
					out.write('f');
					break;
				case '\r':
					out.write('\\');
					out.write('r');
					break;
				default :
					if (ch > 0xf) {
						out.write("\\u00" + hex(ch));
					} else {
						out.write("\\u000" + hex(ch));
					}
					break;
				}
			} else {
				switch (ch) {
				case '\'':
					if (escapeSingleQuote) {
						out.write('\\');
					}
					out.write('\'');
					break;
				case '"':
					out.write('\\');
					out.write('"');
					break;
				case '\\':
					out.write('\\');
					out.write('\\');
					break;
				case '$':
					out.write('\\');
					out.write('$');
					break;
				default :
					out.write(ch);
					break;
				}
			}
		}
		
		final String product = out.toString();
		_log.trace("Command is now: {}", product);
		return product;
	}

	
	 private static String hex(char ch) {
		     return Integer.toHexString(ch).toUpperCase();
		 }

}
