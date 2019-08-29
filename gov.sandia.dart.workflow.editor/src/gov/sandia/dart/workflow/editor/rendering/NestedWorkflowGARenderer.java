package gov.sandia.dart.workflow.editor.rendering;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.Graphics;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class NestedWorkflowGARenderer extends AbstractNestedWorkflowGARenderer {

	public NestedWorkflowGARenderer() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void fillShape(Graphics g) {
		super.fillShape(g);
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		WFNode node = (WFNode) fp.getBusinessObjectForPictogramElement(pe);
		String fileName = PropertyUtils.getProperty(node, "fileName");
		// TODO Avoid loops!
		if (fileName != null) {
			IFile file = getDiagramFolder().getFile(new Path(fileName));
			if (file.exists()) {
				renderWorkflow(node, file, g);
			}
		}
	}
	

}