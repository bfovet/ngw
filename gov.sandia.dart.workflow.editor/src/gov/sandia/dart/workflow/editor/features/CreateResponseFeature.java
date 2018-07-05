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

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.Response;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
 
public class CreateResponseFeature extends AbstractCreateFeature {		
	public CreateResponseFeature(IFeatureProvider featureProvider) {
    	super(featureProvider, "Response", "Create Response");
    }

	@Override
	public boolean canCreate(ICreateContext context) {
        return context.getTargetContainer() instanceof Diagram;
    }
 
    @Override
	public Object[] create(ICreateContext context) {         
        Response newResponse = DomainFactory.eINSTANCE.createResponse();       
        newResponse.setName("r"); 
        newResponse.setType("default"); 
        
        // do the add
        addGraphicalRepresentation(context, newResponse);
         
        // return newly created business object(s)
        return new Object[] { newResponse };	
     }   
 }