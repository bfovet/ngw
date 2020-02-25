/*******************************************************************************
 					* Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.localsubmit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.AbstractRestartableNode;
import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.NodeMemento;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

public class LocalQueueSubmit extends AbstractRestartableNode {

	private static final String SCRIPTS = "scripts";
	private static final String LOG_FILE = "logFile";
	protected static final String STATUS_SCRIPT = "statusScript";
	protected static final String CHECKJOB_SCRIPT = "checkjobScript";
	protected static final String EXECUTE_SCRIPT = "executeScript";
	protected static final String SUBMIT_SCRIPT = "submitScript";
	protected static final String QUEUE_SUBMIT_FLAG = "submit to queue and monitor status";
	
	public static final String CHECKJOB_FREQUENCY = "checkjobFrequency";
	private static final int DEFAULT_FREQUENCY = 15;
	private static final int UPDATE_DEFERAL = 10;


	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		try {
			File componentWorkDir = getComponentWorkDir(runtime, properties);
			clearMemento(componentWorkDir);
			NodeMemento memento = createMemento(properties, workflow, runtime);
			
			boolean queueSubmission = getQueueSubmissionFlag(properties);

			runtime.status(this, "Setting up files");
			File inputFile = setUpEnvironment(properties, workflow, runtime, componentWorkDir, queueSubmission);

			
			StringBuilder output = new StringBuilder();
			
			if (queueSubmission)  {
				int checkjobFrequency = getCheckjobFrequency(properties);
				checkCancelled(runtime);
				runtime.status(this, "Submitting to queue");
				SAWWorkflowException e = runScript("./submit.sh", componentWorkDir, output, runtime);
				try {
					FileUtils.write(new File(componentWorkDir, "submit.log"), output, Charset.defaultCharset());				
				} catch (IOException ex1) {
					runtime.log().warn("Problem writing to 'submit.log': " + ex1.getMessage());
				}
				output.setLength(0);
				if (e != null)
					throw e;
				
				boolean running = true;
				String oldResult = null;
				int deferredUpdates = 0; 
				
				// TODO Timeout? 			
				while (running && !runtime.isCancelled()) {
					output.setLength(0);
					runtime.status(this, "Checking for job completion");
	
					SAWWorkflowException ex = runScript("./checkjob.sh", componentWorkDir, output, runtime);
	
					String result = output.toString().trim();
	
					boolean writeUpdate = false;
					
					if(result.equals(oldResult)) {
						deferredUpdates++;
						if(deferredUpdates >= UPDATE_DEFERAL) {
							writeUpdate = true;
							deferredUpdates = 0;
						}
					}else {
						oldResult = result;
						deferredUpdates = 0;
						writeUpdate = true;
					}
					
					
					if(writeUpdate)
					{
						try {
							FileUtils.write(new File(componentWorkDir, "checkjob.log"), output, Charset.defaultCharset());
						} catch (IOException ex1) {
							runtime.log().warn("Problem writing to 'checkjob.log': " + ex1.getMessage());
						}
					}
					
					runtime.status(this, getFormattedCheckjobMessage(result));
	
					if (checkResult(result, getPositiveCheckResult())) {
						running = false;					
					} else if (checkResult(result, getFailedCheckResult())) {
						running = false;					
					} else if (checkResult(result, getCancelledCheckResult())) {
						running = false;					
					} 
					
					if (ex != null)
						throw ex;
					
					// frequency is in seconds, convert to ms
					if (running && !runtime.isCancelled())
						Thread.sleep(checkjobFrequency*1000);
				}
			} else {
				checkCancelled(runtime);
				runtime.status(this, "Executing job");
				SAWWorkflowException e = runScript("./execute.sh", componentWorkDir, output, runtime);
				try {
					FileUtils.write(new File(componentWorkDir, "execute.log"), output, Charset.defaultCharset());				
				} catch (IOException ex1) {
					runtime.log().warn("Problem writing to 'execute.log': " + ex1.getMessage());
				}
				output.setLength(0);
				if (e != null)
					throw e;
				
			}
			runtime.status(this, "Job completed");
			
			checkCancelled(runtime);

			output.setLength(0);
			checkStatus(runtime, componentWorkDir, output);
			
			// TODO This seems wrong -- needs to be configurable
			String logfilePath = new File(componentWorkDir, FilenameUtils.getBaseName(inputFile.getName()) + ".log").getAbsolutePath(); 
			Map<String, Object> results = Collections.singletonMap(LOG_FILE, logfilePath);
			
			try {
				addOutputsToMemento(memento, results).save(componentWorkDir);
			} catch (IOException ioe) {
				// No memento, I guess
			}
			
			return results;

			
		} catch (IOException | InterruptedException ex) {
			throw new SAWWorkflowException(getName() + ": Error", ex);
		} finally {
			runtime.status(this, "");
		}
	}


	private boolean checkResult(String result, String[] values) {
		for (String value: values) {
			if (result.startsWith(value))
				return true;
		}
		return false;
	}

	private String getFormattedCheckjobMessage(String result) {
		List<String> alternatives = new ArrayList<>();
		alternatives.addAll(Arrays.asList(getPositiveCheckResult()));
		alternatives.addAll(Arrays.asList(getCancelledCheckResult()));		
		alternatives.addAll(Arrays.asList(getFailedCheckResult()));		
		return "Checkjob script returned " + result + ", expecting " + alternatives;
	}
	
	private String getFormattedStatusMessage(String result) {
		List<String> alternatives = new ArrayList<>();
		alternatives.addAll(Arrays.asList(getPositiveStatusResult()));
		return "Status script returned " + result + ", expecting " + alternatives;
	}

	private void checkStatus(RuntimeData runtime, File componentWorkDir, StringBuilder output) {
		runtime.status(this, "Checking for job status");

		runScript("./status.sh", componentWorkDir, output, runtime);
		String result = output.toString().trim();
		try {
			FileUtils.write(new File(componentWorkDir, "status.log"), output, Charset.defaultCharset());
		} catch (IOException ex1) {
			runtime.log().warn("Problem writing to 'status.log': " + ex1.getMessage());
		}

		runtime.status(this, getFormattedStatusMessage(result));

		if (!checkResult(result, getPositiveStatusResult()))
			throw new SAWWorkflowException(getName() + ": compute run failed, see execution log.");
	}


	private int getCheckjobFrequency(Map<String, String> properties) {
		String frequency = properties.get(CHECKJOB_FREQUENCY);
		
		if(frequency != null) {
			try
			{
				return Integer.parseInt(frequency);
			}catch(NumberFormatException nfe) {}
		}
		
		return DEFAULT_FREQUENCY;
	}


	private void checkCancelled(RuntimeData runtime) {
		if (runtime.isCancelled())
			throw new SAWWorkflowException("Workflow cancelled");
	}


	protected String[] getPositiveStatusResult() {
		return new String[] {"Successful"};
	}


	protected String[] getPositiveCheckResult() {
		return new String[] {"COMPLETED"};
	}

	protected String[] getFailedCheckResult() {
		return new String[] {"FAILED", "TIMEOUT"};
	}
	
	protected String[] getCancelledCheckResult() {
		return new String[] {"CANCELLED"};
	}

	private SAWWorkflowException runScript(String script, File componentWorkDir, StringBuilder sb, RuntimeData runtime) {
		ProcessBuilder builder = ProcessUtils.createProcess(runtime).command(script);	

		builder.directory(componentWorkDir);
		Process p = null;
		ICancelationListener listener = null;
		try {
			p = builder.start();
			Process pp = p;
			listener = () -> ProcessUtils.destroyProcess(pp);
			p.getOutputStream().close();

			int exitStatus = UNSET;
			while (exitStatus == UNSET && !runtime.isCancelled()) {
				try {
					exitStatus = p.waitFor();
					break;
				} catch (InterruptedException ex) {
					// May be a spurious wakeup. Check for cancellation, and go check exit status again.
				}
			}
			if (exitStatus != 0) {
				StringBuilder errorBuf = new StringBuilder();
				safeAppendContents(errorBuf, p.getErrorStream());		
				safeAppendContents(sb, p.getInputStream());
				sb.append(errorBuf);

				Exception cause = new Exception(errorBuf.toString());
				SAWWorkflowException ex = new SAWWorkflowException(getName() + ": Error running script " + script, cause);
				
				return ex;
			}
			
			safeAppendContents(sb, p.getInputStream());
			safeAppendContents(sb, p.getErrorStream());			
			return null;
		} catch (IOException e) {
			if (p != null) { 
				safeAppendContents(sb, p.getInputStream());
				safeAppendContents(sb, p.getErrorStream());
			}
			return new SAWWorkflowException(getName() + ": Error running script " + script, e);			
		} finally {
			if (listener != null) 
				runtime.removeCancelationListener(listener);
		}
	}


	private void safeAppendContents(StringBuilder sb, InputStream is) {
		try {
			List<String> lines = IOUtils.readLines(is,  Charset.defaultCharset());
			sb.append(StringUtils.join(lines, "\n"));
		} catch (Exception e) {
			// We tried
		}
	}


	protected File setUpEnvironment(Map<String, String> properties,
			WorkflowDefinition workflow, RuntimeData runtime,
			File componentWorkDir, boolean queueSubmission) throws IOException {
		File inputFile = getInputFile(runtime, properties);

		// Link the input files into the workdir
		linkFile(componentWorkDir, inputFile, runtime);

		// Parameter substitutions for scripts
		Map<String, String> parameters = new HashMap<>();
		
		for (String name: runtime.getParameterNames()) {
			if (runtime.isGlobal(name))
				parameters.put(name, String.valueOf(runtime.getParameter(name).getValue()));
		}
		
		properties.forEach((k, v) -> parameters.put(k, v));
		
		for (String name: workflow.getNode(getName()).inputs.keySet()) {
			parameters.put(name, String.valueOf(runtime.getInput(getName(), name, String.class)));	
		}
		
		// These override the above, providing defaults
		parameters.put("remote.dir", componentWorkDir.getAbsolutePath());
		parameters.put("input.deck.base.name", FilenameUtils.getBaseName(inputFile.getName()));
		parameters.put("input.deck.name", inputFile.getName());
		parameters.put("account", getAccount(properties));
		parameters.put("sierra_code", getSierraCode(properties));
		parameters.put("num.nodes", String.valueOf(getNumNodes(runtime, properties)));
		parameters.put("num.processors", String.valueOf(getNumProcessors(runtime, properties)));
		parameters.put("job.hours", String.valueOf(getNumHours(runtime, properties)));
		parameters.put("job.minutes", String.valueOf(getNumMinutes(runtime, properties)));
		parameters.put("queue", String.valueOf(getQueue(properties)));

		// Generate the scripts with substitutions
		stageScript(runtime, parameters, properties, EXECUTE_SCRIPT, SCRIPTS, "execute.sh");
		stageScript(runtime, parameters, properties, STATUS_SCRIPT, SCRIPTS, "status.sh");

		if (queueSubmission) {
			stageScript(runtime, parameters, properties, SUBMIT_SCRIPT, SCRIPTS, "submit.sh");
			stageScript(runtime, parameters, properties, CHECKJOB_SCRIPT, SCRIPTS, "checkjob.sh");
		}
		
		return inputFile;
	}


	private int getNumMinutes(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "job.minutes", "0");
	}
	
	private int getNumHours(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "job.hours", "0");
	}
	
	private int getNumNodes(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "num.nodes", "1");
	}
	
	protected int getNumProcessors(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "num.processors", "16");
	}

	protected void stageScript(RuntimeData runtime,
			Map<String, String> parameters, Map<String, String> properties,
			String propertyName,
			String scriptDirName,
			String scriptName) throws IOException {
		String data = null;
		String userFileName = getOptionalStringFromPortOrProperty(runtime, properties, propertyName);
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		File targetFile = new File(componentWorkDir, scriptName);
		if (!StringUtils.isEmpty(userFileName)) {
			File userFile = new File(userFileName);
			if (!userFile.isAbsolute())
				userFile = new File(runtime.getHomeDir(), userFileName);
			if (userFile.exists()) {
				data = FileUtils.readFileToString(userFile, Charset.defaultCharset());		
				runtime.log().info("Node {0} using user-supplied script ''{1}'' for ''{2}''" , getName(), userFileName, scriptName);
			} else {
				throw new SAWWorkflowException(String.format("User script file %s not found", userFile.getAbsolutePath()));
			}
		} else {		
			data = IOUtils.toString(getClass().getResource("/" + scriptDirName + "/" + scriptName), Charset.defaultCharset());
			runtime.log().info("Node {0} using built-in script ''/{1}/{2}'' for ''{3}''" , getName(), scriptDirName, scriptName, scriptName);

		}
		
		for (String key: parameters.keySet()) {
			data = data.replaceAll("\\$\\{" + key.replace(".", "\\.") + "\\}", Matcher.quoteReplacement(parameters.get(key)));
		}
		FileUtils.write(targetFile, data,  Charset.defaultCharset());
		targetFile.setExecutable(true);
	}
	
	@Override
	public List<String> getCategories() {
		return Arrays.asList(NodeCategories.EXTERNAL_PROCESSES);
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() { 
		return Arrays.asList(new PropertyInfo("inputFile", "home_file"),
				new PropertyInfo("sierra_code"),
				new PropertyInfo("num.processors", "integer", "16"),
				new PropertyInfo(EXECUTE_SCRIPT, "home_file"),
				new PropertyInfo(STATUS_SCRIPT, "home_file"),
				new PropertyInfo(QUEUE_SUBMIT_FLAG, "boolean", "true"),
				new PropertyInfo("account"),
				new PropertyInfo("queue", "text", "nw"),
				new PropertyInfo("num.nodes", "integer", "1"), 
				new PropertyInfo("job.hours", "integer", "0"),
				new PropertyInfo("job.minutes", "integer", "30"), 
				new PropertyInfo(SUBMIT_SCRIPT, "home_file"),
				new PropertyInfo(CHECKJOB_SCRIPT, "home_file"), 
				new PropertyInfo(CHECKJOB_FREQUENCY, "integer", "15"),
				new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true"));
	}
	
	@Override
	public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo(LOG_FILE, "output_file")); }
	
	@Override
	public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("inputFile", "input_file"));	}


	public File getInputFile(RuntimeData data,  Map<String, String> properties) {
		return getFileFromPortOrProperty(data,  properties, "inputFile", true, true);
	}
	
	protected boolean getQueueSubmissionFlag(Map<String, String> properties) {
		String value = properties.get(QUEUE_SUBMIT_FLAG);
		return value == null || "true".equals(value); // default to true (for backwards compatibility)
	}
	
	protected String getSierraCode(Map<String, String> properties) {
		return confirmExists("sierra_code", properties);
	}

	private String getAccount(Map<String, String> properties) {
		String account = properties.get("account");
		if (account == null)
			account = "NONE";
		return account;
	}
	
	private String getQueue(Map<String, String> properties) {
		String raw = properties.get("queue");
		if (StringUtils.isEmpty(raw))
			raw = "nw";
		return raw;
	}

	
	private int getIntFromPortOrProperty(RuntimeData runtime, Map<String, String> properties, String name, String dflt) {
		String value = null;
		// First, look on inputs.
		String fOnInput = (String) runtime.getInput(getName(), name, String.class);
		if (!StringUtils.isEmpty(fOnInput)) {
			value = fOnInput;
		}
		if (value == null) {
			String fInProps = properties.get(name);
			if (!StringUtils.isEmpty(fInProps))
				value = fInProps;
		}
		if (value == null) {
			value = dflt;
		}

		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new SAWWorkflowException(getName() + ": invalid value for '" + name + "': " + value);
		}
	}

	@Override
	protected boolean outputsAvailable(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		Map<String, OutputPort> outputs = workflow.getNode(getName()).outputs;
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		for (String name: outputs.keySet()) {
			if (name.equals(LOG_FILE))
				continue;				
			OutputPort port = outputs.get(name);
			if (isConnectedOutput(name, workflow) && OUTPUT_FILE.equals(port.type)) {
				String file = getFilenameForOutputPort(properties, workflow, runtime, outputs.get(name));
				if (isGlobPattern(file)) {
					List<String> matches = new ArrayList<>();
					try {
						glob(file, componentWorkDir, matches);									
					} catch(IOException ioe) { }	
					
					if (matches.size() == 0) {
						runtime.log().info("{0}: testing previous state, no files match {1}", getName(), file);					
						return false;
					} else {
						continue;
					}
				}
				if (!new File(componentWorkDir, file).exists()) {
					runtime.log().info("{0}: testing previous state, output file {1} not available", getName(), file);					
					return false;
				}
			}
		}		
		if (isConnectedOutput(LOG_FILE, workflow)) {
			File inputFile = getInputFile(runtime, properties);
			File logFile = new File(componentWorkDir, FilenameUtils.getBaseName(inputFile.getName()) + ".log"); 
			if (!logFile.exists()) {
				runtime.log().info("{0}: testing previous state, log file {1} not available", getName(), logFile.getName());					
				return false;
			}
		} 
		
		return true;
		
	}
}
