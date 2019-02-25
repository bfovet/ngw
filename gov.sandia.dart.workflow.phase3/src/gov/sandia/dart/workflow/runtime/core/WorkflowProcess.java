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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.googlecode.sarasvati.Graph;
import com.googlecode.sarasvati.GraphProcess;
import com.googlecode.sarasvati.impl.MapEnv;
import com.googlecode.sarasvati.mem.MemEngine;
import com.googlecode.sarasvati.mem.MemNode;
import com.googlecode.sarasvati.xml.XmlProcessDefinition;

import gov.sandia.dart.workflow.runtime.Main;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Parameter;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFObject;
import gov.sandia.dart.workflow.runtime.parser.IWFLoader;
import gov.sandia.dart.workflow.runtime.parser.IWFParser;
import gov.sandia.dart.workflow.runtime.util.ProcessUtils;

public class WorkflowProcess {

	private static final String JAVA_VERSION = "java.version";
	private static final String WORKFLOW_FILENAME = "workflow.filename";
	private static final String WORKFLOW_WORKDIR_NAME = "workflow.workdir.name";
	private static final String WORKFLOW_WORKDIR = "workflow.workdir";
	private static final String WORKFLOW_HOMEDIR = "workflow.homedir";
	private static final String USER_HOME = "user.home";
	private static final String SAMPLE_ID = "sample.id";
	private static final String USER_NAME = "user.name";
	final static Set<String> builtIns = new HashSet<>();
	static {
		builtIns.addAll(Arrays.asList(JAVA_VERSION, WORKFLOW_FILENAME, WORKFLOW_HOMEDIR, WORKFLOW_WORKDIR,
				WORKFLOW_WORKDIR_NAME, USER_HOME, USER_NAME, SAMPLE_ID));
	}
	
	private static final String FAILURE = "Workflow terminated with error";
	public static final String SUCCESS = "Workflow completed successfully";
	public static final String WORKFLOW_ENGINE_LOG = "workflow.engine.log";
	private List<IWorkflowMonitor> monitors = new ArrayList<>();
	private File workflowFile;
	private File homeDir;
	private File workDir;
	private PrintWriter out, err;
	private static Map<String, Class<? extends SAWCustomNode>> customNodes = new ConcurrentHashMap<>();
	private Map<String, String> envVars = new HashMap<>();
	private Set<GraphProcess> processes = ConcurrentHashMap.newKeySet();
	private MemEngine engine;
	private volatile boolean cancelled;
	private RuntimeData runtime;
	private String startNode;
	private Map<String, Object> parameters = new HashMap<>();
	private IResponseWriter responseWriter = null;
	private UUID uuid = UUID.randomUUID();
	private String sampleId = null;
	
	public WorkflowProcess() {}
	
	public WorkflowProcess addMonitor(IWorkflowMonitor monitor) {
		monitors.add(monitor);
		return this;
	}
	
	public WorkflowProcess removeMonitor(IWorkflowMonitor monitor) {
		monitors.remove(monitor);
		return this;
	}

	public WorkflowProcess setWorkflowFile(File workflowFile) {
		this.workflowFile = workflowFile;
		return this;
	}


	public WorkflowProcess setHomeDir(File homeDir) {
		this.homeDir = homeDir;
		return this;
	}

	public WorkflowProcess setWorkDir(File workDir) {
		this.workDir = workDir;
		return this;
	}
	
	public File getWorkDir() {
		return workDir;
	}


	public WorkflowProcess setOut(PrintWriter out) {
		this.out = out;
		return this;
	}
	
	public WorkflowProcess setErr(PrintWriter out) {
		this.err = out;
		return this;
	}
	
	public void addCustomNode(String type,  Class<? extends SAWCustomNode> clazz) {
		customNodes.put(type, clazz);
	}
	
	public void run(List<IWFObject> objects) throws SAWWorkflowException {
		runWorkflow(monitors, "INTERNAL", objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars, true);
	}

	public void run() throws SAWWorkflowException {
		IWFParser parser = new IWFParser();
		List<IWFObject> objects = parser.parse(workflowFile);
		runWorkflow(monitors, workflowFile.getName(), objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars, true);
	}
	
