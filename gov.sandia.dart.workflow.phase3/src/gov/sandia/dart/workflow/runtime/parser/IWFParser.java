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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

import gov.sandia.dart.workflow.runtime.core.SAWWorkflowException;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFArc;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFConductor;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFInputPort;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFNode;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFObject;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFOutputPort;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFParameter;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFProperty;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponse;
import gov.sandia.dart.workflow.runtime.parser.Domain.IWFResponseArc;

/**
 * Parses an IWF file. 
 * @author ejfried
 */

public class IWFParser {
	
	private static final String NAMESPACE = "https://dart.sandia.gov/workflow/domain";
	public List<IWFObject> parse(File iwfFile) throws SAWWorkflowException {		
		try {
			Document doc = parseDocument(new InputSource(new FileReader(iwfFile)));
			NodeList elements = doc.getDocumentElement().getElementsByTagNameNS(NAMESPACE, "*");
			IWFObject[] objects = new IWFObject[elements.getLength() + 1];
			createDomainObjects(elements, objects);
			linkObjects(elements, objects);
			return Arrays.asList(Arrays.copyOfRange(objects, 1, objects.length));

		} catch (IOException e) {
			throw new SAWWorkflowException("Error parsing IWF file " + iwfFile.getAbsolutePath(), e);
		}
	}

	public boolean updateContents(File iwfFile, Writer writer, String nodeId, Map<String, String> newProperties, boolean removeMissing) throws SAWWorkflowException {		
		try {
			// Get the elements
			Document doc = parseDocument(new InputSource(new FileReader(iwfFile)));
			NodeList elements = doc.getDocumentElement().getElementsByTagNameNS("https://dart.sandia.gov/workflow/domain", "*");

			// Do some sanity checking before we start processing
			if(elements.getLength() <= 0) {
				// If this happens, there is no node that we can modify
				return false;
			}
			Node parentNode = elements.item(0).getParentNode();
			if(parentNode == null) {
				throw new SAWWorkflowException("Error updating IWF file " + iwfFile.getAbsolutePath() + ": Bad Structure");
			}			
						
			// Build the IWFObjects 
			IWFObject[] objects = new IWFObject[elements.getLength() + 1];
			createDomainObjects(elements, objects);
			linkObjects(elements, objects);	

			
			// Used to keep track of what needs updating
			List<IWFObject> finalObjectList = new ArrayList<>(Arrays.asList(objects));
			Map<IWFObject, Element> objectToElementMap = new HashMap<>();
			List<IWFProperty> toAdd = new ArrayList<>();
			List<IWFProperty> toUpdate = new ArrayList<>();
			List<Element> toRemove = new ArrayList<>();

			
			// Figure out what exactly needs to be updated
			boolean foundNode = false;
			for(int i = 0; i < elements.getLength(); i++) {
				int index = i+1;
				IWFObject object = objects[index];
				Element element = (Element)elements.item(i);
				
				objectToElementMap.put(object, element);
				
				if(object instanceof IWFNode) {
					IWFNode node = (IWFNode) object;
					
					// Run through existing properties and remove/modify the objects
					if(node.name.equals(nodeId)) {
						
						// We found the node we are looking for!
						foundNode = true;

						// Make a new list to avoid concurrent modification issues with "removeMissing"
						List<IWFProperty> properties = new ArrayList<>(node.properties);
						
						Set<String> newPropNames = new HashSet<>(newProperties.keySet());						
						for(IWFProperty property : properties) {
							if(newProperties.containsKey(property.name)) {
								// Properties to be updated
								
								property.value = newProperties.get(property.name);
								toUpdate.add(property);
								newPropNames.remove(property.name);
							}else if(removeMissing) {
								// Properties to be removed
								
								node.properties.remove(property);
								int propertyIndex = 1;
								while(propertyIndex < objects.length) {
									if(objects[propertyIndex] == property) {
										break;
									}
									propertyIndex++;
								}
								toRemove.add((Element)elements.item(propertyIndex-1));
								finalObjectList.remove(property);
							}							
						}
						
						for(String newPropName : newPropNames) {
							// Properties to be added
							
							IWFProperty newProp = new IWFProperty(newPropName, "default", newProperties.get(newPropName));							
							node.properties.add(newProp);
							toAdd.add(newProp);
							finalObjectList.add(newProp);
						}
					}
				}
			}						
			if(!foundNode) {
				// The node that we wanted to modify could not be found, so there is nothing to change
				return false;
			}
			
			
			// Update the XML 
			objectToElementMap.putAll(addNewProperties(parentNode, toAdd));
			updateProperties(toUpdate, objectToElementMap);
			removeOldNodes(toRemove);
			updateAllLinks(finalObjectList, objectToElementMap);
			
			// Write out results			
			writeDocument(doc, new StreamResult(writer));

		} catch (IOException e) {
			throw new SAWWorkflowException("Error updating IWF file " + iwfFile.getAbsolutePath(), e);
		}
		
		return true;
	}


	private void updateProperties(List<IWFProperty> toUpdate, Map<IWFObject, Element> objectToElementMap) {
		for(IWFProperty property : toUpdate) {
			Element element = objectToElementMap.get(property);			
			element.setAttribute("value", property.value);
		}
		
	}

