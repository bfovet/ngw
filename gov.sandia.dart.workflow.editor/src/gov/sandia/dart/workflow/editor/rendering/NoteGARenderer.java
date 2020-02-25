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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;

public class NoteGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

	public static final int CORNER = 10;
	public static final String ID = "wfnote";
	// TODO Use a map instead
	private static Color red, yellow, blue, orange, green, purple;
	public static String[] COLORS = {
			"red", "yellow", "blue", "orange", "green", "purple"
	};
	public NoteGARenderer(IRendererContext rc, IFeatureProvider fp) {
		setRc(rc);
		setFp(fp);
	}

	@Override
	protected void fillShape(Graphics g) {
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Note note = (Note) fp.getBusinessObjectForPictogramElement(pe);

		Font f = WorkflowEditorPlugin.getDefault().getNotesFont();
		g.setFont(f);
		Rectangle r = getInnerBounds();
		int[] poly = { r.x + CORNER, r.y, r.x + r.width, r.y, r.x + r.width, r.y + r.height, r.x, r.y + r.height, r.x, r.y+CORNER, r.x+CORNER, r.y+CORNER, r.x+CORNER, r.y, r.x, r.y+CORNER };
		Color yellow = getColor(note.getColor());
		g.setBackgroundColor(yellow);
		g.setForegroundColor(ColorConstants.black);
		if (WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.CONNECTIONS_BEHIND)) {
			g.setAlpha(100);
		}
		if (note.isDrawBorderAndBackground())
			g.fillPolygon(poly);
		g.setAlpha(255);

		TextLayout tl = new TextLayout(Display.getCurrent());
		tl.setWidth(r.width - 15);	
		tl.setFont(g.getFont());
		tl.setText(note.getText());
		g.drawTextLayout(tl, r.x + CORNER + 2, r.y);	}

	public synchronized static Color getColor(String colorName) {
		if (yellow == null) {	
			initColors();
		}
		if (colorName == null)
			return yellow;
		
		switch (colorName) {
		case "red" :
			return red;
		case "blue" :
			return blue;
		case "green" :
			return green;
		case "orange" :
			return orange;
		case "purple" :
			return purple;
		case "yellow" :
		default:
			return yellow;
		}
	}
	
	private static void initColors() {
		red = new Color(Display.getCurrent(), new RGB(255, 200, 200));
		yellow = new Color(Display.getCurrent(), new RGB(255, 255, 200));
		blue = new Color(Display.getCurrent(), new RGB(200, 200, 255));		
		green = new Color(Display.getCurrent(), new RGB(200, 244, 181));
		orange = new Color(Display.getCurrent(), new RGB(255, 200, 150));
		purple = new Color(Display.getCurrent(), new RGB(255, 200, 255));		
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
		
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Note note = (Note) fp.getBusinessObjectForPictogramElement(pe);
		if (note.isDrawBorderAndBackground())
			g.drawPolygon(poly);
	}
}