	public void dryRun() throws SAWWorkflowException {
		IWFParser parser = new IWFParser();
		List<IWFObject> objects = parser.parse(workflowFile);
		runWorkflow(monitors, workflowFile.getName(), objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars, false);
	}

	
	/**
	 * Test probe
	 */
	public RuntimeData getRuntime() {
		return runtime;
	}
	
	public WorkflowDefinition getWorkflowDefinition() {
		return workflowDefinition;
	}

	
	public void addEnvVar(String name, String value) {
		envVars.put(name, value);
	}

	static Pattern envVarPattern = Pattern.compile("(\\$\\w+)\\W?");
	private Graph graph;
	private WorkflowDefinition workflowDefinition;
	private SAWWorkflowLogger log;
	private boolean validateUndefined = false;
	/**
	 * Substitutes any system environment vars into value before adding name -> value to the environment var
	 * mapping for this WorkflowProcess.
	 * 
	 * @param name
	 * @param value
	 */
	public void addEnvVarWithSystemSubs(String name, String value) {
		Map<String, String> systemEnvironment = System.getenv();
		Matcher matchPattern = envVarPattern.matcher(value);
		StringBuffer stringBuffer = new StringBuffer();
		
		while (matchPattern.find()) {
			String fragment = matchPattern.group(1);
			String possibleValueFromSystemEnvironment = systemEnvironment.get(fragment.substring(1, fragment.length()));
			if (possibleValueFromSystemEnvironment != null)
				matchPattern.appendReplacement(stringBuffer, possibleValueFromSystemEnvironment);
		}
		matchPattern.appendTail(stringBuffer);
		
		addEnvVar(name, stringBuffer.toString());
	}

	public void cancel() {	
		cancelled = true;
				
		// Cancel any running processes. This only stops the engine. Individual nodes that can be interrupted should register an ICancelationListener.
		if (engine != null) {
			for (GraphProcess process: processes) {							
				engine.cancelProcess(process);
			}
		}	
		
		runtime.notifyCancelled();
	}
	
