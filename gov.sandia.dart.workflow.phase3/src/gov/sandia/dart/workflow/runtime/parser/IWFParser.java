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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFConductor;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFObject;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFInputPort;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFOutputPort;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFParameter;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFProperty;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponse;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponseArc;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFArc;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFNode;

/**
 * Parses an IWF file. This initial version writes out a .sarasvati file which is then
 * consumed by other code; will probably refactor to cut out the middle-man.
 * @author ejfried
 */

public class IWFParser {
	public List<IWFObject> parse(File iwfFile) throws SAWWorkflowException {		
		try {
			Document doc = parseDocument(new InputSource(new FileReader(iwfFile)));
			NodeList elements = doc.getDocumentElement().getElementsByTagNameNS("https://dart.sandia.gov/workflow/domain", "*");
			IWFObject[] objects = new IWFObject[elements.getLength() + 1];
			createDomainObjects(elements, objects);
			linkObjects(elements, objects);
			return Arrays.asList(Arrays.copyOfRange(objects, 1, objects.length));

		} catch (IOException e) {
			throw new SAWWorkflowException("Error parsing IWF file " + iwfFile.getAbsolutePath(), e);
		}
	}

	@SuppressWarnings("unchecked")
	private void linkObjects(NodeList elements, IWFObject[] objects) {
		// Fix up links
		for (int i=0; i<elements.getLength(); ++i) {
			int index = i + 1;
			Element e = (Element) elements.item(i);
			String kind = e.getLocalName();
			switch (kind) {
			case "WFNode": {
				((IWFNode) objects[index]).inputPorts.addAll((Collection<? extends IWFInputPort>) gather(objects, e.getAttribute("inputPorts")));
				((IWFNode) objects[index]).outputPorts.addAll((Collection<? extends IWFOutputPort>) gather(objects, e.getAttribute("outputPorts")));
				((IWFNode) objects[index]).properties.addAll((Collection<? extends IWFProperty>) gather(objects, e.getAttribute("properties")));			
				((IWFNode) objects[index]).conductors.addAll((Collection<? extends IWFConductor>) gather(objects, e.getAttribute("conductors")));
				break;
			}
			case "WFArc": {
				((IWFArc) objects[index]).source = (IWFOutputPort) get(objects, e.getAttribute("source"));
				((IWFArc) objects[index]).target = (IWFInputPort) get(objects, e.getAttribute("target"));
				((IWFArc) objects[index]).properties.addAll((Collection<? extends IWFProperty>) gather(objects, e.getAttribute("properties")));
				break;
			}
			case "Conductor": {
				((IWFConductor) objects[index]).properties.addAll((Collection<? extends IWFProperty>) gather(objects, e.getAttribute("properties")));
				break;
			}
			case "InputPort": {
				((IWFInputPort) objects[index]).node = (IWFNode) get(objects, e.getAttribute("node"));
				((IWFInputPort) objects[index]).arcs.addAll((Collection<? extends IWFArc>) gather(objects, e.getAttribute("arcs")));
				((IWFInputPort) objects[index]).properties.addAll((Collection<? extends IWFProperty>) gather(objects, e.getAttribute("properties")));
				break;
			}
			case "OutputPort": {
				((IWFOutputPort) objects[index]).node = (IWFNode) get(objects, e.getAttribute("node"));
				((IWFOutputPort) objects[index]).arcs.addAll((Collection<? extends IWFArc>) gather(objects, e.getAttribute("arcs")));
				((IWFOutputPort ) objects[index]).properties.addAll((Collection<? extends IWFProperty>) gather(objects, e.getAttribute("properties")));

				break;
			}
			case "Response": {
				((IWFResponse) objects[index]).sources.addAll((Collection<? extends IWFResponseArc>) gather(objects, e.getAttribute("source")));
				break;
			}
			case "ResponseArc": {
				((IWFResponseArc) objects[index]).source = (IWFOutputPort) get(objects, e.getAttribute("source"));
				((IWFResponseArc) objects[index]).target = (IWFResponse) get(objects, e.getAttribute("target"));
				break;
			}

			default: // NOTHING
			}
		}
	}



	private IWFObject get(IWFObject[] objects, String attribute) {
		if (StringUtils.isEmpty(attribute))
			return null;
		attribute = attribute.substring(1);
		return objects[Integer.parseInt(attribute)];
	}

	private Collection<? extends IWFObject> gather(IWFObject[] objects, String attribute) {
		List<IWFObject> list = new ArrayList<>();
		int[] indexes = parseIndexes(attribute);
		for (int i=0; i<indexes.length; ++i) {
			list.add(objects[indexes[i]]);
		}
		return list;
	}



	private int[] parseIndexes(String attribute) {
		if (StringUtils.isEmpty(attribute))
			return new int[0];
		attribute = attribute.substring(1);
		String[] tokens = attribute.split("[ /]+");
		int[] indexes = new int[tokens.length];
		for (int i=0; i<tokens.length; ++i) {
			indexes[i] = Integer.parseInt(tokens[i]);
		}
		return indexes;
	}



	private void createDomainObjects(NodeList elements, IWFObject[] objects) {
		for (int i=0; i<elements.getLength(); ++i) {
			int index = i+1;
			Element e = (Element) elements.item(i);
			String kind = e.getLocalName();
			switch (kind) {
			case "Property": {
				IWFProperty dobj = new IWFProperty();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				dobj.value = e.getAttribute("value");
				objects[index] = dobj;
				break;
			}	
			case "WFNode": {
				IWFNode dobj = new IWFNode();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				if ("true".equals(e.getAttribute("start")))
					dobj.isStart = true;
				objects[index] = dobj;
				break;
			}
			case "Conductor": {
				IWFConductor dobj = new IWFConductor();
				objects[index] = dobj;
				break;
			}			
			case "WFArc": {
				IWFArc dobj = new IWFArc();
				dobj.name = e.getAttribute("name");
				objects[index] = dobj;
				break;
			}
			case "InputPort": { 
				IWFInputPort dobj = new IWFInputPort();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				objects[index] = dobj;
				break;
			}
			case "OutputPort": { 
				IWFOutputPort dobj = new IWFOutputPort();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				objects[index] = dobj;
				break;
			}
			case "Parameter": { 
				IWFParameter dobj = new IWFParameter ();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				dobj.value = e.getAttribute("value");
				objects[index] = dobj;
				break;
			}
			case "Response": { 
				IWFResponse dobj = new IWFResponse ();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				objects[index] = dobj;
				break;
			}
			case "ResponseArc": { 
				IWFResponseArc dobj = new IWFResponseArc ();
				dobj.name = e.getAttribute("name");
				dobj.type = e.getAttribute("type");
				objects[index] = dobj;
				break;
			}

			default: {
				// Nothing
			}
			}
		}
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
			throw new SAWWorkflowException("Error parsing IWF file", e);
		} 
		return doc;
	}
}
