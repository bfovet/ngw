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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

import org.apache.commons.lang3.StringUtils;

import com.googlecode.sarasvati.Graph;
import com.googlecode.sarasvati.GraphProcess;
import com.googlecode.sarasvati.impl.MapEnv;
import com.googlecode.sarasvati.mem.MemEngine;
import com.googlecode.sarasvati.mem.MemNode;
import com.googlecode.sarasvati.xml.XmlProcessDefinition;

import gov.sandia.dart.workflow.runtime.VersionInfo;
import gov.sandia.dart.workflow.runtime.components.PFile;
import gov.sandia.dart.workflow.runtime.components.ParameterFileNode;
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
	private static final String WORKFLOW_FILEDIR = "workflow.filedir";
	private static final String WORKFLOW_HOSTNAME = "workflow.hostname";
	private static final String OS_NAME = "os.name";


	private static final String USER_HOME = "user.home";
	private static final String SAMPLE_ID = "sample.id";
	private static final String USER_NAME = "user.name";
	final static Set<String> builtIns = new HashSet<>();
	static {
		builtIns.addAll(Arrays.asList(JAVA_VERSION, WORKFLOW_FILENAME, WORKFLOW_FILEDIR, WORKFLOW_HOMEDIR, WORKFLOW_WORKDIR,
				WORKFLOW_WORKDIR_NAME, USER_HOME, USER_NAME, SAMPLE_ID, WORKFLOW_HOSTNAME, OS_NAME));
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
	private volatile boolean canceled;
	private RuntimeData runtime;
	private String startNode;
	private IResponseWriter responseWriter = null;
	private UUID uuid = UUID.randomUUID();
	private String sampleId = null;
	Map<String, RuntimeParameter> parameters = new ConcurrentHashMap<>();
	private Map<String, RuntimeParameter> parametersFromParent = null;
	private File globalParameterFile;
	private Set<String> breakpoints = new HashSet<>();
	private boolean runThroughBreakpoints = false;


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
		File file = getWorkflowFile();
		String fileName = (file != null) ? file.getName() : "INTERNAL";
			
		runWorkflow(monitors, fileName, objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars);
	}

	public void run() throws SAWWorkflowException {
		IWFParser parser = new IWFParser();
		List<IWFObject> objects = parser.parse(workflowFile);
		runWorkflow(monitors, workflowFile.getName(), objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars);
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
	private volatile SAWWorkflowLogger log;
	private boolean validateUndefined = false;
	private volatile Map<String, String> preloadedProperties;	

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
		canceled = true;
				
		// Cancel any running processes. This only stops the engine. Individual nodes that can be interrupted should register an ICancelationListener.
		if (engine != null) {
			for (GraphProcess process: processes) {							
				engine.cancelProcess(process);
			}
		}	
		
		runtime.notifyCancelled();
	}
	
	private void setParameter(String name, Object value, boolean isGlobal, String source) {
		if (runtime.setParameter(name, value, isGlobal))
			runtime.log().info("assigned {0} workflow parameter {1} from {2}", isGlobal ? "global" : "local", name, source);
		else
			runtime.log().debug("ignoring value from {0} for previously assigned workflow parameter {1}", source, name);
	}
	
	private void runWorkflow(List<IWorkflowMonitor> monitors,
			String workflowFileName,
			List<IWFObject> objects,
			File homeDir,
			File workingDir,
			PrintWriter output,
			PrintWriter error,
			Map<String, Class<? extends SAWCustomNode>> customNodeTypes,
			Map<String, String> envVars) throws SAWWorkflowException {
		
		processes.clear();
		boolean closeResponseWriter = false;
		boolean closeLog = (log == null);
		// TODO Log for nested runtimes
		getLogger();
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
			runtime.log().info("Workflow engine install location: {0}", getWorkflowInstallLocation());
			runtime.log().debug("Workflow node definitions loaded");
			try {
				runtime.log().debug("Translating process definition from {0}", workflowFileName);
				
				XmlProcessDefinition def = loadWorkflow(workflowFileName, objects);
				validateWorkflow(runtime, def);
				
				runtime.log().info("Workflow homedir: {0}", runtime.getHomeDir().getAbsolutePath());
				runtime.log().info("Workflow workdir: {0}", runtime.getWorkDirectory().getAbsolutePath());
				runtime.log().debug("Starting process");

				graph = engine.getRepository().getLatestGraph(def.getName());

				long startTime = System.currentTimeMillis();

				processStartNode();
			
				defineStandardParameters();

				processGlobalParametersFile();

				processPreloadedProperties();
				
				processParametersFromParent();

				processParameterFileNodes();

				processGlobalParameters();

				// If parameters are defined in terms of one another, attempt to resolve these
				resolveParameters(runtime);
				runtime.getParameterNames().forEach((k) -> runtime.log().info("workflow parameter {0} resolved to \"{1}\"", k, runtime.getParameter(k).getValue()));

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


			} catch (SAWWorkflowException e) {
				// TODO Shutdown stuff here				
				runtime.log().error(FAILURE, e);	
				throw e;
			} catch (Throwable e) {
				// TODO Shutdown stuff here				
				runtime.log().error(FAILURE, e);
				throw new SAWWorkflowException(FAILURE, e);
			} finally {
				try {runtime.saveState();} catch (Exception ex) {}
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

	private XmlProcessDefinition loadWorkflow(String workflowFileName, List<IWFObject> objects) {
		XmlProcessDefinition def = IWFLoader.translateProcessDefinition(objects, "workflow", workflowDefinition, runtime);				
		engine.getLoader().loadDefinition(def);

		runtime.log().debug("{0} node(s) in main workflow", workflowDefinition.getNodeNames().size()); 

		runtime.log().debug("Process definition loaded: {0} from file {1}", def.getName(), workflowFileName);
		return def;
	}

	private void processStartNode() throws IOException {
		if (!StringUtils.isEmpty(startNode)) {
			runtime.log().debug("Start node: {0}", startNode);
			Set<String> dummies = StartSetCalculator.computeDummySet(workflowDefinition, startNode);
			if (StartSetCalculator.nodesIndirectlyFireNode(workflowDefinition, dummies, startNode)) {
				runtime.log().debug("Suppressing start node");
				dummies.addAll(StartSetCalculator.immediateFeederNodes(workflowDefinition, startNode));
				startNode = null;
			}
			// Could be very inefficient
			Set<String> originalDummies = new HashSet<>(dummies);
			for (String name: originalDummies) {
				if (StartSetCalculator.nodesIndirectlyFireNode(workflowDefinition, dummies, name)) {
					dummies.remove(name);
				}
			}
			
			runtime.log().debug("Muted nodes: {0}", dummies);

			List<com.googlecode.sarasvati.Node> nodes = graph.getNodes();
			for (com.googlecode.sarasvati.Node node: nodes) {
				if (node instanceof MemNode) {
					MemNode memnode = ((MemNode) node);
					memnode.setStart(false);

					if (memnode.getName().equals(startNode)) {
						memnode.setStart(true);
					} 
					
					if (originalDummies.contains(node.getName())) {
						runtime.setDummy(memnode.getName());
					}
					
					if (dummies.contains(node.getName())) {
						memnode.setStart(true);
					} 					
				}	
			}
			runtime.loadState();					
		}
		try { runtime.getWorkflowMonitors().forEach(m -> m.workflowStarted(runtime, Collections.singleton(startNode))); } catch(Throwable t) {}
	}

	private void defineStandardParameters() {
		// Some standard parameters. Can't be overridden by workflow
		runtime.setParameter(USER_NAME,             System.getProperty(USER_NAME), true);
		runtime.setParameter(SAMPLE_ID,             runtime.getSampleId(), true);
		runtime.setParameter(USER_HOME,             System.getProperty(USER_HOME), true);
		runtime.setParameter(WORKFLOW_HOMEDIR,      runtime.getHomeDir().getAbsolutePath(), true);
		runtime.setParameter(WORKFLOW_WORKDIR,      runtime.getWorkDirectory().getAbsolutePath(), true);
		runtime.setParameter(WORKFLOW_WORKDIR_NAME, runtime.getWorkDirectory().getName(), true);
		runtime.setParameter(WORKFLOW_FILENAME,     runtime.getWorkflowFile().getName(), true);
		runtime.setParameter(WORKFLOW_FILEDIR,      runtime.getWorkflowFile().getAbsoluteFile().getParent(), true);
		runtime.setParameter(JAVA_VERSION,          System.getProperty(JAVA_VERSION), true);
		runtime.setParameter(OS_NAME,               System.getProperty(OS_NAME), true);
		runtime.setParameter(WORKFLOW_HOSTNAME, 	getHostname(), true);		
	}

	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			return "localhost";
		}
	}

	private void processGlobalParameters() {
		// Lastly load definitions from workflow itself
		for (Map.Entry<String, Parameter> entry: workflowDefinition.getParameters().entrySet()) {
			Parameter p = entry.getValue();
			setParameter(entry.getKey(), String.valueOf(p.value), p.global, "workflow parameter node");
		}
	}

	private void processParametersFromParent() {
		// Values from inherited parameters also override values defined in workflow
		if (parametersFromParent != null) {
			runtime.log().info("Loading parameter values from parent workflow");
			parametersFromParent.forEach((k, v) -> setParameter(k, v.getValue(), !workflowDefinition.isLocalParameter(k), "parent workflow")); 			
		}
	}

	private void processPreloadedProperties() {
		if (preloadedProperties != null) {
			preloadedProperties.forEach((key, value) ->
				setParameter(key, value, true, "preloaded")); 
		}
	}
	
	private void processGlobalParametersFile() throws IOException, FileNotFoundException {
		// Parameter values from global parameter file override values defined in workflow,
		// but any definition in workflow determines whether parameter is global or local
		if (globalParameterFile != null) {
			if (!globalParameterFile.exists()) {
				throw new SAWWorkflowException("Global parameters file does not exist: " + globalParameterFile.getAbsolutePath());
			}
			try (FileReader gpr = new FileReader(globalParameterFile)){
				runtime.log().info("Loading parameter values from file {0}", globalParameterFile.getName());
				Properties gp = new Properties();
				gp.load(gpr);
				for (Map.Entry<Object, Object> entry: gp.entrySet()) {
					String key = String.valueOf(entry.getKey());
					String value = String.valueOf(entry.getValue());
					int dot = key.indexOf('.');
					if (dot > 0 && dot < key.length() - 1) {
						String nodeName = key.substring(0, dot);
						String propName = key.substring(dot + 1);
						Node node = workflowDefinition.getNode(nodeName);
						if (node != null) {
							String type = "default";
							Property p = node.properties.get(propName);
							if (p != null)
								type = p.type;
							node.properties.put(propName, new Property(propName, type, value));
							continue;
						}
					}
					
					setParameter(key, value, !workflowDefinition.isLocalParameter(key), "global parameter file"); 
				}
			}						
		}
	}

	private void processParameterFileNodes() {
		// Note: although it may look like there's a "ParameterFileNode" which loads the parameters, it actually happens here, 
		// before the workflow even starts. The parameterFile node just double-checks that the parameters file is valid. These
		// override declared parameters in the workflow itself.
		for (String name: workflowDefinition.getNodeNames()) {
			Node node = workflowDefinition.getNode(name);
			if ("parameterFile".equals(node.type)) {
				try {							
					Property property = node.properties.get(ParameterFileNode.FILENAME);
					String value = property.value;
					// TODO Quick hack. We need to refactor things a little so this can be properly resolved before use
					if (value.indexOf("${user.name}") > -1)
						value = value.replace("${user.name}", System.getProperty("user.name"));
					File f = new File(value);
					if (!f.isAbsolute()) {
						f = new File(runtime.getHomeDir(), value);
					}
					PFile p = new PFile(new FileReader(f));											
					p.map().entrySet().forEach(e -> setParameter(String.valueOf(e.getKey()), String.valueOf(e.getValue()),
							!workflowDefinition.isLocalParameter(String.valueOf(e.getKey())), "parameterFile node " + name));

				} catch (Exception e) {
					runtime.log().error("PropertyFile node " + name + " failed precheck", e);
				}
			}
		}
	}

	private static volatile String workflowVersion = null;
	/**
	 * NOTE: not likely to be thread safe
	 * @return The version of the workflow; if it was not set directly,
	 * then will obtain from the version as specified in the properties file
	 * @author kholson
	 * @since Sep 17, 2019
	 */
	public static String getWorkflowVersion() {
	  
	  if (workflowVersion == null) {
	    workflowVersion = VersionInfo.getVersion();
	  }
	  
	  return workflowVersion; 

	}
	
	private static volatile String workflowInstallPath = null;
	public static String getWorkflowInstallLocation() {
    final String RES = "/gov/sandia/dart/workflow/phase3/version.properties";
	  
		if (workflowInstallPath == null) {
			try {
				URL resourceUrl = WorkflowProcess.class.getResource(RES);
				if (resourceUrl != null) {
					workflowInstallPath = resourceUrl.getPath();
				}
			} catch (Exception e) {
				// Fall through
			}
			if (workflowInstallPath == null) {
				workflowInstallPath = "Install Location Not Determined";
			}
			else {
			  workflowInstallPath = workflowInstallPath.replaceAll("(/bin)?" + RES, "");
			  workflowInstallPath = workflowInstallPath.replaceAll("!$", "");

			}
		}
		return workflowInstallPath;
	}

	
	public static void setWorkflowVersion(String version) {
		workflowVersion = version;
	}
	
	private static final int PASSES=12;
	// Public for testing
	public static int resolveParameters(RuntimeData runtime) {
		Pattern var = Pattern.compile("\\$\\{([^}]+)\\}");
		int pass = 0;
		Map<String, String> systemProperties = getSystemProperties(runtime);
		for (pass=0; pass < PASSES; ++pass) {
			boolean found = false;
			for (String name: runtime.getParameterNames()) {
				String value = String.valueOf(runtime.getParameter(name).getValue());
				if (value.indexOf("$") == -1) {
					continue;					
				}
				Matcher matcher = var.matcher(value);
				boolean matched = matcher.find();
				if (matched) {
					String otherName = matcher.group(1);
					if (runtime.getParameterNames().contains(otherName)) {
						found = true;
						String otherValue = String.valueOf(runtime.getParameter(otherName).getValue());
						value = value.replace("${" + otherName + "}", otherValue);
						runtime.getParameter(name).setValue(value);
					} else if (systemProperties.containsKey(otherName)) {
						value = value.replace("${" + otherName + "}", systemProperties.get(otherName));
						runtime.getParameter(name).setValue(value);
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
				sysProps.put(WORKFLOW_WORKDIR_NAME, Matcher.quoteReplacement(runtime.getWorkDirectory().getName()));
				sysProps.put(WORKFLOW_FILENAME, Matcher.quoteReplacement(runtime.getWorkflowFile().getName()));
				sysProps.put(WORKFLOW_FILEDIR, Matcher.quoteReplacement(runtime.getWorkflowFile().getAbsoluteFile().getParent()));

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
		while (!canceled && !processes.isEmpty()) {	
			for (GraphProcess process: processes) {
				//System.out.println("Active tokens: " + process.hasActiveTokens());
				//System.out.println("Complete: " + process.isComplete());
				//System.out.println("Active arc tokens:" + process.getActiveArcTokens());
				//System.out.println("Active node tokens:" + process.getActiveNodeTokens());
				if (!process.isComplete() && !process.isCanceled() && !process.getActiveNodeTokens().isEmpty()) {
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
		return canceled;
	}

	public WorkflowProcess setStartNode(String startNode) {
		this.startNode = startNode;
		return this;
	}

	public WorkflowProcess setBreakpoints(List<String> breakpoints) {
		if (breakpoints != null) {
			this.breakpoints.addAll(breakpoints);
		}
		return this;
	}

	public WorkflowProcess setRunThroughBreakpoints(boolean runThroughBreakpoints) {
		this.runThroughBreakpoints = runThroughBreakpoints;
		return this;
	}

	public WorkflowProcess setParameter(String parameter, RuntimeParameter value) {
		parameters.put(parameter, value);
		return this;
	}
	
	public WorkflowProcess setParameters(Map<String, RuntimeParameter> values) {
		parameters.putAll(values);
		return this;
	}
	
	public RuntimeParameter getParameter(String name) {
		return parameters.get(name);
	}

	public void setParameter(String name, String value) {
		RuntimeParameter p = parameters.get(name);
		if (p != null)
			p.setValue(value);
		else
			parameters.put(name, new RuntimeParameter(name, value, "default", true, false));
	}

	public WorkflowProcess setResponseWriter(IResponseWriter responseWriter) {
		this.responseWriter = responseWriter;
		return this;
	}
	
	public WorkflowProcess setSampleId(String sampleId) {
		if (runtime != null)
			runtime.setSampleId(sampleId);		
		this.sampleId = sampleId;
		return this;
	}
	
	public String getSampleId() {
		return runtime != null ? runtime.getSampleId() : sampleId;
	}

	public boolean isBreakpoint(String name) {
		return breakpoints.contains(name);
	}
	
	public boolean isRunThroughBreakpoints() {
		return runThroughBreakpoints;
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
		engine.startProcess(graph, env);
		if (responseWriter != null)
			responseWriter.writeRow(runtime);
	}
	
	public synchronized SAWWorkflowLogger getLogger() {
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

	public synchronized void closeLogger() {
		if (log == null)
			return;
		log.close();
		log = null;
	}
	
	
	public WorkflowProcess addMonitors(List<IWorkflowMonitor> monitors) {
		monitors.addAll(monitors);
		return this;
	}

	public WorkflowProcess setValidateUndefined(boolean validateUndefined) {
		this.validateUndefined = validateUndefined;
		return this;
	}

	public Collection<String> getParameterNames() {
		return Collections.unmodifiableSet(parameters.keySet());
	}

	public WorkflowProcess setGlobalParameterFile(File globals) {
		this.globalParameterFile = globals;
		return this;		
	}

	public WorkflowProcess setParametersFromParent(Map<String, RuntimeParameter> parametersFromParent) {
		this.parametersFromParent = parametersFromParent;
		return this;		
	}

	/**
	 * Properties set here will become globals in the running workflow. 
	 */
	public WorkflowProcess preloadProperties(Map<String, String> globals) {
		preloadedProperties = globals;
		return this;
	}
}
