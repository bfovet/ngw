/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree.data;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;

public class DiagramWFTreeRoot extends WFTreeRoot {

	IDiagramTypeProvider diagramTypeProvider_;
	
	public DiagramWFTreeRoot(IDiagramTypeProvider diagramTypeProvider, Object parent) {
		super(diagramTypeProvider.getDiagram().getName(), parent);
		diagramTypeProvider_ = diagramTypeProvider;
	}

	@Override
	protected Resource getRootResource() {
		return diagramTypeProvider_.getDiagram().eResource();
	}

	@Override
	protected boolean needsToBeRefreshed() {
		return true;
	}

}
