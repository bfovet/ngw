/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.monitoring;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.Loader;

/**
 * Bridge plugins can invoke these methods to communicate workflow events to the GUI.
 */
public class WorkflowTracker { 	
	public enum NodeExecutionStatus { PASSED, FAILED, CURRENT, NEVER, BREAK }; 

	/* ***
	 *	Maintain a set of listeners two which workflow lifecycle events can be rebroadcast
	 * ***/ 
	
	private static Set<IWorkflowListener> listeners = ConcurrentHashMap.newKeySet();
	private final static Map<IFile,
							Map<File,
								Map<String,
									NodeExecutionStatus>>> executingNodes = Collections.synchronizedMap(new HashMap<>());
	private static Map<IFile, Map<File, String>> statusMessages = Collections.synchronizedMap(new HashMap<>());
	private static Map<IFile, Loader> loaders = Collections.synchronizedMap(new HashMap<>());
	
	private static Map<String, NodeExecutionStatus> getExecutionStatusMap(IFile file, File workDir) {
		if (file == null || workDir == null)
			throw new IllegalArgumentException("Null file or workdir specified.");
		synchronized (executingNodes) {
			Map<File, Map<String, NodeExecutionStatus>> statusMapsByWorkdir = executingNodes.get(file);
			if (statusMapsByWorkdir == null) {
				statusMapsByWorkdir = Collections.synchronizedMap(new HashMap<>());
				executingNodes.put(file, statusMapsByWorkdir);
			}
			Map<String, NodeExecutionStatus> statusMap = statusMapsByWorkdir.get(workDir);
			if (statusMap == null) {
				statusMap = Collections.synchronizedMap(new HashMap<>());
				statusMapsByWorkdir.put(workDir, statusMap);
			}
			return statusMap;
		}
	}
	
	public static void addWorkflowListener(IWorkflowListener listener) {
		listeners.add(listener);
	}
	
	public static void removeWorkflowListener(IWorkflowListener listener) {
		listeners.remove(listener);
	}

	/* ***
	 * 1) Process/store workflow lifecycle events
	 * 2) Rebroadcast workflow lifecycle events to listeners
	 * ***/
	
	public static void workflowStarted(IFile workflow, File workDir, Collection<String> startNodes) {
		resetLoader(workflow);
		clearExecutingNodes(workflow, workDir, startNodes);

		for (IWorkflowListener listener: listeners) {
			listener.workflowStarted(workflow, workDir, startNodes);
		}
	}

	private static Loader getLoader(IFile workflow) {
		synchronized(loaders) {			
			Loader loader = loaders.get(workflow);
			if (loader == null && workflow != null && workflow.exists()) {
				loader = new Loader();
				loader.load(workflow.getLocation().toFile().getAbsolutePath());
				loaders.put(workflow, loader);
			}
			return loader;
		}
	}	 
	
	private static Loader resetLoader(IFile workflow) {
		synchronized(loaders) {			
			loaders.remove(workflow);
			return getLoader(workflow);
		}
	}	 
	
	public static void nodeEntered(String name, IFile workflow, File workDir) {
		addExecutingNode(name, workflow, workDir);
		for (IWorkflowListener listener: listeners) {
			listener.nodeEntered(name, workflow, workDir);
		}
	}
	
	public static void nodeExited(String name, IFile workflow, File workDir) {
		removeExecutingNode(name, workflow, workDir);

		for (IWorkflowListener listener: listeners) {
			listener.nodeExited(name, workflow, workDir);
		}
	}
	
	public static void workflowStopped(IFile workflow, File workDir) {
		clearStatusMessage(workflow, workDir);

		for (IWorkflowListener listener: listeners) {
			listener.workflowStopped(workflow, workDir);
		}		
	}
	
	public static void nodeAborted(String name, IFile workflow, File workDir, Throwable t) {
		removeExecutingNode(name, workflow, workDir);
		addAbortedNode(name, workflow, workDir, t);
		clearStatusMessage(workflow, workDir);

		for (IWorkflowListener listener: listeners) {
			listener.nodeAborted(name, workflow, workDir, t);
		}
	}
	
	public static void status(String name, IFile workflow, File workDir, String status) {
		setStatusMessage(workflow, workDir, status);

		for (IWorkflowListener listener: listeners) {
			listener.status(name, workflow, workDir, String.valueOf(status));
		}
	}
	
	public static void breakpointHit(String name, IFile workflow, File workDir) {
		addBreakNode(name, workflow, workDir);
		for (IWorkflowListener listener: listeners) {
			listener.breakpointHit(name, workflow, workDir);
		}
	}
	
	private static void clearExecutingNodes(IFile workflow, File workDir, Collection<String> startNodes) {
		Map<String, NodeExecutionStatus> nodes = getExecutionStatusMap(workflow, workDir);
		synchronized (nodes) {


			Set<String> predecessors = new HashSet<>();
			if (nodes != null) {
				Loader loader = getLoader(workflow);				
				if (loader != null) {
					Map<String, WFNode> map = loader.getNodes();
					for (String node: startNodes) {
						gatherPredecessors(node, map, predecessors);
					}

					synchronized (nodes) {
						for (String name: loader.getNodes().keySet()) {
							if (predecessors.contains(name))
								getExecutionStatusMap(workflow, workDir).put(name, NodeExecutionStatus.PASSED);
							else
								getExecutionStatusMap(workflow, workDir).put(name, NodeExecutionStatus.NEVER);
						}
					}
				}
			}
		}
	}
	
