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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.remote.SessionManager;
import gov.sandia.dart.workflow.runtime.core.LoggingWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.NodeDatabase;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowLogger;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;
import gov.sandia.dart.workflow.runtime.dakota.DakotaParamsFile;
import gov.sandia.dart.workflow.runtime.dakota.DakotaResultsFile;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFObject;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponse;
import gov.sandia.dart.workflow.runtime.parser.IWFParser;
import gov.sandia.dart.workflow.runtime.util.GetOpt;
import gov.sandia.dart.workflow.runtime.util.GetOptException;

public class Main {
	
	public static final String ERROR_REPORTING_RESPONSES = "Error reporting responses";
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

		try {
			GetOpt options = new GetOpt(args, "kv", "dhwxpg");
			String[] params = options.params();

			File workDir;
			if (options.hasOption('w'))
				workDir = new File(options.getOption('w'));
			else
				workDir = new File("").getCanonicalFile();

			if (!workDir.exists())
				if (!workDir.mkdirs())
					throw new GetOptException("Working directory can't be created: " + workDir.getAbsolutePath());

			File logFile = new File(workDir, LoggingWorkflowMonitor.DEFAULT_NAME);
			try (LoggingWorkflowMonitor monitor = new LoggingWorkflowMonitor(logFile)) {

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

				String startNode = null;
				if (options.hasOption("x")) {
					startNode = options.getOption('x');
				}

				File homeDir;
				if (options.hasOption('h'))
					homeDir = new File(options.getOption('h'));
				else
					homeDir = workflowFile.getCanonicalFile().getParentFile();

				File globals = null;
				if (options.hasOption('g')) {
					globals = new File(options.getOption('g'));
					if (!globals.exists()) {
						throw new GetOptException("Global parameter file does not exist: " + globals.getAbsolutePath());
					}
				}

				WorkflowProcess process = new WorkflowProcess().
						addMonitor(monitor).
						setWorkflowFile(workflowFile).
						setHomeDir(homeDir).
						setWorkDir(workDir).
						setStartNode(startNode).
						setValidateUndefined(validateUndefined);

				if (globals != null)
					process.setGlobalParameterFile(globals);

				SAWWorkflowLogger log = process.getLogger();

				// TODO Could we do some preflight here -- parse workflow file?
				IWFParser parser = new IWFParser();
				List<IWFObject> objects = parser.parse(workflowFile);

				Map<String, Object> responses = null;
				try {
					if (dakotaMode) {
						String evalId = readDakotaParameters(options.params()[1], process, objects, log);
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

					log.info("Number of threads: {0}", Thread.getAllStackTraces().size());		
					process.setWorkflowFile(workflowFile);
					process.run(objects);
					responses = process.getRuntime().getResponses();
				} finally {
					if (dakotaMode) { 
						try {
							writeDakotaResults(options.params()[1], options.params()[2], process.getSampleId(), responses, log);
						} catch (SAWWorkflowException e) {
							// The error has already been logged, but we put this in status log so
							// live monitoring knows about the problem.
							monitor.logMessage(ERROR_REPORTING_RESPONSES);
						}
					}
					// TODO implement shutdown hook system
					new NGWReaper(REAPER_DELAY, new File(workDir, "stacktrace.log"), log);
					SessionManager.INSTANCE.shutdown();
				}
			} 

		} catch (Throwable e) {
			System.out.println("Error: " + e.getMessage());
			System.out.println("Args: " + Arrays.asList(args));
			System.out.println("CWD: " + new File(".").getAbsolutePath());
			System.out.println("Usage: java Main [-kv] [-d <dump-file>] [-x <start node>] [-h <home dir>] [-w <work dir>] [-g <globals file>] [-p <plugin>] <workflow-file> [<parameter>=<value> ...]");
			throw e;
		}
	}

	private static String readDakotaParameters(String file, WorkflowProcess process, List<IWFObject> objects, SAWWorkflowLogger log) throws IOException {
		DakotaParamsFile paramsFile = new DakotaParamsFile(file);
		for (String name: paramsFile.getVariableNames()) {
			String value = paramsFile.getValue(name);			
			process.setParameter(name, value);
		}
		log.info("Read {0} parameters from file {1}", paramsFile.getVariableNames().size(), file);			

		Set<String> responses = getResponseNames(objects);
		for (String name: paramsFile.getResponseNames()) {
			if (!responses.contains(name) ) {
				throw new SAWWorkflowException(String.format("No response named '%s' in workflow; evaluation %s cannot return to caller", name, paramsFile.getEvalId()));
			}
		}
		
		return paramsFile.getEvalId();
	}
	
	private static Set<String> getResponseNames(List<IWFObject> objects) {
		return objects.stream().filter((o)-> o instanceof IWFResponse).map(o -> o.name).collect(Collectors.toSet());
	}

	private static void writeDakotaResults(String paramsFile, String responseFile, String sampleId, Map<String, Object> responses, SAWWorkflowLogger log) {
		try {
			DakotaParamsFile params = new DakotaParamsFile(paramsFile);
			DakotaResultsFile.write(responseFile, sampleId, params.getResponseNames(), responses);
			log.info("Wrote {0} responses to results file {1}", params.getResponseNames().size(), responseFile);
		} catch (SAWWorkflowException e) {
			log.error(ERROR_REPORTING_RESPONSES, e);
			throw e;
		} catch (Exception e) {
			log.error(ERROR_REPORTING_RESPONSES, e);
		}		
	}

	
}
