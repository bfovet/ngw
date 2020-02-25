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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class ABSwitchGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

	@Override
	protected void fillShape(Graphics g) {		
	}

	@Override
	protected void outlineShape(Graphics g) {		
		Rectangle ib = getInnerBounds();
		g.setForegroundColor(ColorConstants.black);
		g.setBackgroundColor(ColorConstants.white);
		g.setLineStyle(Graphics.LINE_SOLID);
		g.setLineWidth(2);
		
		Rectangle clip = new Rectangle(ib.x, ib.y-GenericWFNodeGARenderer.CLEARANCE, ib.width, ib.height+GenericWFNodeGARenderer.CLEARANCE+GenericWFNodeGARenderer.MORE_CLEARANCE);
		g.setClip(clip);

		
		// Find center point
		Point center = ib.getCenter();
		center.y = ib.y + 30;
		
		// left end
		Point left = new Point(ib.x, center.y);

		// Top branch
		Point top = new Point(ib.x + ib.width, ib.y + 30);
	
		// Bottom branch
		Point bottom = new Point(ib.x + ib.width, ib.y + 45);


		boolean isOn = isOn();

		g.drawLine(left, center);
		g.drawOval(center.x-2, center.y-2, 5, 5);
		if (isOn())
			g.drawLine(center, top);
		else
			g.drawLine(center, bottom);
		
		Rectangle text = new Rectangle(ib.getTopLeft(), center);
		centerText(g, text, isOn ? "A" : "B");
		
		
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			WFNode node = (WFNode) bo;
			Rectangle r = new Rectangle(ib.x + 10, ib.y, ib.width - 15, ib.height);
			if (!StringUtils.isEmpty(node.getLabel()))
				renderTextAbove(g, r, node.getLabel());
		}

	}
	
	private boolean isOn() {
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			WFNode s = (WFNode) bo;
			if (s.getType().equals("abSwitch")) {
				return "true".equals(PropertyUtils.getProperty(s, "a"));
			}
		}
		return false;
	}
}
