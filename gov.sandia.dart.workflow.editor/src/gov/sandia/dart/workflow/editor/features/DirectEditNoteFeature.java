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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editparts.ZoomManager;
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
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.rendering.NoteGARenderer;
 
public class DirectEditNoteFeature extends AbstractDirectEditingFeature implements ICellEditorProvider {
 
    private Note note;

	public DirectEditNoteFeature(IFeatureProvider fp, Note note) {
        super(fp);
		this.note = note;
    }
 
    @Override
	public int getEditingType() {
        return TYPE_CUSTOM;
    }
 
    @Override
    public boolean canDirectEdit(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        return bo instanceof Note;
    }
 
    @Override
	public String getInitialValue(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Note note = (Note) getBusinessObjectForPictogramElement(pe);
        return note.getText();
    }
 
    @Override
    public String checkValueValid(String value, IDirectEditingContext context) {
        // null means that the value is valid
        return null;
    }
 
    @Override
	public void setValue(String value, IDirectEditingContext context) {
        // set the new text for the note
        PictogramElement pe = context.getPictogramElement();
        Note note = (Note) getBusinessObjectForPictogramElement(pe);
        note.setText(value);
 
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

	@Override
	public CellEditor createCellEditor(Composite parent) {
		TextCellEditor tce = new TextCellEditor(parent, SWT.MULTI | SWT.WRAP);
		return tce;
	}

	@Override
	public void relocate(CellEditor cellEditor, IFigure figure) {
		Rectangle bounds = figure.getBounds().getCopy();
		DirectEditNoteFeature.zoomBounds(bounds);

		Control control = cellEditor.getControl();
		control.setLocation(bounds.x + NoteGARenderer.CORNER + 2, bounds.y + 2);
		control.setSize(bounds.width - NoteGARenderer.CORNER - 4, bounds.height - 4);	
		control.setFont(WorkflowEditorPlugin.getDefault().getNotesFont());
		// TODO How to get the actual color??
		control.setBackground(NoteGARenderer.getColor(note.getColor()));
	}

	public static void zoomBounds(Rectangle bounds) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart editor = page.getActiveEditor();
		double zoom = 1;
		if(editor instanceof WorkflowDiagramEditor) {
			WorkflowDiagramEditor workflowEditor = (WorkflowDiagramEditor) editor;
			ZoomManager zm = (ZoomManager) workflowEditor.getAdapter(ZoomManager.class);
			if (zm != null) {
				zoom = zm.getZoom();
				bounds.scale(zoom, zoom);
				Point location = zm.getViewport().getViewLocation();
				bounds.translate(-location.x, -location.y);
			}
		}
	}
}
