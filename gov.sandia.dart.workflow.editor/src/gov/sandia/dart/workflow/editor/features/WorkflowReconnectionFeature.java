/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.features;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.impl.DefaultReconnectionFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFArc;

/**
 * There are quite a few different reconnection cases: moving the source end from an input port to another
 * input port, and then moving the destination end from one output port to another, or from a port to a response
 * or vice versa. Given the existing model classes, they'll each have to be handled explicitly.
 * 
 * First: target end from one output port to a new output port.
 * 
 * @author ejfried
 *
 */

public class WorkflowReconnectionFeature extends DefaultReconnectionFeature {
 
      public WorkflowReconnectionFeature(IFeatureProvider fp) {
          super(fp);
      }
 
      @Override
      public boolean canReconnect(IReconnectionContext context) {
    	    if (super.canReconnect(context)) {
    	      OutputPort currentSource = getOutputPort(context.getConnection().getStart());
    	      NamedObject oldTarget = getTarget(context.getOldAnchor());
    	      NamedObject newTarget = getTarget(context.getNewAnchor());
    	      
    	      // First: target end from one output port to a new output port.
    	      if (movingTargetAcrossInputPorts(currentSource, oldTarget, newTarget)) {
    	    	  	return true;
    	      } 
    	    }
    	    return false;
      }

	public boolean movingTargetAcrossInputPorts(OutputPort currentSource, NamedObject oldTarget, NamedObject newTarget) {
		return currentSource != null && oldTarget instanceof InputPort && newTarget instanceof InputPort && oldTarget != newTarget;
	}
	      
	@Override
	public void postReconnect(IReconnectionContext context) {
		OutputPort currentSource = getOutputPort(context.getConnection().getStart());
		NamedObject oldTarget = getTarget(context.getOldAnchor());
		NamedObject newTarget = getTarget(context.getNewAnchor());
		if (movingTargetAcrossInputPorts(currentSource, oldTarget, newTarget)) {
			WFArc arc = (WFArc) getBusinessObjectForPictogramElement(context.getConnection());
			arc.setTarget((InputPort) newTarget);

		} 
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
      
      private NamedObject getTarget(Anchor anchor) {
          if (anchor != null) {
              Object object =
                  getBusinessObjectForPictogramElement(anchor);
              if (object instanceof NamedObject) {
                  return (NamedObject) object;
              }
          }
          return null;
      }
}
