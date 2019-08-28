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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import gov.sandia.dart.workflow.runtime.parser.ThrowingErrorHandler;
import gov.sandia.dart.workflow.runtime.util.Indenter;

public class Persistor {
	
	private File workDir;
	public Persistor(File workDir) {
		this.workDir = workDir;
	}
	
	private Map<String, Map<String, Datum>> inputs = new HashMap<>();
	private Map<String, RuntimeParameter> parameters = new HashMap<>();
	private Map<String, Object> responses = new HashMap<>();
	
	public void saveState() throws FileNotFoundException {
		// Create XML file
		PrintWriter pw = new PrintWriter(getStateFile());
		try (Indenter xml = new Indenter(pw)) {
			//TODO Timestamp, filenames, etc
			xml.printAndIndent("<state version='1'>");

			// Store response values
			xml.printAndIndent("<responses>");
			for (Map.Entry<String, Object> entry: getResponses().entrySet()) {
				xml.printAndIndent("<response>");
				xml.printIndentedAsElement("name", escapeXml(entry.getKey()));
				xml.printIndentedAsElement("value", escapeXml(String.valueOf(entry.getValue())));		
				xml.unindentAndPrint("</response>");
			}
			xml.unindentAndPrint("</responses>");

			// Store runtime parameter values
			xml.printAndIndent("<parameters>");
			for (RuntimeParameter p: getParameters().values()) {
				xml.printAndIndent("<parameter version='2'>");
				for (Map.Entry<String, Object> entry: p.getMetadata().entrySet()) {
					xml.printAndIndent("<key name=\"" + escapeXml(entry.getKey()) + "\">");
					xml.printIndentedAsElement("value", escapeXml(String.valueOf(entry.getValue())));	
					xml.unindentAndPrint("</key>");
				}
				xml.unindentAndPrint("</parameter>");
			}
			xml.unindentAndPrint("</parameters>");

			// Store node data
			xml.printAndIndent("<nodes>");
			for (String nodeName: getNodeNames()) {
				xml.printAndIndent("<node>");		
				xml.printIndentedAsElement("name", escapeXml(nodeName));
				Map<String, Datum> inputs = getInputs(nodeName);
				for (Map.Entry<String, Datum> input: inputs.entrySet()) {
					xml.printAndIndent("<input>");
					xml.printIndentedAsElement("port", escapeXml(input.getKey()));
					xml.printIndentedAsElement("value", escapeXml((String) input.getValue().getAs(String.class)));
					// TODO Preserve actual type
					xml.printIndentedAsElement("type", escapeXml("default"));
					xml.unindentAndPrint("</input>");
				}
				xml.unindentAndPrint("</node>");
			}
			xml.unindentAndPrint("</nodes>");
			xml.unindentAndPrint("</state>");
		}
	}
	
	public Map<String, Datum> getInputs(String nodeName) {
		if (inputs.get(nodeName) == null)
			inputs.put(nodeName, new HashMap<>());
		return inputs.get(nodeName);
	}

	public Collection<String> getNodeNames() {
		return inputs.keySet();
	}

	public Map<String, RuntimeParameter> getParameters() {
		return parameters;
	}

	public Map<String, Object> getResponses() {
		return responses;
	}

	public String escapeXml(String value) {
		if (value == null)
			return "";
		else
			return StringEscapeUtils.escapeXml10(value);
	}
	
	public void loadState() throws IOException {
		File stateFile = getStateFile();
		if (stateFile.exists()) {
			InputSource source = new InputSource(new FileReader(stateFile));
			Document doc = parseDocument(source);
			NodeList nodes = doc.getDocumentElement().getElementsByTagName("node");
			for (int i=0; i<nodes.getLength(); i++) {
				Element nodeE = (Element) nodes.item(i);
				String name = getChildText(nodeE, "name");
				NodeList inputElements = nodeE.getElementsByTagName("input");
				for (int j=0; j<inputElements.getLength(); j++) {
					Element inputE = (Element) inputElements.item(j);
					String port = getChildText(inputE, "port");
					String value = getChildText(inputE, "value");
					String type = getChildText(inputE, "type");
					putInput(name, port, type, value);
				}
			}

			NodeList params = doc.getDocumentElement().getElementsByTagName("parameter");
			for (int i=0; i<params.getLength(); i++) {
				Element param = (Element) params.item(i);
				if (!param.hasAttribute("version")) {
					String name = getChildText(param, "name");
					String value = getChildText(param, "value");
					setParameter(name, value);
				} else {
					Map<String, Object> metadata = new HashMap<>();
					NodeList keys = param.getElementsByTagName("key");
					for (int j=0; j<keys.getLength(); j++) {
						Element key = (Element) keys.item(j);
						String name = key.getAttribute("name");
						String value = getChildText(key, "value");
						metadata.put(name, value);
					}
					setParameter(metadata);
				}
			}
			
			NodeList respons = doc.getDocumentElement().getElementsByTagName("response");
			for (int i=0; i<respons.getLength(); i++) {
				Element response = (Element) respons.item(i);
				String name = getChildText(response, "name");
				String value = getChildText(response, "value");
				setResponse(name, value);
			}
		}
	}
	public void setResponse(String name, String value) {
		responses.put(name, value);
	}

	// The old ("version 1") persistor didn't actually preserve whether a parameter was global.
	public void setParameter(String name, String value) {
		parameters.put(name,  new RuntimeParameter(name, value, "default", false, true));
	}
	
	public void setParameter(Map<String, Object> metadata) {
		String name = (String) metadata.get(RuntimeParameter.NAME);
		parameters.put(name, new RuntimeParameter(metadata));
	}

	public void putInput(String name, String port, String type, Object value) {
		Map<String, Datum> nodeInputs = inputs.get(name);
		if (nodeInputs == null)
			inputs.put(name, nodeInputs = new HashMap<>());
		nodeInputs.put(port, new Datum(type, value, classOf(value)));
		
	}

	public String getChildText(Element e, String tag) {
		NodeList elements = e.getElementsByTagName(tag);
		return elements.getLength() < 1 ? "" : ((Element) elements.item(0)).getTextContent();
	}

	private File getStateFile() {
		return new File(workDir, "workflow.state.xml");
	}

	private Class<?> classOf(Object value) {
		return value != null ? value.getClass() : Void.class;
	}

	private Document parseDocument(InputSource source) throws IOException {
		Document doc = null;
		try {
			DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
			dfactory.setNamespaceAware(true);
			DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
			docBuilder.setErrorHandler(new ThrowingErrorHandler());
			doc = docBuilder.parse(source);  			
		} catch (Exception e) {
			throw new SAWWorkflowException("Error parsing state file", e);
		} 
		return doc;
	}	
}
