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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.platform.ICellEditorProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import gov.sandia.dart.workflow.domain.Image;
 
public class DirectEditImageFeature extends AbstractDirectEditingFeature implements ICellEditorProvider {
 
    public DirectEditImageFeature(IFeatureProvider fp) {
        super(fp);
    }
 
    @Override
	public int getEditingType() {
        return TYPE_CUSTOM;
    }
 
    @Override
    public boolean canDirectEdit(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        return bo instanceof Image;
    }
 
    @Override
	public String getInitialValue(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Image image = (Image) getBusinessObjectForPictogramElement(pe);
        return image.getText();
    }
 
    @Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        // null means that the value is valid
        return null;
    }
 
    @Override
	public void setValue(String value, IDirectEditingContext context) {
        // set the new text for the image
        PictogramElement pe = context.getPictogramElement();
        Image image = (Image) getBusinessObjectForPictogramElement(pe);
        image.setText(value);
 
        // Explicitly update the shape to display the new value in the diagram
        // Image, that this might not be necessary in future versions of Graphiti
        // (currently in discussion)
 
        // we know, that pe is the Shape of the Text, so its container is the
        // main shape of the WFNode
        updatePictogramElement(((Shape) pe).getContainer());
    }
    
    @Override
    public boolean stretchFieldToFitText() {
    	return true;
    }

	@Override
	public CellEditor createCellEditor(Composite parent) {
		TextCellEditor tce = new TextCellEditor(parent, SWT.MULTI | SWT.WRAP);
		return tce;
	}

	@Override
	public void relocate(CellEditor cellEditor, IFigure figure) {
		Rectangle bounds = figure.getBounds();
		Control control = cellEditor.getControl();
		control.setLocation(bounds.x + 2, bounds.y + 2);
		control.setSize(bounds.width - 4, bounds.height - 4);	
		//control.setBackground(ImageGARenderer.getColor());
	}
}
