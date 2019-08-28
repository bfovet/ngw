package gov.sandia.dart.workflow.editor.features;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IPasteContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.AbstractPasteFeature;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.Image;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.configuration.NodeType;
import gov.sandia.dart.workflow.editor.settings.WFArcSettingsEditor;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class PasteWorkflowObjectFeature extends AbstractPasteFeature {

	public PasteWorkflowObjectFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public void paste(IPasteContext context) {	
		Object[] objects = getFromClipboard();
		
		int[] ul = findUpperLeftCorner(objects);
		Map<String, Pair<WFNode, WFNode>> placemats = new HashMap<>();
		Map<String, Response> responses = new HashMap<>();

		for (Object o: objects) {
			PictogramElement pe = (PictogramElement) o;
			Object bo = getBusinessObjectForPictogramElement(pe);

			if (bo instanceof WFNode) {
				WFNode oldObject = (WFNode) bo;
				NodeType nodeType = new NodeType(oldObject);
				CreateWFNodeFeature createFeature = new CreateWFNodeFeature(getFeatureProvider(), nodeType);
				createFeature.setDuplicating();
				CreateContext cc = getCreateContext(context, pe, ul);
				WFNode newObject = (WFNode) createFeature.create(cc)[0];				
				// DuplicateNodeFeature.matchPortPositions(oldObject, newObject, getFeatureProvider());
				placemats.put(oldObject.getName(), Pair.of(oldObject, newObject));

			} else if (bo instanceof Note) {
				Note oldObject = (Note) bo;
				CreateNoteFeature createFeature = new CreateNoteFeature(getFeatureProvider());
				createFeature.setDuplicating();
				CreateContext cc = getCreateContext(context, pe, ul);
				Note note = (Note) createFeature.create(cc)[0];
				note.setText(oldObject.getText());
				note.setColor(oldObject.getColor());
				note.setDrawBorderAndBackground(oldObject.isDrawBorderAndBackground());

			} else if (bo instanceof Image) {
				Image oldObject = (Image) bo;
				CreateImageFeature createFeature = new CreateImageFeature(getFeatureProvider());
				createFeature.setDuplicating();
				CreateContext cc = getCreateContext(context, pe, ul);
				Image image = (Image) createFeature.create(cc)[0];
				image.setText(oldObject.getText());

			} else if (bo instanceof Response) {
				Response oldObject = (Response) bo;
				CreateResponseFeature createFeature = new CreateResponseFeature(getFeatureProvider());
				createFeature.setDuplicating();
				CreateContext cc = getCreateContext(context, pe, ul);
				Response response = (Response) createFeature.create(cc)[0];
				response.setName(oldObject.getName());
				response.setType(oldObject.getType());
				responses.put(response.getName(), response);
			}
		}
		
		// Now we need to recreate the connections. No idea how to do the routing, sorry.
		for (Pair<WFNode, WFNode> pair: placemats.values()) {
			WFNode oldNode = pair.getLeft();
			WFNode newNode = pair.getRight();
			//     For each outgoing connection of old node
			for (OutputPort ooPort: oldNode.getOutputPorts()) {
				for (WFArc oldArc: ooPort.getArcs()) {
					// If other end is on a node in this set					
					String targetNodeName = oldArc.getTarget().getNode().getName();
					if (placemats.containsKey(targetNodeName)) {
						// Find the anchor for the newly created input and output ports that
						// correspond to the ports on "oldArc"
						Optional<OutputPort> sourcePort = newNode.getOutputPorts().stream().filter(p -> p.getName().equals(oldArc.getSource().getName())).findFirst();
						FixPointAnchor sourceAnchor = (FixPointAnchor) getFeatureProvider().getPictogramElementForBusinessObject(sourcePort.get());

						Pair<WFNode, WFNode> targetNodePair = placemats.get(targetNodeName);
						WFNode newTargetNode = targetNodePair.getRight();
						Optional<InputPort> targetPort = newTargetNode.getInputPorts().stream().filter(p -> p.getName().equals(oldArc.getTarget().getName())).findFirst();
						FixPointAnchor targetAnchor = (FixPointAnchor) getFeatureProvider().getPictogramElementForBusinessObject(targetPort.get());

						// create new arc  
						WFArc newArc = DomainFactory.eINSTANCE.createWFArc();
						newArc.setSource(sourcePort.get());
						newArc.setTarget(targetPort.get());
						newArc.setName(oldArc.getName());
						for (Property p: oldArc.getProperties()) {
							PropertyUtils.setProperty(newArc, p.getName(), p.getValue());
						}

						// Get the old connection so we can clone it
						Connection oldConnection = (Connection) getFeatureProvider().getPictogramElementForBusinessObject(oldArc);

						// Add it to diagram creating graphical connection
						AddConnectionContext addContext =
								new AddConnectionContext(sourceAnchor, targetAnchor);
						addContext.setNewObject(newArc);
						addContext.putProperty(AddWFArcFeature.OLD_CONNECTION, oldConnection);
						PictogramElement pe = getFeatureProvider().addIfPossible(addContext);
						if (pe != null) {
							WFArcSettingsEditor.updateConnectionAppearance(getDiagram(), getFeatureProvider(), newArc);
						}					}					
				}
				for (ResponseArc oldArc: ooPort.getResponseArcs()) {
					// If other end is on a node in this set					
					String targetName = oldArc.getTarget().getName();
					if (responses.containsKey(targetName)) {
						// Find the anchor for the newly created input and output ports that
						// correspond to the ports on "oldArc"
						Optional<OutputPort> sourcePort = newNode.getOutputPorts().stream().filter(p -> p.getName().equals(oldArc.getSource().getName())).findFirst();
						FixPointAnchor sourceAnchor = (FixPointAnchor) getFeatureProvider().getPictogramElementForBusinessObject(sourcePort.get());
					
						Response newTargetNode = responses.get(targetName);
						FixPointAnchor targetAnchor = (FixPointAnchor) getFeatureProvider().getAllPictogramElementsForBusinessObject(newTargetNode)[1];

						// create new arc  
						ResponseArc newArc = DomainFactory.eINSTANCE.createResponseArc();
						newArc.setSource(sourcePort.get());
						newArc.setTarget(newTargetNode);
						newArc.setName(oldArc.getName());
						for (Property p: oldArc.getProperties()) {
							PropertyUtils.setProperty(newArc, p.getName(), p.getValue());
						}

						// Get the old connection so we can clone it
						Connection oldConnection = (Connection) getFeatureProvider().getPictogramElementForBusinessObject(oldArc);

						// Add it to diagram creating graphical connection
						AddConnectionContext addContext =
								new AddConnectionContext(sourceAnchor, targetAnchor);
						addContext.setNewObject(newArc);
						addContext.putProperty(AddWFArcFeature.OLD_CONNECTION, oldConnection);
						PictogramElement pe = getFeatureProvider().addIfPossible(addContext);
						if (pe != null) {
							WFArcSettingsEditor.updateConnectionAppearance(getDiagram(), getFeatureProvider(), newArc);
						}
					}					
				}
			}
		}
		
	}

	private int[] findUpperLeftCorner(Object[] objects) {
		int x = Integer.MAX_VALUE;
		int y = Integer.MAX_VALUE;
		for (Object o: objects) {
			GraphicsAlgorithm ga = ((PictogramElement) o).getGraphicsAlgorithm();
			if (ga.getY() < y)
				y = ga.getY();
			if (ga.getX() < x)
				x = ga.getX();
		}
		return new int[] {x, y};
	}

	private CreateContext getCreateContext(IPasteContext context, PictogramElement pe, int[] ul) {
		CreateContext cc = new CreateContext();
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		cc.setX(context.getX() + ga.getX() - ul[0]);
		cc.setY(context.getY() + ga.getY() - ul[1]);
		cc.setWidth(ga.getWidth());
		cc.setHeight(ga.getHeight());
		cc.setTargetContainer(getFeatureProvider().getDiagramTypeProvider().getDiagram());
		return cc;
	}

	@Override
	public boolean canPaste(IPasteContext context) {
		Object[] objects = getFromClipboard();
		if (objects != null && objects.length > 0) {
			for (Object o: objects) {
				if (o instanceof PictogramElement) {
					Object bo = getBusinessObjectForPictogramElement((PictogramElement) o);
					if (bo instanceof WFNode || bo instanceof Note || bo instanceof Image ||
							bo instanceof WFArc || bo instanceof Response ||
							bo instanceof ResponseArc) {
						continue;
					} else {
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}


	public void addToDiagram(WFNode addednode) {
		if (addednode.eResource() == null) {
			EList<EObject> contents = getDiagram().eResource().getContents();
			contents.add(addednode);
			contents.addAll(addednode.getInputPorts());
			for (InputPort port: addednode.getInputPorts())
				contents.addAll(port.getProperties());
			contents.addAll(addednode.getOutputPorts());   
			for (OutputPort port: addednode.getOutputPorts())
				contents.addAll(port.getProperties());
			contents.addAll(addednode.getProperties());        	
		}
	}

}
