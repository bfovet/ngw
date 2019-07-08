/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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

import gov.sandia.dart.workflow.runtime.core.ICancelationListener;
import gov.sandia.dart.workflow.runtime.core.InputPortInfo;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

public class LocalQueueSubmit extends SAWCustomNode {

	protected static final String STATUS_SCRIPT = "statusScript";
	protected static final String CHECKJOB_SCRIPT = "checkjobScript";
	protected static final String EXECUTE_SCRIPT = "executeScript";
	protected static final String SUBMIT_SCRIPT = "submitScript";
	
	public static final String CHECKJOB_FREQUENCY = "checkjobFrequency";
	private static final int DEFAULT_FREQUENCY = 15;
	private static final int UPDATE_DEFERAL = 10;


	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		try {
			File componentWorkDir = getComponentWorkDir(runtime, properties);
			runtime.status(this, "Setting up files");
			File inputFile = setUpEnvironment(properties, workflow, runtime, componentWorkDir);

			int checkjobFrequency = getCheckjobFrequency(properties);
			
			StringBuilder output = new StringBuilder();
			checkCancelled(runtime);
			runtime.status(this, "Submitting to queue");
			SAWWorkflowException e = runScript("./submit.sh", componentWorkDir, output, runtime);
			try {
				FileUtils.write(new File(componentWorkDir, "submit.log"), output);				
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
						FileUtils.write(new File(componentWorkDir, "checkjob.log"), output);
					} catch (IOException ex1) {
						runtime.log().warn("Problem writing to 'checkjob.log': " + ex1.getMessage());
					}
				}
				
				runtime.status(this, "Checkjob script returned " + result + ", looking for " + getPositiveCheckResult() + " or " + getFailedCheckResult());

				if (getPositiveCheckResult().equals(result)) {
					running = false;					
				} else if (getFailedCheckResult().equals(result)) {
					running = false;					
				} 
				
				if (ex != null)
					throw ex;
				
				// frequency is in seconds, convert to ms
				Thread.sleep(checkjobFrequency*1000);
			}
			runtime.status(this, "Job completed");
			
			checkCancelled(runtime);

			output.setLength(0);
			runtime.status(this, "Checking for job status");

			runScript("./status.sh", componentWorkDir, output, runtime);
			String result = output.toString().trim();
			try {
				FileUtils.write(new File(componentWorkDir, "status.log"), output);
			} catch (IOException ex1) {
				runtime.log().warn("Problem writing to 'status.log': " + ex1.getMessage());
			}

			runtime.status(this, "Status script returned " + result + ", looking for " + getPositiveStatusResult());

			if (!getPositiveStatusResult().equals(result))
				throw new SAWWorkflowException(getName() + ": SIERRA run failed, see execution log.");
			// TODO This seems wrong
			String logfilePath = new File(componentWorkDir, FilenameUtils.getBaseName(inputFile.getName()) + ".log").getAbsolutePath(); 
			return Collections.singletonMap("logFile", logfilePath);
			
		} catch (IOException | InterruptedException ex) {
			throw new SAWWorkflowException(getName() + ": Error", ex);
		}
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


	protected String getPositiveStatusResult() {
		return "Successful";
	}


	protected String getPositiveCheckResult() {
		return "COMPLETED";
	}

	protected String getFailedCheckResult() {
		return "FAILED";
	}


	private SAWWorkflowException runScript(String script, File componentWorkDir, StringBuilder sb, RuntimeData runtime) {
		ProcessBuilder builder = ProcessUtils.createProcess(runtime).command(script);	

		builder.directory(componentWorkDir);
		Process p = null;
		ICancelationListener listener = null;
		try {
			p = builder.start();
			Process pp = p;
			listener = () -> pp.destroy();
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
			File componentWorkDir) throws IOException {
		File inputFile = getInputFile(runtime, properties);

		// Link the input files into the workdir
		linkFile(componentWorkDir, inputFile, runtime);

		// Parameter substitutions for scripts
		// TODO Make this expandable. Should perhaps include all node's properties, global parameters,
		// and inputs (perhaps)
		Map<String, String> parameters = new HashMap<>();
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
		stageScript(runtime, parameters, properties, SUBMIT_SCRIPT, "submit.sh");
		stageScript(runtime, parameters, properties, EXECUTE_SCRIPT,"execute.sh");
		stageScript(runtime, parameters, properties, CHECKJOB_SCRIPT, "checkjob.sh");
		stageScript(runtime, parameters, properties, STATUS_SCRIPT, "status.sh");
		
		return inputFile;
	}


	private int getNumMinutes(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "job.minutes", "30");
	}
	
	private int getNumHours(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "job.hours", "1");
	}
	
	private int getNumNodes(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "num.nodes", "1");
	}
	
	protected int getNumProcessors(RuntimeData runtime,
			Map<String, String> properties) {
		return getIntFromPortOrProperty(runtime, properties, "num.processors", "16");
	}

	protected void stageScript(RuntimeData runtime, Map<String, String> parameters, Map<String, String> properties, String propertyName, String scriptName) throws IOException {
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
				runtime.log().info("Node {0} using user-supplied script '{1}' for '{2}'" , getName(), userFileName, scriptName);
			} else {
				throw new SAWWorkflowException(String.format("User script file %s not found", userFile.getAbsolutePath()));
			}
		} else {		
			data = IOUtils.toString(getClass().getResource("/scripts/" + scriptName), Charset.defaultCharset());
			runtime.log().info("\"Node {0} using built-in script '/scripts/{1}' for '{2}'" , getName(), scriptName, scriptName);

		}
		
		for (String key: parameters.keySet()) {
			data = data.replaceAll("\\$\\{" + key.replace(".", "\\.") + "\\}", Matcher.quoteReplacement(parameters.get(key)));
		}
		FileUtils.write(targetFile, data,  Charset.defaultCharset());
		targetFile.setExecutable(true);
	}
	
	@Override
	public List<String> getCategories() {
		return Arrays.asList("Engineering", NodeCategories.EXTERNAL_PROCESSES);
	}
	
	@Override
	public List<PropertyInfo> getDefaultProperties() { 
		return Arrays.asList(new PropertyInfo("inputFile", "home_file"), new PropertyInfo("account"), new PropertyInfo("sierra_code"), new PropertyInfo("num.nodes", "integer"), 
				new PropertyInfo("num.processors", "integer"), new PropertyInfo("job.hours", "integer"), new PropertyInfo("job.minutes", "integer"), 
				new PropertyInfo(SUBMIT_SCRIPT, "home_file"), new PropertyInfo(EXECUTE_SCRIPT, "home_file"), new PropertyInfo(CHECKJOB_SCRIPT, "home_file"), 
				new PropertyInfo(CHECKJOB_FREQUENCY, "integer"),new PropertyInfo(STATUS_SCRIPT, "home_file"));
	}
	
	@Override
	public List<OutputPortInfo> getDefaultOutputs() { return Arrays.asList(new OutputPortInfo("logFile", "output_file")); }
	
	@Override
	public List<InputPortInfo> getDefaultInputs() { return Arrays.asList(new InputPortInfo("inputFile", "input_file"));	}


	public File getInputFile(RuntimeData data,  Map<String, String> properties) {
		return getFileFromPortOrProperty(data,  properties, "inputFile", true, true);
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

}
