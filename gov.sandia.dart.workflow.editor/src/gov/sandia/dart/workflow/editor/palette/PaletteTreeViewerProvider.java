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

import java.util.List;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.widgets.Composite;

import gov.sandia.dart.workflow.editor.PaletteBuilder;
import gov.sandia.dart.workflow.editor.library.UserCustomNodeLibrary;
import gov.sandia.dart.workflow.util.WorkflowHelp;

public class PaletteTreeViewerProvider extends PaletteViewerProvider {

	private PaletteTreeViewer paletteViewer;


	public PaletteTreeViewerProvider(EditDomain editDomain) {
		super(editDomain);
	}

	@Override
	public PaletteViewer createPaletteViewer(Composite parent) {
		paletteViewer = new PaletteTreeViewer();
		paletteViewer.createTreeControl(parent);
		configurePaletteViewer(paletteViewer);
		hookPaletteViewer(paletteViewer);
		return paletteViewer;
	}

	public PaletteViewer getPaletteViewer() {
		return paletteViewer;
	}
	
	
	@Override
	public void configurePaletteViewer(PaletteViewer viewer) {
		super.configurePaletteViewer(viewer);
		viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
		
		MenuManager menu = viewer.getContextMenu();
		
		menu.addMenuListener(new IMenuListener() {
			Action deleteAction = new Action("Delete") {
				@Override
				public void run() {
					ToolEntry activeTool = viewer.getActiveTool();
					if (activeTool != null) {
						PaletteContainer parent = activeTool.getParent();
						if (PaletteBuilder.USER_DEFINED.equals(parent.getLabel())) {
							parent.remove(activeTool);	
							ToolEntry defaultTool = viewer.getPaletteRoot().getDefaultEntry();
							viewer.setActiveTool(defaultTool);
							UserCustomNodeLibrary.removeNodeType(activeTool.getDescription());							
						}
					}
				}
			};
			Action helpAction = new Action("Help") {
				@Override
				public void run() {
					ToolEntry activeTool = viewer.getActiveTool();
					if (activeTool != null) {
						PaletteContainer parent = activeTool.getParent();
						if (!PaletteBuilder.USER_DEFINED.equals(parent.getLabel())) {
							WorkflowHelp.openDocumentationWebPage(activeTool.getLabel());
						}
					}
				}
			};
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				List<?> parts = viewer.getSelectedEditParts();
				if (parts.isEmpty() || parts.get(0) instanceof PaletteTreeNodeEditPart)
					return;
				ToolEntry activeTool = viewer.getActiveTool();
				PaletteContainer parent = activeTool.getParent();
				
				if (parent != null) {
					if (PaletteBuilder.USER_DEFINED.equals(parent.getLabel())) {
						// User defined tools have no help and can be deleted from palette
						manager.add(deleteAction);
					} else if ("Standard tools".equals(parent.getLabel())) {
						// Nothing
					} else {
						// One of our tools, show help
						manager.add(helpAction);
					}
				}
			}
		});

	}
}
