package gov.sandia.dart.workflow.runtime;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import org.apache.commons.io.FileUtils;

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;

class NGWReaper {
	NGWReaper(int timeout, File location, SAWWorkflowLogger logger) {
		Thread t = new Thread(() ->  {
			try { Thread.sleep(timeout); } catch (InterruptedException ex) { return; }
			ThreadInfo[]  threadInfos = ManagementFactory.getThreadMXBean()
					.dumpAllThreads(true,
							true);
			logger.warn("Workflow engine did not exit, see " + location.getName());

			StringBuilder dump = new StringBuilder("*** WORKFLOW ENGINE DID NOT EXIT\n");
			for (ThreadInfo threadInfo : threadInfos) {
				dump.append(threadInfo);
			}
			try {
				FileUtils.write(location, dump.toString());
			} catch (IOException ex) {
				logger.error("Can't write stack trace", ex);
			}
			System.exit(0);
		});
		t.setDaemon(true);
		t.start();
	}
}