	protected void removeOldNodes(List<Element> toRemove) {
		for(Element removing : toRemove) {
			Node parentNode = removing.getParentNode();
			
			if(parentNode == null) {
				return;
			}
			
			Node checkNode = removing.getNextSibling();
			
			// We want to remove whitespace formatting for the lines that are no longer there				
			//TODO I imagine this can be taken care of in the write operation
			while(checkNode instanceof Text) {
				Text textNode = (Text) checkNode;

				if(textNode.getTextContent().trim().isEmpty()) {
					checkNode = textNode.getNextSibling();
					parentNode.removeChild(textNode);
				}else {
					checkNode = null;
				}
			}
			
			parentNode.removeChild(removing);
		}
	}

	private Map<IWFObject,Element> addNewProperties(org.w3c.dom.Node parentNode, List<IWFProperty> toAdd) {
		Map<IWFObject, Element> objectToElementMap = new HashMap<>();

		for(IWFProperty newProperty : toAdd) {
			Document doc = parentNode.getOwnerDocument();
			Element newElement = doc.createElementNS(NAMESPACE, "Property");
			newElement.setAttribute("name", newProperty.name);
			newElement.setAttribute("type", newProperty.type);
			newElement.setAttribute("value", newProperty.value);

			// Add in formatting along with the new item 
			//TODO I imagine this can be taken care of in the translator
			parentNode.appendChild(doc.createTextNode("  "));
			parentNode.appendChild(newElement);
			parentNode.appendChild(doc.createTextNode("\n"));

			objectToElementMap.put(newProperty, newElement);
		}
			
		return objectToElementMap;
	}

	private void updateAllLinks(List<IWFObject> objectList, Map<IWFObject, Element> objectToElementMap) {
		
		for(IWFObject object : objectList) {			
			Element element = objectToElementMap.get(object);
			if(object instanceof IWFNode) {
				IWFNode item = (IWFNode) object;
				updateLinks(objectList, item.inputPorts, element, "inputPorts");
				updateLinks(objectList, item.outputPorts, element, "outputPorts");
				updateLinks(objectList, item.properties, element, "properties");
				updateLinks(objectList, item.conductors, element, "conductors");
			}else if(object instanceof IWFArc) {
				IWFArc item = (IWFArc) object;
				updateLinks(objectList, Collections.singletonList(item.source), element, "source");
				updateLinks(objectList, Collections.singletonList(item.target), element, "target");
				updateLinks(objectList, item.properties, element, "properties");
			}else if(object instanceof IWFConductor) {
				IWFConductor item = (IWFConductor) object;
				updateLinks(objectList, item.properties, element, "properties");
			}else if(object instanceof IWFInputPort) {
				IWFInputPort item = (IWFInputPort) object;
				updateLinks(objectList, Collections.singletonList(item.node), element, "node");
				updateLinks(objectList, item.arcs, element, "arcs");
				updateLinks(objectList, item.properties, element, "properties");
			}else if(object instanceof IWFOutputPort) {
				IWFOutputPort item = (IWFOutputPort) object;
				updateLinks(objectList, Collections.singletonList(item.node), element, "node");
				updateLinks(objectList, item.arcs, element, "arcs");
				updateLinks(objectList, item.properties, element, "properties");
			}else if(object instanceof IWFResponse) {
				IWFResponse item = (IWFResponse) object;
				updateLinks(objectList, item.sources, element, "source");
			}else if(object instanceof IWFResponseArc) {
				IWFResponseArc item = (IWFResponseArc) object;
				updateLinks(objectList, Collections.singletonList(item.source), element, "source");
				updateLinks(objectList, Collections.singletonList(item.target), element, "target");
				updateLinks(objectList, item.properties, element, "properties");

			}else if(object instanceof IWFParameter) {
				updateLinks(objectList, Collections.emptyList(), element, "node");				
			}else if(object instanceof IWFProperty) {
				updateLinks(objectList, Collections.emptyList(), element, "node");				
			}
		}			
	}

	protected void updateLinks(List<IWFObject> objectList, List<? extends IWFObject> items, Element element, String name) {
		if(element == null) {
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		for(IWFObject linkedObject : items) {
			int linkedIndex = objectList.indexOf(linkedObject);
			if(linkedIndex > 0) {
				if(sb.length() != 0) {
					sb.append(' ');
				}
				sb.append('/');
				sb.append(linkedIndex);
			}
		}

		if(sb.length() != 0) {
			element.setAttribute(name, sb.toString());
		}else if(element.hasAttribute(name)) {
			element.removeAttribute(name);
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
				((IWFResponseArc) objects[index]).properties.addAll((Collection<? extends IWFProperty>) gather(objects, e.getAttribute("properties")));

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
	
	private void writeDocument(Document doc, StreamResult result){
		try {
			DOMSource source = new DOMSource(doc);

		    TransformerFactory transformerFactory = TransformerFactory.newInstance();
		    Transformer transformer = transformerFactory.newTransformer();
		    // TODO setup the transformer to nicely format the XML
		    transformer.transform(source, result);
		} catch (Exception e) {
			throw new SAWWorkflowException("Error writing IWF file", e);
		} 
	}	
}
