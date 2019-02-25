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

import java.util.List;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.graphiti.ui.editor.IDiagramContainerUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.swt.SWT;

class WorkflowDiagramBehavior extends DiagramBehavior {

	public WorkflowDiagramBehavior(IDiagramContainerUI diagramContainer) {
		super(diagramContainer);
	}

	@Override
	protected ContextMenuProvider createContextMenuProvider() {
		return new DiagramEditorContextMenuProvider(getDiagramContainer().getGraphicalViewer(),
				getDiagramContainer().getActionRegistry(),
				getConfigurationProvider()) {
			@Override
			protected void addDefaultMenuGroupRest(IMenuManager manager) {
				super.addDefaultMenuGroupRest(manager);
				manager.remove("predefined remove action");
			}

		};
	}
	
	@Override
	protected KeyHandler getCommonKeyHandler() {
		KeyHandler handler = super.getCommonKeyHandler();
		handler.put(KeyStroke.getPressed('d', SWT.NONE),
				getDiagramContainer().getActionRegistry().getAction(DuplicateAction.ID));
		return handler;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void initActionRegistry(ZoomManager zoomManager) {
		super.initActionRegistry(zoomManager);
		IDiagramContainerUI diagramContainer = getDiagramContainer();
		final ActionRegistry registry = diagramContainer.getActionRegistry();
		Action action = new DuplicateAction(getParentPart(), getConfigurationProvider());
		registry.registerAction(action);
		List selectionActions = diagramContainer.getSelectionActions();
		selectionActions.add(DuplicateAction.ID);
	}
	
	@Override
	protected DefaultPaletteBehavior createPaletteBehaviour(){
		return new WorkflowPaletteBehavior(this);
	}
	
	@Override
	protected DefaultPersistencyBehavior createPersistencyBehavior() {
		return new WorkflowPersistencyBehavior(this);
	}
	
	@Override 
	protected DefaultRefreshBehavior createRefreshBehavior() {
		return new WorkflowRefreshBehavior(this);
	}
	
	
	// AJR - Trying to fix a bug where the workflow editor behaves badly when the workflow file 
	// has been updated behind the scenes
	//
	// This is definitely a hack to fix what appears to be a bug in graphiti. It SHOULD pop up 
	// the "File has been modified, do you want to reload?" dialog, and it occasionally does if 
	// Jupiter is aligned correctly with Venus. Otherwise it updates the workflow, shows it as 
	// dirty and asks the user to "Save"/"Don't save" even though there is nothing to save.
	//
	// What Graphiti does normally is call DiagramBehavior.refreshContent() to trigger an update
	// Internally this calls getPersistencyBehaviour().loadDiagram() which loads up the new file 
	// and in theory marks everything as clean. Other stuff gets called to update things.
	// Finally getRefreshBehavior().handleAutoUpdateAtReset() gets called which updates the UI.
	// Unfortunately this update of the UI bits is processed as a new command, even though the 
	// diagram has not changed since it was loaded. DefaultPersistencyBehavior.isDirty() then 
	// looks and says "Hey, a new command has been executed since the file was marked clean
	// therefore it is now dirty..."
	//
	// The hack here is to modify getRefreshBehavior().handleAutoUpdateAtReset() to do the UI 
	// update normally and then explicitly mark the diagram as clean via the 
	// WorkbenchPersistencyBehavior.markClean() method (added just for this), then tell eclipse 
	// to refresh based on the new dirty state (which is now once again clean!).
	//
	// TODO: See if we can get the "File has been modified, do you want to reload?" dialog to show up consistently.
	class WorkflowRefreshBehavior extends DefaultRefreshBehavior {

		public WorkflowRefreshBehavior(DiagramBehavior diagramBehavior) {
			super(diagramBehavior);
		}

		@Override
		public void handleAutoUpdateAtReset() {
			super.handleAutoUpdateAtReset();
			
			if(getPersistencyBehavior() instanceof WorkflowPersistencyBehavior) {
			
				((WorkflowPersistencyBehavior) getPersistencyBehavior()).markClean();
	
				diagramBehavior.getDiagramContainer().updateDirtyState();				
			}
		}

	}

}
