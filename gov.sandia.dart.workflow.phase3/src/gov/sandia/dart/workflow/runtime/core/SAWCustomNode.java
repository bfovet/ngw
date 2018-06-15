/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.core;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.regex.Matcher;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.googlecode.sarasvati.CustomNode;
import com.googlecode.sarasvati.Engine;
import com.googlecode.sarasvati.NodeToken;
import com.googlecode.sarasvati.env.Env;

import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Parameter;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;
import gov.sandia.dart.workflow.runtime.util.OSValidator;

public abstract class SAWCustomNode extends CustomNode {
	private static final String LINK_INCOMING_FILE_TO_TARGET = "linkIncomingFileToTarget";
	private static final String EXPAND_WILDCARDS = "expandWildcards";
	private static final String READ_IN_FILE = "readInFile";

	private static final String PRIVATE_WORK_DIR = "privateWorkDir";
	private static int delay = 0;
	public static final String DEFAULT_INPUT = "x";
	public static final String WORKFLOW_DEFINITION = "workflowDefinition";
	public static final String RUNTIME_DATA = "runtimeData";	
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

		runtime.getWorkflowMonitor().enterNode(this, runtime);
		Context context = new Context(engine, token);
		contexts.push(context);
		Map<String, String> properties = context.properties;
		setUpProperties(properties, workflow, runtime);
		stageUserDefinedInputFiles(properties, workflow, runtime);
		slowdownForDemo(engine, runtime, token);

