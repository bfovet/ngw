/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.CreateContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TreeItem;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.features.CreateArcFeature;
import gov.sandia.dart.workflow.editor.features.CreateWFNodeFeature;
import gov.sandia.dart.workflow.editor.features.DeleteWFArcFeature;
import gov.sandia.dart.workflow.editor.features.DropResourceFeature;
import gov.sandia.dart.workflow.editor.tree.WorkflowTreePreferences;

public class WorkflowTreeDropTargetListener extends ViewerDropAdapter {

	// Amount to shift new node down from previous sibling
	private static final int Y_OFFSET = 25;

	// Placeholder height for rough centering (because this is mainly used for files, it's good for now)
	private static final int PLACEHOLDER_HEIGHT = 70;
	
	// Amount to shift new node from parent if no siblings
	private static final int X_OFFSET = 250;
	
	
	private IDiagramTypeProvider typeProvider_;
	
	private WorkflowTreePreferences treePreferences_;
	
	public WorkflowTreeDropTargetListener(Viewer viewer, IDiagramTypeProvider typeProvider, WorkflowTreePreferences treePreferences) {
		super(viewer);
		setFeedbackEnabled(false);
		typeProvider_ = typeProvider;
		treePreferences_ = treePreferences;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		if(target == null) {
			return true;
		}
		
		if(target instanceof InputPort) {
			// AJR - shoudl try to support this
			// NOTE: if this is a multi-select drop, should return FALSE
			
			return false;
		}
		
		WFNode targetNode = Platform.getAdapterManager().getAdapter(target, WFNode.class);
		
		if(targetNode != null) {
			DropTargetEvent event = getCurrentEvent();
			
			// TODO make sure we are not dropping on ourself -- this doesn't quite work...		
//			if(event.getSource() instanceof DropTarget) {
//				if((getViewer().getControl() == ((DropTarget)event.getSource()).getControl()) 
//						&& (getViewer().getSelection() instanceof IStructuredSelection)) {
//					IStructuredSelection selection = (IStructuredSelection)getViewer().getSelection();
//					
//					for(Object item : selection.toList()) {
//						if(item == target) {
//							return false;
//						}
//					}
//				}
//			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public boolean performDrop(Object data) {
		WFNode dropTarget = null;				
		// Get Target Node
		DropTargetEvent event = getCurrentEvent();
		
		if(event.item instanceof TreeItem) {
			TreeItem treeItem = (TreeItem) event.item;
			dropTarget = Platform.getAdapterManager().getAdapter(treeItem.getData(), WFNode.class);
		}

		
		if (typeProvider_ == null) {
			return false;
		} else if (!(typeProvider_.getFeatureProvider() instanceof WorkflowFeatureProvider)) {
			return false;
		} 
		WorkflowFeatureProvider featureProvider = (WorkflowFeatureProvider) typeProvider_.getFeatureProvider();			

		// If there is no target, just add it as a final node
		if(dropTarget == null) {
			ILocation referenceLocation = null;			

			ILocation checkLocation;
			int refHeight = 0;
			
			// try to find the rightmost/bottom terminal node 
			for(Shape shape : typeProvider_.getDiagram().getChildren()) {
				checkLocation = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(shape);
				
				if(referenceLocation == null || 
						(checkLocation.getX() > referenceLocation.getX()) || checkLocation.getY() > checkLocation.getY()) {
					referenceLocation = checkLocation;
					refHeight = shape.getGraphicsAlgorithm().getHeight(); 
				}
			}
			
			// Position the new node BELOW the previous one
			if(referenceLocation != null) {
				referenceLocation.setY(referenceLocation.getY() + refHeight + Y_OFFSET);
			}
			processDrop(event.data, event, typeProvider_.getDiagram(), null, referenceLocation, false);
			return true;
		}
		
		List<WFNode> eventNodes = new ArrayList<>();
		if(event.data instanceof IStructuredSelection) {
			for(Object item : ((IStructuredSelection)event.data).toArray()){
				if(item == dropTarget) {
					return false;
				}else if(item instanceof WFNode){
					eventNodes.add((WFNode) item);
				}
			}
		}
		
		// TODO - handle multi drop....
		if(eventNodes.size() > 1) {
			return false;
		}
		
		
		
		
//		List<NodeLinkDescriptor> upstreamNodes = new ArrayList<NodeLinkDescriptor>();
		List<NodeLinkDescriptor> downstreamNodes = new ArrayList<>();
		List<WFArc> arcsToRemove = new ArrayList<>();
		Object droppedItem;
		
		droppedItem = event.data;
		downstreamNodes.add(new NodeLinkDescriptor(dropTarget));
		
		
		List<ContainerShape> downstreamShapes = new ArrayList<>();
		
		for(NodeLinkDescriptor descriptor :downstreamNodes) {
				downstreamShapes.add((ContainerShape)featureProvider.getPictogramElementForBusinessObject(descriptor.node));					
		}
		
		
		// TODO - do this differently
		// "target" is needed when dropping IResources and for positioning
		// This technically works because if the event.data is a resource, at this point downstreamNodes will neccesarily have one item
		// Probably a better way to do things would be to separate processing for IResources from other stuff
		ContainerShape target;
		
		if(downstreamShapes.isEmpty()) {
			target = typeProvider_.getDiagram();
		}else {
			target = downstreamShapes.get(0);
		}

		
		// Get first target InputPort and a reference point 
		
		InputPort checkPort = null;
		boolean center = false;
		ILocation referenceLocation = null;			


		// TODO - all this layout stuff should be done differently, possibly an autolayout AFTER everything is connected
		
		if(!downstreamNodes.isEmpty()) {
			for(InputPort port : downstreamNodes.get(0).node.getInputPorts()) {
				
				
				if(port.getArcs().isEmpty()) {
					if(checkPort == null) {
						checkPort = port;
						break;
					}
				}else {
					WFNode source = port.getArcs().get(0).getSource().getNode();
					Shape shape = (Shape)featureProvider.getPictogramElementForBusinessObject(source);					
					referenceLocation = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(shape);
					
					if(checkPort == null) {				
						// If this is a previous sibling, the new node will be BELOW it.
						referenceLocation.setY(referenceLocation.getY() + shape.getGraphicsAlgorithm().getHeight() + Y_OFFSET);
					}
					
				}
	
				if(checkPort != null && referenceLocation != null) {
					break;
				}
			}
		}

		// If there were no siblings, the new node will be to the left of the parent.
		if(referenceLocation == null) {
			if(!downstreamShapes.isEmpty())
			{
				referenceLocation = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(downstreamShapes.get(0));				
				referenceLocation.setX(referenceLocation.getX() - X_OFFSET);
				
				center = true;				
			}else {
				// Just stick it somewhere for now
				ILocation checkLocation;
				int refHeight = 0;
				
				// try to find the rightmost/bottom terminal node 
				for(Shape shape : typeProvider_.getDiagram().getChildren()) {
					checkLocation = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(shape);
					
					if(referenceLocation == null || 
							(checkLocation.getX() > referenceLocation.getX()) || checkLocation.getY() > checkLocation.getY()) {
						referenceLocation = checkLocation;
						refHeight = shape.getGraphicsAlgorithm().getHeight(); 
					}
				}
				
				// Position the new node BELOW the previous one
				if(referenceLocation != null) {
					referenceLocation.setY(referenceLocation.getY() + refHeight + Y_OFFSET);
				}
			}
		}
		
		final ILocation finalLocation = referenceLocation;
		final boolean finalCenter = center;
					
		// Create new Node

		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(typeProvider_.getDiagram());			
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			public void doExecute() {
				List<NodeLinkDescriptor> droppedNodes = processDrop(droppedItem, event, target, arcsToRemove, finalLocation, finalCenter);

				if(droppedNodes.isEmpty()) {
					return;
				}

				buildConnections(featureProvider, droppedNodes, downstreamNodes);		
//				buildConnections(featureProvider, upstreamNodes, droppedNodes);		
			}
		});
		
