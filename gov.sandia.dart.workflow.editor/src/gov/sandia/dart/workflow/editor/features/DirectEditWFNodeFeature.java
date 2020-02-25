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
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.MultiText;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;

import gov.sandia.dart.workflow.domain.WFNode;
 
public class DirectEditWFNodeFeature extends AbstractDirectEditingFeature {
 
    public DirectEditWFNodeFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
	public int getEditingType() {
        // there are several possible editor-types supported:
        // text-field, checkbox, color-chooser, combobox, ...
        return TYPE_TEXT;
    }
 
    @Override
    public boolean canDirectEdit(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
        // support direct editing, if it is a WFNode, and the user clicked
        // directly on the text and not somewhere else in the rectangle
        if (bo instanceof  WFNode && ga instanceof MultiText) {
            return true;
        }
        // direct editing not supported in all other cases
        return false;
    }
 
    @Override
	public String getInitialValue(IDirectEditingContext context) {
        // return the current name of the WFNode
        PictogramElement pe = context.getPictogramElement();
        WFNode no = (WFNode) getBusinessObjectForPictogramElement(pe);
        return no.getLabel();
    }
 
    @Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        if (value.length() < 1)
            return "Please enter any text as node label.";

        // null means, that the value is valid
        return null;
    }
 
    @Override
	public void setValue(String value, IDirectEditingContext context) {
        // set the new name for the WFNode
        PictogramElement pe = context.getPictogramElement();
        WFNode no = (WFNode) getBusinessObjectForPictogramElement(pe);
        no.setLabel(value);
 
        // Explicitly update the shape to display the new value in the diagram
        // Note, that this might not be necessary in future versions of Graphiti
        // (currently in discussion)
 
        // we know, that pe is the Shape of the Text, so its container is the
        // main shape of the WFNode
        updatePictogramElement(((Shape) pe).getContainer());
    }
    
    @Override
    public boolean stretchFieldToFitText() {
    		return true;
    }
}
