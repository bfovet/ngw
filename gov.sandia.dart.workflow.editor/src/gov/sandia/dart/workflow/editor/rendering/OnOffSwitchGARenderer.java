package gov.sandia.dart.workflow.editor.rendering;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.swt.SWT;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class OnOffSwitchGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

	@Override
	protected void fillShape(Graphics g) {		
		Rectangle bounds = getInnerBounds().getShrinked(10, 10);
		g.setAntialias(SWT.ON);
		g.setForegroundColor(ColorConstants.black);
		g.setLineStyle(Graphics.LINE_SOLID);
		g.setLineWidth(1);	
		g.setBackgroundColor(ColorConstants.lightGray);
		Rectangle switchBounds = new Rectangle(bounds.getTopLeft(), bounds.getBottomRight());		
		g.fillRoundRectangle(switchBounds, 1, 1);
		g.drawRoundRectangle(switchBounds, 1, 1);

	}

	@Override
	protected void outlineShape(Graphics g) {		
		Rectangle ib = getInnerBounds();
		g.setForegroundColor(ColorConstants.black);
		g.setBackgroundColor(ColorConstants.white);
		g.setLineStyle(Graphics.LINE_SOLID);
		g.setLineWidth(1);
		
		Rectangle clip = new Rectangle(ib.x, ib.y-GenericWFNodeGARenderer.CLEARANCE, ib.width, ib.height+GenericWFNodeGARenderer.CLEARANCE+GenericWFNodeGARenderer.MORE_CLEARANCE);
		g.setClip(clip);

		// Find center point
		Point center = ib.getCenter();
		Rectangle switchBounds = getInnerBounds().getShrinked(10, 10);

		boolean isOn = isOn();
		int thumbLeft = isOn ? center.x :  switchBounds.x;
		int textLeft = !isOn ? center.x :  switchBounds.x;

		Rectangle thumb = new Rectangle(thumbLeft, switchBounds.y, switchBounds.width/2, switchBounds.height);		
		Rectangle text = new Rectangle(textLeft, switchBounds.y, switchBounds.width/2, switchBounds.height);		

		g.fillRoundRectangle(thumb, 10, 10);
		g.drawRoundRectangle(thumb, 10,  10);
		centerText(g, text, isOn ? "ON" : "OFF");
		
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
			if (s.getType().equals("onOffSwitch")) {
				return "true".equals(PropertyUtils.getProperty(s, "onOff"));
			}
		}
		return false;
	}

	static boolean hideLabels(WFNode node) {
		String type = node.getType();
		return type.equals("onOffSwitch") || type.equals("abSwitch");
	}

}
