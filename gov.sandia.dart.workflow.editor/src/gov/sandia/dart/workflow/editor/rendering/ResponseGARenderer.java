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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IRendererContext;
import org.eclipse.graphiti.platform.ga.IVisualState;
import org.eclipse.graphiti.platform.ga.IVisualStateChangeListener;
import org.eclipse.graphiti.platform.ga.IVisualStateHolder;
import org.eclipse.graphiti.platform.ga.VisualState;
import org.eclipse.graphiti.platform.ga.VisualStateChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class ResponseGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer, IVisualStateHolder, IVisualStateChangeListener {

	public static final String ID = "wfresponse";
	private IVisualState visualState;
	private Color topColor = ColorConstants.darkGray;
	
	public ResponseGARenderer(IRendererContext rc, IFeatureProvider fp) {
		setRc(rc);
		setFp(fp);
	}

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

		Pattern pattern = new Pattern(Display.getCurrent(), innerBounds.getTopRight().x,
				innerBounds.getTopRight().y, innerBounds.getTopLeft().x, innerBounds.getTopLeft().y,
				topColor, ColorConstants.blue);
		g.setAntialias(SWT.ON);
		g.setBackgroundPattern(pattern);
		g.fillRoundRectangle(new Rectangle(innerBounds.getTopLeft(), innerBounds.getBottomRight()), 10, 10);
		g.setBackgroundPattern(null);
		pattern.dispose();
	}

	@Override
	protected void outlineShape(Graphics g) {		
		Font f = WorkflowEditorPlugin.getDefault().getDiagramFont();
		g.setFont(f);
		Rectangle innerBounds = getInnerBounds();
		g.setForegroundColor(ColorConstants.white);
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

	@Override
	public void visualStateChanged(VisualStateChangedEvent e) {
		int selectionFeedback = getVisualState().getSelectionFeedback();
		if (selectionFeedback == IVisualState.SELECTION_PRIMARY) {
			topColor = ColorConstants.blue;
		} else  {
			topColor = ColorConstants.darkGray;
		}
	}
}
