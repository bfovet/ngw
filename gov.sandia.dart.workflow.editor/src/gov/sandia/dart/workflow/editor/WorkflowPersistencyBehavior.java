/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

class WorkflowPersistencyBehavior extends DefaultPersistencyBehavior {

	public WorkflowPersistencyBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
	}
	
	// We could modify diagram after loading
	@Override
	public Diagram loadDiagram(URI uri) {
		return super.loadDiagram(uri);
	}
	
	@Override
	protected Set<Resource> save(TransactionalEditingDomain domain,
			Map<Resource, Map<?, ?>> saveOptions, IProgressMonitor monitor) {
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			
			@Override
			protected void doExecute() {
				// We may have messed with connection transparency. Reset those before saving.
				Diagram diagram = diagramBehavior.getDiagramTypeProvider().getDiagram();
				EList<Connection> connections = diagram.getConnections();
				for (Connection c: connections) {
					c.getGraphicsAlgorithm().setTransparency(0.0);
				}			
				
			}
		});

		return super.save(domain, saveOptions, monitor);
	}

	// Needed for a hack bugfix concerning the dirty marker (see WorkflowDiagramBehavior#WorkflowRefreshBehavior)
	void markClean() {
		savedCommand = diagramBehavior.getEditingDomain().getCommandStack().getUndoCommand();
	}
}
