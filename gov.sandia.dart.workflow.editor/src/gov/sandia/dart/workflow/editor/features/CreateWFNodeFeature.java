/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowImageProvider;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.util.ParameterUtils;
 
public class CreateWFNodeFeature extends AbstractCreateNodeFeature {	
	NodeType nodeType;
	private boolean duplicating;

	public CreateWFNodeFeature(IFeatureProvider featureProvider, NodeType nodeType) {
    	super(featureProvider, "Workflow Node", "Create Workflow Node");
    	this.nodeType = nodeType;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
        return context.getTargetContainer() instanceof Diagram;
    }
 
    @Override
	public Object[] create(ICreateContext context) {         
        WFNode newNode = DomainFactory.eINSTANCE.createWFNode();               
        String newName = makeNameUnique(getNodeName(), context);                
        newNode.setName(newName);
        newNode.setType(nodeType.getName());
        String label = nodeType.getDisplayLabel();       
        newNode.setLabel(StringUtils.isEmpty(label) ? nodeType.getName() : label);

        addPortsAndProperties(newNode, nodeType);  
    	
        if (ParameterUtils.isParameter(newNode) && !duplicating)
        	ParameterUtils.setValue(newNode, "");
        
        // do the add
        addGraphicalRepresentation(context, newNode);
 
        // activate direct editing after object creation
        if (!duplicating)
        		getFeatureProvider().getDirectEditingInfo().setActive(true);
        
        // return newly created business object(s)
        return new Object[] { newNode };	
     }

	private String makeNameUnique(String newName, ICreateContext context) {
		String checkName = newName;		
		if(context.getTargetContainer() instanceof Diagram){
			Diagram diagram = (Diagram) context.getTargetContainer();

			boolean foundMatch = false;			
			int suffix = 1;
			
			do{				
				if(foundMatch){
					checkName = newName + suffix;
					suffix++;
				}
				
				foundMatch = false;
				
				for(EObject object : diagram.eResource().getContents()){
					if(object instanceof WFNode && ((NamedObject)object).getName().equals(checkName)){
						foundMatch = true;
						break;
					}
				}
			}while(foundMatch);			
		}		
		return checkName;
	}

	private String getNodeName() {
		if (duplicating)
			return nodeType.getLabel();
		
		String name = nodeType.getLabel();
		if (StringUtils.isEmpty(name))
			name = nodeType.getName();
		return name;
	}

	@Override
    public String getCreateImageId() {
    	String id = WorkflowImageProvider.PREFIX + nodeType.getName();
    	if (WorkflowImageProvider.get().getImageFilePath(id) != null)
    		return id;
    	else
    		return null;    			
    }
    
	@Override
    public String getCreateLargeImageId() {
    	return getCreateImageId();
    }   
	
	public NodeType getNodeType() {
		return nodeType;
	}

	public void setDuplicating() {
		duplicating = true;
	}	
}
