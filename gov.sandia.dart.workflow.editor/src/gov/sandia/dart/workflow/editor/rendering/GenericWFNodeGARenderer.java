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
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.WorkflowImageProvider;
import gov.sandia.dart.workflow.editor.WorkflowToolBehaviorProvider;
import gov.sandia.dart.workflow.editor.WorkflowToolBehaviorProvider.NodeExecutionStatus;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class GenericWFNodeGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

    public static final String ID = "wfnode";
	static final int CLEARANCE = 100;
	static final int MORE_CLEARANCE = 24;
	
	static Map<String, Image> images = new ConcurrentHashMap<>();
	
	@Override
	protected void fillShape(Graphics g) {		
		Rectangle innerBounds = getInnerBounds();
		Color fillColor = ColorConstants.white;
		
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();		
		Object bo = fp.getBusinessObjectForPictogramElement(pe);

		IToolBehaviorProvider tbp = fp.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
		if (tbp instanceof WorkflowToolBehaviorProvider) {
			NodeExecutionStatus status = ((WorkflowToolBehaviorProvider) tbp).getExecutionStatus((WFNode) bo);
			if (status != null) {
				switch (status) {
				case CURRENT: fillColor = ColorConstants.yellow; break;
				case FAILED: fillColor = ColorConstants.red; break;
				case PASSED: fillColor = ColorConstants.lightGreen; break;
				case NEVER: fillColor = ColorConstants.white; break;
				}
			}
		}
		
		g.setAntialias(SWT.ON);
		if (WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.TRANSLUCENT_COMPONENTS)) {
			g.setAlpha(200);
		}
		g.setBackgroundColor(fillColor);
		g.fillRectangle(new Rectangle(innerBounds.getTopLeft(), innerBounds.getBottomRight()));
		g.setAlpha(255);
		Image image = getIcon((WFNode) bo);
		g.drawImage(image, innerBounds.getTopLeft().translate(5, 5));
				
	}

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
		g.drawRectangle(new Rectangle(ib.getTopLeft(), ib.getBottomRight()));
		
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
				NodeExecutionStatus status = ((WorkflowToolBehaviorProvider) tbp).getExecutionStatus((WFNode) bo);			
				if (status == NodeExecutionStatus.CURRENT) {
					g.setForegroundColor(ColorConstants.darkGreen);
					g.setLineStyle(Graphics.LINE_DASHDOT);
					g.setLineWidth(3);
					Point br = new Point(ib.getBottomRight().x-2, ib.getBottomRight().y-2);
					g.drawRectangle(new Rectangle(ib.getTopLeft(), br));
				}
			}
		}
	}
}
