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

import java.util.Arrays;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.platform.ga.IGraphicsAlgorithmRenderer;
import org.eclipse.graphiti.platform.ga.IRendererContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.preferences.IWorkflowEditorPreferences;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class PortGARenderer extends AbstractGARenderer implements IGraphicsAlgorithmRenderer {

	public static final String ID = "wfport";
	private static final int PORT_HEIGHT = 10;
	public PortGARenderer(IRendererContext rc, IFeatureProvider fp) {
		setRc(rc);
		setFp(fp);
	}

	@Override
	protected void fillShape(Graphics g) {
		Rectangle r = getInnerBounds();
		int[] poly = { r.x, r.y, r.x + r.width, r.y + r.height / 2, r.x, r.y + r.height, r.x, r.y };
		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (bo instanceof OutputPort) {
			if (hasSelectedConnection(bo))
				g.setBackgroundColor(ColorConstants.green);
			else
				g.setBackgroundColor(ColorConstants.black);
		} else {
			if (hasSelectedConnection(bo))
				g.setBackgroundColor(ColorConstants.red);
			else
				g.setBackgroundColor(ColorConstants.white);
		}
		g.fillPolygon(poly);
	}

	private boolean hasSelectedConnection(Object bo) {
		if (bo instanceof OutputPort) {
			OutputPort port = (OutputPort) bo;
			if (port.getArcs().size() > 0) {
				WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(port);
				PictogramElement[] elements = editor.getSelectedPictogramElements();
				for (WFArc arc: port.getArcs()) {
					PictogramElement element = fp.getPictogramElementForBusinessObject(arc);
					if (Arrays.asList(elements).contains(element))
						return true;
				}				
			}	
			if (port.getResponseArcs().size() > 0) {
				WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(port);
				PictogramElement[] elements = editor.getSelectedPictogramElements();
				for (ResponseArc arc: port.getResponseArcs()) {
					PictogramElement element = fp.getPictogramElementForBusinessObject(arc);
					if (Arrays.asList(elements).contains(element))
						return true;
				}				
			}	
		} else if (bo instanceof InputPort) {
			InputPort port = (InputPort) bo;
			if (port.getArcs().size() > 0) {
				WFArc arc = port.getArcs().get(0);
				PictogramElement element = fp.getPictogramElementForBusinessObject(arc);
				WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(port);
				PictogramElement[] elements = editor.getSelectedPictogramElements();
				return Arrays.asList(elements).contains(element);
				
			}
		}  else if (bo instanceof Response) {
			Response response = (Response) bo;
			if (response.getSource().size() > 0) {
				WorkflowDiagramEditor editor = NOWPSettingsEditorUtils.getDiagramEditor(response);
				PictogramElement[] elements = editor.getSelectedPictogramElements();
				for (ResponseArc arc: response.getSource()) {
					PictogramElement element = fp.getPictogramElementForBusinessObject(arc);
					if (Arrays.asList(elements).contains(element))
						return true;
				}				
			}
		}
		return false;
	}

	@Override
	protected void outlineShape(Graphics g) {	
		Rectangle r = getInnerBounds();
		// TODO Better way to do this?
		g.setClip(new Rectangle(r.x-100, r.y-100,  1000, 1000)); 
		g.setForegroundColor(ColorConstants.black);

		PictogramElement pe = rc.getPlatformGraphicsAlgorithm().getPictogramElement();
		Object bo = fp.getBusinessObjectForPictogramElement(pe);
		if (!hasSelectedConnection(bo)) {

			int[] poly = { r.x, r.y, r.x + r.width, r.y + r.height/2, r.x, r.y + r.height, r.x, r.y    };
			g.drawPolygon(poly);
		}

		if (bo instanceof Port && showPortLabels(bo)) {

			if (bo instanceof Port) {
				Port p = (Port) bo;
				WFNode node = getNode(p);
				if (!ParameterUtils.isParameter(node)) {
					Font f = WorkflowEditorPlugin.getDefault().getDiagramFont();
					g.setFont(f);
					String text = ((Port)bo).getName();			
					TextLayout tl = new TextLayout(Display.getCurrent());
					tl.setFont(g.getFont());
					tl.setText(text);
					int h = g.getFontMetrics().getHeight();
					if (bo instanceof InputPort) {
						g.drawTextLayout(tl, r.x + r.width + 3, r.y + PORT_HEIGHT - h);
						tl.setAlignment(SWT.LEFT);

					} else {
						tl.setAlignment(SWT.RIGHT);

						int width = tl.getLineBounds(0).width;
						g.drawTextLayout(tl, r.x - 3 - width, r.y + PORT_HEIGHT - h);
					}
				}
			}
		}
	}

	private boolean showPortLabels(Object bo) {
		Port p = (Port) bo;
		WFNode node = getNode(p);
		if (OnOffSwitchGARenderer.hideLabels(node))
			return false;
		else
			return WorkflowEditorPlugin.getDefault().getPreferenceStore().getBoolean(IWorkflowEditorPreferences.PORT_LABELS);
	}	

	private WFNode getNode(Port p) {
		WFNode node = p instanceof InputPort ? ((InputPort) p).getNode() : ((OutputPort) p).getNode();
		return node;
	}
}
