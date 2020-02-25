package gov.sandia.dart.workflow.runtime.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Connection;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Node;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.OutputPort;

class NodeSorter {
	private static class MarkedNode {
		Node node;
		boolean visited;
		MarkedNode(Node node, boolean visited) {
			this.node = node;
			this.visited = visited;
		}
	}
	private Map<String, MarkedNode> nodes; 
	
	NodeSorter(WorkflowDefinition workflow) {
		buildDataStructures(workflow);
	}
	
	 List<Node> sort() {
		List<Node> result = new ArrayList<>();

		for (Map.Entry<String, MarkedNode> entry: nodes.entrySet()) {
			if (!entry.getValue().visited)
				sort(entry.getKey(), result);	
		}
		Collections.reverse(result);
		return result;
	}

	
	
	private void sort(String name, List<Node> result) {
		nodes.get(name).visited = true;

		Node node = nodes.get(name).node;		
		for (OutputPort port: node.outputs.values()) {
			for (Connection arc: port.connections) {		
				String otherNode = arc.node;
				MarkedNode markedNode = nodes.get(otherNode);
				if (markedNode != null && !markedNode.visited) {
					sort(otherNode, result);
				}
			}
			
		}
	}
	
	private void buildDataStructures(WorkflowDefinition workflow)  {		
		nodes = new HashMap<>();

		for(String name: workflow.getNodeNames()) {
				nodes.put(name, new MarkedNode(workflow.getNode(name), false));
		}
		
	}
}
