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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IRendererContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractGARenderer extends RectangleFigure implements IGraphicsAlgorithmRenderer {

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

	protected Insets defaultFigureInsets = new Insets(2);
	protected IRendererContext rc;
	protected IFeatureProvider fp;

	protected Rectangle getInnerBounds() {
		return getBounds().getCopy().getShrinked(defaultFigureInsets);
	}

	protected void centerText(Graphics g, Rectangle r, String text) {
		TextLayout tl = new TextLayout(Display.getCurrent());
		tl.setWidth(r.width);	
		tl.setAlignment(SWT.CENTER);
		tl.setFont(g.getFont());
		tl.setText(text == null ? "" : text);
		FontMetrics lineMetrics = tl.getLineMetrics(0);
		int top = r.y + r.height/2 - lineMetrics.getHeight() + 5;

		g.drawTextLayout(tl, r.x, top);	
		tl.dispose();
	}

	public void setRc(IRendererContext rc) {
		this.rc = rc;
	}

	public void setFp(IFeatureProvider fp) {
		this.fp = fp;
	}
}
