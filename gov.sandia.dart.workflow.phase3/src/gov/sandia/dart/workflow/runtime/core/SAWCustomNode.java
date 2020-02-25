/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import com.googlecode.sarasvati.CustomNode;
import com.googlecode.sarasvati.Engine;
import com.googlecode.sarasvati.NodeToken;
import com.googlecode.sarasvati.env.Env;

import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Connection;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.RInput;
import gov.sandia.dart.workflow.runtime.util.OSValidator;

public abstract class SAWCustomNode extends CustomNode implements ISAWCustomNode {
	protected static final String OUTPUT_FILE = "output_file";
	protected static final int UNSET = 1234567890;
	protected static final String LINK_INCOMING_FILE_TO_TARGET = "linkIncomingFileToTarget";
	protected static final String COPY_INCOMING_FILE_TO_TARGET = "copyIncomingFileToTarget";

	protected static final String TRIM_WHITESPACE = "trimWhitespace";
	protected static final String EXPAND_WILDCARDS = "expandWildcards";
	protected static final String READ_IN_FILE = "readInFile";
	protected static final String NOT_A_LOCAL_PATH = "notALocalPath";
	protected static final String NEW_FILE_NAME = "newFileName";


	public static final String DEFAULT_INPUT = "x";
	public static final String WORKFLOW_DEFINITION = "workflowDefinition";
	public static final String RUNTIME_DATA = "runtimeData";	
	public static final String CLEAR_NODE_DIR = "clear private work directory";	
	public static final String PRIVATE_WORK_DIR = "use private work directory";
	public static final String OLD_PRIVATE_WORK_DIR = "privateWorkDir";
	public static final String HIDE_IN_NAVIGATOR = "hide in navigator";
	
	public static final String ASYNC = "async";
	private static class Context {
		Engine engine;
		NodeToken token;
		Map<String, String> properties = new HashMap<>(); 

		Context(Engine engine, NodeToken token) {
			this.engine = engine;
			this.token = token;
		}		
	}
	
	private Stack<Context> contexts = new Stack<>();
	@Override
	public void execute(Engine engine, NodeToken token) {
		Env env = token.getFullEnv();
		WorkflowDefinition workflow = (WorkflowDefinition) env.getTransientAttribute(WORKFLOW_DEFINITION);
		RuntimeData runtime = (RuntimeData) env.getTransientAttribute(RUNTIME_DATA);
		WorkflowDefinition.Node node = workflow.getNode(getName());

		if (runtime.isBreakpoint(getName())) {
			runtime.breakpointHit(this);
			if(!runtime.isRunThroughBreakpoints()) {
				return;
			}
		// Skip implementation altogether under these conditions
		} else if (runtime.isDummy(getName()) && outputsPresent(runtime, node)) {
			if (!token.isComplete()) {
				runtime.enterNode(this);
				runtime.log().debug("Reusing outputs from muted node {0}", getName());
				Set<String> outputNames = node.outputs.keySet();
				engine.completeMany(token, outputNames.toArray(new String[outputNames.size()]));	
				runtime.exitNode(this);
				runtime.clearDummy(getName());
				return;
			}
		}
		
		runtime.enterNode(this);
		Map<String, String> properties;
		try {
			Context context = new Context(engine, token);
			contexts.push(context);
			properties = context.properties;
			setUpProperties(properties, workflow, runtime);
		} catch (RuntimeException e) {
			runtime.abortNode(this, e);
			throw e;
		} 
		
		if (canReuseExistingState(workflow, runtime, properties)) {
			runtime.log().info("In node {0}: don''t need to run, reusing previous result", getName());
			reuseExistingState(node, workflow, runtime, properties);
			
		} else {
			clearComponentWorkdirIfRequested(runtime, properties);
			stageUserDefinedInputFiles(properties, workflow, runtime);
			if (isAsync(properties.get(ASYNC))) {
				executeAsync(engine, token, properties, workflow, runtime, node);
				
			} else {
				executeSync(engine, token, properties, workflow, runtime, node);
			}
		}
	}

	// Returns true if the workflow state contains at least one input
	// that's been fed from each output of this node
	private boolean outputsPresent(RuntimeData runtime, Node node) {
		for (OutputPort port: node.outputs.values()) {
			if (port.connections.size() > 0) {
				Connection connection = port.connections.get(0);
				if (runtime.getInput(connection.node, connection.port, Object.class) == null)
					return false;
			}
		}
		return true;
	}

	private void clearComponentWorkdirIfRequested(RuntimeData runtime, Map<String, String> properties) {
		if (shouldClearComponentWorkDir(properties)) {
			try {
				File workDir = getComponentWorkDir(runtime, properties);
				File[] files = workDir.listFiles();
				if (files == null)
					throw new SAWWorkflowException(workDir.getAbsolutePath() + " is not a directory or can't be read.");

				for (File file: files) {
					if (FileUtils.isSymlink(file))
						continue;						
					else if (file.isDirectory()) {
						FileUtils.deleteDirectory(file);						
					} else {
						file.delete();
					}
					
				}
			} catch (IOException e) {
				runtime.abortNode(this, e);
				throw new SAWWorkflowException("Error cleaning component directory", e);
			}
		}
	}
	
	private boolean isAsync(String property) {
		return "true".equals(property);
	}
	
