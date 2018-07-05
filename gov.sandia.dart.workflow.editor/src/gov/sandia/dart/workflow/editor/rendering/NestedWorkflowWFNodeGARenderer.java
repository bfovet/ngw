/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
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
import org.eclipse.graphiti.platform.ga.IVisualState;
import org.eclipse.graphiti.platform.ga.IVisualStateChangeListener;
import org.eclipse.graphiti.platform.ga.IVisualStateHolder;
import org.eclipse.graphiti.platform.ga.VisualState;
import org.eclipse.graphiti.platform.ga.VisualStateChangedEvent;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.WorkflowToolBehaviorProvider;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class NestedWorkflowWFNodeGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer, IVisualStateHolder, IVisualStateChangeListener {


	private IVisualState visualState;
	private Color topColor = ColorConstants.lightGray;
	
	@Override
	public IVisualState getVisualState() {
		if (visualState == null) {
			visualState = new VisualState();
			visualState.addChangeListener(this);
		}
		return visualState;
	}
	
	@Override
	protected void fillShape(Graphics g) {		
		Rectangle innerBounds = getInnerBounds();

		Pattern pattern = new Pattern(Display.getCurrent(), innerBounds.getTopLeft().x,
				innerBounds.getTopLeft().y, innerBounds.getTopRight().x, innerBounds.getTopRight().y,
				topColor, ColorConstants.white);
		g.setAntialias(SWT.ON);
		g.setBackgroundPattern(pattern);
		g.fillRoundRectangle(new Rectangle(innerBounds.getTopLeft(), innerBounds.getBottomRight()), 10, 10);
		g.setBackgroundPattern(null);
		pattern.dispose();
		
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();		
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		Image image = GenericWFNodeGARenderer.getIcon((WFNode) bo);
		g.drawImage(image, innerBounds.getTopLeft().translate(5, 5));

	}

	@Override
	protected void outlineShape(Graphics g) {
		Font f = WorkflowEditorPlugin.getDefault().getDiagramFont();
		g.setFont(f);
		Rectangle ib = getInnerBounds();
		g.setForegroundColor(ColorConstants.black);
		g.setLineStyle(Graphics.LINE_SOLID);
		g.setLineWidth(1);
		g.drawRoundRectangle(new Rectangle(ib.getTopLeft(), ib.getBottomRight()), 10, 10);
		
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();		
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof WFNode) {
			WFNode node = (WFNode) bo;			
			Rectangle clip = new Rectangle(ib.x, ib.y- GenericWFNodeGARenderer.CLEARANCE, ib.width, ib.height+ GenericWFNodeGARenderer.CLEARANCE+ GenericWFNodeGARenderer.MORE_CLEARANCE);
			g.setClip(clip);
			Rectangle r = new Rectangle(ib.x + 10, ib.y, ib.width - 15, ib.height);
			if (!StringUtils.isEmpty(node.getLabel()))
				GenericWFNodeGARenderer.renderTextAbove(g, r, node.getLabel());
			
			g.setForegroundColor(ColorConstants.blue);
	        if (WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.NODE_TYPE_HEADERS))
	        		g.drawText(node.getType(), ib.x + 24, ib.y + 4);
	        else
        			g.drawText(node.getName(), ib.x + 24, ib.y + 4);
			
			IToolBehaviorProvider tbp = fp.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
			if (tbp instanceof WorkflowToolBehaviorProvider) {
				Boolean marker = ((WorkflowToolBehaviorProvider) tbp).getExecutionStatus((WFNode) bo);			
				if (marker != null) {
					g.setForegroundColor(marker ? ColorConstants.darkGreen : ColorConstants.red);
					g.setLineStyle(Graphics.LINE_DASHDOT);
					g.setLineWidth(3);
					Point br = new Point(ib.getBottomRight().x-2, ib.getBottomRight().y-2);
					g.drawRoundRectangle(new Rectangle(ib.getTopLeft(), br), 10, 10);
				}
			}
		}
	}
	
	@Override
	public void visualStateChanged(VisualStateChangedEvent e) {
		int selectionFeedback = getVisualState().getSelectionFeedback();
		if (selectionFeedback == IVisualState.SELECTION_PRIMARY) {
			topColor = ColorConstants.white;
		} else  {
			topColor = ColorConstants.lightGray;
		}
	}
}
