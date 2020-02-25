/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.rendering;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.WorkflowImageProvider;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class ParameterWFNodeGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {


	private Color topColor = ColorConstants.white;
	
	
	@Override
	protected void fillShape(Graphics g) {		
		Rectangle innerBounds = getInnerBounds();

		g.setAntialias(SWT.ON);
		g.setBackgroundColor(topColor);
		g.fillRoundRectangle(new Rectangle(innerBounds.getTopLeft(), innerBounds.getBottomRight()), 10, 10);
	}

	@Override
	protected void outlineShape(Graphics g) {		
		Font f = WorkflowEditorPlugin.getDefault().getDiagramFont();
		g.setFont(f);
		Rectangle innerBounds = getInnerBounds();
		g.setForegroundColor(ColorConstants.black);
		g.setLineStyle(Graphics.LINE_SOLID);
		g.setLineWidth(1);
		g.drawRoundRectangle(new Rectangle(innerBounds.getTopLeft(), innerBounds.getBottomRight()), 10, 10);
		
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();		
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode && ParameterUtils.isParameter((WFNode) bo)) {
			int iconOffset = 0;
			WFNode node = (WFNode) bo;
			if (isGlobal(node)) {
				Image image = getIcon();
				g.drawImage(image, innerBounds.getTopLeft().translate(5, 5));
				iconOffset = 20;
			}
			g.drawText(getLabel(node), innerBounds.x + iconOffset + 10, innerBounds.y + 5);			
		}
	}
	
	private boolean isGlobal(WFNode node) {
		for (OutputPort port: node.getOutputPorts() ) {
			if (port.getArcs().size() > 0 || port.getResponseArcs().size() > 0)
				return false;
		}
		return true;
	}

	private String getLabel(WFNode node) {
		return node.getName() + ": " + ParameterUtils.getValue(node);
	}

	private Image getIcon() {
		Image image = GenericWFNodeGARenderer.images.get(WorkflowImageProvider.IMG_GLOBE);
		if (image != null) {
			return image;
		}
		
		String path = WorkflowImageProvider.get().getImageFilePath(WorkflowImageProvider.IMG_GLOBE);		
		ImageDescriptor descriptor = WorkflowEditorPlugin.getImageDescriptor(path);
		image = descriptor.createImage();
		
		GenericWFNodeGARenderer.images.put(WorkflowImageProvider.IMG_GLOBE, image);
		return image;
	}
}
