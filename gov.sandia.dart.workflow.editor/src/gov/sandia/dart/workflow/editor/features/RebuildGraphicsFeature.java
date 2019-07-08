/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.Image;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.editor.configuration.WorkflowTypesManager;
import gov.sandia.dart.workflow.editor.settings.WFArcSettingsEditor;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class RebuildGraphicsFeature extends AbstractCustomFeature {

	public RebuildGraphicsFeature(IFeatureProvider fp) {
		super(fp);
	}
	
	@Override
	public boolean canExecute(ICustomContext context) {
		return true;
	}
	
	@Override
	public String getName() {
		return "Rebuild graphics";
	}

	@Override
	public void execute(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length > 0) {
			Diagram diagram = getDiagram();			
			rebuildNotes(diagram);
			rebuildImages(diagram);
			rebuildNodesConnectionsAndResponses(diagram);

			diagram.getStyles().clear();
			diagram.getFonts().clear();
		}
	}

	private void rebuildNotes(Diagram diagram) {
		for (EObject o: diagram.eContents()) {
			if (o instanceof PictogramElement) {
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement((PictogramElement) o);
				if (bo instanceof Note) {
					Note note = (Note) bo;
					PictogramElement pe = getFeatureProvider().getPictogramElementForBusinessObject(note);
					GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
					AddContext ac = new AddContext();
					ac.setLocation(ga.getX(), ga.getY());
					ac.setSize(ga.getWidth(), ga.getHeight());
					ac.setNewObject(note);
					ac.setTargetContainer(diagram);				
					RemoveContext rc = new RemoveContext(pe);
					getFeatureProvider().getRemoveFeature(rc).remove(rc);
					getFeatureProvider().getAddFeature(ac).add(ac);
				}
			}
		}		
	}

	private void rebuildImages(Diagram diagram) {
		for (EObject o: diagram.eContents()) {
			if (o instanceof PictogramElement) {
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement((PictogramElement) o);
				if (bo instanceof Image) {
					Image image = (Image) bo;
					PictogramElement pe = getFeatureProvider().getPictogramElementForBusinessObject(image);
					GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
					AddContext ac = new AddContext();
					ac.setLocation(ga.getX(), ga.getY());
					ac.setSize(ga.getWidth(), ga.getHeight());
					ac.setNewObject(image);
					ac.setTargetContainer(diagram);				
					RemoveContext rc = new RemoveContext(pe);
					getFeatureProvider().getRemoveFeature(rc).remove(rc);
					getFeatureProvider().getAddFeature(ac).add(ac);
				}
			}
		}		
	}

	protected void rebuildNodesConnectionsAndResponses(Diagram diagram) {
		List<WFNode> nodes = new ArrayList<>();
		List<Response> responses = new ArrayList<>();

		for (EObject o: diagram.eContents()) {
			if (o instanceof PictogramElement) {
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement((PictogramElement) o);
				if (bo instanceof WFNode) {
					nodes.add((WFNode) bo);
				} else if (bo instanceof Response) {
					responses.add((Response) bo);
				}
			}
		}
		
		// Make sure each node has all the properties it should have
		for (WFNode node: nodes) {
			NodeType nodeType = WorkflowTypesManager.get().getNodeType(node.getType());
			if (nodeType != null) {
				for (Prop p: nodeType.getProperties()) {

					if (!PropertyUtils.hasProperty(node, p.getName())) {
						PropertyUtils.setProperty(node, p.getName(), p.getType().toString(), p.getValue());
					}
				}
			}
		}

		// Update GAs for nodes
		for (WFNode node: nodes) {
			PictogramElement pe = getFeatureProvider().getPictogramElementForBusinessObject(node);
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			AddContext ac = new AddContext();
			ac.setLocation(ga.getX(), ga.getY());
			ac.setNewObject(node);
			ac.setTargetContainer(diagram);			
			ac.setSize(ga.getWidth(), ga.getHeight());

			RemoveContext rc = new RemoveContext(pe);
			getFeatureProvider().getRemoveFeature(rc).remove(rc);
			getFeatureProvider().getAddFeature(ac).add(ac);
		}	
		
		// Update GAs for Responses
		for (Response response: responses) {
			PictogramElement pe = getFeatureProvider().getPictogramElementForBusinessObject(response);
			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			AddContext ac = new AddContext();
			ac.setLocation(ga.getX(), ga.getY());
			ac.setNewObject(response);
			ac.setTargetContainer(diagram);				
			RemoveContext rc = new RemoveContext(pe);
			getFeatureProvider().getRemoveFeature(rc).remove(rc);
			getFeatureProvider().getAddFeature(ac).add(ac);
		}	
		// Update connections between nodes
		for (WFNode node: nodes) {
			for (OutputPort op: node.getOutputPorts()) {
				for (WFArc arc: op.getArcs()) {
					InputPort ip = arc.getTarget();
					Anchor opa = (Anchor) getFeatureProvider().getPictogramElementForBusinessObject(op);
					Anchor ipa = (Anchor) getFeatureProvider().getPictogramElementForBusinessObject(ip);
					AddConnectionContext acc = new AddConnectionContext(opa, ipa);
					acc.setNewObject(arc);
					acc.setTargetContainer(diagram);
					getFeatureProvider().addIfPossible(acc);
					
					WFArcSettingsEditor.updateConnectionAppearance(diagram, getFeatureProvider(), arc);
				}
			}
		}
		// Update connections from nodes to responses
		for (WFNode node: nodes) {
			for (OutputPort op: node.getOutputPorts()) {
				for (ResponseArc arc: op.getResponseArcs()) {
					Response ip = arc.getTarget();
					Anchor opa = (Anchor) getFeatureProvider().getPictogramElementForBusinessObject(op);
					Anchor ipa = getResponseAnchor(ip);
					AddConnectionContext acc = new AddConnectionContext(opa, ipa);
					acc.setNewObject(arc);
					acc.setTargetContainer(diagram);
					getFeatureProvider().addIfPossible(acc);
				}
			}
		}
	}

	protected Anchor getResponseAnchor(Response ip) {
		PictogramElement[] pes = getFeatureProvider().getAllPictogramElementsForBusinessObject(ip);
		for (PictogramElement pe: pes) {
			if (pe instanceof Anchor)
				return (Anchor) pe;			
		}
		return null;
	}

}
