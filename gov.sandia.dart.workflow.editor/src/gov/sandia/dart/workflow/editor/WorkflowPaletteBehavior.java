/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.dialogs.FilteredTree;

import gov.sandia.dart.workflow.editor.library.INodeLibraryChangeListener;
import gov.sandia.dart.workflow.editor.library.UserCustomNodeLibrary;
import gov.sandia.dart.workflow.editor.palette.PaletteTreeViewer;
import gov.sandia.dart.workflow.editor.palette.PaletteTreeViewerProvider;

public class WorkflowPaletteBehavior extends DefaultPaletteBehavior implements INodeLibraryChangeListener {

	private PaletteTreeViewerProvider pvp;
	private PaletteRoot paletteRoot;

	public WorkflowPaletteBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
		UserCustomNodeLibrary.addListener(this);
	}

	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		pvp = new PaletteTreeViewerProvider(this.diagramBehavior.getEditDomain());
		return pvp;
	}
	
	@Override
	protected PaletteRoot createPaletteRoot() {
		paletteRoot = super.createPaletteRoot();
		paletteRoot.remove((PaletteEntry) paletteRoot.getChildren().get(0));
		return paletteRoot;
	}
	
	@Override
	public PaletteRoot getPaletteRoot() {
		if (paletteRoot == null) {
			createPaletteRoot();			
		}
		return paletteRoot;
	}
	
	@Override
	public void refreshPalette() {
		if (pvp != null) {
			PaletteTreeViewer viewer = (PaletteTreeViewer) pvp.getPaletteViewer();
			Object expanded = getExandedDrawers(viewer);
			paletteRoot = null;
			pvp.getPaletteViewer().setPaletteRoot(createPaletteRoot());
			pvp.configurePaletteViewer(viewer);	
			setExpandedDrawers(viewer, expanded);
		}
	}
	
	private void setExpandedDrawers(PaletteViewer viewer, Object expanded) {
		FilteredTree tree = (FilteredTree) viewer.getControl();
		TreeViewer tv = tree.getViewer();
		tv.setExpandedTreePaths((TreePath[]) expanded);	
	}

	private Object getExandedDrawers(PaletteViewer viewer) {
		FilteredTree tree = (FilteredTree) viewer.getControl();
		TreeViewer tv = tree.getViewer();
		return tv.getExpandedTreePaths();	
	}

	@Override
	public void dispose() {
		UserCustomNodeLibrary.removeListener(this);
	}

	@Override
	public void nodeLibraryChanged() {
		refreshPalette();		
	}

}
