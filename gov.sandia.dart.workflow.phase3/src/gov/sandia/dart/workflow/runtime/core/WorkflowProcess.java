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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Parameter;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFObject;
import gov.sandia.dart.workflow.runtime.parser.IWFLoader;
import gov.sandia.dart.workflow.runtime.parser.IWFParser;

public class WorkflowProcess {

	private IWorkflowMonitor monitor = NullMonitor.INSTANCE;
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
	private Map<String, String> parameters = new HashMap<>();
	private IResponseWriter responseWriter = null;
	private UUID uuid = UUID.randomUUID();
	
	public WorkflowProcess() {}
	
	public WorkflowProcess setMonitor(IWorkflowMonitor monitor) {
		this.monitor = monitor;
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
		runWorkflow(monitor, "INTERNAL", objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars, true);
	}

	public void run() throws SAWWorkflowException {
		IWFParser parser = new IWFParser();
		List<IWFObject> objects = parser.parse(workflowFile);
		runWorkflow(monitor, workflowFile.getName(), objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars, true);
	}
	
	public void dryRun() throws SAWWorkflowException {
		IWFParser parser = new IWFParser();
		List<IWFObject> objects = parser.parse(workflowFile);
		runWorkflow(monitor, workflowFile.getName(), objects, homeDir, workDir, out, err == null ? out : err, customNodes, envVars, false);
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
	
	private void runWorkflow(IWorkflowMonitor monitor,
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
		runtime = new RuntimeData(monitor, homeDir, workingDir, engine, log);
		runtime.setOut(output);
		runtime.setErr(error);		
		runtime.setWorkflowProcess(this);	
				
		for (Map.Entry<String, String> entry: envVars.entrySet()) {
			runtime.setenv(entry.getKey(), entry.getValue());
		}
		try {
			runtime.log().debug("Workflow node definitions loaded");
			try {
				runtime.log().debug("Translating process definition from {0}", workflowFileName);
				
				XmlProcessDefinition def = IWFLoader.translateProcessDefinition(objects, "workflow", workflowDefinition, runtime);				
				engine.getLoader().loadDefinition(def);

				runtime.log().debug("{0} node(s) in main workflow", workflowDefinition.getNodeNames().size()); 

				runtime.log().debug("Process definition loaded: {0} from file {1}", def.getName(), workflowFileName);
				
				validateWorkflow(def);
				
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
						runtime.setParameter(entry.getKey(), entry.getValue().value);	
					}
					for (Map.Entry<String, String> entry: parameters.entrySet()) {
						runtime.setParameter(entry.getKey(), entry.getValue());	
					}
					
					if (responseWriter == null && workflowDefinition.getResponses().size() > 0) {
						closeResponseWriter = true;
						responseWriter = runtime.openResponseWriter(workflowDefinition);
					}
					runWorkflow(workflowDefinition, runtime, responseWriter);
					
					waitForProcesses();
					runtime.awaitTermination();
					runtime.log().debug("Workflow completed successfully");
					runtime.log().info("Execution time {0} sec", (System.currentTimeMillis() - startTime) / 1000.);
				}

			} catch (SAWWorkflowException e) {
				// TODO Shutdown stuff here				
				runtime.log().error("Workflow terminated with error", e);				
				throw e;
			} catch (Throwable e) {
				// TODO Shutdown stuff here				
				runtime.log().error("Workflow terminated with error", e);
				throw new SAWWorkflowException("Workflow terminated with error", e);
			} finally {
				try {if (execute) runtime.saveState();} catch (Exception ex) {}
				// This is a bit dodgy, sorry. But if we created the log in this method, then we should close it.
				// If someone else created it earlier, then its their job.
				try {if (closeLog) log.close(); } catch (Exception ex) {}
				try {if (closeResponseWriter) responseWriter.close();} catch (Exception ex) {}
			}
		} catch (SAWWorkflowException e) {
			throw e;
		} catch (Exception e) {
			throw new SAWWorkflowException("Workflow could not start", e);
		}
	}

	public void validateWorkflow(XmlProcessDefinition def) {
		Graph graph = engine.getRepository().getLatestGraph(def.getName());
		for (com.googlecode.sarasvati.Node node: graph.getNodes()) {
			if (!NodeDatabase.hasNodeType(node.getType()) && !(customNodes != null && customNodes.containsKey(node.getType()))) {
				throw new SAWWorkflowException(String.format("Undefined node type '%s' found in workflow", node.getType()));
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
		NodeDatabase.loadDefinitions(log, new File(RuntimeData.getWFLIB(), "plugins"));
		
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

	public WorkflowProcess setParameter(String parameter, String value) {
		parameters.put(parameter, value);
		return this;
	}
	
	public WorkflowProcess setParameters(Map<String, String> values) {
		parameters.putAll(values);
		return this;
	}


	public WorkflowProcess setResponseWriter(IResponseWriter responseWriter) {
		this.responseWriter = responseWriter;
		return this;
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
		if (responseWriter != null)
			runtime.setSampleId(String.valueOf(responseWriter.getNextSampleId()));
		engine.startProcess(graph, env);
		if (responseWriter != null)
			responseWriter.writeRow(runtime);
	}
	
	public SAWWorkflowLogger getLogger() {
		try {
			if (log != null)
				return log;
			if (workDir != null) {
				log = new SAWWorkflowLogger(new File(workDir, "workflow.engine.log"));
				return log;
			}
		} catch (IOException e) {
			throw new SAWWorkflowException("Failed to open log", e);
		}
		throw new SAWWorkflowException("Can't get logger before workdir is set");
	}
}