	private static void gatherPredecessors(String name, Map<String, WFNode> nodes, Set<String> predecessors) {
		WFNode node = nodes.get(name);
		if (node != null) {
			for (InputPort port: node.getInputPorts()) {
				if (!port.getArcs().isEmpty()) {
					WFNode sourceNode = port.getArcs().get(0).getSource().getNode();
					String sourceName = sourceNode.getName();
					if (!predecessors.contains(sourceName)) {
						predecessors.add(sourceName);
						gatherPredecessors(sourceName, nodes, predecessors);
					}
				}
			}
		}
	}

	/* ***
	 * Public query methods
	 * ***/
	public static NodeExecutionStatus getExecutionStatus(String name, IFile workflow, File workDir) {
		Map<String, NodeExecutionStatus> nodes = getExecutionStatusMap(workflow, workDir);
		NodeExecutionStatus status = nodes.get(name);
		return status == null ? NodeExecutionStatus.NEVER : status;
	}

	public static boolean canExecute(String name, IFile workflow, File workDir) {
		NodeExecutionStatus status = getExecutionStatus(name, workflow, workDir);
		if (status == NodeExecutionStatus.BREAK || status == NodeExecutionStatus.PASSED) {
			return true;			
		} else {
			Loader loader = getLoader(workflow);
			if (loader != null) {
				WFNode node = loader.getNodes().get(name);
				if (node != null) {
					for (InputPort port: node.getInputPorts()) {
						if (!port.getArcs().isEmpty()) {
							WFNode otherNode = port.getArcs().get(0).getSource().getNode();
							NodeExecutionStatus otherStatus = getExecutionStatus(otherNode.getName(), workflow, workDir);
							if (otherStatus != NodeExecutionStatus.PASSED)
								return false;
						}
					}
				}
				return true;
			}
		}
		return false;
	}

	
	/* ***
	 * Utility methods
	 * ***/
	
	private static void addExecutingNode(String name, IFile workflow, File workDir) {
		Map<String, NodeExecutionStatus> nodes = getExecutionStatusMap(workflow, workDir);
		nodes.put(name, NodeExecutionStatus.CURRENT);
	}
	
	private static void addBreakNode(String name, IFile workflow, File workDir) {
		Map<String, NodeExecutionStatus> nodes = getExecutionStatusMap(workflow, workDir);
		nodes.put(name, NodeExecutionStatus.BREAK);
	}

	private static void addAbortedNode(String name, IFile workflow, File workDir, Throwable t) {
		Map<String, NodeExecutionStatus> nodes = getExecutionStatusMap(workflow, workDir);
		nodes.put(name, NodeExecutionStatus.FAILED);
	}

	private static void removeExecutingNode(String name, IFile workflow, File workDir) {
		Map<String, NodeExecutionStatus> nodes = getExecutionStatusMap(workflow, workDir);
		nodes.put(name, NodeExecutionStatus.PASSED);
	}

	private static void setStatusMessage(IFile workflow, File workDir, String status) {
		if (workflow == null || workDir == null)
			throw new IllegalArgumentException("Null file or workdir specified.");
		synchronized (statusMessages) {
			Map<File, String> messagesByWorkdir = statusMessages.get(workflow);
			if (messagesByWorkdir == null) {
				messagesByWorkdir = Collections.synchronizedMap(new HashMap<>());
				statusMessages.put(workflow, messagesByWorkdir);			
			}

			messagesByWorkdir.put(workDir, status);
		}
	}

	private static void clearStatusMessage(IFile workflow, File workDir) {
		if (workflow == null || workDir == null)
			throw new IllegalArgumentException("Null file or workdir specified.");
		synchronized (statusMessages) {
			Map<File, String> messagesByWorkdir = statusMessages.get(workflow);
			if (messagesByWorkdir == null) {
				messagesByWorkdir = Collections.synchronizedMap(new HashMap<>());
				statusMessages.put(workflow, messagesByWorkdir);			
			}
			messagesByWorkdir.put(workDir, "");
		}
	}

	public static void updateRunData(IFile workflowFile, File workdir) {
		clearExecutingNodes(workflowFile, workdir, Collections.emptySet());
		if (workdir.exists()) {				
			File status = new File(workdir, "workflow.status.log");
			if (status.exists()) {
				try (FileReader sr = new FileReader(status)) {
					for (String s: IOUtils.readLines(sr)) {
						if (s.startsWith("START: ")) {
							String[] startNodes = s.substring("START: ".length()).split(",");
							if (startNodes.length > 0) {
								clearExecutingNodes(workflowFile, workdir, Arrays.asList(startNodes));	
							}
						} else if (s.startsWith("ENTER: ")) {
							WorkflowTracker.nodeEntered(s.substring("ENTER: ".length()), workflowFile, workdir);
						} else if (s.startsWith("EXIT: ")) {
							WorkflowTracker.nodeExited(s.substring("EXIT: ".length()), workflowFile, workdir);
						} else if (s.startsWith("ABORT: ")) {
							WorkflowTracker.nodeAborted(s.substring("ABORT: ".length()), workflowFile, workdir, new Exception());
						} else if (s.startsWith("BREAK: ")) {
							WorkflowTracker.breakpointHit(s.substring("BREAK: ".length()), workflowFile, workdir);
						}
					}
				} catch (IOException e) {
					WorkflowEditorPlugin.getDefault().logError("Failed to read status", e);
				}
			}
		}
	}
}
