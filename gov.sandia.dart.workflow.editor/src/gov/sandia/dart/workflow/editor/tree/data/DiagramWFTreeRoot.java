package gov.sandia.dart.workflow.editor.tree.data;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class DiagramWFTreeRoot extends WFTreeRoot {

	Diagram diagram_;
	
	public DiagramWFTreeRoot(Diagram diagram, Object parent) {
		super(diagram.getName(), parent);
		diagram_ = diagram;
	}

	@Override
	protected Resource getRootResource() {
		return diagram_.eResource();
	}

	@Override
	protected boolean needsToBeRefreshed() {
		return true;
	}

}
