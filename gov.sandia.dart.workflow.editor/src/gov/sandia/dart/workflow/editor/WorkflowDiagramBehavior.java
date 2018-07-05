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
		return new WorkbenchPersistencyBehavior(this);
	}
}
