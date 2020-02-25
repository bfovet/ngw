/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.strikewire.snl.apc.resources.CommonResourceUtils;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.tree.WorkflowTreePreferences.Mode;
import gov.sandia.dart.workflow.editor.tree.data.WFTreeRoot;

public class WorkflowFileContentProvider implements ITreeContentProvider {
	
	private WorkflowTreePreferences preferences_ = new WorkflowTreePreferences();
	
	public void setPreferences(WorkflowTreePreferences preferences) {
		preferences_ = preferences;
	}
	
	protected WorkflowTreePreferences getPreferneces() {
		return preferences_;
	}
		
	@Override
	public Object[] getElements(Object inputElement) {			
		Collection<Object> children = new ArrayList<Object>();
		WFTreeRoot wfRoot = Adapters.adapt(inputElement, WFTreeRoot.class);
		if (wfRoot != null) {

			if(preferences_.getMode() == Mode.HIERARCHICAL) {
				children.addAll(wfRoot.getRootNodes());
			}else{
				children.addAll(wfRoot.getNodes());
			}
		} else if(inputElement instanceof WFNode) {
			children.addAll(getWFNodeChildren((WFNode)inputElement));
		}
		
		return processChildren(inputElement, children);
	}

	protected Object[] processChildren(Object inputElement, Collection<Object> children) {
		//Filter out Nodes tagged as hidden from navigator		
		Collection<Object> results = new ArrayList<Object>();		
		for(Object item : children) {
			if(item instanceof WFNode) {
				WFNode node = (WFNode) item;
				
				boolean hidden = false;
				for(Property prop : node.getProperties()) {
					// AJR - check this out...
					if(prop.getName().equals("hideInNaigator")) {
						if(Boolean.parseBoolean(prop.getValue())) {
							hidden = true;
							break;
						}
					}
				}
				if(!hidden) {
					results.add(new WFTreeNode(node, inputElement));
				}
			}else {
				results.add(item);
			}
		}
		return results.toArray();
	}

	protected Collection<Object> getWFNodeChildren(WFNode node) {		
		List<Object> children = new ArrayList<Object>();
		
		loadWFNodeFiles(node, children);			

		if(preferences_.getMode() == Mode.HIERARCHICAL)
		{
			for(InputPort ip : node.getInputPorts()) {
				boolean connected = false;
				
				for(WFArc arc : ip.getArcs()){
					WFNode childNode = arc.getSource().getNode();
	
					if(childNode != null) {
						
						children.add(childNode);
						connected = true;
					}
				}
	
				if(preferences_.getShowUnconnectedInputs())
				{
					if(!connected) {
						children.add(ip);
					}
				}
			}		
		}
		return children;
	}

	
	protected void loadWFNodeFiles(WFNode node, List<Object> children)
	{		
		IPath rootFilePath = null;
		
		for(Property prop : node.getProperties()) {
			
			
			if(prop.getType().equals("home_file") || prop.getType().equals("local_file")) {
				if(rootFilePath == null) {
					Resource resouce = node.eResource();			
					URI rootUri = resouce.getURI();
					if(rootUri.isPlatformResource()) {
						rootFilePath = new Path(rootUri.toPlatformString(true));
					}else if(rootUri.isFile()) {
						rootFilePath = new Path(rootUri.toFileString());						
					}

					if(rootFilePath == null) {
						break;
					}
				}
				
				String fileName = prop.getValue();
				
				if(fileName == null || fileName.trim().isEmpty()) {
					continue;
				}
				
				IPath filePath = new Path(fileName);
				
				IFile file = null;
					
				if(!filePath.isAbsolute()) {
					filePath = rootFilePath.removeLastSegments(1).append(filePath);
				}
				
				file = CommonResourceUtils.getFileForPath(filePath.toString());
				

				children.add(file);							
			}				
		}
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return getElements(parentElement);
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof WFTreeNode) {
			return ((WFTreeNode)element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;	
	}

}