	private void runWorkflow(List<IWorkflowMonitor> monitors,
			String workflowFileName,
			List<IWFObject> objects,
			File homeDir,
			File workingDir,
			PrintWriter output,
			PrintWriter error,
			Map<String, Class<? extends SAWCustomNode>> customNodeTypes,
			Map<String, String> envVars, 
			boolean execute) throws SAWWorkflowException {
		
		processes.clear();
		// TODO Log for nested runtimes
		getLogger();
		boolean closeResponseWriter = false, closeLog = (log == null);
		engine = createEngine(customNodeTypes, log);
		workflowDefinition = new WorkflowDefinition();	
		runtime = new RuntimeData(monitors, homeDir, workingDir, engine, log);
		runtime.setOut(output);
		runtime.setErr(error);		
		runtime.setWorkflowProcess(this);	
		if (sampleId != null)
			runtime.setSampleId(sampleId);
				
		for (Map.Entry<String, String> entry: envVars.entrySet()) {
			runtime.setenv(entry.getKey(), entry.getValue());
		}
		try {
			runtime.log().info("Workflow \"{0}\" running at {1} on {2}, pid={3}", workflowFileName, new Date(), ProcessUtils.gethost(), ProcessUtils.getpid());
			runtime.log().info("Workflow engine version: {0}", getWorkflowVersion());
			runtime.log().debug("Workflow node definitions loaded");
			try {
				runtime.log().debug("Translating process definition from {0}", workflowFileName);
				
				XmlProcessDefinition def = IWFLoader.translateProcessDefinition(objects, "workflow", workflowDefinition, runtime);				
				engine.getLoader().loadDefinition(def);

				runtime.log().debug("{0} node(s) in main workflow", workflowDefinition.getNodeNames().size()); 

				runtime.log().debug("Process definition loaded: {0} from file {1}", def.getName(), workflowFileName);
				
				validateWorkflow(runtime, def);
				
				runtime.log().info("Workflow homedir: {0}", runtime.getHomeDir().getAbsolutePath());
				runtime.log().info("Workflow workdir: {0}", runtime.getWorkDirectory().getAbsolutePath());
				runtime.log().debug("Starting process");

				graph = engine.getRepository().getLatestGraph(def.getName());

				if (!StringUtils.isEmpty(startNode)) {
					runtime.loadState();
					List<com.googlecode.sarasvati.Node> startNodes = graph.getStartNodes();
					for (com.googlecode.sarasvati.Node node: startNodes) {
						if (node instanceof MemNode) {
							((MemNode) node).setStart(false);
						}
					}
					List<com.googlecode.sarasvati.Node> nodes = graph.getNodes();
					for (com.googlecode.sarasvati.Node node: nodes) {
						if (node.getName().equals(startNode) && node instanceof MemNode) {
							((MemNode) node).setStart(true);
						}	
					}
				}
				if (execute) {
					long startTime = System.currentTimeMillis();
					for (Map.Entry<String, Parameter> entry: workflowDefinition.getParameters().entrySet()) {
						Parameter p = entry.getValue();
						runtime.setParameter(entry.getKey(), p.value, p.global);	
					}

					for (Map.Entry<String, Object> entry: parameters.entrySet()) {
						// Only global if overriding global value?
						runtime.setParameter(entry.getKey(), entry.getValue(), runtime.isGlobal(entry.getKey()));	
					}
					
					// Note: although it may look like there's a "ParameterFileNode" which loads the parameters, it actually happens here, 
					// before the workflow even starts. The parameterFile node just double-checks that the parameters file is valid.
					for (String name: workflowDefinition.getNodeNames()) {
						Node node = workflowDefinition.getNode(name);
						if ("parameterFile".equals(node.type)) {
							try {
								Property property = node.properties.get("fileName");
								File f = new File(property.value);
								if (!f.isAbsolute()) {
									f = new File(runtime.getHomeDir(), property.value);
								}
								Properties p = new Properties();
								try (FileInputStream fis = new FileInputStream(f)) {
									p.load(fis);
								}							
								p.entrySet().forEach(e -> runtime.setParameter(String.valueOf(e.getKey()), String.valueOf(e.getValue()), true));

							} catch (Exception e) {
								runtime.log().error("PropertyFile node " + name + " failed precheck", e);
							}
						}
					}

					// Some standard parameters
					runtime.setParameter(USER_NAME,             System.getProperty(USER_NAME), true);
					runtime.setParameter(SAMPLE_ID,             runtime.getSampleId(), true);
					runtime.setParameter(USER_HOME,             System.getProperty(USER_HOME), true);
					runtime.setParameter(WORKFLOW_HOMEDIR,      runtime.getHomeDir().getAbsolutePath(), true);
					runtime.setParameter(WORKFLOW_WORKDIR,      runtime.getWorkDirectory().getAbsolutePath(), true);
					runtime.setParameter(WORKFLOW_WORKDIR_NAME, runtime.getWorkDirectory().getName(), true);
					runtime.setParameter(WORKFLOW_FILENAME,     runtime.getWorkflowFile().getName(), true);
					runtime.setParameter(JAVA_VERSION,          System.getProperty(JAVA_VERSION), true);

					
					// If parameters are defined in terms of one another, attempt to resolve these
					resolveParameters(runtime.getParameters(), runtime);
					runtime.getParameters().forEach((k, v) -> runtime.log().info("Workflow parameter {0} set to {1}",k, v));
					
					if (responseWriter == null && workflowDefinition.getResponses().size() > 0) {
						closeResponseWriter = true;
						responseWriter = runtime.openResponseWriter(workflowDefinition);
					}
					runWorkflow(workflowDefinition, runtime, responseWriter);
					
					waitForProcesses();
					runtime.awaitTermination();
					runtime.log().debug(SUCCESS);
					Map<String, Object> responses = runtime.getResponses();
					if (responses.size() > 0)
						runtime.log().debug("Responses:");
					
					for (String name: responses.keySet()) {
						runtime.log().debug("  {0} = {1}", name, ResponsesOutWriter.format(responses.get(name)));
					}

					runtime.log().info("Execution time {0} sec", (System.currentTimeMillis() - startTime) / 1000.);
				}

			} catch (SAWWorkflowException e) {
				// TODO Shutdown stuff here				
				runtime.log().error(FAILURE, e);	
				throw e;
			} catch (Throwable e) {
				// TODO Shutdown stuff here				
				runtime.log().error(FAILURE, e);
				throw new SAWWorkflowException(FAILURE, e);
			} finally {
				try {if (execute) runtime.saveState();} catch (Exception ex) {}
				// This is a bit dodgy, sorry. But if we created the log in this method, then we should close it.
				// If someone else created it earlier, then its their job.
				try {if (closeLog) log.close(); } catch (Exception ex) {}
				try {if (closeResponseWriter) responseWriter.close();} catch (Exception ex) {}
			}
		} catch (SAWWorkflowException e) {
			try { runtime.getWorkflowMonitors().forEach(m -> m.terminated(runtime, e)); } catch(Throwable t) {}
			throw e;
		} catch (Exception e) {
			try { runtime.getWorkflowMonitors().forEach(m -> m.terminated(runtime, e)); } catch(Throwable t) {}
			throw new SAWWorkflowException("Workflow could not start", e);
		}
	}

