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
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeLayoutService;
import org.eclipse.swt.widgets.Display;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.NodeType;

public class DuplicateNodeFeature extends AbstractCustomFeature {
	public static final int DUP_Y_OFFSET = 20;
	public static final int DUP_X_OFFSET = 20;

	public DuplicateNodeFeature(IFeatureProvider fp) {
		super(fp);
		
	}

	@Override
	public String getName() {
		return "Duplicate";
	}
	
	@Override
	public void execute(ICustomContext context) {		
		PictogramElement[] elements = context.getPictogramElements();
		Arrays.sort(elements, new Comparator<PictogramElement>() {

			@Override
			public int compare(PictogramElement o1, PictogramElement o2) {
				// TODO Assumes containing object is always in common; not sure what to do otherwise.
				EList<EObject> objs = o1.eContainer().eContents();
				int p1 = objs.indexOf(o1);
				int p2 = objs.indexOf(o2);
				return p1 - p2;
			}
		});
		IPeLayoutService layoutService = Graphiti.getPeLayoutService();
		
		Map<String, Pair<WFNode, WFNode>> createdNodes= new HashMap<>();

		for (PictogramElement pe: elements) {			
			final GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();	
			Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
			if (bo instanceof WFNode) {
				// Determine location of existing node
				Shape shape = (Shape) pe;	
				ILocation location = layoutService.getLocationRelativeToDiagram(shape);
				
				WFNode oldObject = (WFNode) bo;
				NodeType nodeType = new NodeType(oldObject);
				//nodeType.setLabel(oldObject.getName());

				// Create actual node with correct ports, with correct location and dimensions
				CreateWFNodeFeature createFeature = new CreateWFNodeFeature(getFeatureProvider(), nodeType);
				createFeature.setDuplicating();
				CreateContext cc = new CreateContext();
				cc.setX(location.getX() + DUP_X_OFFSET);
				cc.setY(location.getY() + DUP_Y_OFFSET);
				cc.setWidth(ga.getWidth());
				cc.setHeight(ga.getHeight());
				cc.setTargetContainer(getFeatureProvider().getDiagramTypeProvider().getDiagram());
				WFNode newObject = (WFNode) createFeature.create(cc)[0];				

				// Keep nodes so we can fix connections later				
				createdNodes.put(oldObject.getName(), Pair.of(oldObject, newObject));
				
				// Adjust any ports in the copy that are in non-standard positions in the original
				matchPortPositions(oldObject, newObject);
			}
		}
		
		duplicateConnections(createdNodes);

		if (createdNodes.size() > 0) {
			Display.getCurrent().asyncExec(() -> {
				getDiagramBehavior().getDiagramContainer().selectPictogramElements(new PictogramElement[] {});
				List<PictogramElement> pes = new ArrayList<>();
				for (Pair<WFNode, WFNode> pair: createdNodes.values()) {			
					pes.add(getFeatureProvider().getPictogramElementForBusinessObject(pair.getRight()));
				}
				getDiagramBehavior().getDiagramContainer().selectPictogramElements(pes.toArray(new PictogramElement[pes.size()]));
			});
		}
	}


	private void duplicateConnections(Map<String, Pair<WFNode, WFNode>> createdNodes) {
		for (Pair<WFNode, WFNode> pair: createdNodes.values()) {
			WFNode oldNode = pair.getLeft();
			WFNode newNode = pair.getRight();
			//     For each outgoing connection of old node
			for (OutputPort ooPort: oldNode.getOutputPorts()) {
				for (WFArc oldArc: ooPort.getArcs()) {
					// If other end is on a node in this set					
					String targetNodeName = oldArc.getTarget().getNode().getName();
					if (createdNodes.containsKey(targetNodeName)) {
						// Find the anchor for the newly created input and output ports that
						// correspond to the ports on "oldArc"
						Optional<OutputPort> sourcePort = newNode.getOutputPorts().stream().filter(p -> p.getName().equals(oldArc.getSource().getName())).findFirst();
						FixPointAnchor sourceAnchor = (FixPointAnchor) getFeatureProvider().getPictogramElementForBusinessObject(sourcePort.get());

						Pair<WFNode, WFNode> targetNodePair = createdNodes.get(targetNodeName);
						WFNode newTargetNode = targetNodePair.getRight();
						Optional<InputPort> targetPort = newTargetNode.getInputPorts().stream().filter(p -> p.getName().equals(oldArc.getTarget().getName())).findFirst();
						FixPointAnchor targetAnchor = (FixPointAnchor) getFeatureProvider().getPictogramElementForBusinessObject(targetPort.get());
						
						// create new arc  
				        WFArc newArc = DomainFactory.eINSTANCE.createWFArc();
				        newArc.setSource(sourcePort.get());
				        newArc.setTarget(targetPort.get());
				        newArc.setName(oldArc.getName());
							
				        // Get the old connection so we can clone it
						Connection oldConnection = (Connection) getFeatureProvider().getPictogramElementForBusinessObject(oldArc);
				        
				        // Add it to diagram creating graphical connection
						AddConnectionContext addContext =
		        				new AddConnectionContext(sourceAnchor, targetAnchor);
		        			addContext.setNewObject(newArc);
		        			addContext.putProperty(AddWFArcFeature.OLD_CONNECTION, oldConnection);
		        			getFeatureProvider().addIfPossible(addContext);
					}					
				}
			}
		}		
	}

	private void matchPortPositions(WFNode oldObject, WFNode newObject) {
		try {
			Map<String, Port> ports = new HashMap<>();
			for (Port port: oldObject.getInputPorts()) {
				ports.put(port.getName(), port);
			}
			for (Port newPort: newObject.getInputPorts()) {
				Port oldPort = ports.get(newPort.getName());
				PictogramElement oldPe = getFeatureProvider().getPictogramElementForBusinessObject(oldPort);
				FixPointAnchor oldFpa = (FixPointAnchor) oldPe;
				PictogramElement newPe = getFeatureProvider().getPictogramElementForBusinessObject(newPort);
				FixPointAnchor newFpa = (FixPointAnchor) newPe;
				newFpa.setLocation(EcoreUtil.copy(oldFpa.getLocation()));			
			}
			for (Port port: oldObject.getOutputPorts()) {
				ports.put(port.getName(), port);
			}
			for (Port newPort: newObject.getOutputPorts()) {
				Port oldPort = ports.get(newPort.getName());
				PictogramElement oldPe = getFeatureProvider().getPictogramElementForBusinessObject(oldPort);
				FixPointAnchor oldFpa = (FixPointAnchor) oldPe;
				PictogramElement newPe = getFeatureProvider().getPictogramElementForBusinessObject(newPort);
				FixPointAnchor newFpa = (FixPointAnchor) newPe;
				newFpa.setLocation(EcoreUtil.copy(oldFpa.getLocation()));									
			}
		} catch (RuntimeException ex) {
			WorkflowEditorPlugin.getDefault().logError("Internal error while adjusting port geometry", ex);
		}
	}

	@Override
	public boolean canExecute(ICustomContext context) {
		PictogramElement[] elems = context.getPictogramElements();
		if (elems != null) {
			for (PictogramElement pe: elems) {
				Object bo = getFeatureProvider().getBusinessObjectForPictogramElement(pe);
				if (! (bo instanceof WFNode || bo instanceof WFArc) )
					return false;				
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean hasDoneChanges() {
		return true;
	}

}
