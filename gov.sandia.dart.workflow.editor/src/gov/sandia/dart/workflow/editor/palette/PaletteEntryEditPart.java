/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.palette;

import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.graphics.Image;

public class PaletteEntryEditPart extends
		org.eclipse.gef.editparts.AbstractTreeEditPart {

	public PaletteEntryEditPart(PaletteEntry model) {
		super(model);
	}

	@Override
	protected String getText() {
		PaletteEntry entry = (PaletteEntry) getModel();
		return entry.getLabel();
	}

	@Override
	protected Image getImage() {
		PaletteEntry entry = (PaletteEntry) getModel();
		ImageDescriptor smallIcon = entry.getSmallIcon();
		if (smallIcon == null) {
			return getDefaultImage();
		}		
		return smallIcon.createImage();
	}
	
	protected Image getDefaultImage(){
		return null;
	}
	
	public DragTracker getDragTracker(Request req) {
		return new TreeSelectionTracker(this);
	}

	
	// Tracker to un-select current tool on drop
	
	private class TreeSelectionTracker extends SelectEditPartTracker {

		public TreeSelectionTracker(EditPart owner) {
			super(owner);
		}

		protected boolean handleNativeDragFinished(DragSourceEvent event) {
			((PaletteTreeViewer)getViewer()).setActiveTool(null);
			return true;
		}
	}

}