		if (isAsync(properties.get("async"))) {
			executeAsync(engine, token, properties, workflow, runtime, node);
		} else {
			executeSync(engine, token, properties, workflow, runtime, node);
		}
	}

	/**
	 * You can specify a delay in milliseconds  to pause before each node. This is
	 * intended for demo purposes, just so you can watch things happening that
	 * would otherwise be too fast to follow.
	 */

	private void slowdownForDemo(Engine engine, RuntimeData runtime, NodeToken token) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {
			runtime.getWorkflowMonitor().abortNode(SAWCustomNode.this, runtime, e);
			engine.cancelProcess(token.getProcess());
			throw new SAWWorkflowException("Delay interrupted");
		}
	} 
	
	private boolean isAsync(String property) {
		return "true".equals(property);
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
						runtime.getWorkflowMonitor().abortNode(SAWCustomNode.this, runtime, e);
						engine.cancelProcess(token.getProcess());
					} finally {					
						contexts.pop();
					}	
				}
			};
			runtime.submit(r);
		
		
		} catch (RuntimeException e) {
			contexts.pop();
			runtime.getWorkflowMonitor().exitNode(SAWCustomNode.this, runtime);
			throw e;
		}
	} 
	
	private void executeSync(Engine engine, NodeToken token, Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, Node node) {
		try {
			doActualExecute(properties, workflow, runtime, node);
			
		} catch (SAWWorkflowException e) {		
			runtime.getWorkflowMonitor().abortNode(SAWCustomNode.this, runtime, e);
			throw e;
			
		} catch (RuntimeException e) {		
			runtime.getWorkflowMonitor().abortNode(SAWCustomNode.this, runtime, e);
			throw new SAWWorkflowException(String.format("Error in node %s", getName()), e);		
			
		} finally {
			contexts.pop();
		}
	}

	private void doActualExecute(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime, Node node) {

		// Record the inputs. For now, this is only for debugging.
		// TODO Make this optional
		// recordInputs(node, properties, runtime);
		
		// Execute the actual node implementation
		Map<String, Object> outputs = doExecute(properties, workflow, runtime);

		// We're making a copy of the returned map since we want to be able to change it,
		// and node implementations may return immutable maps
		Map<String, Object> results = new HashMap<>(outputs);	
		
		// This may add data to the "results" map	
		transmitOutputs(node, results, workflow, runtime, properties);
		
		// Record the outputs. For now, this is only for debugging.
	    // recordOutputs(results, runtime);
		
		// Set the Responses, and notify the engine of completed token
		complete(results, workflow, runtime);		
		runtime.getWorkflowMonitor().exitNode(SAWCustomNode.this, runtime);
	} 

	
	private void recordOutputs(Map<String, Object> outputs, Map<String, String> properties, RuntimeData runtime) {
		// Inputs are more complicated. This is not going to do the trick
		{
			Properties p = new Properties();
			for  (String port: outputs.keySet()) {
				String output = String.valueOf(outputs.get(port));
				output = StringUtils.abbreviateMiddle(output, "...", 128);
				p.setProperty(port, output);
			}
			
			try (FileOutputStream fos = new FileOutputStream(new File(getComponentWorkDir(runtime, properties), "wfnode_outputs.txt"))) {
				p.store(fos, "// Outputs for node");
			} catch (IOException ioe) {
				runtime.log().error("Non-fatal error recording outputs for node " + getName(), ioe);
			}
		}
	}

	private void recordInputs(Node node, Map<String, String> properties, RuntimeData runtime) {
		// Properties are easy
		{
			Properties p = new Properties();
			p.putAll(properties);
			try (FileOutputStream fos = new FileOutputStream(new File(getComponentWorkDir(runtime, properties), "wfnode_properties.txt"))) {
				p.store(fos, "// Properties for node");
			} catch (IOException ioe) {
				runtime.log().error("Non-fatal error recording properties for node " + getName(), ioe);
			}
		}
		
		// Inputs are more complicated. This is not going to do the trick
		{
			Properties p = new Properties();
			for  (String port: runtime.getInputNames(node.name)) {
				String input = (String) runtime.getInput(node.name, port, String.class);
				input = StringUtils.abbreviateMiddle(input, "...", 128);
				p.setProperty(port, input);
			}
			
			try (FileOutputStream fos = new FileOutputStream(new File(getComponentWorkDir(runtime, properties), "wfnode_inputs.txt"))) {
				p.store(fos, "// Inputs for node");
			} catch (IOException ioe) {
				runtime.log().error("Non-fatal error recording inputs for node " + getName(), ioe);
			}
		}
	}


	// TODO Error checking -- and this is hella inefficient.
	// Properties get, in this order:
	// 1) Global parameter contents
	// 2) Input port data
	// 3) A list of standard variables.
	private void setUpProperties(Map<String, String> properties, WorkflowDefinition workflow, RuntimeData runtime) {
		WorkflowDefinition.Node node = workflow.getNode(getName());
		for (Map.Entry<String, Property> entry: node.properties.entrySet()) {			
			String propertyValue = entry.getValue().value;

			propertyValue = performStandardSubstitutions(workflow, runtime, propertyValue, properties);

			properties.put(entry.getKey(), propertyValue);
		}
	}

	protected String  performStandardSubstitutions(WorkflowDefinition workflow, RuntimeData runtime, String propertyValue, Map<String, String> properties) {
		// "properties" just here so we know if there's a per-node workdir or not. Be careful with it.
		
		// Substitute global parameter contents, if parameter name appears as variable in property
		for (Map.Entry<String, Parameter> pEntry: workflow.getParameters().entrySet())  {
			String name = pEntry.getKey();
			if (propertyValue.contains("${" + name + "}")) {
				if (pEntry.getValue().global) {
					String value = String.valueOf(runtime.getParameter(name));
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
		propertyValue = propertyValue.replaceAll("\\$\\{user.name\\}",        Matcher.quoteReplacement(System.getProperty("user.name")));
		propertyValue = propertyValue.replaceAll("\\$\\{user.home\\}",        Matcher.quoteReplacement(System.getProperty("user.home")));
		propertyValue = propertyValue.replaceAll("\\$\\{workflow.homedir\\}", Matcher.quoteReplacement(runtime.getHomeDir().getAbsolutePath()));
		propertyValue = propertyValue.replaceAll("\\$\\{workflow.workdir\\}", Matcher.quoteReplacement(runtime.getWorkDirectory().getAbsolutePath()));
		propertyValue = propertyValue.replaceAll("\\$\\{workflow.nodedir\\}", Matcher.quoteReplacement(getComponentWorkDir(runtime, properties).getAbsolutePath()));
		propertyValue = propertyValue.replaceAll("\\$\\{workflow.filename\\}", Matcher.quoteReplacement(runtime.getWorkflowFile().getName()));
		propertyValue = propertyValue.replaceAll("\\$\\{java.version\\}", System.getProperty("java.version"));
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

			Property expandWildcards = conn.properties.get(EXPAND_WILDCARDS);
			if (expandWildcards != null && "true".equals(expandWildcards.value)) {
				String value = (String) runtime.getInput(getName(), ip.name, String.class);	
				if (isGlobPattern(value)) {					
					List<String> matches = new ArrayList<>();
					try {						
						File patternFile = new File(value);
						File container = patternFile.isAbsolute() ? patternFile.getParentFile() : runtime.getHomeDir();
						glob(new File(value).getName(), container, matches);
						runtime.putInput(getName(), ip.name, "array", (String[]) matches.toArray(new String[matches.size()]));
					} catch (IOException e) {
						runtime.log().warn("Error expanding wildcards in path", e.getMessage());
					}
				}						
			}
			Property property = conn.properties.get(LINK_INCOMING_FILE_TO_TARGET);
			if (property != null && "true".equals(property.value)) {
				// TODO Could allow File, Path, other objects as well. Need to revisit the API here.
				Object value = runtime.getInput(getName(), ip.name, String[].class);	
				if (value instanceof String[]) {
					for (String filename: (String[]) value) {
						File original = new File(filename);
						if (!original.isAbsolute()) {
							runtime.log().info("resolving relative pathname \"{0}\" from port \"{1}\" relative to workflow homedir", filename, ip.name);
							original = new File(runtime.getHomeDir(), filename); // TODO: Why isn't this relative to workdir?
						}
						if (original.exists()) {
							linkFile(componentWorkDir, original);
						} else {
							throw new SAWWorkflowException(getName() + ": user-defined input file " + filename + " missing for node \"" + getName() + "\"");
						}
					}
				} 
				// Else warn, or error?
			}
			
			Property readInFile = conn.properties.get(READ_IN_FILE);
			if (readInFile != null && "true".equals(readInFile.value)) {
				// TODO Could allow File, Path, other objects as well. Need to revisit the API here.
				Object value = runtime.getInput(getName(), ip.name, String[].class);	
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
					runtime.putInput(getName(), ip.name, "array", newValues);
				} 
				// Else warn, or error?
			}
		}
		
		// From here down is the "old stuff"
		
		// DELETED: Link home_file properties into component working directory
		
		// TODO: Delete this magic stuff next 
		for (WorkflowDefinition.InputPort port: node.inputs.values()) {
			if (getDefaultInputNames().contains(port.name))
				continue;
			if ("input_file".equals(port.type)) {
				Object value = runtime.getInput(getName(), port.name, String.class);
				if (value instanceof String && ! StringUtils.isEmpty(value.toString())) {
					// TODO -- should we reset the value of the input to point to the linked file?
					File inputFile = new File(value.toString());
					if (inputFile.exists()) {
						linkFile(componentWorkDir, inputFile);
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
						linkFile(componentWorkDir, inputFile);
					}
					// If there are spread files, do those too
					File dir = inputFile.getParentFile();
					String spreadPattern = inputFile.getName() + ".*.*";
					String nemPattern = inputFile.getName() + ".nem";

					for (String name : dir.list()) {
						if (FilenameUtils.wildcardMatchOnSystem(name, spreadPattern)) {
							linkFile(componentWorkDir, new File(dir, name));
						} else if (name.equals(nemPattern)) {
							linkFile(componentWorkDir, new File(dir, name));
						}
					}
				} else {
					// TODO Throw an exception?
				}				
			}
		}
	}

	protected boolean isHomeFile(Property property) {
		return "local_file".equals(property.type) || "home_file".equals(property.type);
	}
	
	private boolean isGlobPattern(String filename) {
		return filename.indexOf('*') > -1 || filename.indexOf('?') > -1;
	}
	
	private static void glob(String pattern, File location, List<String> matches) throws IOException {		
		File[] files = location.listFiles();
		for (File file: files) {
			if (FilenameUtils.wildcardMatch(file.getName(), pattern)) {
				matches.add(file.getAbsolutePath());
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

	private void setResponses(WorkflowDefinition.Node node,
			Map<String, Object> results, RuntimeData runtime) {
		for (WorkflowDefinition.Response response: node.responses.values()) {
			for (Pair<String, String> input: response.inputs) {
			Object result = results.get(input.getValue());
			if (result != null) {
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
			runtime.log().debug("Warning: Invalid output name(s) in complete() for node \"" + getName() + "\":" + bad.toString());
		}
				
		for (WorkflowDefinition.OutputPort port: node.outputs.values()) {
			Object result = results.get(port.name);

			// Output set by calling routine
			if (result != null) {
				for (WorkflowDefinition.Connection conn: port.connections) {
					runtime.putInput(conn.node, conn.port, port.type, result);
					runtime.log().debug("Sending {0} to {1}.{2}",  render(result), conn.node, conn.port);
				}
				
			// Special handling for user-defined outputs, not set by caller
			} else if ("output_file".equals(port.type)) {
				String filename = getFilenameForOutputPort(properties, workflow, runtime, port);
				
				if (port.connections.size() > 0) {
					File file = new File(getComponentWorkDir(runtime, properties), filename);
					if (file.exists() || isGlobPattern(filename)) {
						for (WorkflowDefinition.Connection conn: port.connections) {
							runtime.putInput(conn.node, conn.port, port.type, file.getAbsolutePath());
							runtime.log().debug("Sending {0} to {1}.{2}",  render(file.getAbsolutePath()), conn.node, conn.port);
						}
						results.put(port.name, file.getAbsolutePath());
					} else {
						throw new SAWWorkflowException(getName() + ": user-defined output file " + filename + " was not created");
					}
				}

			} else if ("exodus_file".equals(port.type)) {
				String filename = getFilenameForOutputPort(properties, workflow, runtime, port);

				if (port.connections.size() > 0) {
					File file = new File(getComponentWorkDir(runtime, properties), filename);
					if (fileOrSpreadFilesExist(file)) {
						for (WorkflowDefinition.Connection conn: port.connections) {
							runtime.putInput(conn.node, conn.port, port.type, file.getAbsolutePath());
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
		return "output_file".equals(portType) || "exodus_file".equals(portType);
	}

	private boolean fileOrSpreadFilesExist(File inputFile) {
		
		if (inputFile.exists())
			return true;
		
		File dir = inputFile.getParentFile();
		String spreadPattern = inputFile.getName() + ".*.*";

		for (String name : dir.list()) {
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
			output = StringUtils.abbreviateMiddle(output, "...", 30).trim();
			return output;
		} else {
			return null;
		}
	}
	
	/**
	 * @return The default list of properties built into this node. The runtime
	 *         property list derived from the diagram may be larger or smaller.
	 */
	public List<String> getDefaultProperties() {
		return Collections.emptyList();
	}

	/**
	 * @return The data types of the default list of properties built into this node. The runtime
	 *         property list derived from the diagram may be larger or smaller.
	 */
	public List<String> getDefaultPropertyTypes() {
		List<String> types = new ArrayList<>();
		getDefaultProperties().forEach(unused -> types.add(RuntimeData.DEFAULT_TYPE)); 			
		return types;
	}
	
	/**
	 * @return The default list of input ports built into this node. The runtime
	 *         port list derived from the diagram may be larger or smaller.
	 */
	public List<String> getDefaultInputNames() {
		return Collections.emptyList();
	}
	
	/**
	 * @return The data types of the default list of input ports built into this node. The runtime
	 *         port list derived from the diagram may be larger or smaller.
	 */
	public List<String> getDefaultInputTypes() {
		List<String> types = new ArrayList<>();
		getDefaultInputNames().forEach(unused -> types.add(RuntimeData.DEFAULT_TYPE));
		return types;
	}
	
	/**
	 * @return The default list of output ports built into this node. The runtime
	 *         port list derived from the diagram may be larger or smaller.
	 */
	public List<String> getDefaultOutputNames() {
		return Collections.emptyList();
	}
	
	/**
	 * @return The data types of the default list of output ports built into this node. The runtime
	 *         port list derived from the diagram may be larger or smaller.
	 */
	public List<String> getDefaultOutputTypes() {
		List<String> types = new ArrayList<>();
		getDefaultOutputNames().forEach(unused -> types.add(RuntimeData.DEFAULT_TYPE));
		return types;
	}

	public String getCategory() {
		return "Miscellaneous";
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
				result = new File(runtime.getHomeDir(), fOnInput); // TODO: Why isn't this relative to workdir?
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
			result = linkFile(getComponentWorkDir(runtime, properties), result);
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
	
	protected File linkFile(File componentWorkDir, File inputFile) {
		File newFile = new File(componentWorkDir, inputFile.getName());	
		try {
			if (inputFile.getCanonicalFile().equals(newFile.getCanonicalFile())) {
				return newFile;
			}
		} catch (IOException e) {
			throw new SAWWorkflowException(getName() + ": error analyzing file transfer", e);
		}

		if (shouldLink()) {
			ProcessBuilder builder = new ProcessBuilder("ln", "-s", inputFile.getAbsolutePath(), inputFile.getName());
			builder.directory(componentWorkDir);
			try {
				Process start = builder.start();
				start.waitFor();
				return new File(componentWorkDir, inputFile.getName());

			} catch (IOException | InterruptedException e) {
				throw new SAWWorkflowException(getName() + ": error linking file", e);
			}
		} else {
			try {
				FileUtils.copyFile(inputFile, newFile);
				return newFile;
			} catch (IOException e) {
				throw new SAWWorkflowException(getName() + ": error copying file", e);
			}
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
		return !StringUtils.isEmpty(privateWorkDir) && !"false".equals(privateWorkDir);
	}

	public static void setDelay(int delay) {
		SAWCustomNode.delay = delay;
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
	
	protected  int getRequiredIntPropery(Map<String, String> properties, String name) {
		String raw = properties.get(name);	
		if (StringUtils.isEmpty(raw) || !StringUtils.isNumeric(raw))
			throw new SAWWorkflowException("Value missing for required parameter '" + name + "' in node " + getName());
		return Integer.parseInt(raw);
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
		else
			return node.outputs.get(port).isConnected();
	}

	protected boolean isConnectedInput(String port, WorkflowDefinition workflow) {
		Node node = workflow.getNode(getName());
		if (!node.inputs.containsKey(port))
			return false;
		else
			return node.inputs.get(port).isConnected();
	}
}
