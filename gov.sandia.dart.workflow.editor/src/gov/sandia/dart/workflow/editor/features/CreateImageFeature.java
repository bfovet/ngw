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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.Image;
 
public class CreateImageFeature extends AbstractCreateFeature {		
	private boolean duplicating;

	public CreateImageFeature(IFeatureProvider featureProvider) {
    	super(featureProvider, "Image", "Create Image");
    }

	@Override
	public boolean canCreate(ICreateContext context) {
        return context.getTargetContainer() instanceof Diagram;
    }
 
    @Override
	public Object[] create(ICreateContext context) {         
        Image newImage = DomainFactory.eINSTANCE.createImage();       
        newImage.setText(""); 
        
        // do the add
        addGraphicalRepresentation(context, newImage);
 
        // activate direct editing after object creation
        if (!duplicating)
        	getFeatureProvider().getDirectEditingInfo().setActive(true);
        
        // return newly created business object(s)
        return new Object[] { newImage };	
     }

	public void setDuplicating() {
		duplicating = true;
	}   
 }
