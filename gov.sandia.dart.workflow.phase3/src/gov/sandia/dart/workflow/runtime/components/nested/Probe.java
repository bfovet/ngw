/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.nested;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

class Probe {
	enum Action { SUCCESS, RETRY, RETRY_IF_NOT, FAIL, FAIL_IF_NOT, REPORT, REPORT_IF_NOT }
	
	File file;
	String pattern;
	int delay;
	Action action;

	Probe(File file, String pattern, int delay, Action action) {
		this.file = file;
		this.pattern = pattern;
		this.delay = delay;
		this.action = action;
	}

	@Override
	public String toString() {
		return "Probe [file=" + StringUtils.abbreviateMiddle(file.getPath(), "...", 30) + ", pattern=" + pattern + ", delay=" + delay + ", action=" + action + "]";
	}
}
