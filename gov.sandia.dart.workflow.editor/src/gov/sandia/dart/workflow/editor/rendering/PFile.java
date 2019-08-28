/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.rendering;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Vastly simplified version of java.util.Properties that preserves key order, allows
 * only "=" separators and "#" comments, and doesn't do any escaping of file contents.
  */

public class PFile {

	private Map<String, String> contents = new LinkedHashMap<>();
	public PFile(Reader reader) throws IOException {
		try {
			for (String line: IOUtils.readLines(reader)) {

				if (line.startsWith("#"))
					continue;
				else if (StringUtils.isBlank(line))
					continue;
				int index = line.indexOf('=');
				if (index < 1 || index > line.length() - 2)
					throw new IOException("Bad line: " + line);
				String key = line.substring(0, index);
				String value = line.substring(index + 1);
				contents.put(key, value);
			}
		} finally { 
			reader.close();
		}
	}

	public Set<String> keys() {
		return contents.keySet();
	}

	public String get(String key) {
		return contents.get(key);
	}

	public Map<String, String> map() {
		return Collections.unmodifiableMap(contents);
	}

}
