/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.text.StringEscapeUtils;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.strikewire.snl.apc.util.ExtensionPointUtils;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.extensions.IWorkflowEditorNodeTypeContributor;
import gov.sandia.dart.workflow.util.Indenter;

public class WorkflowTypesManager {

	public static final String DEFAULT_TYPE = "default";

	private static WorkflowTypesManager INSTANCE;
	
	private Map<String, NodeType> nodeTypes;
	private Map<String, ConductorType> conductorTypes;
	
	private WorkflowTypesManager(InputSource source) throws IOException, ParserConfigurationException, SAXException {
		
		Document doc = parseXML(source);
		nodeTypes = parseNodeTypes(doc);
		conductorTypes =parseConductorTypes(doc);
	}
	
	public static synchronized WorkflowTypesManager get() {
		if (INSTANCE == null) {
		    Bundle bundle = Platform.getBundle(WorkflowEditorPlugin.PLUGIN_ID);	        
			try {
				URL entry = bundle.getEntry("config/nodeTypeDump.xml");
				if (entry == null) {
					throw new IOException("Can't load node types database");
				}

				INSTANCE = new WorkflowTypesManager(new InputSource(entry.openStream()));
				
				List<IWorkflowEditorNodeTypeContributor> contribs =
						ExtensionPointUtils.getExtensionInstances(WorkflowEditorPlugin.PLUGIN_ID, "nodeTypeContributor", "contributor");
				
				for (IWorkflowEditorNodeTypeContributor contrib: contribs) {
					List<NodeType> types = contrib.getNodeTypes();
					for (NodeType type: types) {
						INSTANCE.nodeTypes.put(type.getName(), type);
					}
				}
				
			} catch (IOException | ParserConfigurationException | SAXException e) {
				WorkflowEditorPlugin.getDefault().logError("Can't load node types database", e);
			} 
		}
		return INSTANCE;		
	}
	
	public Map<String, NodeType> getNodeTypes() {
		return Collections.unmodifiableMap(nodeTypes);
	}
	
	public Map<String, ConductorType> getConductorTypes() {
		return Collections.unmodifiableMap(conductorTypes);
	}
	
	public static void writeNodeTypes(Writer writer, List<NodeType> nodeTypes) {
		Indenter out = new Indenter(writer);
		out.printAndIndent("<workflowData>");
		out.printAndIndent("<nodeTypes>");
		for (NodeType nodeType: nodeTypes) {
			renderNodeType(out, nodeType);
		}
		out.unindentAndPrint("</nodeTypes>");			
		out.unindentAndPrint("</workflowData>");

	}

	public static String renderNodeType(NodeType nodeType) {
		StringWriter writer = new StringWriter();
		Indenter out = new Indenter(writer);
		renderNodeType(out, nodeType);
		return writer.toString();
	}
	
	private static void renderNodeType(Indenter out, NodeType nodeType) {
		out.printAndIndent(String.format("<nodeType name='%s' category='%s' label='%s' displayLabel='%s'>", StringEscapeUtils.escapeXml10(nodeType.getName())
				, "User Defined", StringEscapeUtils.escapeXml10(nodeType.getLabel()), StringEscapeUtils.escapeXml10(nodeType.getDisplayLabel())));
		out.printAndIndent("<inputs>");
		for (Input input: nodeType.getInputs()) {
			out.printIndented(String.format("<input name='%s' type='%s'/>", StringEscapeUtils.escapeXml10(input.getName()),
					StringEscapeUtils.escapeXml10(input.getType())));
		}			
		out.unindentAndPrint("</inputs>");
		
		out.printAndIndent("<outputs>");
		for (Output output: nodeType.getOutputs()) {
			out.printIndented(String.format("<output name='%s' type='%s' filename='%s'/>", StringEscapeUtils.escapeXml10(output.getName()),
					StringEscapeUtils.escapeXml10(output.getType()), StringEscapeUtils.escapeXml10(output.getFilename())));
		}			
		out.unindentAndPrint("</outputs>");

		out.printAndIndent("<properties>");
		for (Prop property: nodeType.getProperties()) {				
			out.printAndIndent(String.format("<property name='%s' type='%s'>", StringEscapeUtils.escapeXml10(property.getName()),
					StringEscapeUtils.escapeXml10(property.getTypeName()),
					StringEscapeUtils.escapeXml10(property.getValue())));
			// TODO Escape XML
			out.printIndentedAsElement("value", property.getValue());
			out.unindentAndPrint("</property>");
		}			
		out.unindentAndPrint("</properties>");
		out.unindentAndPrint("</nodeType>");
	}
	