	private void reuseExistingState(Node node, WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		try {
			Map<String, Object> outputs = getPreviousResults(workflow, runtime, properties);	
			
			// We're making a copy of the returned map since we want to be able to change it,
			// and node implementations may return immutable maps
			Map<String, Object> results = new HashMap<>(outputs);

			// This may add data to the "results" map	
			transmitOutputs(node, results, workflow, runtime, properties);

			// Set the Responses, and notify the engine of completed token
			complete(results, workflow, runtime);
			runtime.exitNode(SAWCustomNode.this);

		}  catch (SAWWorkflowException e) {		
			runtime.abortNode(SAWCustomNode.this, e);
			throw e;
			
		} catch (RuntimeException e) {		
			runtime.abortNode(SAWCustomNode.this, e);
			throw new SAWWorkflowException(String.format("Error in node %s", getName()), e);		
		
		} finally {
			contexts.pop();
		}		
	}	

	private void executeAsync(Engine engine, NodeToken token, Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, Node node) {
		
		try {
			Runnable r = new Runnable() {
				@Override
				public void run() {
					try {
						doActualExecute(properties, workflow, runtime, node);
					} catch (Exception e) {
						runtime.log().error("Error during asynchronous execution", e);
						runtime.abortNode(SAWCustomNode.this, e);
						engine.cancelProcess(token.getProcess());
					} finally {					
						contexts.pop();
					}	
				}
			};
			runtime.submit(r);
		
		
		} catch (RuntimeException e) {
			contexts.pop();
			runtime.exitNode(SAWCustomNode.this);
			throw e;
		}
	} 
	
	private void executeSync(Engine engine, NodeToken token, Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, Node node) {
		try {
			doActualExecute(properties, workflow, runtime, node);
			
		} catch (SAWWorkflowException e) {		
			runtime.abortNode(SAWCustomNode.this, e);
			throw e;
			
		} catch (RuntimeException e) {		
			runtime.abortNode(SAWCustomNode.this, e);
			throw new SAWWorkflowException(String.format("Error in node %s", getName()), e);		
			
		} finally {
			contexts.pop();
		}
	}

