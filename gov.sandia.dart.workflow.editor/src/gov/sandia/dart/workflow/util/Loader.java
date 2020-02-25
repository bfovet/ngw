/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.services.GraphitiUi;

import gov.sandia.dart.workflow.domain.DomainPackage;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.WFNode;

public class Loader {
	private List<Object> objects;
	private Diagram diagram;
	private IFeatureProvider fp;
	private Map<String, WFNode> nodes;
	public List<Object> load(String path) {
		DomainPackage.eINSTANCE.eClass();

		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		Map<String, Object> m = reg.getExtensionToFactoryMap();
		m.put("iwf", new XMIResourceFactoryImpl());

		// Obtain a new resource set
		ResourceSet resSet = new ResourceSetImpl();

		Resource resource = resSet.getResource(URI.createFileURI(path), true);
		objects = new ArrayList<>();
		objects.addAll(resource.getContents());
		
		// Get offline diagram
		Optional<Object> oDiagram = objects.stream().filter(o -> o instanceof Diagram).findFirst();
		diagram = (Diagram) oDiagram.get();
		fp = GraphitiUi.getExtensionManager().createFeatureProvider(diagram);

		return objects;
	}
	
	public List<WFNode> getParameters() {
		if (objects == null)
			throw new IllegalStateException("Loader has not loaded diagram");
		List<Pair<Integer, WFNode>> pairs = new ArrayList<>();
				
		for (Object o: objects) {
			if (o instanceof WFNode && ParameterUtils.isParameter((WFNode) o)) {
				WFNode node = (WFNode) o;
				PictogramElement pe = fp.getPictogramElementForBusinessObject(node);
				int y = pe != null ? pe.getGraphicsAlgorithm().getY() : 0;
				pairs.add(Pair.of(y, node));
			}
		}
		
		pairs.sort(new Comparator<Pair<Integer, WFNode>>() {
			@Override
			public int compare(Pair<Integer, WFNode> arg0, Pair<Integer, WFNode> arg1) {
				return arg0.getLeft() - arg1.getLeft();
			}
		});
			
		return pairs.stream().map(p -> p.getRight()).collect(Collectors.toList());
	}

	public List<Response> getResponses() {
		if (objects == null)
			throw new IllegalStateException("Loader has not loaded diagram");
		List<Pair<Integer, Response>> pairs = new ArrayList<>();
				
		for (Object o: objects) {
			if (o instanceof Response) {
				Response response = (Response) o;
				PictogramElement pe = fp.getPictogramElementForBusinessObject(response);
				int y = pe != null ? pe.getGraphicsAlgorithm().getY() : 0;
				pairs.add(Pair.of(y, response));
			}
		}
		
		pairs.sort(new Comparator<Pair<Integer, Response>>() {
			@Override
			public int compare(Pair<Integer, Response> arg0, Pair<Integer, Response> arg1) {
				return arg0.getLeft() - arg1.getLeft();
			}
		});
			
		return pairs.stream().map(p -> p.getRight()).collect(Collectors.toList());
	}

	public synchronized Map<String, WFNode> getNodes() {
		if (nodes == null) {
			Map<String, WFNode> map = new HashMap<>();
			for (Object o: objects) {
				if (o instanceof WFNode) {
					WFNode node = (WFNode) o;
					map.put(node.getName(), node);
				}
			}
			nodes = Collections.unmodifiableMap(map);
		}
		return nodes;
	}
}
