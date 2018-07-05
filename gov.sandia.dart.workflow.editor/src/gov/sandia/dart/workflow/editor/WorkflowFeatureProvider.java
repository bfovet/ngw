/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IDeleteFeature;
import org.eclipse.graphiti.features.IDirectEditingFeature;
import org.eclipse.graphiti.features.IFeature;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReconnectionFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.IReconnectionContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.features.DefaultFeatureProvider;

import gov.sandia.dart.workflow.domain.Conductor;
import gov.sandia.dart.workflow.domain.Note;
import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.features.AddNoteFeature;
import gov.sandia.dart.workflow.editor.features.AddResponseArcFeature;
import gov.sandia.dart.workflow.editor.features.AddResponseFeature;
import gov.sandia.dart.workflow.editor.features.AddWFArcFeature;
import gov.sandia.dart.workflow.editor.features.AddWFNodeFeature;
import gov.sandia.dart.workflow.editor.features.CreateArcFeature;
import gov.sandia.dart.workflow.editor.features.DeleteConductorFeature;
import gov.sandia.dart.workflow.editor.features.DeletePortFeature;
import gov.sandia.dart.workflow.editor.features.DeleteWFArcFeature;
import gov.sandia.dart.workflow.editor.features.DeleteWFNodeFeature;
import gov.sandia.dart.workflow.editor.features.DirectEditNoteFeature;
import gov.sandia.dart.workflow.editor.features.DropResourceFeature;
import gov.sandia.dart.workflow.editor.features.LayoutNoteFeature;
import gov.sandia.dart.workflow.editor.features.LayoutResponseFeature;
import gov.sandia.dart.workflow.editor.features.LayoutWFNodeFeature;
import gov.sandia.dart.workflow.editor.features.MoveThingsOntoNotesFeature;
import gov.sandia.dart.workflow.editor.features.UpdateNoteFeature;
import gov.sandia.dart.workflow.editor.features.UpdateResponseFeature;
import gov.sandia.dart.workflow.editor.features.UpdateWFArcFeature;
import gov.sandia.dart.workflow.editor.features.UpdateWFNodeFeature;
import gov.sandia.dart.workflow.editor.features.WorkflowReconnectionFeature;

public class WorkflowFeatureProvider extends DefaultFeatureProvider {
 
    public WorkflowFeatureProvider(IDiagramTypeProvider dtp) {
        super(dtp);
    }
    
    @Override
    public IFeature[] getDragAndDropFeatures(IPictogramElementContext context) {
      // simply return all create connection features
      return getCreateConnectionFeatures();
    }
    
    @Override
    public IAddFeature getAddFeature(IAddContext context) {
        Object newObject = context.getNewObject();
		if (newObject instanceof WFNode) {
			return new AddWFNodeFeature(this);
        } else if (newObject instanceof WFArc) {
            return new AddWFArcFeature(this);
        } else if (newObject instanceof ResponseArc) {
            return new AddResponseArcFeature(this);
        } else if (newObject instanceof Note) {
            return new AddNoteFeature(this);
        } else if (newObject instanceof Response) {
            return new AddResponseFeature(this);
        } else {
        		IResource resource = Platform.getAdapterManager().getAdapter(newObject, IResource.class);
        		if (resource != null) {
        			return new DropResourceFeature(resource, context.getTargetContainer(), this);        
        		} 
        	}
        return super.getAddFeature(context);
    }

	@Override
	public IUpdateFeature getUpdateFeature(IUpdateContext context) {
		PictogramElement pictogramElement = context.getPictogramElement();
		Object bo = getBusinessObjectForPictogramElement(pictogramElement);
		if (pictogramElement instanceof ContainerShape) {       
			if (bo instanceof WFNode) {
				return new UpdateWFNodeFeature(this);
			} else if (bo instanceof Response) {
				return new UpdateResponseFeature(this);
			} else if (bo instanceof Note) {
				return new UpdateNoteFeature(this);
			}
		} else if (pictogramElement instanceof Connection) {                 
			if (bo instanceof WFArc) {
				return new UpdateWFArcFeature(this);               
			}
		}       
		return super.getUpdateFeature(context);
	}

    @Override
    public ILayoutFeature getLayoutFeature(ILayoutContext context) {
        PictogramElement pictogramElement = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pictogramElement);
        if (bo instanceof WFNode) {
            return new LayoutWFNodeFeature(this);
        } else if (bo instanceof Note) {
            return new LayoutNoteFeature(this);
        } else if (bo instanceof Response) {
            return new LayoutResponseFeature(this);
        }
        return super.getLayoutFeature(context);
    }
    
    @Override
    public ICreateConnectionFeature[] getCreateConnectionFeatures() {
       return new ICreateConnectionFeature[] { 
           new CreateArcFeature (this)
       };
    }
    
    @Override
    public IReconnectionFeature getReconnectionFeature(IReconnectionContext context) {
        return new WorkflowReconnectionFeature(this);
    }
    
    @Override
    public IDirectEditingFeature getDirectEditingFeature(IDirectEditingContext context) {
        PictogramElement pe = context.getPictogramElement();
        Object bo = getBusinessObjectForPictogramElement(pe);
        if (bo instanceof Note) {
            return new DirectEditNoteFeature(this);
        }
        return super.getDirectEditingFeature(context);
    }
    
    @Override
    public IDeleteFeature getDeleteFeature(IDeleteContext context) {
    	 PictogramElement pe = context.getPictogramElement();
         Object bo = getBusinessObjectForPictogramElement(pe);
         if (bo instanceof WFNode) {
             return new DeleteWFNodeFeature(this);
         } else if (bo instanceof WFArc) {
             return new DeleteWFArcFeature(this);
         } else if (bo instanceof Port) {
            return new DeletePortFeature(this);
         } else if (bo instanceof Conductor) {
             return new DeleteConductorFeature(this);
         }
         return super.getDeleteFeature(context);
    }
    
   @Override
   public IMoveShapeFeature getMoveShapeFeature(IMoveShapeContext context) {
	   PictogramElement pe = context.getPictogramElement();
       Object bo = getBusinessObjectForPictogramElement(pe);
       if (bo instanceof WFNode || bo instanceof Response) {
           return new MoveThingsOntoNotesFeature(this);
       }
       return super.getMoveShapeFeature(context);
	}
} 