	private static Map<String, NodeType> parseNodeTypes(Document doc) throws IOException, ParserConfigurationException, SAXException {
		Map<String, NodeType> nodeTypes = new HashMap<>();
		NodeList nodeTypeElements = doc.getElementsByTagName("nodeType");
		for (int i=0; i<nodeTypeElements.getLength(); ++i) {
			Element nodeTypeE = (Element) nodeTypeElements.item(i);
			final String name = nodeTypeE.getAttribute("name");
			//final String category = nodeTypeE.getAttribute("category");
			String label = nodeTypeE.getAttribute("label");
			String displayLabel = nodeTypeE.getAttribute("displayLabel");
			
			NodeType nodeType = new NodeType(name);
			nodeType.setLabel(label);
			nodeType.setDisplayLabel(displayLabel);
			nodeTypes.put(nodeType.getName(), nodeType);
			
			NodeList categoryElements = nodeTypeE.getElementsByTagName("category");
			List<String> categories = new ArrayList<>();
			for (int j=0; j<categoryElements.getLength(); ++j) {
				Element categoryE = (Element) categoryElements.item(j);
				categories.add(categoryE.getAttribute("name"));
			}
			nodeType.setCategories(categories);
			
			NodeList inputElements = nodeTypeE.getElementsByTagName("input");
			List<Input> inputs = new ArrayList<>();
			for (int j=0; j<inputElements.getLength(); ++j) {
				Element inputE = (Element) inputElements.item(j);
				inputs.add(new Input(inputE.getAttribute("name"), inputE.getAttribute("type")));
			}
			nodeType.setInputs(inputs);
			
			NodeList outputElements = nodeTypeE.getElementsByTagName("output");
			List<Output> outputs = new ArrayList<>();
			for (int j=0; j<outputElements.getLength(); ++j) {
				Element outputE = (Element) outputElements.item(j);
				outputs.add(new Output(outputE.getAttribute("name"), outputE.getAttribute("type"), outputE.getAttribute("filename")));
			}
			nodeType.setOutputs(outputs);
			
			NodeList propertyElements = nodeTypeE.getElementsByTagName("property");
			List<Prop> propertys = new ArrayList<>();
			for (int j=0; j<propertyElements.getLength(); ++j) {
				Element propertyE = (Element) propertyElements.item(j);
				String value = null;
				NodeList values = propertyE.getElementsByTagName("value");
				if (values.getLength() > 0) {
					value = values.item(0).getTextContent().trim();
				} else {
					value = propertyE.getAttribute("value");
				}
				boolean advanced = "true".equals(propertyE.getAttribute("advanced"));
				final Prop prop = new Prop(propertyE.getAttribute("name"), propertyE.getAttribute("type"), value, advanced);
				propertys.add(prop);
								
			}
			nodeType.setProperties(propertys);
		}
		return nodeTypes;
	}
		
	private Map<String, ConductorType> parseConductorTypes(Document doc) throws IOException, ParserConfigurationException, SAXException {
		Map<String, ConductorType> conductorTypes = new HashMap<>();
		NodeList nodeTypeElements = doc.getElementsByTagName("conductorType");
		for (int i=0; i<nodeTypeElements.getLength(); ++i) {
			Element nodeTypeE = (Element) nodeTypeElements.item(i);
			final String name = nodeTypeE.getAttribute("name");
			ConductorType conductorType = new ConductorType(name);
			conductorTypes.put(conductorType.getName(), conductorType);
						
			NodeList propertyElements = nodeTypeE.getElementsByTagName("property");
			List<Prop> propertys = new ArrayList<>();
			for (int j=0; j<propertyElements.getLength(); ++j) {
				Element propertyE = (Element) propertyElements.item(j);
				propertys.add(new Prop(propertyE.getAttribute("name"), propertyE.getAttribute("type")));
			}
			conductorType.setProperties(propertys);
		}
		return conductorTypes;
	}


	private static Document parseXML(InputSource source) throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		docBuilder.setErrorHandler(new ThrowingErrorHandler());
		return docBuilder.parse(source);
	}

	private static class ThrowingErrorHandler implements ErrorHandler {
		@Override
		public void warning(SAXParseException exception) throws SAXException {
			throw exception;
		}
		@Override
		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}
		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}
	}

	public NodeType getNodeType(String type) {
		return nodeTypes.get(type);
	}
	
	public ConductorType getConductorType(String type) {
		return conductorTypes.get(type);
	}

	public static List<NodeType> parseNodeTypes(File file) throws IOException, ParserConfigurationException, SAXException {
		try (InputStream reader = new FileInputStream(file)) {
			return parseNodeTypes(reader);
		}
	}
	
	public static List<NodeType> parseNodeTypes(InputStream stream) throws IOException, ParserConfigurationException, SAXException {
		Document doc = parseXML(new InputSource(stream));
		Map<String, NodeType> typeMap = parseNodeTypes(doc);
		return new ArrayList<>(typeMap.values());
	}
	
}
