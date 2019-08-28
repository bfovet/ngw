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