		return true;
	}

	protected class NodeLinkDescriptor{
		public WFNode node;
		public InputPort inputPort;
		public OutputPort outputPort;
		
		public NodeLinkDescriptor(WFNode node) {
			this.node = node;
			this.inputPort = null;
			this.outputPort = null;
		}

		public NodeLinkDescriptor(WFNode node, InputPort inputPort, OutputPort outputPort) {
			this.node = node;
			this.inputPort = inputPort;
			this.outputPort = outputPort;
		}
	}
	

	protected void buildConnections(WorkflowFeatureProvider featureProvider, List<NodeLinkDescriptor> sourceDescriptors, List<NodeLinkDescriptor> targetDescriptors) {

		for(NodeLinkDescriptor sourceDescriptor : sourceDescriptors) {
			WFNode sourceNode = sourceDescriptor.node;			
			
			if(!sourceNode.getOutputPorts().isEmpty()) {
				OutputPort outputPort = sourceDescriptor.outputPort;
				
				if(outputPort == null) {
				
					outputPort = sourceNode.getOutputPorts().get(0);
										
					// If this is nested, use the second port (i.e. the first response) if possible
					if(isNestedWorkflow(sourceNode) && sourceNode.getOutputPorts().size() > 1) {
						outputPort = sourceNode.getOutputPorts().get(1);						
					}								
				}
				
				
		        PictogramElement oppe = featureProvider.getPictogramElementForBusinessObject(outputPort);
		        		        
		        if(oppe == null) {
		        	continue;
		        }
				
		        for(NodeLinkDescriptor targetDescriptor : targetDescriptors)
		        {
		        	WFNode targetNode = targetDescriptor.node;
			        CreateConnectionContext arcContext = new CreateConnectionContext();		        
			        arcContext.setSourceAnchor((Anchor) oppe);				
			        
					// Get next open InputPort
					
					InputPort inputPort = targetDescriptor.inputPort;
					if(inputPort == null)
					{
						for(InputPort port : targetNode.getInputPorts()) {
												
							if(port.getArcs().isEmpty()) {
								inputPort = port;
								break;
							}
						}
					}					
					
	
					if(inputPort != null) {
				        arcContext.setTargetAnchor((Anchor) featureProvider.getPictogramElementForBusinessObject(inputPort));				
					}else {
						PictogramElement pe = featureProvider.getPictogramElementForBusinessObject(targetNode);
						arcContext.setTargetPictogramElement(pe);
						ILocation location = getCenterOfPictogramElement(pe);
		
						arcContext.setTargetLocation(location);
					}
					
								
			        CreateArcFeature createArcFeature = new CreateArcFeature (featureProvider);
		
			        
			        if(!createArcFeature.canCreate(arcContext)) {
			        	continue;
			        }		        
			        createArcFeature.create(arcContext);
		        }
			}
		}
		
		return;
	}

	private boolean portTypesMatch(InputPort inputPort, OutputPort outputPort) {
		
		
		if(inputPort.getType().equals(outputPort.getType())) {
			return true;
		}
		
		if(inputPort.getType().contains("file") && outputPort.getType().contains("file")) {
			return true;
		}

		if(inputPort.getType().contains("default") || outputPort.getType().contains("default")) {
			return true;
		}
		
		return false;
	}

	private boolean isNestedWorkflow(WFNode sourceNode) {
		return sourceNode.getType().contains("nested") || sourceNode.getType().contains("Nested");					
	}

	protected ILocation getCenterOfPictogramElement(PictogramElement pe) {
		Shape shape = (Shape) pe;
		ILocation location = Graphiti.getPeLayoutService().getLocationRelativeToDiagram(shape);
		
		// change the location to be at the center of the targetNode
		location.setX(location.getX() + shape.getGraphicsAlgorithm().getWidth()/2);
		location.setY(location.getY() + shape.getGraphicsAlgorithm().getHeight()/2);
		return location;
	}

	protected List<NodeLinkDescriptor> processDrop(Object droppedItem, DropTargetEvent event, ContainerShape target, List<WFArc> arcsToRemove, ILocation referenceLocation, boolean center) {
		List<NodeLinkDescriptor> newNodes = new ArrayList<>(); 

		Map<WFNode, Set<Object>> draggedNodes = new HashMap<>();
		List<IResource> droppedResources = new ArrayList<>();
		
		
		boolean isCopy = ((event.detail & DND.DROP_COPY) != 0);
		
		if(droppedItem instanceof WFNode) {
			draggedNodes.put((WFNode)droppedItem, null);					
		}
		if(droppedItem instanceof TreeSelection) {
			TreeSelection selection = (TreeSelection) droppedItem;

			for(Object selected : selection.toList()) {
				if(selected instanceof WFNode) {
					if(draggedNodes.containsKey(selected)) {
						continue;
					}					
					
					WFNode node = (WFNode) selected;
					Set<Object> parents = new HashSet<>();
					for(TreePath path : selection.getPathsFor(node)){

						if(path.getSegmentCount() > 1) {
							parents.add(path.getSegment(path.getSegmentCount()-2));
						}
					}
					
					draggedNodes.put(node, parents);					
					
					continue;
				}
	    		IResource resource = Platform.getAdapterManager().getAdapter(selected, IResource.class);
				if(resource != null) {
					droppedResources.add(resource);
				}
			}					
		}else if(droppedItem instanceof IResource) {
			droppedResources.add((IResource) droppedItem);
		}else if(droppedItem instanceof CreationFactory) {
			// Drop from the Palette
						
			CreationFactory factory = (CreationFactory) droppedItem;
			
			Object newItem = factory.getNewObject();
			if(newItem instanceof CreateWFNodeFeature) {
				CreateContext createContext = new CreateContext();					
				createContext.setTargetContainer(typeProvider_.getDiagram());	
				
				if(referenceLocation != null) {
					createContext.setLocation(referenceLocation.getX(), referenceLocation.getY());
				}
				
				Object[] newObjects = ((CreateWFNodeFeature) newItem).create(createContext);
				
				
				if((newObjects.length != 0) && (newObjects[0] instanceof WFNode)) {
					newNodes.add(new NodeLinkDescriptor((WFNode)newObjects[0]));
				}
			};
			// Create new WorkflowNode
		}
		
		
		
		if(!droppedResources.isEmpty()){						
			int x = 0; 
			int y = 0;
			
			if(referenceLocation != null) {
				x = referenceLocation.getX();
				y = referenceLocation.getY();
				
				int count = droppedResources.size();
				
				if(center && count > 1) {
					y -= (((PLACEHOLDER_HEIGHT-1)*count + Y_OFFSET*(count -1))/2);
				}
			}
	
			for(IResource resource : droppedResources) {
    			y += PLACEHOLDER_HEIGHT + Y_OFFSET;     					
				NodeLinkDescriptor newResourceNode = buildResourceNode(target, x, y, resource);
					 
				if(newResourceNode != null) {
					newNodes.add(newResourceNode);
				}
			}
		}
		
		
		// TODO - Can we pull this out of this method and do it elsewhere?
		if(arcsToRemove ==  null) {
			arcsToRemove = new ArrayList<>();
		}
		
		if(!draggedNodes.isEmpty()) {			
			for(Entry<WFNode, Set<Object>> nodeEntry : draggedNodes.entrySet()) {				
								
				// If we are moving (i.e. not copying) find any arcs we need to remove
				if(!isCopy && (nodeEntry.getValue() != null)) {

					// Since the same node can be found down several tree paths, make sure to only disconnect from
					// the path it was dragged from
					for(OutputPort port : nodeEntry.getKey().getOutputPorts()) {
						for(WFArc arc :port.getArcs()) {
							if(nodeEntry.getValue().contains(arc.getTarget().getNode())) {
								arcsToRemove.add(arc);									
							}
						}
					}
				}
				
				newNodes.add(new NodeLinkDescriptor(nodeEntry.getKey()));
			}
		}
			
		if(!arcsToRemove.isEmpty()) {
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(typeProvider_.getDiagram());			
			
			DeleteWFArcFeature deleteFeature = new DeleteWFArcFeature(typeProvider_.getFeatureProvider());
			MultiDeleteInfo info = new MultiDeleteInfo(false, false, arcsToRemove.size());    			
			
			List<WFArc> finalArcsToRemove = arcsToRemove;
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				public void doExecute() {
					for(WFArc arc : finalArcsToRemove) {
						PictogramElement pe = typeProvider_.getFeatureProvider().getPictogramElementForBusinessObject(arc);

						DeleteContext context = new DeleteContext(pe);	
						context.setMultiDeleteInfo(info);

						if(deleteFeature.canExecute(context)) {
							deleteFeature.execute(context);
						}
					}
				}
			});
		}			

		return newNodes;
	}

	protected NodeLinkDescriptor buildResourceNode(ContainerShape target, int x, int y, IResource resource) {		
		DropResourceFeature dropResource = new DropResourceFeature(resource, target, typeProvider_.getFeatureProvider());        

		AddContext addContext = new AddContext();					
		addContext.setTargetContainer(typeProvider_.getDiagram());	
		
		addContext.setLocation(x,y);
		
		if(dropResource.canAdd(addContext)) {
			
			PictogramElement result = dropResource.add(addContext);
			if(result != null && result != target) {    					
				Object bo = typeProvider_.getFeatureProvider().getBusinessObjectForPictogramElement(result);
				
				if(bo instanceof WFNode) {
					return new NodeLinkDescriptor((WFNode) bo);	    						
				}			    					
			}    				
		}
		return null;
	}
	
	protected IDiagramTypeProvider getDiagramTypeProvider() {
		return typeProvider_;
	}
	

}
