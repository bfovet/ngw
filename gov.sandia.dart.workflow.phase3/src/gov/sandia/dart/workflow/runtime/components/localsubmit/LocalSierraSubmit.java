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
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWCustomNode;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;

public class LocalSierraSubmit extends SAWCustomNode {

	protected static final String STATUS_SCRIPT = "statusScript";
	protected static final String CHECKJOB_SCRIPT = "checkjobScript";
	protected static final String EXECUTE_SCRIPT = "executeScript";
	protected static final String SUBMIT_SCRIPT = "submitScript";


	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		try {
			File componentWorkDir = getComponentWorkDir(runtime, properties);

			File inputFile = setUpEnvironment(properties, workflow, runtime, componentWorkDir);

			StringBuilder output = new StringBuilder();
			if (runtime.isCancelled())
				throw new SAWWorkflowException("Workflow cancelled");
			SAWWorkflowException e = runScript("./submit.sh", componentWorkDir, output, runtime);
			FileUtils.write(new File(componentWorkDir, "submit.log"), output);
			if (e != null)
				throw e;
			
			boolean running = true;
			// TODO Timeout? 
			String result = output.toString().trim();
			while (running && !runtime.isCancelled()) {
				output.setLength(0);
				SAWWorkflowException ex = runScript("./checkjob.sh", componentWorkDir, output, runtime);
				if (getPositiveCheckResult().equals(result)) {
					running = false;					
				} else if (getFailedCheckResult().equals(result)) {
					running = false;					
				}
				System.out.println(output.toString());
				if (ex != null)
					throw ex;
				Thread.sleep(3000);
			}
			if (runtime.isCancelled())
				throw new SAWWorkflowException("Workflow cancelled");

			output.setLength(0);
			runScript("./status.sh", componentWorkDir, output, runtime);
			if (!getPositiveStatusResult().equals(result))
				throw new SAWWorkflowException(getName() + ": SIERRA run failed, see execution log.");
			
			String logfilePath = new File(componentWorkDir, FilenameUtils.getBaseName(inputFile.getName()) + ".log").getAbsolutePath(); 
			return Collections.singletonMap("logFile", logfilePath);
			
		} catch (IOException | InterruptedException ex) {
			throw new SAWWorkflowException(getName() + ": Error", ex);
		}
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
		ProcessBuilder builder = new ProcessBuilder(script);	
		builder.environment().putAll(runtime.getenv());
		builder.directory(componentWorkDir);
		Process p = null;
		ICancelationListener listener = null;
		try {
			p = builder.start();
			Process pp = p;
			listener = () -> pp.destroy();
			p.getOutputStream().close();
			int status = p.waitFor();
			if (status != 0) {
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
		} catch (IOException | InterruptedException e) {
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
		linkFile(componentWorkDir, inputFile);

		// Parameter substitutions for scripts
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
		String userFileName = properties.get(propertyName);
		File componentWorkDir = getComponentWorkDir(runtime, properties);
		File targetFile = new File(componentWorkDir, scriptName);
		if (!StringUtils.isEmpty(userFileName)) {
			File userFile = new File(runtime.getHomeDir(), userFileName);
			if (userFile.exists()) {
				data = FileUtils.readFileToString(userFile, Charset.defaultCharset());				
			} else {
				throw new SAWWorkflowException(String.format("User script file %s not found", userFileName));
			}
		} else {		
			data = IOUtils.toString(getClass().getResource("/scripts/" + scriptName), Charset.defaultCharset());
		}
		
		for (String key: parameters.keySet()) {
			data = data.replaceAll("\\$\\{" + key.replace(".", "\\.") + "\\}", Matcher.quoteReplacement(parameters.get(key)));
		}
		FileUtils.write(targetFile, data,  Charset.defaultCharset());
		targetFile.setExecutable(true);
	}
	
	@Override
	public String getCategory() {
		return "Engineering";
	}
	
	@Override
	public List<String> getDefaultProperties() {
		return Arrays.asList("inputFile", "account", "sierra_code", "num.nodes", "num.processors", "job.hours", "job.minutes", SUBMIT_SCRIPT, EXECUTE_SCRIPT, CHECKJOB_SCRIPT, STATUS_SCRIPT);
	}
	
	@Override
	public List<String> getDefaultPropertyTypes() {
		return Arrays.asList("home_file", "default", "default", "integer", "integer", "integer", "integer", "home_file", "home_file", "home_file", "home_file");
	}
	
	@Override
	public List<String> getDefaultOutputNames() {
		return Arrays.asList("logFile");
	}
	
	@Override
	public List<String> getDefaultOutputTypes() {
		return Arrays.asList("output_file");
	}
	
	@Override
	public List<String> getDefaultInputNames() {
		return Arrays.asList("inputFile");		
	}
	
	@Override
	public List<String> getDefaultInputTypes() {
		return Arrays.asList("input_file");		
	}

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
