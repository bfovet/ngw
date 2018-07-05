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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class NoteGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

	private static final int CORNER = 10;
	public static final String ID = "wfnote";
	private static Color color;
	public NoteGARenderer(IRendererContext rc, IFeatureProvider fp) {
		setRc(rc);
		setFp(fp);
	}

	@Override
	protected void fillShape(Graphics g) {
		Font f = WorkflowEditorPlugin.getDefault().getNotesFont();
		g.setFont(f);
		Rectangle r = getInnerBounds();
		int[] poly = { r.x + CORNER, r.y, r.x + r.width, r.y, r.x + r.width, r.y + r.height, r.x, r.y + r.height, r.x, r.y+CORNER, r.x+CORNER, r.y+CORNER, r.x+CORNER, r.y, r.x, r.y+CORNER };
		Color yellow = getColor();
		g.setBackgroundColor(yellow);
		g.setForegroundColor(ColorConstants.black);
		g.fillPolygon(poly);
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Note note = (Note) fp.getBusinessObjectForPictogramElement(pe);
		TextLayout tl = new TextLayout(Display.getCurrent());
		tl.setWidth(r.width - 15);	
		tl.setFont(g.getFont());
		tl.setText(note.getText());
		g.drawTextLayout(tl, r.x + CORNER + 2, r.y);	}

	private synchronized static Color getColor() {
		if (color == null) {
			color = new Color(Display.getCurrent(), new RGB(255, 255, 200));
		}
		return color;
	}

	@Override
	protected void outlineShape(Graphics g) {	
		Font f = WorkflowEditorPlugin.getDefault().getNotesFont();
		g.setFont(f);
		Rectangle r = getInnerBounds();
		int[] poly = { r.x + CORNER, r.y, r.x + r.width, r.y, r.x + r.width, r.y + r.height, r.x, r.y + r.height, r.x, r.y+CORNER, r.x+CORNER, r.y+CORNER, r.x+CORNER, r.y, r.x, r.y+CORNER };
		g.setForegroundColor(ColorConstants.black);
		g.setLineStyle(Graphics.LINE_SOLID);
		g.setLineWidth(1);
		g.drawPolygon(poly);
	}
}
