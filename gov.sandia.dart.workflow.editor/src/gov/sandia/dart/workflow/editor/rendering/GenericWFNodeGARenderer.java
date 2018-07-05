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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.WorkflowImageProvider;
import gov.sandia.dart.workflow.editor.WorkflowToolBehaviorProvider;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class GenericWFNodeGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer, IVisualStateHolder, IVisualStateChangeListener {

    public static final String ID = "wfnode";
	static final int CLEARANCE = 100;
	static final int MORE_CLEARANCE = 24;
	private IVisualState visualState;
	private Color topColor = ColorConstants.white;
	
	static Map<String, Image> images = new ConcurrentHashMap<>();

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
				innerBounds.getTopLeft().y, innerBounds.getBottomLeft().x, innerBounds.getBottomLeft().y,
				topColor, ColorConstants.cyan);
		g.setAntialias(SWT.ON);
		g.setBackgroundPattern(pattern);
		g.fillRoundRectangle(new Rectangle(innerBounds.getTopLeft(), innerBounds.getBottomRight()), 10, 10);
		g.setBackgroundPattern(null);
		pattern.dispose();
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();		
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		
		Image image = getIcon((WFNode) bo);
		g.drawImage(image, innerBounds.getTopLeft().translate(5, 5));
				
	}

	// TODO We need a mechanism for contributing these icons.
	public static Image getIcon(WFNode bo) {
		String id = WorkflowImageProvider.PREFIX + bo.getType();
		Image image = images.get(id);
		if (image != null) {
			return image;
		}
		
		String path = WorkflowImageProvider.get().getImageFilePath(id);
		if (path == null) {
			if (images.get(WorkflowImageProvider.IMG_GEAR) != null)
				return images.get(WorkflowImageProvider.IMG_GEAR);
			path = WorkflowImageProvider.get().getImageFilePath(WorkflowImageProvider.IMG_GEAR);
		}		
		
		ImageDescriptor descriptor = WorkflowEditorPlugin.getImageDescriptor(path);
		image = descriptor.createImage();
		images.put(id, image);
		return image;
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
			Rectangle clip = new Rectangle(ib.x, ib.y-CLEARANCE, ib.width, ib.height+CLEARANCE+MORE_CLEARANCE);
			g.setClip(clip);
			Rectangle r = new Rectangle(ib.x + 10, ib.y, ib.width - 15, ib.height);
			if (!StringUtils.isEmpty(node.getLabel()))
				renderTextAbove(g, r, node.getLabel());
			
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

	protected static void renderTextAbove(Graphics g, Rectangle r, String text) {
		TextLayout tl = new TextLayout(Display.getCurrent());
		tl.setWidth(r.width);	
		tl.setAlignment(SWT.CENTER);
		tl.setFont(g.getFont());
		tl.setText(text == null ? "" : text);
		int top = r.y;
		int count = tl.getLineCount();
		FontMetrics lineMetrics = tl.getLineMetrics(0);
		top = r.y - (count * lineMetrics.getHeight()) - 5;
		g.drawTextLayout(tl, r.x, top);	
		
		tl.dispose();
	}
	
	@Override
	public void visualStateChanged(VisualStateChangedEvent e) {
		int selectionFeedback = getVisualState().getSelectionFeedback();
		if (selectionFeedback == IVisualState.SELECTION_PRIMARY) {
			topColor = ColorConstants.cyan;
		} else  {
			topColor = ColorConstants.white;
		}
	}
}
