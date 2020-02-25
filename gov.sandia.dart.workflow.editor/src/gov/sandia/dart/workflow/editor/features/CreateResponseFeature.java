/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.Response;
 
public class CreateResponseFeature extends AbstractCreateFeature {		
	public static final String NAME="name";
	private boolean duplicating;
	
	public CreateResponseFeature(IFeatureProvider featureProvider) {
    	super(featureProvider, "Response", "Create Response");
    }

	@Override
	public boolean canCreate(ICreateContext context) {
        return context.getTargetContainer() instanceof Diagram;
    }
 
    @Override
	public Object[] create(ICreateContext context) {    
    	Object name = context.getProperty(NAME);
    	Response newResponse = DomainFactory.eINSTANCE.createResponse();       
        newResponse.setName(makeNameUnique(name == null ? "r" : String.valueOf(name), context)); 
        newResponse.setType("default"); 
        
        // do the add
        addGraphicalRepresentation(context, newResponse);
         
        // activate direct editing after object creation
        if (!duplicating)
        	getFeatureProvider().getDirectEditingInfo().setActive(true);

        // return newly created business object(s)
        return new Object[] { newResponse };	
     }

	public void setDuplicating() {
		duplicating = true;		
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
					if(object instanceof Response && ((NamedObject)object).getName().equals(checkName)){
						foundMatch = true;
						break;
					}
				}
			}while(foundMatch);			
		}		
		return checkName;
	}
 }
