/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.remote.SessionManager;
import gov.sandia.dart.workflow.runtime.core.LoggingWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.NodeDatabase;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;
import gov.sandia.dart.workflow.runtime.dakota.DakotaParamsFile;
import gov.sandia.dart.workflow.runtime.dakota.DakotaResultsFile;
import gov.sandia.dart.workflow.runtime.util.GetOpt;
import gov.sandia.dart.workflow.runtime.util.GetOptException;

public class Main {
	
	private static final int REAPER_DELAY = 10000;

	/**
	 * @param args
	 * @throws Exception
	 */
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Throwable {
		
		File logFile = new File(LoggingWorkflowMonitor.DEFAULT_NAME);
		try (LoggingWorkflowMonitor monitor = new LoggingWorkflowMonitor(logFile)){

			GetOpt options = new GetOpt(args, "kv", "sdhxp");
			String[] params = options.params();

			boolean dakotaMode = options.hasOption('k');
			boolean validateUndefined = options.hasOption('v');

			if (options.hasOption("p")) {
				for (String plugin: options.getOptions("p")) {
					NodeDatabase.addPlugin(plugin);
				}
			}
			
			
			if (options.hasOption("d")) {
				SAWWorkflowLogger logger = new SAWWorkflowLogger();
				NodeDatabase.loadDefinitions(logger);
				NodeDatabase.dump(options.getOption("d"));
				logFile.delete();
				System.exit(0);

			}					
			
			if (params.length < 1) {
				throw new GetOptException("Missing workflow file name");
			}
			
			if (dakotaMode && params.length < 3) {
				throw new GetOptException("Missing dakota params/results file names");
			}
			
			String workflowFilePath = options.params()[0];
			if (!workflowFilePath.toLowerCase().endsWith(".iwf")) {
				throw new GetOptException("Supplied argument must be *.iwf file: " + workflowFilePath);
			}
			File workflowFile = new File(workflowFilePath);
			if (!workflowFile.exists()) {
				throw new GetOptException("Workflow definition file does not exist: " + workflowFilePath);
			}
			
			
			if (options.hasOption("s")) {
				try {
					int delay = Integer.parseInt(options.getOption('s'));
					SAWCustomNode.setDelay(delay);
				} catch (NumberFormatException e) {
					throw new GetOptException("Invalid argument for -s");
				}
			}

			String startNode = null;
			if (options.hasOption("x")) {
				startNode = options.getOption('x');
			}
			
			File homeDir;
			if (options.hasOption('h'))
				homeDir = new File(options.getOption('h'));
			else
				homeDir = workflowFile.getCanonicalFile().getParentFile();
			
			File workDir = new File("").getCanonicalFile();
			
			WorkflowProcess process = new WorkflowProcess().
					addMonitor(monitor).
					setWorkflowFile(workflowFile).
					setHomeDir(homeDir).
					setWorkDir(workDir).
					setStartNode(startNode).
					setValidateUndefined(validateUndefined);
			
			// TODO Could we do some preflight here -- parse workflow file?

			if (dakotaMode) {
				SAWWorkflowLogger log = process.getLogger();
				String evalId = readDakotaParameters(options.params()[1], process, log);
				if (StringUtils.isNotEmpty(evalId))
					process.setSampleId(evalId);
			} else if (params.length > 1) {
				for (int i = 1; i < params.length; i++) {
					String[] keyAndValue = params[i].split("=", 2);
					if (keyAndValue.length < 2) {
						throw new Exception("spurious argument \"" + params[i] + "\"");
					}
					process.setParameter(keyAndValue[0], keyAndValue[1]);
				}
			}

			Map<String, Object> responses = null;
			try {
				process.run();
				responses = process.getRuntime().getResponses();
			} finally {
				Throwable deferred = null;
				if (dakotaMode) { 
					try {
						SAWWorkflowLogger log = process.getLogger();
						writeDakotaResults(options.params()[1], options.params()[2], process.getSampleId(), responses, log);
					} catch (Throwable t) {
						deferred = t;
					}
				}
				// TODO implement shutdown hook system
				SAWWorkflowLogger log = process.getLogger();

				new NGWReaper(REAPER_DELAY, new File(workDir, "stacktrace.log"), log);
				SessionManager.INSTANCE.shutdown();
				
				if (deferred != null)
					throw deferred;
			}
								
		} catch (Throwable e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Args: " + Arrays.asList(args));
			System.out.println("CWD: " + new File(".").getAbsolutePath());
			System.out.println("Usage: java Main [-d <dump-file>] [-s <delayInMs>] [-x <start node>] <workflow-file> [<parameter>=<value> ...]");
			throw e;
		}
	}

	private static String readDakotaParameters(String file, WorkflowProcess process, SAWWorkflowLogger log) throws IOException {
		DakotaParamsFile paramsFile = new DakotaParamsFile(file);
		for (String name: paramsFile.getVariableNames()) {
			process.setParameter(name, paramsFile.getValue(name));
		}
		log.info("Read {0} Dakota parameters from file {1}", paramsFile.getVariableNames().size(), file);			

		// TODO Validate that requested responses are available. We can get response names from Dakota params file, but 
		// the way things are done right now the WorkflowProcess hasn't parsed the workflow definition yet
		// so we can't validate them.
		
		return paramsFile.getEvalId();
	}
	
	private static void writeDakotaResults(String paramsFile, String responseFile, String sampleId, Map<String, Object> responses, SAWWorkflowLogger log) throws IOException {
		DakotaParamsFile params = new DakotaParamsFile(paramsFile);
		DakotaResultsFile.write(responseFile, sampleId, params.getResponseNames(), responses);
		log.info("Wrote {0} responses to Dakota results file {1}", params.getResponseNames().size(), responseFile);		
		// Since we opened the log before the run, we have to close it now.
		log.close();
	}

	
}
