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

import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultPersistencyBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

class WorkbenchPersistencyBehavior extends DefaultPersistencyBehavior {

	public WorkbenchPersistencyBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
	}
	
	// We could modify diagram after loading
	@Override
	public Diagram loadDiagram(URI uri) {
		return super.loadDiagram(uri);
	}
	
	// We could modify diagram before saving
	@Override
	protected Set<Resource> save(TransactionalEditingDomain editingDomain,
			Map<Resource, Map<?, ?>> saveOptions, IProgressMonitor monitor) {
		return super.save(editingDomain, saveOptions, monitor);
	}
}
