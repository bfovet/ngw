package gov.sandia.dart.workflow.runtime.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Connection;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.InputPort;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;

public class StartSetCalculator {

	public static Set<String> computeStartSet(WorkflowDefinition workflow, String startName) {
		Set<String> set = new HashSet<>();
		Node startNode = workflow.getNode(startName);
		if (startNode == null)
			throw new SAWWorkflowException("Nonexistent start node specified: " + startName);
		
		// Add all nodes to the set
		set.addAll(workflow.getNodeNames());
		
		// Remove nodes before index if they feed into startNode
		removeFeederNodes(startNode, workflow, set);
		
		// Remove internal nodes
		set.removeIf((x) -> isInternal(x, workflow)); 
		
		// Add back the actual start node
		set.add(startName);
		return set;
	}
	
	public static Set<String> computeDummySet(WorkflowDefinition workflow, String startName) {
		Set<String> reachableNodes = new HashSet<>();
		Set<String> dummies = new HashSet<>();

		Node startNode = workflow.getNode(startName);
		if (startNode == null)
			throw new SAWWorkflowException("Nonexistent start node specified: " + startName);

		// Collect all the nodes reachable from the start node
		reachableNodes.add(startName);
		addDownstreamNodesToSet(workflow, startNode, reachableNodes);
		
		// For each reachable node, if it has any connected inputs
		// coming from outside this set, mark the outside node as a dummy.
		for (String nodeName: reachableNodes) {
			Node node = workflow.getNode(nodeName);
			if (node == startNode)
				continue;
			for (InputPort port: node.inputs.values()) {
				if (port.isConnected()) {
					if (!reachableNodes.contains(port.connection.sourceNode)) {
						dummies.add(port.connection.sourceNode);
					}
				}
			}
		}
		
		return dummies;
	}

	private static void addDownstreamNodesToSet(WorkflowDefinition workflow, Node node, Set<String> set) {
		for (OutputPort port: node.outputs.values()) {
			for (Connection connection: port.connections) {
				String nextName = connection.node;
				if (!set.contains(nextName)) {
					set.add(nextName);
					Node nextNode = workflow.getNode(nextName);
					addDownstreamNodesToSet(workflow, nextNode, set);
				}
			}
		}
	}

	static void removeFeederNodes(Node startNode, WorkflowDefinition workflow, Set<String> set) {
		if (set.contains(startNode.name)) {
			set.remove(startNode.name);
			for (InputPort port: startNode.inputs.values()) {
				if (port.isConnected()) {
					Node otherNode = workflow.getNode(port.connection.sourceNode);			
					removeFeederNodes(otherNode, workflow, set);
				}
			}	
		}
	}

	static boolean isInternal(String name, WorkflowDefinition workflow) {
		Node node = workflow.getNode(name);
		for (InputPort port: node.inputs.values()) {
			if (port.isConnected())
				return true;
		}
		return false;
	}

	public static boolean nodesIndirectlyFireNode(WorkflowDefinition workflow, Set<String> dummies, String startNode) {
		Set<String> downstream = new HashSet<>();
		for (String dummy: dummies) {
			Node node = workflow.getNode(dummy);
			addDownstreamNodesToSet(workflow, node, downstream);			
		}
		return downstream.contains(startNode);
	}

	public static Collection<String> immediateFeederNodes(WorkflowDefinition workflow, String startName) {
		Node startNode = workflow.getNode(startName);
		Set<String> feeders = new HashSet<>();
		for (InputPort port: startNode.inputs.values()) {
			if (port.isConnected()) {
				feeders.add(port.connection.sourceNode);				
			}
		}
		return feeders;
	}

}
