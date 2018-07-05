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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.impl.AbstractLayoutFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.Response;
 
public class LayoutResponseFeature extends AbstractLayoutFeature {
 
    private static final int MIN_HEIGHT = 20; 
    private static final int MIN_WIDTH = 50;
 
    public LayoutResponseFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
	public boolean canLayout(ILayoutContext context) {
       // return true, if pictogram element is linked to a Response
       PictogramElement pe = context.getPictogramElement();
       if (!(pe instanceof ContainerShape))
           return false;
       EList<EObject> businessObjects = pe.getLink().getBusinessObjects();
       return businessObjects.size() == 1 
              && businessObjects.get(0) instanceof Response;
    }
 
    @Override
	public boolean layout(ILayoutContext context) {
        boolean anythingChanged = false;
        ContainerShape containerShape =
            (ContainerShape) context.getPictogramElement();
        GraphicsAlgorithm containerGa = containerShape.getGraphicsAlgorithm();
 
        // height
        if (containerGa.getHeight() < MIN_HEIGHT) {
            containerGa.setHeight(MIN_HEIGHT);
            anythingChanged = true;
        }
 
        // width
        if (containerGa.getWidth() < MIN_WIDTH) {
            containerGa.setWidth(MIN_WIDTH);
            anythingChanged = true;
        }
 
        return anythingChanged;
    }
}
