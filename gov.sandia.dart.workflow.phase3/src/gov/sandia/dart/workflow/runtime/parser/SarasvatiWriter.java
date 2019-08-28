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


import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import gov.sandia.dart.workflow.runtime.parser.Domain.IWFArc;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFConductor;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFInputPort;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFNode;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFOutputPort;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFParameter;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFProperty;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponse;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponseArc;
import gov.sandia.dart.workflow.runtime.util.Indenter;

public class SarasvatiWriter {

	private static List<Object> cullWorkflowObjects(List<?> objects) {
		List<Object> resources = new ArrayList<>();
		for (Object o: objects) {				
			if (o instanceof IWFNode || o instanceof IWFArc || o instanceof IWFParameter || o instanceof IWFResponse) {
				resources.add(o);
			}
		}
		return resources;
	}
	
	
	public static String emitSarasvatiWorkflow(List<?> objects) {
		objects = cullWorkflowObjects(objects);
		StringWriter w = new StringWriter();
		Indenter out = new Indenter(w);
		out.printIndented("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		out.printAndIndent("<process-definition xmlns=\"http://sarasvati.googlecode.com/ProcessDefinition\" name=\"workflow\">");
		out.printAndIndent("<custom>");
		// TODO Omit LBEGIN/LEND ports; set target property instead.
		emitPortAndPropertyInfo(objects, out);
		emitParameterInfo(objects, out);
		emitResponseInfo(objects, out);
		out.unindentAndPrint("</custom>");
		emitNodeInfo(objects, out);

		out.unindentAndPrint("</process-definition>");
		out.getPrintWriter().close();
		return w.toString();
	}


	private static void emitNodeInfo(List<?> objects, Indenter out) {
		for (Object object: objects) {
			if (object instanceof IWFNode) {
				IWFNode node = (IWFNode) object;
				// TODO Error Checking
				// TODO Uniquify node names somehow

				String joinType = getJoinType(node);
				String joinParam = getJoinParam(node);
				out.printAndIndent(String.format("<node name=\"%s\" joinType=\"%s\" type=\"%s\" isStart=\"%s\">",
						escapeXml(node.name), 
						joinType,
						escapeXml(node.type),
						String.valueOf(node.isStart)));
				if (StringUtils.isNotEmpty(joinParam))
					out.printIndented(String.format("<join>%s</join>", joinParam));
				// TODO Omit LEND / LBEGIN connections
				for (IWFOutputPort port: node.outputPorts) {
					if (port.name.equals("_LEND_")) {
						continue;
					}
					for (IWFArc arc: port.arcs) {
						IWFInputPort target = arc.target;
						if (target == null || target.node == null)
							continue;

						String name = target.name;							
						int dotIndex = name.indexOf('.');
						if (dotIndex < 0) {
							out.printIndented(String.format("<arc to=\"%s\" name=\"%s\"/>",
									escapeXml(target.node.name),
									escapeXml(port.name)));		
						} else {
							String eNodeName = name.substring(0, dotIndex);
							out.printIndented(String.format("<arc external=\"%s\" to=\"%s\" name=\"%s\"/>",
									escapeXml(target.node.name),
									escapeXml(eNodeName),
									escapeXml(port.name)));			
						}							

					}
				}
				out.unindentAndPrint("</node>");
			}
		}

		
	}

	// if joinType is "class", then this can return the name of a class implementing the joinType
	private static String getJoinParam(IWFNode node) {
		return null;
	}
	
	private static String getJoinType(IWFNode node) {
		return "or".equals(node.type) ? "or" : "and";
	}


	private static void emitParameterInfo(List<?> objects, Indenter out) {
		out.printAndIndent("<parameters>");
		for (Object object: objects) {
			if (object instanceof IWFParameter) {
				IWFParameter node = (IWFParameter) object;
				out.printIndented(String.format("<parameter name='%s' type='%s' value='%s'/>", escapeXml(node.name),
						escapeXml(node.type),
						escapeXml(node.value)));
			}
		}
		out.unindentAndPrint("</parameters>");
	}
	
	private static void emitResponseInfo(List<?> objects, Indenter out) {
		out.printAndIndent("<responses>");
		for (Object object: objects) {
			if (object instanceof IWFResponse) {
				IWFResponse response = (IWFResponse) object;
				for (IWFResponseArc source: response.sources) {
					IWFOutputPort port = source.source;
					IWFNode node = port.node;
					out.printAndIndent(String.format("<response name='%s' type='%s' node='%s' port='%s'>",
							escapeXml(response.name),
							escapeXml(response.type),
							escapeXml(node.name),
							escapeXml(port.name)));
						List<IWFProperty> plist = source.properties;
						if (!plist.isEmpty()) {
							out.printAndIndent("<properties>");
							for (IWFProperty p: plist) {
								out.printAndIndent("<property>");
								out.printIndentedAsElement("name", escapeXml(p.name));
								out.printIndentedAsElement("type", escapeXml(p.type));
								out.printIndentedAsElement("value", escapeXml(p.value));						
								out.unindentAndPrint("</property>");
							}
							out.unindentAndPrint("</properties>");
						}
					out.unindentAndPrint("</response>");


				}
			}
		}
		out.unindentAndPrint("</responses>");
	}