	private static volatile String workflowVersion = null;
	public static String getWorkflowVersion() {
		if (workflowVersion == null) {
			try {
				InputStream stream = Main.class.getResourceAsStream("/ngw.version");
				if (stream != null) {
					String text = IOUtils.toString(stream);
					workflowVersion = text.split("=")[1];			
					IOUtils.closeQuietly(stream);
				}
			} catch (IOException e) {
				workflowVersion = "unknown";
			}
		}
		return workflowVersion;
	}
	
	public static void setWorkflowVersion(String version) {
		workflowVersion = version;
	}
	
	private static final int PASSES=12;
	// Public for testing
	public static int resolveParameters(Map<String, Object> parameters, RuntimeData runtime) {
		Pattern var = Pattern.compile("\\$\\{([^}]+)\\}");
		int pass = 0;
		Map<String, String> systemProperties = getSystemProperties(runtime);
		for (pass=0; pass < PASSES; ++pass) {
			boolean found = false;
			for (String name: parameters.keySet()) {
				String value = String.valueOf(parameters.get(name));
				if (value.indexOf("$") == -1) {
					continue;					
				}
				Matcher matcher = var.matcher(value);
				boolean matched = matcher.find();
				if (matched) {
					String otherName = matcher.group(1);
					if (parameters.containsKey(otherName)) {
						found = true;
						String otherValue = String.valueOf(parameters.get(otherName));
						value = value.replace("${" + otherName + "}", otherValue);
						parameters.put(name, value);
					} else if (systemProperties.containsKey(otherName)) {
						value = value.replace("${" + otherName + "}", systemProperties.get(otherName));
						parameters.put(name, value);
					}
				}				
			}
			if (!found)
				break;
		}			
		return pass;
	}
	private static Map<String, String> sysProps;
	private synchronized static Map<String, String> getSystemProperties(RuntimeData runtime) {
		if (sysProps == null) {
			sysProps = new HashMap<>();
			sysProps.put(USER_NAME,   Matcher.quoteReplacement(System.getProperty(USER_NAME)));
			sysProps.put(USER_HOME,        Matcher.quoteReplacement(System.getProperty(USER_HOME)));
			sysProps.put(JAVA_VERSION, Matcher.quoteReplacement(System.getProperty(JAVA_VERSION)));
			if (runtime != null) {
				sysProps.put(WORKFLOW_HOMEDIR, Matcher.quoteReplacement(runtime.getHomeDir().getAbsolutePath()));		
				sysProps.put(WORKFLOW_WORKDIR, Matcher.quoteReplacement(runtime.getWorkDirectory().getAbsolutePath()));
				sysProps.put(WORKFLOW_FILENAME, Matcher.quoteReplacement(runtime.getWorkflowFile().getName()));
			}
		}
		return sysProps;
	}
	
	

