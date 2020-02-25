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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IRendererContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class ResponseGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

	public static final String ID = "wfresponse";
	private Color topColor = ColorConstants.white;
	
	public ResponseGARenderer(IRendererContext rc, IFeatureProvider fp) {
		setRc(rc);
		setFp(fp);
	}
	
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
		if (bo instanceof Response) {
			Response node = (Response) bo;
			g.drawText(node.getName(), innerBounds.x + 10, innerBounds.y + 5);			
		}
	}
}
