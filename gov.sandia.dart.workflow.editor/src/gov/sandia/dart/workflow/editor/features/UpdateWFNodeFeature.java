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
import java.util.Collection;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.features.impl.AbstractUpdateFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFNode;

public class UpdateWFNodeFeature extends AbstractUpdateFeature {

	public UpdateWFNodeFeature(IFeatureProvider fp) {
		super(fp);
	}

	@Override
	public boolean canUpdate(IUpdateContext context) {
		Object bo = getBusinessObjectForPictogramElement(context.getPictogramElement());
		return (bo instanceof WFNode);
	}

	// Since the graphics aren't stateful I can't tell if they're out of date, so assume they are
	
	@Override
	public IReason updateNeeded(IUpdateContext context) {		
		return Reason.createTrueReason();
	}

	private boolean portsNeedChanging(IUpdateContext context) {		
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (!(bo instanceof WFNode)) {
			return false;
		}
		WFNode dartNode = (WFNode) bo;
		Collection<String> inputPortNames = new ArrayList<>();
		dartNode.getInputPorts().forEach(p -> inputPortNames.add(p.getName()));
		Collection<String> outputPortNames = new ArrayList<>();
		dartNode.getOutputPorts().forEach(p -> outputPortNames.add(p.getName()));
		boolean portsChanged = false;
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Anchor a : new ArrayList<>(cs.getAnchors())) {
				InputPort ip = getInputPort(a);
				if (ip != null) {
					if (!inputPortNames.contains(ip.getName())) {	
						portsChanged = true;

					} else {
						// Input Exists
						inputPortNames.remove(ip.getName());
					}
				}
				OutputPort op = getOutputPort(a);
				if (op != null) {
					if (!outputPortNames.contains(op.getName())) {    		
						portsChanged = true;

					} else {
						// Output Exists
						outputPortNames.remove(op.getName());
					}
				}
			}            
			for (String inputName: inputPortNames) {
				InputPort port = getInputPort(dartNode, inputName);
				if (port != null) {
					portsChanged = true;
				}
			}

			for (String outputName: outputPortNames) {
				OutputPort port = getOutputPort(dartNode, outputName);
				if (port != null) {
					portsChanged = true;
				}
			}
		}

		return portsChanged;
	}
	
	
	@Override
	public boolean update(IUpdateContext context) {		
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (!(bo instanceof WFNode)) {
			return false;
		}
		WFNode dartNode = (WFNode) bo;
		
		if (!portsNeedChanging(context))
			return false;

		Collection<String> inputPortNames = new ArrayList<>();
		dartNode.getInputPorts().forEach(p -> inputPortNames.add(p.getName()));
		Collection<String> outputPortNames = new ArrayList<>();
		dartNode.getOutputPorts().forEach(p -> outputPortNames.add(p.getName()));
		boolean portsChanged = false;
		if (pictogramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pictogramElement;
			for (Anchor a : new ArrayList<>(cs.getAnchors())) {
				InputPort ip = getInputPort(a);
				if (ip != null) {
					if (!inputPortNames.contains(ip.getName())) {
						for (Connection c: new ArrayList<>(a.getIncomingConnections())) {
							DeleteContext dc = new DeleteContext(c);
							dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
							getFeatureProvider().getDeleteFeature(dc).delete(dc);  
						}
						DeleteContext dc = new DeleteContext(a);
						dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));   

						getFeatureProvider().getDeleteFeature(dc).delete(dc);  
						portsChanged = true;

					} else {
						// Input Exists
						inputPortNames.remove(ip.getName());
					}
				}
				OutputPort op = getOutputPort(a);
				if (op != null) {
					if (!outputPortNames.contains(op.getName())) {
						for (Connection c: new ArrayList<>(a.getOutgoingConnections())) {
							DeleteContext dc = new DeleteContext(c);
							dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
							getFeatureProvider().getDeleteFeature(dc).delete(dc);  
						}
						DeleteContext dc = new DeleteContext(a);
						dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
						getFeatureProvider().getDeleteFeature(dc).delete(dc);  
						portsChanged = true;

					} else {
						// Output Exists
						outputPortNames.remove(op.getName());
					}
				}
			}            
			AddWFNodeFeature adder = new AddWFNodeFeature(getFeatureProvider());
			EList<EObject> contents = getDiagram().eResource().getContents();
			for (String inputName: inputPortNames) {
				InputPort port = getInputPort(dartNode, inputName);
				if (port != null) {
					contents.add(port);
					adder.createInputAnchor(cs, cs.getGraphicsAlgorithm(), 3, port);
					portsChanged = true;
				}
			}

			for (String outputName: outputPortNames) {
				OutputPort port = getOutputPort(dartNode, outputName);
				if (port != null) {
					contents.add(port);
					adder.createOutputAnchor(cs, cs.getGraphicsAlgorithm(), 3, port);
					portsChanged = true;
				}
			}
		}

		if (portsChanged) {
			// Now, there may be overlapping anchors, or gaps. Need to make a pass over them and position them well.
			ContainerShape cs = (ContainerShape) pictogramElement;
			int inputIndex = 0, outputIndex = 0;
			AddWFNodeFeature adder = new AddWFNodeFeature(getFeatureProvider());
			for (Anchor anchor : new ArrayList<>(cs.getAnchors())) {
				if (anchor instanceof FixPointAnchor) {
					FixPointAnchor a = (FixPointAnchor) anchor;
					Object abo = getBusinessObjectForPictogramElement(a);
					if (abo instanceof InputPort) {
						String name = ((NamedObject)abo).getName();
						adder.positionInputAnchor(name, a, cs.getGraphicsAlgorithm(), inputIndex++);
					} else if (abo instanceof OutputPort) {
						String name = ((NamedObject)abo).getName();
						adder.positionOutputAnchor(name, a, cs.getGraphicsAlgorithm(), dartNode, outputIndex++);                	
					}
				}
			}
		}

		return portsChanged;
	}
	
	
	

	private InputPort getInputPort(WFNode dartNode, String name) {
		for (InputPort port: dartNode.getInputPorts())
			if (port.getName().equals(name))
				return port;
		return null;				
	}

	private OutputPort getOutputPort(WFNode dartNode, String name) {
		for (OutputPort port: dartNode.getOutputPorts())
			if (port.getName().equals(name))
				return port;
		return null;				
	}

	private InputPort getInputPort(Anchor anchor) {
		if (anchor != null) {
			Object object =
					getBusinessObjectForPictogramElement(anchor);
			if (object instanceof InputPort) {
				return (InputPort) object;
			}
		}
		return null;
	}

	private OutputPort getOutputPort(Anchor anchor) {
		if (anchor != null) {
			Object object =
					getBusinessObjectForPictogramElement(anchor);
			if (object instanceof OutputPort) {
				return (OutputPort) object;
			}
		}
		return null;
	}
	
	
}