	private void doActualExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, Node node) {		
		// Execute the actual node implementation
		Map<String, Object> outputs = doExecute(properties, workflow, runtime);

		// We're making a copy of the returned map since we want to be able to change it,
		// and node implementations may return immutable maps
		Map<String, Object> results = new HashMap<>(outputs);

		// This may add data to the "results" map	
		transmitOutputs(node, results, workflow, runtime, properties);

		// Set the Responses, and notify the engine of completed token
		complete(results, workflow, runtime);
		runtime.exitNode(SAWCustomNode.this);
	} 


	// TODO Error checking -- and this is hella inefficient.
	// Properties get, in this order:
	// 1) Global parameter contents
	// 2) Input port data
	// 3) A list of standard variables.
	private void setUpProperties(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		WorkflowDefinition.Node node = workflow.getNode(getName());

		// fill in map before making substitutions so that we know if
		// privateWorkDir is true
		for (Map.Entry<String, Property> entry: node.properties.entrySet()) {			
			String propertyValue = entry.getValue().value;
			properties.put(entry.getKey(), propertyValue);
		}
		
		for (Map.Entry<String, String> entry: properties.entrySet()) {			
			properties.put(entry.getKey(), performStandardSubstitutions(workflow, runtime, entry.getValue(), properties));
		}
	}

	protected String  performStandardSubstitutions(WorkflowDefinition workflow, RuntimeData runtime, String propertyValue, Map<String, String> properties) {
		// "properties" just here so we know if there's a per-node workdir or not. Be careful with it.		
		// Substitute global parameter contents, if parameter name appears as variable in property
		for (String name: runtime.getParameterNames())  {
			if (runtime.isGlobal(name)) {
				if (propertyValue.contains("${" + name + "}")) {
					String value = String.valueOf(runtime.getParameter(name).getValue());
					propertyValue = propertyValue.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(value));
				}
			}
		}

		// Substitute port contents, if port name appears as variable in property
		for (String name: runtime.getInputNames(this.getName())) {
			if (propertyValue.contains("${" + name + "}")) {
				String value = runtime.getInput(getName(), name, String.class).toString();
				propertyValue = propertyValue.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(value));
			}
		}

		// Some magic system variables		
		propertyValue = propertyValue.replaceAll("\\$\\{workflow.nodedir\\}", Matcher.quoteReplacement(getComponentWorkDir(runtime, properties).getAbsolutePath()));

		return propertyValue;
	}
	
	
	/**
	 * This method links or copies needed files in the runtime homeDir to an individual component's working directory. The properties map should have parameter
	 * substitution already done.
	 */
	protected void stageUserDefinedInputFiles(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		defaultStageUserDefinedInputFiles(properties, workflow, runtime, getComponentWorkDir(runtime, properties));
	}

	/**
	 * This is the default implementation of {@link #stageUserDefinedInputFiles(Map, WorkflowDefinition, RuntimeData)}. The properties map should have parameter
	 * substitution already done.
	 */

	protected final void defaultStageUserDefinedInputFiles(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, File componentWorkDir) {
		WorkflowDefinition.Node node = workflow.getNode(getName());
		if (node == null) {
			throw new SAWWorkflowException("Port info missing for node \"" + getName() + "\"");
		}
		
		for (WorkflowDefinition.InputPort ip: node.inputs.values()) {
			WorkflowDefinition.Connection conn = ip.connection;
			if (conn == null)
				continue;
			// TODO Parameter substitution?
			Property trimWhitespace = conn.properties.get(TRIM_WHITESPACE);
			if (isTrue(trimWhitespace)) {
				String value = (String) runtime.getInput(getName(), ip.name, String.class);
				if (value != null) {
					runtime.putInput(false, getName(), ip.name, ip.type, value.trim());	
				}
			}
			
			Property expandWildcards = conn.properties.get(EXPAND_WILDCARDS);
			if (isTrue(expandWildcards)) {
				String value = (String) runtime.getInput(getName(), ip.name, String.class);
				if (value == null)
					throw new SAWWorkflowException("No input at port " + ip.name + " for node " + getName());
				value = value.trim();
				if (isGlobPattern(value)) {
					List<String> matches = new ArrayList<>();
					try {						
						File patternFile = new File(value);
						File container = patternFile.isAbsolute() ?
								patternFile.getParentFile() :
								new File(runtime.getHomeDir(), value).getParentFile();
						// System.out.println("Looking for " + patternFile.getName() + " in " + container.getAbsolutePath());
						glob(new File(value).getName(), container, matches);						
						matches.sort(null); // Sorting paths helps with reproducibility for reuse/restart .
						runtime.putInput(getType().equals("or"), getName(), ip.name, "array", (String[]) matches.toArray(new String[matches.size()]));
						// runtime.log().info("Expanded wildcards for input {0} on node {1}, found {2} result(s)", getName(), ip.name, matches.size());

					} catch (Exception e) {
						runtime.log().warn("Error in node ''{0}'' expanding wildcards in path for input ''{1}'': {2}",
								getName(), ip.name, 
								e.getMessage());
					}
				}						
			}
			Property newFileNameProp = conn.properties.get(NEW_FILE_NAME);
			
			String newFileName = newFileNameProp == null ? null : newFileNameProp.value.trim();
			if (newFileName != null) {
				newFileName = performStandardSubstitutions(workflow, runtime, newFileName, properties);
			}
			Property linkFile = conn.properties.get(LINK_INCOMING_FILE_TO_TARGET);
			Property copyFile = conn.properties.get(COPY_INCOMING_FILE_TO_TARGET);

			if (isTrue(linkFile) && isTrue(copyFile)) {
				throw new SAWWorkflowException(getName() + ": cannot specify both copy and link.");				
			}
			
			if (isTrue(linkFile)) {
				// TODO Could allow File, Path, other objects as well. Need to revisit the API here.
				Object value = runtime.getInput(getName(), ip.name, String[].class);	
				if (value == null)
					throw new SAWWorkflowException("No input at port " + ip.name + " for node " + getName());
				if (value instanceof String[]) {
					List<String> newFiles = new ArrayList<>();
					String[] values = (String[]) value;
					if (values.length > 1 && StringUtils.isNotBlank(newFileName) && !newFileName.contains("*")) {
						newFileName += "/*";
					}
					for (String filename: values) {
						String newFile = linkOneFile(runtime, componentWorkDir, ip, newFileName, filename);
						newFiles.add(newFile);
					}
					runtime.putInput(getType().equals("or"), getName(), ip.name, "array", (String[]) newFiles.toArray(new String[newFiles.size()]));
				} 
				// Else warn, or error?
			}
			
			if (isTrue(copyFile)) {
				// TODO Could allow File, Path, other objects as well. Need to revisit the API here.
				Object value = runtime.getInput(getName(), ip.name, String[].class);	
				if (value == null)
					throw new SAWWorkflowException("No input at port " + ip.name + " for node " + getName());
				if (value instanceof String[]) {
					List<String> newFiles = new ArrayList<>();
					String[] values = (String[]) value;
					if (values.length > 1 && StringUtils.isNotBlank(newFileName) && !newFileName.contains("*")) {
						newFileName += "/*";
					}
					for (String filename: values) {
						String newFile = copyOneFile(runtime, componentWorkDir, ip, newFileName, filename);
						newFiles.add(newFile);
					}
					runtime.putInput(getType().equals("or"), getName(), ip.name, "array", (String[]) newFiles.toArray(new String[newFiles.size()]));
				} 
				// Else warn, or error?
			}
			
			Property readInFile = conn.properties.get(READ_IN_FILE);
			if (isTrue(readInFile)) {
				// TODO Could allow File, Path, other objects as well. Need to revisit the API here.
				Object value = runtime.getInput(getName(), ip.name, String[].class);	
				if (value == null)
					throw new SAWWorkflowException("No input at port " + ip.name + " for node " + getName());
				if (value instanceof String[]) {
					String[] values = (String[]) value;
					String[] newValues = new String[values.length];
					for (int i=0; i<values.length; ++i) {
						String filename = values[i];
						File original = new File(filename);
						if (!original.isAbsolute()) {
							runtime.log().info("resolving relative pathname \"{0}\" from port \"{1}\" relative to workflow homedir", filename, ip.name);
							original = new File(runtime.getHomeDir(), filename); // TODO: Why isn't this relative to workdir?

						}
						if (original.exists()) {
							try {
								newValues[i] = FileUtils.readFileToString(original);
							} catch (IOException e) {
								throw new SAWWorkflowException(getName() + ": user-defined input file " + filename + " unreadable for node \"" + getName() + "\"");
							}
						} else {
							throw new SAWWorkflowException(getName() + ": user-defined input file " + filename + " missing for node \"" + getName() + "\"");
						}
					}
					runtime.putInput(getType().equals("or"),getName(), ip.name, "array", newValues);
				} 
				// Else warn, or error?
			}
		}
		
		// From here down is the "old stuff"
		
		// DELETED: Link home_file properties into component working directory
		
		// TODO: Delete this magic stuff next 
		for (WorkflowDefinition.InputPort port: node.inputs.values()) {
			if (propertiesContains(getDefaultInputs(), port.name))
				continue;
			if ("input_file".equals(port.type)) {
				Object value = runtime.getInput(getName(), port.name, String.class);
				if (value instanceof String && ! StringUtils.isEmpty(value.toString())) {
					// TODO -- should we reset the value of the input to point to the linked file?
					File inputFile = new File(value.toString());
					if (inputFile.exists()) {
						linkFile(componentWorkDir, inputFile, runtime);
					} else {
						// TODO Throw an exception?
					}
				}
			} else if ("exodus_file".equals(port.type)) {
				Object value = runtime.getInput(getName(), port.name, String.class);
				if (value instanceof String && ! StringUtils.isEmpty(value.toString())) {
					// TODO -- should we reset the value of the input to point to the linked file?
					File inputFile = new File(value.toString());
					if (inputFile.exists()) {
						linkFile(componentWorkDir, inputFile, runtime);
					}
					// If there are spread files, do those too
					File dir = inputFile.getParentFile();					
					String spreadPattern = inputFile.getName() + ".*.*";
					String nemPattern = inputFile.getName() + ".nem";
					if (dir != null) {
						String[] list = dir.list();
						if (list != null) {
							for (String name : list) {
								if (FilenameUtils.wildcardMatchOnSystem(name, spreadPattern)) {
									linkFile(componentWorkDir, new File(dir, name), runtime);
								} else if (name.equals(nemPattern)) {
									linkFile(componentWorkDir, new File(dir, name), runtime);
								}
							}
						}
					}
				} else {
					// TODO Throw an exception?
				}				
			}
		}
	}

	private String copyOneFile(RuntimeData runtime, File componentWorkDir, WorkflowDefinition.InputPort ip,
			String newFileName, String filename) {
		File original = new File(filename);
		if (!original.isAbsolute()) {
			runtime.log().info("resolving relative pathname \"{0}\" from port \"{1}\" relative to workflow homedir", filename, ip.name);
			original = new File(runtime.getHomeDir(), filename); // TODO: Why isn't this relative to workdir?
		}
		if (original.exists()) {
			File newFile = copyFile(componentWorkDir, original, newFileName, runtime);
			return newFile.getAbsolutePath();
		} else {
			throw new SAWWorkflowException(getName() + ": user-defined input file " + filename + " missing for node \"" + getName() + "\"");
		}
	}

	private String linkOneFile(RuntimeData runtime, File componentWorkDir, WorkflowDefinition.InputPort ip, String newFileName, String oldFileName) {
		File original = new File(oldFileName);
		if (!original.isAbsolute()) {
			runtime.log().info("resolving relative pathname \"{0}\" from port \"{1}\" relative to workflow homedir", oldFileName, ip.name);
			original = new File(runtime.getHomeDir(), oldFileName); // TODO: Why isn't this relative to workdir?
		}
		if (original.exists()) {
			File newFile = linkFile(componentWorkDir, original, newFileName, runtime);
			// runtime.log().info("retargeting filename to {0}", newFile.getAbsolutePath());
			return newFile.getAbsolutePath();
		} else {
			throw new SAWWorkflowException(getName() + ": user-defined input file " + oldFileName + " missing for node \"" + getName() + "\"");
		}
	}

	private boolean isTrue(Property p) {
		return p != null && "true".equals(p.value);
	}

	protected boolean isHomeFile(Property property) {
		return "local_file".equals(property.type) || "home_file".equals(property.type);
	}
	
	public static boolean isGlobPattern(String filename) {
		return filename.indexOf('*') > -1 || filename.indexOf('?') > -1;
	}
	/*
	 * The pattern can be a simple filename or a relative path. This routine will only handle wildcards
	 * in the filename part of the pattern. 
	 */
	public static void glob(String pattern, File location, List<String> matches) throws IOException {	
		File compositeFile = new File(location, pattern);
		location = compositeFile.getParentFile();
		pattern = compositeFile.getName();
		File[] files = location.listFiles();
		if (files != null) {
			for (File file: files) {
				if (FilenameUtils.wildcardMatchOnSystem(file.getName(), pattern)) {
					matches.add(file.getAbsolutePath());
				}
			}
		}
	}
	
	protected abstract Map<String, Object> doExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime);		
			
	/**
	 * The first argument gives the data for each output. Only arcs corresponding to outputs in this map are followed.
	 */
	protected void complete(Map<String, Object> results, WorkflowDefinition workflow, RuntimeData runtime) {
		WorkflowDefinition.Node node = workflow.getNode(getName());
		setResponses(node, results, runtime);	
		Context context = contexts.peek();
		Engine engine = context.engine;
		NodeToken token = context.token;
		if (!token.isComplete()) {
			engine.completeMany(token, results.keySet().toArray(new String[results.size()]));			
		}
	}

	private void setResponses(WorkflowDefinition.Node node, Map<String, Object> results, RuntimeData runtime) {
		for (WorkflowDefinition.Response response: node.responses.values()) {
			for (RInput input: response.inputs) {
				Object result = results.get(input.port);
				if (result != null) {
					Map<String, String> props = input.props;
					if (Objects.equals("true", String.valueOf(props.get(TRIM_WHITESPACE)))) {
						result = String.valueOf(result).trim();
					}
					
					if (Objects.equals("true", String.valueOf(props.get(EXPAND_WILDCARDS)))) {
						String stringResult = String.valueOf(result);

						if (isGlobPattern(stringResult)) {					
							List<String> matches = new ArrayList<>();
							try {						
								File patternFile = new File(stringResult);
								File container = patternFile.isAbsolute() ? patternFile.getParentFile() : runtime.getHomeDir();
								glob(new File(stringResult).getName(), container, matches);						
								matches.sort(null); // Sorting paths helps with reproducibility for reuse/restart .
								result = (String[]) matches.toArray(new String[matches.size()]);
								runtime.log().info("Expanded wildcards for response {0} from node {1}, found {2} result(s)", response.name, getName(), matches.size());

							} catch (Exception e) {
								runtime.log().warn("Error in node ''{0}'' expanding wildcards in path for response ''{1}'': {2}",
										getName(), response.name, 
										e.getMessage());
							}
						}						
					}
					if (Objects.equals("true", String.valueOf(props.get(READ_IN_FILE)))) {
						if (!(result instanceof String[])) {
							result = new String[] { String.valueOf(result) };
						}

						String[] values = (String[]) result;
						String[] newValues = new String[values.length];
						for (int i=0; i<values.length; ++i) {
							String filename = values[i];
							File original = new File(filename);
							if (!original.isAbsolute()) {
								runtime.log().info("resolving relative pathname \"{0}\" for response \"{1}\" relative to workflow homedir", filename, response.name);
								original = new File(runtime.getHomeDir(), filename); // TODO: Why isn't this relative to workdir?

							}
							if (original.exists()) {
								try {
									newValues[i] = FileUtils.readFileToString(original);
								} catch (IOException e) {
									throw new SAWWorkflowException(getName() + ": user-defined input file " + filename + " unreadable for node \"" + getName() + "\"");
								}
							} else {
								throw new SAWWorkflowException(getName() + ": user-defined input file " + filename + " missing for node \"" + getName() + "\"");
							}
						}
						result = newValues;
					}
					
					runtime.setResponse(response.name, result);
				} else {
					// TODO: add boolean flag to make responses "optional"
					runtime.log().warn(String.format("No value for response %s", response.name));
					//throw new SAWWorkflowException(String.format("No value for response %s", response.name));
				}
			}
		}
	}

	private Map<String, Object> transmitOutputs(WorkflowDefinition.Node node,
			Map<String, Object> results,
			WorkflowDefinition workflow,
			RuntimeData runtime,
			Map<String, String> properties) {
		Collection<String> outputNames = node.outputs.keySet();
		if (!outputNames.containsAll(results.keySet())) {
			List<String> bad = new ArrayList<>(results.keySet());
			bad.removeAll(outputNames);
			runtime.log().warn("Invalid output name(s) in complete() for node {0}: {1}.\n" +
			"    This may mean you have removed or renamed an output port necessary to the functioning of this node.\n" +
			"    Workflow may fail or it may appear to hang. To fix, restore the original output port configuration.", getName(), bad.toString());
		}
				
		for (WorkflowDefinition.OutputPort port: node.outputs.values()) {
			Object result = results.get(port.name);

			// Output set by calling routine
			if (result != null) {
				for (WorkflowDefinition.Connection conn: port.connections) {
					boolean purge = "or".equals(workflow.getNode(conn.node).type);
					runtime.putInput(purge, conn.node, conn.port, port.type, result);
					runtime.log().debug("Sending {0} to {1}.{2}",  render(result), conn.node, conn.port);
				}
				
			// Special handling for user-defined outputs, not set by caller
			} else if (OUTPUT_FILE.equals(port.type)) {
				String filename = getFilenameForOutputPort(properties, workflow, runtime, port);
				boolean hasResponse = hasConnectedResponse(node, port);
				if (port.connections.size() > 0 || hasResponse) {
					File file = new File(getComponentWorkDir(runtime, properties), filename);
					if (file.exists() || isGlobPattern(filename)) {
						// Inputs of other nodes
						for (WorkflowDefinition.Connection conn: port.connections) {
							boolean purge = "or".equals(workflow.getNode(conn.node).type);
							runtime.putInput(purge, conn.node, conn.port, port.type, file.getAbsolutePath());
							runtime.log().debug("Sending {0} to {1}.{2}",  render(file.getAbsolutePath()), conn.node, conn.port);
						}
						if (hasResponse) {
							runtime.log().debug("Sending {0} to connected responses",  render(file.getAbsolutePath()));
						}

						results.put(port.name, file.getAbsolutePath());
					} else {
						throw new SAWWorkflowException(getName() + ": user-defined output file " + file.getAbsolutePath() + " was not created");
					}
				}

			} else if ("exodus_file".equals(port.type)) {
				String filename = getFilenameForOutputPort(properties, workflow, runtime, port);

				if (port.connections.size() > 0) {
					File file = new File(getComponentWorkDir(runtime, properties), filename);
					if (fileOrSpreadFilesExist(file)) {
						for (WorkflowDefinition.Connection conn: port.connections) {
							boolean purge = "or".equals(workflow.getNode(conn.node).type);
							runtime.putInput(purge, conn.node, conn.port, port.type, file.getAbsolutePath());
							runtime.log().debug("Sending {0} to {1}.{2}",  render(file.getAbsolutePath()), conn.node, conn.port);
						}
						results.put(port.name, file.getAbsolutePath());
					} else {
						throw new SAWWorkflowException(getName() + ": user-defined output file " + filename + " was not created");
					}
				} else {
					// TODO This should be detected earlier!
					throw new SAWWorkflowException(getName() + ": no filename for user-defined output file on port " + port.name);					
				}
			}
		}
		return results;
	}

	private boolean hasConnectedResponse(Node node, OutputPort port) {
		for (WorkflowDefinition.Response response: node.responses.values()) {
			for (RInput triple: response.inputs ) {
				if (node.name.equals(triple.node) && port.name.equals(triple.port)){
					return true;
				}
			}
		}
		return false;
	}

	protected String getFilenameForOutputPort(Map<String, String> properties,
			WorkflowDefinition workflow,
			RuntimeData runtime,
			WorkflowDefinition.OutputPort port) {
		WorkflowDefinition.Property fn = port.properties.get("filename");
		String filename = fn != null ? fn.value : port.name;

		filename = performStandardSubstitutions(workflow, runtime, filename, properties);
		
		if (filename.contains("${") && filename.contains("}")) {
			for (String name: properties.keySet()) {
				if (filename.contains("${" + name + "}")) {
					String value = properties.get(name);
					filename = filename.replaceAll("\\$\\{" + name + "\\}", Matcher.quoteReplacement(value));
				}
			}
		}
		return filename;
	}
	
	/**
	 * Returns true if, in the absence of a supplied value, a value would be assigned to the port automatically by this class
	 */
	protected boolean hasDefaultHandler(OutputPort port) {
		String portType = port.type;
		return OUTPUT_FILE.equals(portType) || "exodus_file".equals(portType);
	}

	private boolean fileOrSpreadFilesExist(File inputFile) {
		
		if (inputFile.exists())
			return true;
		
		File dir = inputFile.getParentFile();
		String spreadPattern = inputFile.getName() + ".*.*";

		if (dir == null)
			return false;
		String[] list = dir.list();
		if (list == null)
			return false;
		for (String name : list) {
			if (FilenameUtils.wildcardMatchOnSystem(name, spreadPattern)) {
				return true;
			} 
		}
		return false;
	}

	protected Object render(Object result) {
		if (result != null && result.getClass().isArray()) {
			StringBuilder builder = new StringBuilder("array of ");
			builder.append(Array.getLength(result));
			builder.append(" ");
			builder.append(result.getClass().getSimpleName());
			builder.append("(s)");
			return builder.toString();
		} else if (result != null) {			
			String output = result.toString();
			output = StringUtils.abbreviateMiddle(output, "...", 256).trim();
			return output;
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.workflow.runtime.core.ISAWCustomNode#getDefaultProperties()
	 */
	@Override
	public List<PropertyInfo> getDefaultProperties() {
		return Collections.emptyList();
	}
	
	public static List<PropertyInfo> reservedProperties = Collections.unmodifiableList(Arrays.asList(
				new PropertyInfo(CLEAR_NODE_DIR, "boolean"),
				new PropertyInfo(ASYNC, "boolean"),
				new PropertyInfo(PRIVATE_WORK_DIR, "boolean"),
				new PropertyInfo(HIDE_IN_NAVIGATOR, "boolean")));
	
	public static List<PropertyInfo> getReservedProperties() {
		return reservedProperties;
	}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.workflow.runtime.core.ISAWCustomNode#getDefaultInputs()
	 */
	@Override
	public List<InputPortInfo> getDefaultInputs() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.workflow.runtime.core.ISAWCustomNode#getDefaultOutputs()
	 */
	@Override
	public List<OutputPortInfo> getDefaultOutputs() {
		return Collections.emptyList();
	}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.workflow.runtime.core.ISAWCustomNode#getCategory()
	 */
	@Override
	public String getCategory() {
		return "Miscellaneous";
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.workflow.runtime.core.ISAWCustomNode#getCategories()
	 */
	@Override
	public List<String> getCategories() {
		return Arrays.asList(getCategory());
	}

	protected String confirmExists(String key, Map<String, String> properties) {
		String raw = properties.get(key);
		if (raw == null)
			throw new SAWWorkflowException(getName() + ": missing required parameter " + key);
		return raw;
	}
	
	protected File getFileFromProperty(RuntimeData runtime, Map<String, String> properties, String name) {
		File result = null;
		
		String fInProps = properties.get(name);
		if (!StringUtils.isEmpty(fInProps)) {	
			result = new File(fInProps);
			if (!result.isAbsolute()) {
				runtime.log().info("resolving relative pathname \"{0}\" in property \"{1}\" relative to workflow homedir {2}", fInProps, name, runtime.getHomeDir());
				result = new File(runtime.getHomeDir(), fInProps);

			}
		}
		return result;
	}

	protected File getFileFromPort(RuntimeData runtime, String name) {
		File result = null;
		
		String fOnInput = (String) runtime.getInput(getName(), name, String.class);
		if (!StringUtils.isEmpty(fOnInput)) {
			result = new File(fOnInput);
			if (!result.isAbsolute()) {
				runtime.log().info("resolving relative pathname \"{0}\" from port \"{1}\" relative to workflow homedir", fOnInput, name);
				result = new File(runtime.getHomeDir(), fOnInput);
			}
		}
		
		return result;
	}

	/**
	 * Return a File object corresponding to the filename found on (a) the named port, or if there is no such port or the port data is empty, that found via the property of the same name.
	 * 
	 * @param runtime
	 * @param properties
	 * @param name
	 * @param reportErrors	Throw an exception if neither a port nor a property is found, or if the referenced file doesn't exist.
	 * @param linkFile		Link the referenced file into the component work directory.
	 * @return				File object corresponding to referenced file
	 */
	protected File getFileFromPortOrProperty(RuntimeData runtime,
			Map<String, String> properties,
			String name, boolean reportErrors, boolean linkFile) {
		File result = getFileFromPort(runtime, name);
		
		if (result == null) {
			result = getFileFromProperty(runtime, properties, name);
		}
		
		if (linkFile && result != null && result.exists()) {
			result = linkFile(getComponentWorkDir(runtime, properties), result, runtime);
		}

		if (reportErrors) {
			if (result == null)		
				throw new SAWWorkflowException(getName() + ": no definition for '" + name + "'");
			else if (!result.exists())
				throw new SAWWorkflowException(getName() + ": file does not exist: " + result.getAbsolutePath());
		}
				
		return result;			
	}
	
	protected File getFileFromPortOrProperty(RuntimeData data, Map<String, String> properties, String name) {
		return getFileFromPortOrProperty(data, properties, name, false, true);
	}

	protected File linkFile(File componentWorkDir, File inputFile, RuntimeData runtime) {
		return linkFile(componentWorkDir, inputFile, inputFile.getName(), runtime);
	}
	
	protected File linkFile(File componentWorkDir, File inputFile, String newFileName, RuntimeData runtime) {
		if (StringUtils.isBlank(newFileName)) {
			newFileName = inputFile.getName();
			
		} else if (newFileName.startsWith("*")) {
			newFileName = inputFile.getName();
			
		} else {
			File expander = new File(newFileName);
			if (expander.getName().contains("*")) {
				newFileName = new File(expander.getParentFile(), inputFile.getName()).getPath();
			}
		}
		
		File newFile = new File(componentWorkDir, newFileName);	
		try {
			if (inputFile.getCanonicalFile().equals(newFile.getCanonicalFile())) {
				return newFile;
			}
		} catch (IOException e) {
			throw new SAWWorkflowException(getName() + ": error analyzing file transfer", e);
		}

		if (shouldLink()) {
			File obstruction = new File(componentWorkDir, newFileName);
			if (obstruction.exists() && !obstruction.delete())
				throw new SAWWorkflowException(obstruction.getAbsolutePath() + " is in the way and cannot be deleted.");
			obstruction.getParentFile().mkdirs();
			if (!obstruction.getParentFile().exists())
				throw new SAWWorkflowException("Can't create directory " + obstruction.getParentFile().getAbsolutePath());				
			ProcessBuilder builder = new ProcessBuilder().command("ln", "-s", inputFile.getAbsolutePath(), newFileName);
			builder.directory(componentWorkDir);
			try {
				Process p = builder.start();
				int exitStatus = UNSET;
				while (exitStatus == UNSET && !runtime.isCancelled()) {
					try {
						exitStatus = p.waitFor();
						break;
					} catch (InterruptedException ex) {
						// May be a spurious wakeup. Check for cancellation, and go check exit status again.
					}
				}
				// TODO confirm success!
				return new File(componentWorkDir, newFileName);

			} catch (IOException e) {
				throw new SAWWorkflowException(getName() + ": error linking file", e);
			}
		} else {
			try {
				// TODO confirm success!
				FileUtils.copyFile(inputFile, newFile);
				return newFile;
			} catch (IOException e) {
				throw new SAWWorkflowException(getName() + ": error copying file", e);
			}
		}
	}
	protected File copyFile(File componentWorkDir, File inputFile, RuntimeData runtime) {
		return copyFile(componentWorkDir, inputFile, inputFile.getName(), runtime);
	}
	
	protected File copyFile(File componentWorkDir, File inputFile, String newFileName, RuntimeData runtime) {
		if (StringUtils.isBlank(newFileName)) {
			newFileName = inputFile.getName();
			
		} else if (newFileName.equals("*")) {
			newFileName = inputFile.getName();
			
		} else {
			File expander = new File(newFileName);
			if (expander.getName().contains("*")) {
				newFileName = new File(expander.getParentFile(), inputFile.getName()).getPath();
			}
		}
		File newFile = new File(componentWorkDir, newFileName);	
		try {
			if (inputFile.getCanonicalFile().equals(newFile.getCanonicalFile())) {
				return newFile;
			}
			File obstruction = new File(componentWorkDir, newFileName);
			if (obstruction.exists() && !obstruction.delete())
				throw new SAWWorkflowException(obstruction.getAbsolutePath() + " is in the way and cannot be deleted.");

			// TODO Confirm success!
			FileUtils.copyFile(inputFile, newFile);
			return newFile;
		} catch (IOException e) {
			throw new SAWWorkflowException(getName() + ": error copying file", e);
		}
	}


	private boolean shouldLink() {
		return (OSValidator.isMac() || OSValidator.isUnix());
	}

	protected File getComponentWorkDir(RuntimeData runtime, Map<String, String> properties) {
		if (shouldCreateComponentWorkDir(properties)) {
			File componentWorkDir = new File(runtime.getWorkDirectory(), getName());
			if (!componentWorkDir.exists()  && !componentWorkDir.mkdirs())
				throw new SAWWorkflowException(getName() + ": couldn't create working directory " + componentWorkDir.getAbsolutePath());
			return componentWorkDir;
		} else {
			return runtime.workDir;
		}
	}
	
	protected boolean shouldCreateComponentWorkDir(Map<String, String> properties) {
		String privateWorkDir = properties.get(PRIVATE_WORK_DIR);
		if (privateWorkDir == null)
			privateWorkDir = properties.get(OLD_PRIVATE_WORK_DIR);
		return !StringUtils.isEmpty(privateWorkDir) && !"false".equals(privateWorkDir);
	}
	
	// only clear if there's a private node workdir
	protected boolean shouldClearComponentWorkDir(Map<String, String> properties) {
		String clearNodeDir = properties.get(CLEAR_NODE_DIR);
		return shouldCreateComponentWorkDir(properties) && !StringUtils.isEmpty(clearNodeDir) && !"false".equals(clearNodeDir);
		
	}
	
	protected int getIntFromPortOrProperty(RuntimeData data,
			Map<String, String> properties,
			String name) {
		
		Integer result = null;
		
		try {
			String fOnInput = (String) data.getInput(getName(), name, String.class);
			if (!StringUtils.isEmpty(fOnInput)) {
				result = new Integer((int) Double.parseDouble(fOnInput));
			}
			
			if (result == null) {
				String fInProps = properties.get(name);
				if (!StringUtils.isEmpty(fInProps))
					result = new Integer((int) Double.parseDouble(fInProps));
			}
		} catch (NumberFormatException e) {
			throw new SAWWorkflowException(getName() + ": bad value for '" + name + "'", e);
		}
		
		if (result == null) {
			throw new SAWWorkflowException(getName() + ": no definition for '" + name + "'");
		}
				
		return result.intValue();
	}
	
	protected Object getObjectFromPortOrProperty(RuntimeData data,
			Map<String, String> properties,
			String name) {
		
		Object result = data.getInput(getName(), name, Object.class);

		if (result == null)
			result = properties.get(name);
		
		if (result == null) {
			throw new SAWWorkflowException(getName() + ": no definition for '" + name + "'");
		}
				
		return result;
	}
	
	protected String getOptionalStringFromPortOrProperty(RuntimeData data, Map<String, String> properties, String name) {
		String result = (String) data.getInput(getName(), name, String.class);

		return result != null ? result : properties.get(name);
	}
	
	protected boolean getOptionalBooleanProperty(Map<String, String> properties, String name) {
		String result = properties.get(name);
		return "true".equals(result);
	}

	
	protected String getStringFromPortOrProperty(RuntimeData data,
			Map<String, String> properties,
			String name) {
		
		String result = (String) data.getInput(getName(), name, String.class);

		if (result == null)
			result = properties.get(name);
		
		if (result == null) {
			throw new SAWWorkflowException(getName() + ": no definition for '" + name + "'");
		}
				
		return result;
	}
	
	protected Map<?,?> getMapFromPort(RuntimeData data,
			Map<String, String> properties,
			String name) {
		
		Object input = data.getInput(getName(), name, Map.class);
		
		if (input != null && input instanceof Map) {
			Map<?,?> result = (Map<?,?>) input;
			return result;
		}
		return null;
	}
	
	protected String getRequiredProperty(Map<String, String> properties, String name) {
		String raw = properties.get(name);	
		if (StringUtils.isEmpty(raw))
			throw new SAWWorkflowException("Value missing for required parameter '" + name + "' in node " + getName());
		return raw;
	}
	
	protected  int getRequiredIntProperty(Map<String, String> properties, String name) {
		String raw = properties.get(name);	
		if (StringUtils.isEmpty(raw) || !StringUtils.isNumeric(raw))
			throw new SAWWorkflowException("Value missing for required parameter '" + name + "' in node " + getName());
		return Integer.parseInt(raw);
	}

	@SuppressWarnings("serial")
	protected class NodeException extends SAWWorkflowException {
		public NodeException(String message, Throwable cause) {
			super("Node \"" + getName() + "\": " + message, cause);
		}

		public NodeException(String message) {
			super("Node \"" + getName() + "\": " + message);
		}
	}

	// ----------------------------------
	// EXPERIMENTAL LOOPING IMPLEMENTATION
	// ----------------------------------
	
	protected void loop(Object marker) {
		Context context = contexts.peek();
		if (marker instanceof RuntimeData.Marker) {
			NodeToken token = ((RuntimeData.Marker) marker).token;
			context.engine.backtrack(token);
		}
	}

	protected Object getMarker(RuntimeData runtime, String name) {
		return runtime.markers.get(name);
	}

	public void clearMarker(RuntimeData runtime) {
		runtime.markers.remove(getName());
	}

	public void setMarker(RuntimeData runtime, Object data) {
		Context context = contexts.peek();
		NodeToken token = context.token;
		runtime.markers.put(getName(), new RuntimeData.Marker(token, data));
	}

	public Object getMarkerData(RuntimeData runtime) {
		RuntimeData.Marker marker = runtime.markers.get(getName());
		if (marker != null)
			return marker.data;
		else
			return null;
	}

	protected boolean isConnectedOutput(String port, WorkflowDefinition workflow) {
		Node node = workflow.getNode(getName());
						
		if (!node.outputs.containsKey(port))
			return false;
		if (!node.outputs.get(port).connections.isEmpty())
			return true;
		return hasConnectedResponse(node, node.outputs.get(port));
	}

	protected boolean isConnectedInput(String port, WorkflowDefinition workflow) {
		Node node = workflow.getNode(getName());
		if (!node.inputs.containsKey(port))
			return false;
		else
			return node.inputs.get(port).isConnected();
	}
	
	public static boolean propertiesContains(List<? extends IPropertyInfo> props, String name) {
		for (IPropertyInfo prop : props) {
			if (prop.getName().equals(name))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean canReuseExistingState(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		return false;
	}
	
	@Override
	public Map<String, Object> getPreviousResults(WorkflowDefinition workflow, RuntimeData runtime, Map<String, String> properties) {
		throw new NotImplementedException("Can't override canReuseExistingState() without overriding reportExistingState()");
	}
	
	protected File stageGlobalsFile(RuntimeData runtime, Map<String, String> properties) {
		File file = new File(getComponentWorkDir(runtime, properties), getGlobalsFileName());
		
		try (PrintWriter writer = new PrintWriter(file)) {
			Properties pFile = new Properties();
			for (String name: runtime.getParameterNames()) {
				if (runtime.isGlobal(name) && !RuntimeData.isBuiltIn(name)) {
					pFile.setProperty(name, String.valueOf(runtime.getParameter(name).getValue()));
				}
			}
			pFile.store(writer, "Global parameters");
		} catch (IOException e) {
			throw new SAWWorkflowException("Error staging globals file for node: " + getName(), e);
		}
		return file;
	}
	
	private String getGlobalsFileName() {
		return "globals.in";
	}

}
