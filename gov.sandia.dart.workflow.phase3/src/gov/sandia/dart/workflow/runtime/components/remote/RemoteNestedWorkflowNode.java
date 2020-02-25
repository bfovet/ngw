/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.components.remote;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.components.AbstractExternalNode;
import gov.sandia.dart.workflow.runtime.components.AbstractNestedWorkflowNode;
import gov.sandia.dart.workflow.runtime.components.nested.NestedWorkflowNode;
import gov.sandia.dart.workflow.runtime.core.LoggingWorkflowMonitor;
import gov.sandia.dart.workflow.runtime.core.NodeCategories;
import gov.sandia.dart.workflow.runtime.core.OutputPortInfo;
import gov.sandia.dart.workflow.runtime.core.PropertyInfo;
import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Connection;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;
import gov.sandia.dart.workflow.runtime.core.WorkflowProcess;

public class RemoteNestedWorkflowNode extends AbstractNestedWorkflowNode {

	private static final String WFLIB_DEFAULT = "/projects/dart/prod/wflib";
	private static final String WFLIB = "wflib";
	private static final String FILES_TO_UPLOAD = "Other files/directories";
	public static final String TYPE = "remoteNestedWorkflow";

	@Override
	protected Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow,
			RuntimeData runtime) {
		try {
			// Fetch parameters
			String dstHost = getStringFromPortOrProperty(runtime, properties, RemoteCommandNode.HOSTNAME).trim();
			String dstUser = getOptionalStringFromPortOrProperty(runtime, properties, RemoteCommandNode.USERNAME);
			if (dstUser != null)
				dstUser = dstUser.trim();
			if (StringUtils.isEmpty(dstUser))
				dstUser = System.getProperty("user.name");
			String jmpHost = getOptionalStringFromPortOrProperty(runtime, properties, RemoteCommandNode.JMPHOST);
			if (jmpHost != null)
				jmpHost = jmpHost.trim();
			String jmpUser = getOptionalStringFromPortOrProperty(runtime, properties, RemoteCommandNode.JMPUSER);
			if (jmpUser != null)
				jmpUser = jmpUser.trim();

			String path = getStringFromPortOrProperty(runtime, properties, RemoteCommandNode.REMOTE_PATH).trim();
			String wflib = getOptionalStringFromPortOrProperty(runtime, properties, WFLIB);
			ByteArrayOutputStream errStream = new ByteArrayOutputStream();
			StringBuilder stdout = new StringBuilder();
			StringBuilder stderr = new StringBuilder();
			File workflowFile = getSubWorkflowFile(properties, runtime);
			Remote remote = connect(runtime, dstHost, dstUser, jmpHost, jmpUser);		

			// TODO Can we assume -p ?
			log(runtime, "Creating remote directory '" + path + "' on " + dstHost);
			try {
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				int exitCode = remote.execute("mkdir -p " + path + "; : > " + LoggingWorkflowMonitor.DEFAULT_NAME, outStream, errStream, runtime);
				remote.setPath(path);
				stdout.append(outStream.toString());
				stderr.append(errStream.toString());
				errStream.reset();
				if (exitCode != 0)
					throw new SAWWorkflowException("Error creating remote directory '" + path + "' in node " + getName() + ": " + errStream.toString());
			} catch (SAWWorkflowException t) {
				throw t;			
			} catch (Exception e) {
				throw new SAWWorkflowException("Error creating remote directory '" + path + "' in node " + getName() + ": " + errStream.toString());
			}

			log(runtime, "Preparing remote directory");
			try {
				// We need to make sure the status log exists and is empty, in case we start tailing it before workflow engine starts
				ByteArrayOutputStream outStream = new ByteArrayOutputStream();
				int exitCode = remote.execute(": > " + LoggingWorkflowMonitor.DEFAULT_NAME, outStream, errStream, runtime);
				stdout.append(outStream.toString());
				stderr.append(errStream.toString());
				errStream.reset();
				if (exitCode != 0)
					throw new SAWWorkflowException("Error preparing remote directory '" + path + "' in node " + getName() + ": " + errStream.toString());
			} catch (SAWWorkflowException t) {
				throw t;			
			} catch (Exception e) {
				throw new SAWWorkflowException("Error preparing remote directory '" + path + "' in node " + getName() + ": " + errStream.toString());
			}

			// Transfer input files to remote
			try {
				log(runtime, "Uploading workflow file '" + workflowFile.getName() + "'");
				boolean result = remote.upload(workflowFile, workflowFile.getName(), runtime);
				if (!result)
					throw new SAWWorkflowException("Failed uploading workflow file '" + workflowFile.getName() + "' in node " + getName());

			} catch (IOException e) {
				throw new SAWWorkflowException("Error uploading workflow file '" + workflowFile.getName() + "' in node " + getName(), e);
			}

			log(runtime, "Gathering files to send to remote for node " + getName());
			Map<String, String> corrections = new HashMap<>();
			uploadFilesToRemote(properties, runtime, workflow, remote, corrections, errStream);
			stderr.append(errStream.toString());
			errStream.reset();		

			// Write remote properties file 
			try {
				log(runtime, "Writing remote properties file '" + getParamsFileName() + "'");
				File paramsFile = stageParametersFile(runtime, workflow, properties, corrections);
				boolean result = remote.upload(paramsFile, paramsFile.getName(), runtime);
				if (!result)
					throw new SAWWorkflowException("Failed uploading params file '" + paramsFile.getName() + "' in node " + getName());

			} catch (IOException e) {
				throw new SAWWorkflowException("Error creating remote properties file '" + getParamsFileName() + "' in node " + getName(), e);
			}

			// Write remote global properties file 
			File globalsFile = stageGlobalsFile(runtime, properties);
			try {
				log(runtime, "Writing remote global parameters file '" + globalsFile.getName() + "'");

				boolean result = remote.upload(globalsFile, globalsFile.getName(), runtime);
				if (!result)
					throw new SAWWorkflowException("Failed uploading params file '" + globalsFile.getName() + "' in node " + getName());

			} catch (IOException e) {
				throw new SAWWorkflowException("Error creating remote globals parameters file '" + globalsFile.getName() + "' in node " + getName(), e);
			}


			// Run remote command using Dakota property/response protocol
			log(runtime, "Executing remote workflow '" + workflowFile.getName() + "'");
			final String commandLine = makeCommandLine(workflowFile.getName(), wflib);
			log(runtime, "  " + commandLine);

			ExecutorService executor = Executors.newFixedThreadPool(2);

			Future<?> statMon = executor.submit(() -> {
				try {
					ByteArrayOutputStream es2 = new ByteArrayOutputStream();
					Function<String, Void> callback = new Function<String, Void>() {
						@Override
						public Void apply(String t) {
							if (t != null) {
								String[] lines = t.trim().split("\\r?\\n");
								for (String line: lines) {
									log(runtime, "(remote): " + line);
								}
							}

							return null;
						}
					};
					remote.execute("tail -f " + LoggingWorkflowMonitor.DEFAULT_NAME, es2, callback, runtime);

				} catch (IOException e) {
					// Eventually tail dies and we end up here.
				}	

			});
			
			Future<?> rnwf = executor.submit( () -> {
				try {
					ByteArrayOutputStream outStream = new ByteArrayOutputStream();
					int exitCode = remote.execute(commandLine, outStream, errStream, runtime);
					stdout.append(outStream.toString());
					stderr.append(errStream.toString());
					errStream.reset();
					if (exitCode != 0)
						throw new SAWWorkflowException(String.format("%s: Remote workflow exited with status %d: '%s'", getName(), exitCode, stderr.toString()));
				} catch (SAWWorkflowException t) {
					throw t;

				} catch (Throwable t) {
					throw new SAWWorkflowException("Error executing remote workflow in node " + getName(), t);
				}
			});


			try {
				executor.shutdown();
				rnwf.get();
				// Wait for any additional log output after workflow process terminates
				executor.awaitTermination(2, TimeUnit.SECONDS);
				// Kill the "tail" job
				statMon.cancel(true);
			} catch (InterruptedException e1) {
				throw new SAWWorkflowException("Remote workflow interrupted in " + getName());

			} catch (ExecutionException e) {
				Throwable t = e.getCause();
				if (t instanceof SAWWorkflowException)
					throw (SAWWorkflowException) t;
				else
					throw new SAWWorkflowException("Error executing remote workflow in node " + getName(), t);
			}


			// Retrieve remote log
			try {
				log(runtime, "Retrieving remote workflow log '" + WorkflowProcess.WORKFLOW_ENGINE_LOG + "'");
				File localLog;
				if (shouldCreateComponentWorkDir(properties)) 
					localLog = new File(getComponentWorkDir(runtime, properties), WorkflowProcess.WORKFLOW_ENGINE_LOG);
				else
					localLog = new File(runtime.getWorkDirectory(), getName() + "." + WorkflowProcess.WORKFLOW_ENGINE_LOG);
				// System.err.println("downloading remote log to " + localLog.getAbsolutePath());
				boolean result = remote.download(localLog, WorkflowProcess.WORKFLOW_ENGINE_LOG, runtime);		
				if (!result)
					throw new SAWWorkflowException("Failed downloading workflow log '" + WorkflowProcess.WORKFLOW_ENGINE_LOG + "' in node " + getName());

				log(runtime, "Checking remote workflow status in node " + getName());
				if (!FileUtils.readFileToString(localLog, Charset.defaultCharset()).contains(WorkflowProcess.SUCCESS))
					throw new SAWWorkflowException("Remote workflow did not successfully complete in node " + getName());	
			} catch (IOException e) {
				throw new SAWWorkflowException("Error downloading workflow log '" + WorkflowProcess.WORKFLOW_ENGINE_LOG + "' in node " + getName(), e);

			}

			// Retrieve remote response file.
			String responseFileName = getResponseFileName();	
			log(runtime, "Downloading response file '" + responseFileName + "'");

			File responseFile = new File(getComponentWorkDir(runtime, properties), responseFileName);
			try {
				boolean result = remote.download(responseFile, responseFileName, runtime);		
				if (!result)
					throw new SAWWorkflowException("Failed downloading response file '" + getResponseFileName() + "' in node " + getName());

			} catch (IOException e) {
				throw new SAWWorkflowException("Error downloading response file in node " + getName(), e);

			}

			// Create node outputs	
			log(runtime, "Extracting responses from remote response file in node " + getName());
			Map<String, Object> responses = extractResponses(responseFile);
			downloadRemoteOutputFiles(runtime, properties, remote, responses, errStream);
			responses.put(AbstractExternalNode.STDOUT_PORT_NAME, stdout.toString());
			responses.put(AbstractExternalNode.STDERR_PORT_NAME, stderr.toString());	
			return responses; 
		} finally {
			log(runtime, "");
		}
	}
	private void downloadRemoteOutputFiles(RuntimeData runtime, Map<String, String> properties, Remote remote,
			Map<String, Object> responses, ByteArrayOutputStream errStream) {
		for (String name: responses.keySet()) {
			try {
				List<String> files = new ArrayList<>();
				String[] tokens = remoteGlob(runtime, remote, String.valueOf(responses.get(name)));
				for (String response: tokens) {
					if (checkFileExists(runtime, remote, response)) {
						String filename = new File(response).getName();
						log(runtime, "Downloading file '" + filename + "' in node " + getName());
						File localFile = new File(getComponentWorkDir(runtime, properties), filename);
						String originalPath = remote.getPath();
						try {
							remote.setPath("");
							if (!remote.download(localFile, response, runtime)) {
								throw new SAWWorkflowException(
										"Failed to download '" + filename + "' in node " + getName());
							} 
						} finally {
							remote.setPath(originalPath);
						}
						files.add(localFile.getAbsolutePath());
					}
				}
				if (!files.isEmpty()) {
					responses.put(name, (String[]) files.toArray(new String[files.size()]));
				}
			} catch (IOException e) {
				throw new SAWWorkflowException("Error downloading output files in node " + getName(), e);	
			}
		}		
	}

	private String[] remoteGlob(RuntimeData runtime, Remote remote, String response) throws IOException {
		if (response.indexOf('*') != -1 || response.indexOf('?') != -1) {
			String command = "bash -c 'shopt -s nullglob; list=(" + response + "); echo \"${list[@]}\"'";
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ByteArrayOutputStream err = new ByteArrayOutputStream();

			int code = remote.execute(command, out, err, runtime);
			if (code != 0) {
				throw new IOException("Error resolving remote filenames: " + err.toString());
			}
			response = out.toString();
		}
		return response.split("\\s+", 0);
	}

	private boolean checkFileExists(RuntimeData runtime, Remote remote, String response) throws IOException {
		String command = "bash -c \"if [ -f '" + response + "' ]; then echo true; else echo false; fi\"";
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		int code = remote.execute(command, out, err, runtime);
		if (code != 0) {
			throw new IOException("Error checking for remote file: " + err.toString());
		}
		return "true".equals(String.valueOf(out.toString()).trim());
	}

	private void uploadFilesToRemote(Map<String, String> properties, RuntimeData runtime, WorkflowDefinition workflow, Remote remote, Map<String, String> corrections, OutputStream errStream) {
		ArrayList<File> uploadManifest = new ArrayList<>();
		Collection<String> names = runtime.getInputNames(getName());
		for (String name: names) {
			// TODO Can we handle arrays of paths on these inputs
			File probe = new File((String) runtime.getInput(getName(), name, String.class));
			if (probe.exists()) {
				// This is the path to a local file. The user may not want us to transfer it though;
				// they can set a flag on the incoming connection.
				InputPort inputPort = workflow.getNode(getName()).inputs.get(name);
				Connection connection = inputPort.connection;
				Property notALocalFile = connection.properties.get(NOT_A_LOCAL_PATH);
				if (notALocalFile != null && "true".equals(notALocalFile.value)) {
					log(runtime, "Not uploading '" + probe.getName() + "' due to notALocalPath flag");
				} else {
					uploadManifest.add(probe);
					corrections.put(name, probe.getName());
				}
			}
		}
		String otherFilesToUpload = properties.get(FILES_TO_UPLOAD);
		if (otherFilesToUpload != null) {
			// System.err.println("got manifest");
			for (String name : otherFilesToUpload.split("\\R+")) {
				name = name.trim();
				if (name.isEmpty())
					continue;
				log(runtime, "considering " + name + "for upload");
				File probe = new File(name);
				if (!probe.isAbsolute())
					probe = new File(runtime.getHomeDir(), name);
				if (probe.exists()) {
					uploadManifest.add(probe);
					corrections.put(name, probe.getName());					
				} else {
					log(runtime, "Not uploading " + probe.getName() + " because it doesn't exist");
				}
			}
		}
		for (File probe: uploadManifest) {
			log(runtime, "manifest item: " + probe.getName() + ", absolute " + probe.getAbsolutePath());
			// System.err.println("manifest item: " + probe.getName() + ", absolute " + probe.getAbsolutePath());
			try {
				if (probe.isFile()) {
					log(runtime, "Uploading '" + probe.getName() + "'");
					boolean result = remote.upload(probe, probe.getName(), runtime);
					if (!result)
						throw new SAWWorkflowException("Failed uploading file '" + probe.getName() + "' in node " + getName());
				} else {
					uploadDirectory(remote, probe, runtime, errStream);						
				}
			} catch (IOException e) {
				throw new SAWWorkflowException("Error uploading  file '" + probe.getName() + "' in node " + getName(), e);
			}
		}
	}

	private void uploadDirectory(Remote remote, File dir, RuntimeData runtime, OutputStream errStream) throws IOException {
		String originalPath = remote.getPath();
		log(runtime, "Creating directory '" + dir.getName() + "'");		
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		int exitCode = remote.execute("mkdir -p " + dir.getName(), outStream, errStream, runtime);
		if (exitCode != 0) {
			throw new SAWWorkflowException("Error making remote directory '" + dir.getName() + "' in node " + getName());
		}
		remote.setPath(new File(originalPath, dir.getName()).getPath());

		log(runtime, outStream.toString());
		File[] listFiles = dir.listFiles();
		if (listFiles == null)
			throw new SAWWorkflowException(dir.getAbsolutePath() + " is not a directory or can't be read.");
		for (File child: listFiles) {
			if (child.isFile()) {
				log(runtime, "Uploading '" + child.getName() + "'");
				boolean result = remote.upload(child, child.getName(), runtime);
				if (!result) {
					throw new SAWWorkflowException("Error uploading  file '" + child.getName() + "' in node " + getName());
				}
			} else {
				uploadDirectory(remote, child, runtime, errStream);				
			}
		}
		remote.setPath(originalPath);
	}

	private void log(RuntimeData runtime, String status) {
		runtime.status(this, status);
	}

	private Remote connect(RuntimeData runtime, String dstHost, String dstUser, String jmpHost, String jmpUser) {		
		Remote remote = new Remote(dstHost, dstUser, jmpHost, jmpUser);
		try {
			if (StringUtils.isNoneEmpty(jmpHost, jmpUser)) {
				runtime.log().debug("Connecting to {0}@{1} via {2}@{3}", dstUser, dstHost, jmpUser, jmpHost);
			} else {
				runtime.log().debug("Connecting to {0}@{1}", dstUser, dstHost);
			}
			remote.connect(runtime.log());
		} catch (IOException e) {
			throw new SAWWorkflowException("Can't connect to host " + dstHost + " in node " + getName(), e);
		}
		return remote;
	}
	
	public String getFilename(Map<String, String> properties) {
		return properties.get(NestedWorkflowNode.FILENAME);
	}
	
	
	protected File getSubWorkflowFile(Map<String, String> properties, RuntimeData runtime)
	{
		String filename = getStringFromPortOrProperty(runtime, properties, NestedWorkflowNode.FILENAME).trim();
		File subworkflowFile = new File(filename);
		if (!subworkflowFile.isAbsolute())
		{
			subworkflowFile = new File(runtime.getHomeDir(), filename);
		}
		if (!subworkflowFile.exists())
			throw new SAWWorkflowException("Workflow file " + subworkflowFile.getAbsolutePath() + " does not exist in node " + getName());
		return subworkflowFile;
	}
	private Map<String, Object> extractResponses(File responseFile) {
		try {
			List<String> lines = FileUtils.readLines(responseFile);
			Map<String, Object> responses = new HashMap<>();
			for (String line: lines) {
				String[] split = line.split("\\s+");
				if (split.length != 2) {
					throw new SAWWorkflowException("Remote responses file has bad format at line '" + line + "'");					
				}
				responses.put(split[1], split[0]);
			}
			return responses;
		} catch (IOException e) {
			throw new SAWWorkflowException("Error reading remote responses file", e);
		}
	}

	private String getResponseFileName() {
		return "responses.out";
	}

	private String makeCommandLine(String workflowFileName, String wflib) {
		// TODO Search around for WFLIB?
		if (StringUtils.isEmpty(wflib))
			wflib = WFLIB_DEFAULT;
		return wflib + "/run.sh -g globals.in -k " + workflowFileName + " params.in responses.out";
	}

	private File stageParametersFile(RuntimeData runtime, WorkflowDefinition workflow, Map<String, String> properties, Map<String, String> corrections) {
		// TODO Is Dakota format going to be good enough?
		File file = new File(getComponentWorkDir(runtime, properties), getParamsFileName());
		Collection<String> inputNames = runtime.getInputNames(getName());
		Collection<String> outputNames = new HashSet<>(workflow.getNode(getName()).outputs.keySet());
		getDefaultOutputs().forEach(o -> outputNames.remove(o.getName()));
		Collection<String> propertyNames = new HashSet<>(properties.keySet());
		getDefaultProperties().forEach(p -> propertyNames.remove(p.getName()));
		getReservedProperties().forEach(p -> propertyNames.remove(p.getName()));
		inputNames.stream().filter(i -> isConnectedInput(i, workflow)).forEach(i -> propertyNames.remove(i));

		try (PrintWriter writer = new PrintWriter(file)) {
			int nvars = inputNames.size() + propertyNames.size();
			Stream<String> parameterNames = Stream.concat(inputNames.stream(), propertyNames.stream());
			writer.println(nvars + "\tvariables");
			parameterNames.forEach(parameterName -> {
				String value = getCorrectedParameterValue(runtime, parameterName, corrections);
				writer.print(value);
				writer.print("\t");
				writer.println(parameterName);				
			});
			
			writer.println(outputNames.size() + "\tfunctions");
			for (String outputName: outputNames) {
				writer.println("1 ASV_1:" + outputName);
			}
			writer.println(runtime.getSampleId() + " eval_id");
			
		} catch (IOException e) {
			throw new SAWWorkflowException("Error staging parameters file for remote workflow node: " + getName(), e);
		}
		return file;
	}

	private String getCorrectedParameterValue(RuntimeData runtime, String inputName, Map<String, String> corrections) {
		if (corrections.get(inputName) != null)
			return corrections.get(inputName);
		return (String) runtime.getInput(getName(), inputName, String.class);
	}

	private String getParamsFileName() {
		return "params.in";
	}
	

	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Arrays.asList(
				new PropertyInfo(NestedWorkflowNode.FILENAME, "home_file"),
				new PropertyInfo(RemoteCommandNode.HOSTNAME, "default"),
				new PropertyInfo(RemoteCommandNode.USERNAME, "default"),
				new PropertyInfo(RemoteCommandNode.JMPHOST, "default"),
				new PropertyInfo(RemoteCommandNode.JMPUSER, "default"),
				new PropertyInfo(RemoteCommandNode.REMOTE_PATH, "default"),
				new PropertyInfo("wflib", "default"),
				new PropertyInfo(FILES_TO_UPLOAD, "multitext"),
				new PropertyInfo(PRIVATE_WORK_DIR, "boolean", "true")
				);				
	}
	
	@Override
	public List<OutputPortInfo> getDefaultOutputs() {
		return Arrays.asList(
				new OutputPortInfo(AbstractExternalNode.STDOUT_PORT_NAME),
				new OutputPortInfo(AbstractExternalNode.STDERR_PORT_NAME)
				);
	}
	
	@Override public List<String> getCategories() { return Arrays.asList(NodeCategories.CONTROL, NodeCategories.REMOTE, NodeCategories.WORKFLOW); }
	
}