	private static void emitPortAndPropertyInfo(List<?> objects, Indenter out) {
		out.printAndIndent("<ports>");
		for (Object object: objects) {
			if (object instanceof IWFNode) {
				IWFNode node = (IWFNode) object;
				out.printAndIndent("<node>");
				out.printIndentedAsElement("name", escapeXml(node.name));	
				out.printIndentedAsElement("type", escapeXml(node.type));				

				for (IWFInputPort port: node.inputPorts) {
					if (port.name.equals("_LBEGIN_"))
						continue;
					out.printAndIndent("<input>");
					out.printIndentedAsElement("name", escapeXml(port.name));
					out.printIndentedAsElement("type", escapeXml(port.type));
					List<IWFProperty> list = port.properties;
					if (!list.isEmpty()) {
						out.printAndIndent("<properties>");
						for (IWFProperty p: list) {
							out.printAndIndent("<property>");
							out.printIndentedAsElement("name", escapeXml(p.name));
							out.printIndentedAsElement("type", escapeXml(p.type));
							out.printIndentedAsElement("value", escapeXml(p.value));						
							out.unindentAndPrint("</property>");
						}
						out.unindentAndPrint("</properties>");
					}

					out.unindentAndPrint("</input>");					
				}
				for (IWFOutputPort port: node.outputPorts) {
					if (port.name.equals("_LEND_")) {
						if (!port.arcs.isEmpty()) {
							IWFArc arc = port.arcs.get(0);
							IWFProperty p = new IWFProperty();
							p.name = "target";
							p.value = arc.target.node.name;
							p.type = "default";
							node.properties.add(p);
						}
						continue;
					}

					out.printAndIndent("<output>");
					out.printIndentedAsElement("name", escapeXml(port.name));
					out.printIndentedAsElement("type", escapeXml(port.type));
					List<IWFProperty> list = port.properties;
					if (!list.isEmpty()) {
						out.printAndIndent("<properties>");
						for (IWFProperty p: list) {
							out.printAndIndent("<property>");
							out.printIndentedAsElement("name", escapeXml(p.name));
							out.printIndentedAsElement("type", escapeXml(p.type));
							out.printIndentedAsElement("value", escapeXml(p.value));						
							out.unindentAndPrint("</property>");
						}
						out.unindentAndPrint("</properties>");
					}

					for (IWFArc arc: port.arcs) {
						// TODO Detect and fix
						if (arc.target == null || arc.target.node == null)
							continue;
						out.printAndIndent("<connection>");		
						out.printIndentedAsElement("targetNode", escapeXml(arc.target.node.name));
						out.printIndentedAsElement("targetPort", escapeXml(arc.target.name));	
						List<IWFProperty> plist = arc.properties;
						if (!plist.isEmpty()) {
							out.printAndIndent("<properties>");
							for (IWFProperty p: plist) {
								out.printAndIndent("<property>");
								out.printIndentedAsElement("name", escapeXml(p.name));
								out.printIndentedAsElement("type", escapeXml(p.type));
								out.printIndentedAsElement("value", escapeXml(p.value));						
								out.unindentAndPrint("</property>");
							}
							out.unindentAndPrint("</properties>");
						}
						out.unindentAndPrint("</connection>");
					}
					out.unindentAndPrint("</output>");
				}

				out.printAndIndent("<properties>");
				List<IWFProperty> list = node.properties;
				for (IWFProperty p: list) {
					out.printAndIndent("<property>");
					out.printIndentedAsElement("name", escapeXml(p.name));
					out.printIndentedAsElement("type", escapeXml(p.type));
					out.printIndentedAsElement("value", escapeXml(p.value));						
					out.unindentAndPrint("</property>");
				}
				out.unindentAndPrint("</properties>");
				
				if (node.conductors.size() > 0) {
					out.printAndIndent("<conductors>");
					for (IWFConductor c: node.conductors) {
						out.printAndIndent("<conductor>");
						out.printAndIndent("<properties>");
						for (IWFProperty p: c.properties) {
							out.printAndIndent("<property>");
							out.printIndentedAsElement("name", escapeXml(p.name));
							out.printIndentedAsElement("type", escapeXml(p.type));
							out.printIndentedAsElement("value", escapeXml(p.value));						
							out.unindentAndPrint("</property>");
						}
						out.unindentAndPrint("</properties>");
						out.unindentAndPrint("</conductor>");
					}
					out.unindentAndPrint("</conductors>");

				}
				out.unindentAndPrint("</node>");
			}
		}			
		out.unindentAndPrint("</ports>");
	}


	private static String escapeXml(String value) {
		if (value == null)
			return "";
		else
			return StringEscapeUtils.escapeXml10(value);
	}
}
