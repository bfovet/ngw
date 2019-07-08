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
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.impl.AbstractDirectEditingFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.platform.ICellEditorProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
 
public class DirectEditResponseFeature extends AbstractDirectEditingFeature implements ICellEditorProvider {
 
    public DirectEditResponseFeature(IFeatureProvider fp) {
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
        return bo instanceof Response;
    }
 
    @Override
	public String getInitialValue(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Response response = (Response) getBusinessObjectForPictogramElement(pe);
        return response.getName();
    }
 
    @Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        // null means that the value is valid
        return StringUtils.isNotEmpty(value) ? null : "Response name must not be empty";
    }
 
    @Override
	public void setValue(String value, IDirectEditingContext context) {
    	
    	if (StringUtils.isEmpty(value))
    		return;
    	
        // set the new name for the Response
        PictogramElement pe = context.getPictogramElement();
        Response response = (Response) getBusinessObjectForPictogramElement(pe);
        response.setName(value);
 
        // Explicitly update the shape to display the new value in the diagram
        // Note, that this might not be necessary in future versions of Graphiti
        // (currently in discussion)
 
        // we know, that pe is the Shape of the Text, so its container is the
        // main shape of the Response
        updatePictogramElement(((Shape) pe).getContainer());
    }
    
    @Override
    public boolean stretchFieldToFitText() {
    	return true;
    }
    
	@Override
	public CellEditor createCellEditor(Composite parent) {
		TextCellEditor tce = new TextCellEditor(parent, SWT.NONE);
		tce.setValidator(new ICellEditorValidator() {
			
			@Override
			public String isValid(Object value) {
				return checkValueValid(String.valueOf(value), null);
			}
		});
		return tce;
	}

	@Override
	public void relocate(CellEditor cellEditor, IFigure figure) {
		Rectangle bounds = figure.getBounds();
		Control control = cellEditor.getControl();
		control.setLocation(bounds.x + 8, bounds.y + 8);
		control.setSize(bounds.width - 10, bounds.height - 6);	
		control.setFont(WorkflowEditorPlugin.getDefault().getDiagramFont());
	}
}
