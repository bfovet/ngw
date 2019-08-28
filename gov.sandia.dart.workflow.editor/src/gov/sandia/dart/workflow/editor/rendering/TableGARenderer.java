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
import java.util.Map.Entry;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ScrollPane;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public abstract class TableGARenderer extends GenericWFNodeGARenderer implements IGraphicsAlgorithmRenderer {

	public static final int L_MARGIN = 10;
	public static final int L_PADDING = 5;
	public static final int R_MARGIN = 20;
	public static final int T_MARGIN = 28;
	public static final int B_MARGIN = 10;

	private ScrollPane scrollPane_;

	private TableFigure content_;


	public TableGARenderer(){	

		scrollPane_ = new ScrollPane();
		scrollPane_.setScrollBarVisibility(ScrollPane.AUTOMATIC);

		content_ = new TableFigure();

		scrollPane_.setLocation(new Point(L_MARGIN, T_MARGIN));

		scrollPane_.setContents(content_);

		scrollPane_.setBorder(new LineBorder(ColorConstants.black));
		scrollPane_.setBackgroundColor(ColorConstants.white);


		add(scrollPane_);

		this.addLayoutListener(new LayoutListener() {

			@Override
			public void invalidate(IFigure container) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean layout(IFigure container) {

				resizeContents();
				return false;
			}

			@Override
			public void postLayout(IFigure container) {
				// TODO Auto-generated method stub

			}

			@Override
			public void remove(IFigure child) {
				// TODO Auto-generated method stub

			}

			@Override
			public void setConstraint(IFigure child, Object constraint) {
				// TODO Auto-generated method stub

			}

		});

		this.add(scrollPane_);

	}

	private void resizeContents() {
		Dimension size = getSize();
		lineHeight = -1;
		int scrollWidth = size.width - L_MARGIN - R_MARGIN;
		int scrollHeight = size.height - T_MARGIN - B_MARGIN;

		Map<String, String> properties = getNameValuePairs();	
		int lineHeight = getLineHeight(WorkflowEditorPlugin.getDefault().getDiagramFont());
		int contentHeight = lineHeight*properties.size()+1;

		int contentWidth = scrollWidth-2;		

		if(contentHeight > scrollHeight) {
			contentWidth -= scrollPane_.getVerticalScrollBar().getSize().width;
		}



		// Resize contents first so that scroll bars show up properly
		content_.setSize(contentWidth, contentHeight);				

		scrollPane_.setSize(scrollWidth, scrollHeight);

	}

	private int lineHeight = -1;
	private int getLineHeight(Font f) {
		if (lineHeight == -1) {
			lineHeight = FigureUtilities.getStringExtents("HELLO", f).height;
		}
		return lineHeight;
	}
	
	
	abstract protected Map<String,String> getNameValuePairs();

	@Override
	protected void outlineShape(Graphics g) {		
		g.setForegroundColor(ColorConstants.black);
		g.setBackgroundColor(ColorConstants.white);
		g.setLineStyle(Graphics.LINE_SOLID);
		super.outlineShape(g);
	}

	private class TableFigure extends RectangleFigure{
		@Override
		protected void fillShape(Graphics g) {

			super.fillShape(g);

			Rectangle r = getBounds();

			g.setForegroundColor(ColorConstants.black);
			g.setBackgroundColor(ColorConstants.white);
			g.setLineStyle(Graphics.LINE_SOLID);
			g.setFont(WorkflowEditorPlugin.getDefault().getDiagramFont());

			Map<String, String> properties = getNameValuePairs();		
			int column1Width = computeBoxWidth(r, g.getFont(), properties);		
			int column2Width = r.width() + 2*L_PADDING - column1Width;
			
			int boxHeight = r.height(); 
			int lineHeight = getLineHeight(g.getFont());
			int count = properties.size();

			StringBuilder propertyNames = new StringBuilder();
			StringBuilder propertyValues = new StringBuilder();
			for(Entry<String,String> entry : properties.entrySet())
			{
				String name = trimToSize(entry.getKey(), g.getFont(), column1Width);
				String value = trimToSize(entry.getValue(), g.getFont(), column2Width);

				propertyNames.append(name);
				propertyNames.append('\n');
				propertyValues.append(value);
				propertyValues.append('\n');
			}

			// Get layout locations`
			int gridYStart = r.y();

			int gridYEnd = gridYStart + count*lineHeight;

			int gridYEndMax = gridYStart + boxHeight;

			if(gridYEnd > gridYEndMax) {
				gridYEnd = gridYEndMax;
			}


			int gridX1 = r.x();
			int gridX2 = gridX1 + L_PADDING + column1Width;
			int gridX3 = r.x() + r.width() + 2*L_PADDING;

			// Draw grid
			g.fillRectangle(gridX1, gridYStart, gridX3-gridX1, gridYEnd-gridYStart);				

			for(int gridY = gridYStart; gridY < gridYEnd; gridY += lineHeight) {
				g.drawLine(gridX1, gridY, gridX3, gridY);
			}				

			g.drawLine(gridX1, gridYEnd, gridX3, gridYEnd);

			//g.drawLine(gridX1, gridYStart,gridX1, gridYEnd);
			g.drawLine(gridX2, gridYStart,gridX2, gridYEnd);
			//g.drawLine(gridX3, gridYStart,gridX3, gridYEnd);		


			// Draw text
			TextLayout names = new TextLayout(Display.getCurrent());
			names.setWidth(column1Width);
			names.setFont(g.getFont());
			names.setText(propertyNames.toString());
			g.drawTextLayout(names, gridX1 + L_PADDING, gridYStart);

			TextLayout values = new TextLayout(Display.getCurrent());
			values.setWidth(column2Width);	
			values.setFont(g.getFont());
			values.setText(propertyValues.toString());
			g.drawTextLayout(values, gridX2 + L_PADDING, gridYStart);


		}


		private int computeBoxWidth(Rectangle r, Font font, Map<String, String> properties) {			
			int limit = r.width()/2 - L_PADDING;
			int max = 0;
			for (String key: properties.keySet()) {
				if (key == null)
					continue;
				max = Math.max(max, FigureUtilities.getTextWidth(key, font) + L_PADDING);
				if (max > limit)
					return limit;
			}
			return max;
		}


		private String trimToSize(String string, Font font, int boxWidth) {
			if(string == null || boxWidth <= 0) {
				return "";
			}

			if(font == null) {
				return string;
			}

			while(string.length() > 0 && FigureUtilities.getTextWidth(string, font) > boxWidth) {
				string = string.substring(0, string.length() - 1); 
			}

			return string;
		}


	}
}
