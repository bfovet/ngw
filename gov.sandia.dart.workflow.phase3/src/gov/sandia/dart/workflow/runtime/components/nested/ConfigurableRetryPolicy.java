package gov.sandia.dart.workflow.runtime.components.nested;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class ConfigurableRetryPolicy implements RetryPolicy {
	
	public static final String CONFIG_FILE_NAME = "retry.conf";
	private static final int MAX_RETRIES = 5;

	private WorkflowProcess process;
	private List<Probe> configuration;
	private int attempts = 0;

	public ConfigurableRetryPolicy(File configFile, WorkflowProcess process) {
		this.process = process;
		try {
			configure(configFile);
		} catch (Exception e) {
			throw new SAWWorkflowException("Error while reading retry configuration file", e);
		}
	}

	private void configure(File configFile) throws IOException {
		List<String> lines = FileUtils.readLines(configFile, Charset.defaultCharset());
		configuration = new ArrayList<>();
		for (String line: lines) { 
			if (line.startsWith("#"))
				continue;
			String[] tokens = line.split(",");
			if (tokens.length < 4) {
				// TODO Log this somewhere?
				continue;
			}

			File workDir = process.getWorkDir();
			File file = new File(workDir, tokens[0].trim());
			String pattern = tokens[1].trim();
			int delay = Integer.parseInt(tokens[2].trim());
			Probe.Action action = Probe.Action.valueOf(tokens[3].trim());
			Probe probe = new Probe(file, pattern, delay, action);			
			configuration.add(probe);
		}		
	}


	@Override
	public boolean execute(SAWWorkflowLogger log) {
		log.info("{0} probes in retry configuration", configuration.size());
		if (++attempts < MAX_RETRIES) {
			boolean threw = false;
			try {
				// log.info("About to run subworkflow, attempt {0}", attempts);
				process.run();
				// log.info("Subworkflow ran OK.");

			} catch (Exception ex) {
				// log.info("Subworkflow threw an exception.");
				threw = true;
			}	
			RetryPolicy.Result result = checkForResults(log);
			if (result == null)
				result = threw ? Result.RETRY : Result.SUCCESS;
			
			switch (result) {
			case FAIL:
				// log.info("Aborting as per retry strategy");
				throw new SAWWorkflowException("Execution failed.");
			case RETRY:
				// log.info("Retrying as per retry strategy");
				return true;
			case SUCCESS:
				// log.info("Succeeded as per retry strategy");
				return false;
			case REPORT:
				throw new SAWWorkflowException("Internal error, strategy returned REPORT");
			}			
		}
		log.info("Too many retries, aborting...");
		throw new SAWWorkflowException("Execution failed.");
	}

	private Result checkForResults(SAWWorkflowLogger log) {		
		try {
			for (Probe probe: configuration) {				

				List<String> paths = new ArrayList<>();
				if (SAWCustomNode.isGlobPattern(probe.file.getPath())) {
					String name = probe.file.getName();
					File parent = probe.file.getParentFile();
					if (parent != null && parent.exists()) {
						SAWCustomNode.glob(name, parent, paths);
					}
				} else {
					paths.add(probe.file.getAbsolutePath());
				}
				for (String path: paths) {
					File file = new File(path);
					if (file.exists())  {
						boolean found = findProbeInFile(log, probe.pattern, file);
					
						switch (probe.action) {
						case FAIL:
							if (found) {
								logFound(log, probe, file);
								return Result.FAIL;
							}
							break;
						case FAIL_IF_NOT:
							if (!found) {
								logNotFound(log, probe, file);
								return Result.FAIL;
							}
							break;
						case REPORT:
							if (found) {
								logFound(log, probe, file);
							}
							break;
						case REPORT_IF_NOT:
							if (!found) {
								logNotFound(log, probe, file);
							}
							break;
						case RETRY:
							if (found) {
								logFound(log, probe, file);
								return Result.RETRY;
							}
							break;
						case RETRY_IF_NOT:
							if (!found) {
								logNotFound(log, probe, file);
								return Result.RETRY;
							}
							break;
						case SUCCESS:
							if (found) {
								logFound(log, probe, file);
								return Result.SUCCESS;
							}
							break;						

						} 
					}
				}
			}
		} catch (Exception e) {
			throw new SAWWorkflowException("Internal error testing results", e);
		}

		return null;
	}

	private void logNotFound(SAWWorkflowLogger log, Probe probe, File file) {
		log.info("FF {0}: ''{1}'' not found in {2}", probe.action, probe.pattern, file.getName());
	}

	private void logFound(SAWWorkflowLogger log, Probe probe, File file) {
		log.info("FF {0}: ''{1}'' found in {2}", probe.action, probe.pattern, file.getName());
	}

	private boolean findProbeInFile(SAWWorkflowLogger log, String pattern, File file) throws IOException {
		try (LineIterator iterator = FileUtils.lineIterator(file)) {
			while (iterator.hasNext()) {
				if (iterator.next().indexOf(pattern) > -1) {
					return true;
				} 
			}
		}
		return false;
	}

}
