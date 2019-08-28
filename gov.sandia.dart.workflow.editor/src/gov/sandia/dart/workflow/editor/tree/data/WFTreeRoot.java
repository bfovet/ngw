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
import gov.sandia.dart.workflow.editor.tree.WorkflowTreePreferences;
import gov.sandia.dart.workflow.editor.tree.WorkflowTreePreferences.Mode;

public abstract class WFTreeRoot{
	private Object parent_;
	
	private String name_;
	
	private List<Object> nodes_ = null;
	
	private Mode oldMode_ = Mode.FLAT;

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

	public List<Object> getNodes(WorkflowTreePreferences preferences) {
		
		boolean refresh = needsToBeRefreshed();
		
		if(refresh || oldMode_ != preferences.getMode()) {
			oldMode_ = preferences.getMode();
			nodes_ = null;
		}
		
		if(nodes_ == null) {
			
			nodes_ = new ArrayList<>();
			
			try {
				Resource resource = getRootResource();
				resource.load(Collections.EMPTY_MAP);
		
		
				if(preferences.getMode() == Mode.FLAT) {
					loadFlatNodes(resource);
				}else {
					loadHierarchicalNodes(resource);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return nodes_;
	}

	abstract protected Resource getRootResource();

	abstract protected boolean needsToBeRefreshed();
		
	private void loadHierarchicalNodes(Resource resource) {
		
		for(EObject object : resource.getContents())
		{
			
			if(object instanceof WFNode) {
				WFNode node = (WFNode) object;
				
				
				boolean hasConnection = false;

				for(OutputPort op : node.getOutputPorts()) {
					if(!op.getArcs().isEmpty())
					{
						hasConnection = true;
						break;
					}						
				}
				
				if(!hasConnection) {
					nodes_.add(node);
				}
			}
		}
		
		
		// Find circular references which have no root...
		
		for(EObject object : resource.getContents())
		{
			if(object instanceof WFNode) {
				WFNode node = (WFNode) object;
				
				if(nodes_.contains(node)) {
					continue;
				}
				
				if(!canFindRoot(node, null)) {
					nodes_.add(node);
				}
			}			
		}
		
	}

	// See if this node can be traced back to one of the "root" nodes
	private boolean canFindRoot(WFNode checkNode, Set<WFNode> checkedNodes) {
		if(nodes_.contains(checkNode)) {
			return true;
		}		

		if(checkedNodes == null) {
			checkedNodes = new HashSet<>();
		}else if(checkedNodes.contains(checkNode)) {
			return false;
		}
		
		checkedNodes.add(checkNode);
		
		for(OutputPort op :checkNode.getOutputPorts()) {
			for(WFArc arc : op.getArcs()) {
				WFNode childNode = arc.getTarget().getNode();
				
				// It we entered a loop, check the next node

				if(canFindRoot(childNode, checkedNodes)) {
					return true;
				}
			}
		}
		return false;
	}

	private void loadFlatNodes(Resource resource){
		
		for(EObject object : resource.getContents())
		{
			if(object instanceof WFNode) {
				WFNode node = (WFNode) object;						
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
									nodes_.add(nestedWF);
								}catch(IOException e) {
									
								}
								
							}
							

						}
					}				
				}else {
					nodes_.add(object);
				}
			}
		}
	}	
}