	public void validateWorkflow(RuntimeData runtime, XmlProcessDefinition def) {
		Graph graph = engine.getRepository().getLatestGraph(def.getName());
		for (com.googlecode.sarasvati.Node node: graph.getNodes()) {
			if (!NodeDatabase.hasNodeType(node.getType()) && !(customNodes != null && customNodes.containsKey(node.getType()))) {
				if (validateUndefined)
					throw new SAWWorkflowException(String.format("Undefined node type '%s' found in workflow", node.getType()));
				else
					runtime.log().warn("Undefined node type ''{0}'' found in workflow", node.getType());
			}
		}
	}

	private void waitForProcesses() {
		outer:
		while (!cancelled && !processes.isEmpty()) {			
			for (GraphProcess process: processes) {
				//System.out.println("Active tokens: " + process.hasActiveTokens());
				//System.out.println("Complete: " + process.isComplete());
				//System.out.println("Active arc tokens:" + process.getActiveArcTokens());
				//System.out.println("Active node tokens:" + process.getActiveNodeTokens());
				
				if (!process.isComplete() && !process.isCanceled()) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// Keep going
					}
					continue outer;
				}					
			}
			return;
		}		
	}

	private MemEngine createEngine(Map<String, Class<? extends SAWCustomNode>> customNodeTypes, SAWWorkflowLogger log) {
		MemEngine engine = new WorkflowEngine(this);
		NodeDatabase.loadDefinitions(log);

		Map<String, Class<? extends SAWCustomNode>> nodeTypes = NodeDatabase.nodeTypes();
		for (String type : nodeTypes.keySet()) {
			engine.addNodeType(type, nodeTypes.get(type));
		}
		
		if (customNodeTypes != null) {
			for (String type : customNodeTypes.keySet()) {
				engine.addNodeType(type, customNodeTypes.get(type));
			}
		}
		engine.addExecutionListener(WorkflowListener.class);
		
		return engine;
	}

	void addProcess(GraphProcess process) {
		processes.add(process);
	}


	void removeProcess(GraphProcess process) {
		processes.remove(process);
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public WorkflowProcess setStartNode(String startNode) {
		this.startNode = startNode;
		return this;
	}

	public WorkflowProcess setParameter(String parameter, Object value) {
		parameters.put(parameter, value);
		return this;
	}
	
	public WorkflowProcess setParameters(Map<String, Object> values) {
		parameters.putAll(values);
		return this;
	}


	public WorkflowProcess setResponseWriter(IResponseWriter responseWriter) {
		this.responseWriter = responseWriter;
		return this;
	}
	
	public WorkflowProcess setSampleId(String sampleId) {
		this.sampleId = sampleId;
		return this;
	}
	
	public String getSampleId() {
		return sampleId;
	}


	public File getWorkflowFile() {
		return workflowFile;
	}

	public String getUUID() {
		return uuid.toString();
	}

	private void runWorkflow(WorkflowDefinition workflow, RuntimeData runtime, IResponseWriter responseWriter) {
		MapEnv env = new MapEnv();
		env.setTransientAttribute(SAWCustomNode.WORKFLOW_DEFINITION, workflow);
		env.setTransientAttribute(SAWCustomNode.RUNTIME_DATA, runtime);
		//if (responseWriter != null)
		//	runtime.setSampleId(String.valueOf(responseWriter.getNextSampleId()));
		engine.startProcess(graph, env);
		if (responseWriter != null)
			responseWriter.writeRow(runtime);
	}
	
	public SAWWorkflowLogger getLogger() {
		try {
			if (log != null)
				return log;
			if (workDir != null) {
				log = new SAWWorkflowLogger(new File(workDir, WORKFLOW_ENGINE_LOG));
				return log;
			}
		} catch (IOException e) {
			throw new SAWWorkflowException("Failed to open log", e);
		}
		throw new SAWWorkflowException("Can't get logger before workdir is set");
	}

	public WorkflowProcess addMonitors(List<IWorkflowMonitor> monitors) {
		monitors.addAll(monitors);
		return this;
	}

	public WorkflowProcess setValidateUndefined(boolean validateUndefined) {
		this.validateUndefined = validateUndefined;
		return this;
	}
}
