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

import java.util.List;
import java.util.Optional;

import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.features.impl.AbstractCreateConnectionFeature;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowImageProvider;
import gov.sandia.dart.workflow.util.ParameterUtils;
import gov.sandia.dart.workflow.util.PropertyUtils;
 
public class CreateArcFeature extends
       AbstractCreateConnectionFeature {
 
	public CreateArcFeature (IFeatureProvider fp) {
        // provide name and description for the UI, e.g. the palette
        super(fp, "Make Connections", "Connect elements within a workflow");
    }
 
    @Override
	public boolean canCreate(ICreateConnectionContext context) {
        OutputPort source = getOutputPort(context.getSourceAnchor());

        // We have to have a source
        if (source == null) {
        		return false;        
        }
        
        // It the target is an input port, it can't have more than one incoming connection
        InputPort ipTarget = getInputPort(context.getTargetAnchor());
        if (ipTarget != null && ipTarget.getArcs().isEmpty()) {
        		return true;
        } 
        
       // If it's a response it can.
        Response rTarget = getResponse(context.getTargetAnchor());
        if (rTarget != null) {
        		return true;
        }
        
        WFNode nodeTarget = getWFNode(context);
        if (nodeTarget != null)
        		return true;
        
        return false;
    }
 
    private WFNode getWFNode(ICreateConnectionContext context) {
    		PictogramElement pe = context.getTargetPictogramElement();
    		if (pe != null) {
    			GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
			Object bo = getBusinessObjectForPictogramElement(pe);
            if (bo instanceof WFNode) {
            		ILocation loc = context.getTargetLocation();
            		if (loc.getX() - ga.getX() > 10 && (ga.getX() + ga.getWidth()) - loc.getX() > 10)
            			return ((WFNode) bo);
            } 
    		}
        return null;
	}

	@Override
	public boolean canStartConnection(ICreateConnectionContext context) {
        OutputPort source = getOutputPort(context.getSourceAnchor());
        return source != null;
    }
 
	@Override
	public Connection create(ICreateConnectionContext context) {
		Connection newConnection = null;

		// get EClasses which should be connected
		OutputPort source = getOutputPort(context.getSourceAnchor());
		InputPort ipTarget = getInputPort(context.getTargetAnchor());
		Response rTarget = getResponse(context.getTargetAnchor());
		WFNode nTarget = getWFNode(context);

		if (source != null) {
			if (ipTarget != null) {
				newConnection = connectToInputPort(context, source, ipTarget);

			} else if (rTarget != null) {
				newConnection = connectToResponse(context, source, rTarget);

			} else if (nTarget != null) {
				newConnection = connectToNodeOnNewPort(context, source, nTarget);
			}
		}
		return newConnection;
	}

	protected Connection connectToInputPort(ICreateConnectionContext context, OutputPort source, InputPort ipTarget) {
		Connection newConnection;
		// create new business object 
		WFArc arc = createDARTArc(source, ipTarget);
		// add connection for business object
		AddConnectionContext addContext =
				new AddConnectionContext(context.getSourceAnchor(), context
						.getTargetAnchor());
		addContext.setNewObject(arc);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
		return newConnection;
	}

	protected Connection connectToResponse(ICreateConnectionContext context, OutputPort source, Response rTarget) {
		Connection newConnection;
		// create new business object 
		ResponseArc arc = createResponseArc(source, rTarget);
		// add connection for business object
		AddConnectionContext addContext =
				new AddConnectionContext(context.getSourceAnchor(), context
						.getTargetAnchor());
		addContext.setNewObject(arc);
		newConnection =
				(Connection) getFeatureProvider().addIfPossible(addContext);
		return newConnection;
	}
	protected Connection connectToNodeOnNewPort(ICreateConnectionContext context, OutputPort source, WFNode nTarget) {
		Connection newConnection;
		// Creating the new port is easy enough, but...
		String  inputPortName = getPortName(source);
		Optional<InputPort> oPort = nTarget.getInputPorts().stream().filter(x -> x.getName().equals(inputPortName)).findFirst();
		InputPort port;
		if (!oPort.isPresent()) {
			port = DomainFactory.eINSTANCE.createInputPort();			
			port.setName(inputPortName);
			port.setType(getInputPortType(source));
			nTarget.getInputPorts().add(port);
		} else {
			port = oPort.get();
		}
		// Update the node, to create the anchors
		List<PictogramElement> nodePe = Graphiti.getLinkService().getPictogramElements(getDiagram(), nTarget);
		UpdateContext uc = new UpdateContext(nodePe.get(0));
		new UpdateWFNodeFeature(getFeatureProvider()).update(uc);
		
		// Now create a new connection.
		List<PictogramElement> anchorPe = Graphiti.getLinkService().getPictogramElements(getDiagram(), port);			
		WFArc arc = createDARTArc(source, port);
		AddConnectionContext addContext =
				new AddConnectionContext(context.getSourceAnchor(), (Anchor) anchorPe.get(0));
		addContext.setNewObject(arc);
		newConnection = (Connection) getFeatureProvider().addIfPossible(addContext);
		return newConnection;
	}

	protected String getPortName(OutputPort source) {
		if (ParameterUtils.isParameter(source.getNode()))
			return ParameterUtils.getName(source.getNode());
		else if (source.getNode().getType().equals("constant"))
			return source.getNode().getName();
		else if (isFileOrFolder(source))
			return source.getNode().getName();
		else
			return source.getName();
	}

	protected boolean isFileOrFolder(OutputPort source) {
		String type = source.getNode().getType();
		return type.equals("file") || type.equals("folder");
	}

	private String getInputPortType(OutputPort source) {
		String type = source.getType();
		if ("output_file".equals(type) || "exodus_file".equals(type))
			return "input_file";
		else
			return type;
	}
 
    /**
     * Returns the InputPort belonging to the anchor, or null if not available.
     */
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

    private Response getResponse(Anchor anchor) {
        if (anchor != null) {
            Object object =
                getBusinessObjectForPictogramElement(anchor);
            if (object instanceof Response) {
                return (Response) object;
            }
        }
        return null;
    }

 
    private WFArc createDARTArc(OutputPort source, InputPort target) {
        WFArc arc = DomainFactory.eINSTANCE.createWFArc();
        arc.setSource(source);
        arc.setTarget(target);
        arc.setName(getWFArcName(arc));
        
        PropertyUtils.setProperty(arc, PropertyUtils.LINK_INCOMING_FILE_TO_TARGET, "false");
        PropertyUtils.setProperty(arc, PropertyUtils.EXPAND_WILDCARDS, "false");
        PropertyUtils.setProperty(arc, PropertyUtils.READ_IN_FILE, "false");


        return arc;
   }
    
    private ResponseArc createResponseArc(OutputPort source, Response target) {
        ResponseArc arc = DomainFactory.eINSTANCE.createResponseArc();
        arc.setSource(source);
        arc.setTarget(target);
        arc.setName(getResponseArcName(arc));

        return arc;
   }

    
    static String getWFArcName(WFArc arc) {
    	return String.format("%s -> %s", arc.getSource().getName(), arc.getTarget().getName());
    }
    
    static String getResponseArcName(ResponseArc arc) {
    	return String.format("%s -> %s", arc.getSource().getName(), arc.getTarget().getName());
    }
    
    @Override
    public String getCreateImageId() {
    	return WorkflowImageProvider.IMG_PLUG;
    }
    
    @Override
    public String getCreateLargeImageId() {
    	return WorkflowImageProvider.IMG_PLUG;
    }

}
