/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree.data;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;

public abstract class WFTreeRoot{
	private Object parent_;
	
	private String name_;
	
	private List<Object> allNodes_ = null;
	
	private List<WFNode> rootNodes_ = null;
	
	public WFTreeRoot(String name, Object parent) {
		name_ = name;
		parent_ = parent;
	}
	
	public Object getParent() {
		return parent_;
	}

	public void setName(String name) {
		name_ = name;
	}
	

	public String getName() {
		if(name_ != null) {			
			return name_;		
		}
		return "Workflow Root";
	}

	public List<Object> getNodes() {
		
		if(needsToBeRefreshed()) {
			allNodes_ = null;
		}
		
		if(allNodes_ == null) {
			try {
				loadNodes();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return allNodes_;
	}
	
	public List<WFNode> getRootNodes(){
		if(needsToBeRefreshed()) {
			rootNodes_ = null;
		}
		
		if(rootNodes_ == null) {
			try {
				// make sure nodes are loaded first
				getNodes();
				rootNodes_ = findRootNodes();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return rootNodes_;
		
	}

	abstract protected Resource getRootResource();

	abstract protected boolean needsToBeRefreshed();
		
	private void loadNodes() throws IOException {
		Resource resource = getRootResource();
		resource.load(Collections.EMPTY_MAP);

		allNodes_ = new ArrayList<Object>();
		
		for(EObject object : resource.getContents())
		{
			
			if(object instanceof WFNode) {
				WFNode node = (WFNode) object;
				
				
				///////////////////////////
				// See what should be added to allNodes_
				///////////////////////////
				if(node.getType().equals("nestedInternalWorkflow")) {				
					for(Property prop : node.getProperties()) {
						// AJR - check this out.... 
						if(prop.getName().equals("fileContents")) {
							String fileContents = prop.getValue();
							
							if(fileContents != null && !fileContents.trim().isEmpty()) {
								try {
									File tmpFile = File.createTempFile(node.getName(), ".iwf.tmp");
									tmpFile.deleteOnExit();
									try(FileWriter fw = new FileWriter(tmpFile))
									{
										fw.write(fileContents);
									}
									
									WFTreeRoot nestedWF = new FileWFTreeRoot(new Path(tmpFile.getAbsolutePath()), this);
									nestedWF.setName(node.getName());
									allNodes_.add(nestedWF);
								}catch(IOException e) {
									
								}
								
							}
							

						}
					}				
				}else {
					allNodes_.add(object);
				}

			}
		}
		
	}
		
	private List<WFNode> findRootNodes() {
		List<WFNode> applicableNodes = new ArrayList<WFNode>();
		List<WFNode> rootNodes = new ArrayList<WFNode>();				

		
		for(Object node : allNodes_) {
			if(node instanceof WFNode) {				
				applicableNodes.add((WFNode)node);
			}
		}
		
		if(!applicableNodes.isEmpty()) {
			Set<WFNode> handledNodes = new HashSet<WFNode>();
			
			for(WFNode node :applicableNodes) {
				traceToRoot(node, rootNodes, handledNodes);
			}
		}

		return rootNodes;
	}

	private void traceToRoot(WFNode checkNode, List<WFNode> rootNodes, Set<WFNode> handledNodes) {
		internalTraceToRoot(checkNode, rootNodes, handledNodes, new HashSet<WFNode>());
	}
	
	private boolean internalTraceToRoot(WFNode checkNode, List<WFNode> rootNodes, Set<WFNode> handledNodes, Set<WFNode> checkingNodes) {
		// We found our way to a root node
		if(rootNodes.contains(checkNode) || handledNodes.contains(checkNode)) {
			return true;
		}
		
		// We passed through a cycle
		if(checkingNodes.contains(checkNode)) {
			return false;
		}
		
		checkingNodes.add(checkNode);
		
		for(OutputPort op : checkNode.getOutputPorts()) {
			
			for(WFArc arc : op.getArcs()) {
				if(arc.getTarget() != null && arc.getTarget().getNode() != null) {
					WFNode parent = arc.getTarget().getNode();
					
					if(internalTraceToRoot(parent, rootNodes, handledNodes, checkingNodes)) {
						handledNodes.add(checkNode);
						return true;
					}
				}
			}
		}

		// If we get to this point, it is a root node. 
		// Either we didn't find any parents, or all traces were cyclical.
		rootNodes.add(checkNode);
		return true;
	}
}
