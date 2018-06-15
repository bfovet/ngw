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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public final class WorkflowDefinition {


	private final Map<String, Node> nodes = new HashMap<>();
	private final Map<String, Response> responses = new LinkedHashMap<>();
	private final Map<String, Parameter> parameters = new LinkedHashMap<>();
	
	public WorkflowDefinition() {
	}
	
	public static class Conductor {
		public final Map<String, Property> properties = new HashMap<>();
	}
 
	public static class Parameter {
		public final boolean global;
		public final String name;
		public String type;
		public Object value;
		
		public Parameter(String name, String type, Object value, boolean global) {
			this.name = name;
			this.type = type;
			this.value = value;
			this.global = global;			
		}
	}
	
	public static class InputPort {
		public InputPort(String name, String type) {
			this.name = name;
			this.type = type;
		}
		public final String name;
		public final String type;
		public final Map<String, Property> properties = new HashMap<>();
		public Connection connection;
		public boolean isConnected() { return connection != null; }
	}
	
	public static class OutputPort {
		public OutputPort(String name, String type) {
			this.name = name;
			this.type = type;
		}
		public final String name;
		public final String type;
		public final List<Connection> connections = new ArrayList<>();
		public final Map<String, Property> properties = new HashMap<>();
		public boolean isConnected() { return connections.size() > 0; }
	}
	
	public static class Connection {
		public Connection(String node, String port) {
			this.node = node;
			this.port = port;
		}
		public final String node;
		public final String port;
		public final Map<String, Property> properties = new HashMap<>();
	}
	
	public static class Property {
		public final String value;
		public final String type;
		public final String name;

		public Property(String name, String type, String value) {
			this.name = name;
			this.type = type;
			this.value = value;
		}
		@Override
		public String toString() {
			return "[Property(" + type + "): " + name + " = " + value + "]"; 
		}
	}
	
	public static class Node {
		public Node(String name, String type) {
			this.name = name;
			this.type = type;
		}
		public final String name;
		public final String type;
		public final Map<String, InputPort> inputs = new HashMap<>();
		public final Map<String, OutputPort> outputs = new HashMap<>();
		public final Map<String, Property> properties = new HashMap<>();
		public final Map<String, Response> responses = new LinkedHashMap<>();
		public final List<Conductor> conductors = new ArrayList<>();
	}
	
	public static class Response {
		public final String name;
		public Set<Pair<String, String>> inputs = new HashSet<>();
		
		Response(String name, String nodeName, String portName) {
			this.name = name;
			inputs.add(Pair.of(nodeName, portName));
		}
		
		public void addInput(String nodeName, String portName) {		
			inputs.add(Pair.of(nodeName, portName));
		}

		@Override
		public String toString() {
			return String.format("[Response: %s (%s -> %s)]", name, inputs); 
		}
	}

	public void addNode(String name, Node node) {
		nodes.put(name, node);
		if ("parameter".equals(node.type)) {
			Property property = node.properties.get("value");
			String value = property == null ? "" : property.value;
			String type = property == null ? "default" : property.type;
			boolean global = ! hasOutgoingConnections(node);
			parameters.put(name, new Parameter(name, type, value, global));
		}
	}

	private boolean hasOutgoingConnections(Node node) {
		for (OutputPort port: node.outputs.values()) {
			if (port.connections.size() > 0)
				return true;
		}
		return false;
	}

	public Collection<String> getNodeNames() {
		return nodes.keySet();
	}

	public Node getNode(String name) {
		return nodes.get(name);
	}

	public void addResponse(String name, String nodeName, String portName) {
		Node node = getNode(nodeName);
		if (node == null) {
			throw new SAWWorkflowException(String.format("Bad node name %s in response %s", node, name));
		}
		
		Response response = responses.get(name);
		if (response == null) {
			response = new Response(name, nodeName, portName);
			node.responses.put(name, response);
			responses.put(name, response);
		} else {
			if (node.responses.get(name) == null) {
				node.responses.put(name, response);
			}
			response.addInput(nodeName, portName);
		}
	}
	
	public Map<String, WorkflowDefinition.Response> getResponses() {
		return responses;
	}
	
	public Map<String, Parameter> getParameters() {
		return parameters;
	}
}
