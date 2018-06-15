/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.runtime.parser;

import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.sandia.dart.workflow.runtime.core.RuntimeData;
import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition;
import gov.sandia.dart.workflow.runtime.core.WorkflowDefinition.Property;

public class CustomXMLParser {
	public static void parse(List<Object> customs, WorkflowDefinition def, RuntimeData data) {
		for (Object o : customs) {
			if (o instanceof Element) {
				Element e = (Element) o;
				if ("ports".equals(e.getTagName())) {
					setupPorts(e, def);
				} else if ("responses".equals(e.getTagName())) {
					setupResponses(e, def);
				} 
			}
		}
	}	
	
	private static void setupResponses(Element e, WorkflowDefinition def) {
		NodeList params = e.getElementsByTagName("response");
		for (int i = 0; i < params.getLength(); ++i) {
			Element param = (Element) params.item(i);			
			String name = param.getAttribute("name");
			String node = param.getAttribute("node");
			String port = param.getAttribute("port");
			def.addResponse(name, node, port);
		}
	}

	
	public static void setupPorts(Element e, WorkflowDefinition def) {
		NodeList nodes = e.getElementsByTagName("node");
		for (int i = 0; i < nodes.getLength(); ++i) {
			Element nodeE = (Element) nodes.item(i);
			String nodeName = getChildText(nodeE, "name");
			String nodeType = getChildText(nodeE, "type");

			WorkflowDefinition.Node node = new WorkflowDefinition.Node(nodeName, nodeType);

			NodeList inputs = nodeE.getElementsByTagName("input");
			for (int j = 0; j < inputs.getLength(); ++j) {
				Element inputE = (Element) inputs.item(j);
				String inputName = getChildText(inputE, "name");
				String inputType = getChildText(inputE, "type");		
				WorkflowDefinition.InputPort input = new WorkflowDefinition.InputPort(inputName, inputType);
				NodeList properties = inputE.getElementsByTagName("property");
				for (int m = 0; m < properties.getLength(); ++m) {
					Element propE = (Element) properties.item(m);
					String name = getChildText(propE, "name");
					String type = getChildText(propE, "type");
					String value = getChildText(propE, "value");
					input.properties.put(name, new Property(name, type, value));
				}
				node.inputs.put(inputName, input);
			}

			NodeList outputs = nodeE.getElementsByTagName("output");
			for (int j = 0; j < outputs.getLength(); ++j) {
				Element outputE = (Element) outputs.item(j);
				String outputName = getChildText(outputE, "name");
				String outputType = getChildText(outputE, "type");
				WorkflowDefinition.OutputPort output = new WorkflowDefinition.OutputPort(outputName, outputType);

				NodeList connections = outputE.getElementsByTagName("connection");
				for (int k = 0; k < connections.getLength(); ++k) {
					Element connE = (Element) connections.item(k);
					String targetNode = getChildText(connE, "targetNode");
					String targetPort = getChildText(connE, "targetPort");
					WorkflowDefinition.Connection conn = new WorkflowDefinition.Connection(targetNode, targetPort);
					NodeList properties = getProperties(connE);
					if (properties != null) {
						for (int m = 0; m < properties.getLength(); ++m) {
							Element propE = (Element) properties.item(m);
							String name = getChildText(propE, "name");
							String type = getChildText(propE, "type");
							String value = getChildText(propE, "value");
							conn.properties.put(name, new Property(name, type, value));
						}
					}
					output.connections.add(conn);
				}
				NodeList properties = getProperties(outputE); 
				if (properties != null) {
					for (int m = 0; m < properties.getLength(); ++m) {
						Element propE = (Element) properties.item(m);
						String name = getChildText(propE, "name");
						String type = getChildText(propE, "type");
						String value = getChildText(propE, "value");
						output.properties.put(name, new Property(name, type, value));
					}
				}
				node.outputs.put(outputName, output);				
			}

			NodeList conductors = nodeE.getElementsByTagName("conductor");
			if (conductors != null) {
				for (int j = 0; j < conductors.getLength(); ++j) {
					WorkflowDefinition.Conductor cond = new WorkflowDefinition.Conductor();

					Element condE = (Element) conductors.item(j);
					NodeList properties = getProperties(condE);
					if (properties != null) {
						for (int m = 0; m < properties.getLength(); ++m) {
							Element propE = (Element) properties.item(m);
							String name = getChildText(propE, "name");
							String type = getChildText(propE, "type");
							String value = getChildText(propE, "value");
							cond.properties.put(name, new Property(name, type, value));
						}
					}
					node.conductors.add(cond);
				}
			}

			NodeList properties = getProperties(nodeE);
			if (properties != null) {
				for (int j = 0; j < properties.getLength(); ++j) {
					Element propE = (Element) properties.item(j);
					String name = getChildText(propE, "name");
					String type = getChildText(propE, "type");
					String value = getChildText(propE, "value");
					node.properties.put(name, new Property(name, type, value));
				}
			}
			


			def.addNode(nodeName, node);
		}
		
		// Backpatch connections to inputs
		for (String name: def.getNodeNames()) {
			try {
				WorkflowDefinition.Node node = def.getNode(name);
				for (WorkflowDefinition.OutputPort op: node.outputs.values()) {
					for (WorkflowDefinition.Connection conn: op.connections) {
						String targetNode = conn.node;
						String targetPort = conn.port;
						WorkflowDefinition.InputPort ip = def.getNode(targetNode).inputs.get(targetPort);
						ip.connection = conn;
					}
					
				}
			} catch (NullPointerException e1) {
				throw new SAWWorkflowException("Invalid workflow specification", e1);
			}
		}
		
	}

	private static NodeList getProperties(Element nodeE) {		
		NodeList children = nodeE.getChildNodes();
		for (int m = 0; m < children.getLength(); ++m) {
			Node child = children.item(m);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element childE = (Element) child;
				if ("properties".equals(childE.getTagName())) {
					return childE.getElementsByTagName("property");
				}				
			}		
		}
		return null;
	}

	private static String getChildText(Element node, String name) {
		NodeList list = node.getElementsByTagName(name);
		if (list.getLength() == 0)
			return null;
		else
			return list.item(0).getTextContent().trim();
	}

}
