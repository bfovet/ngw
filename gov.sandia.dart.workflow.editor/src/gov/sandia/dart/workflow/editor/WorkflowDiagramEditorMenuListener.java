package gov.sandia.dart.workflow.editor;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ISetSelectionTarget;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.features.DeleteWFNodeFeature;

public class WorkflowDiagramEditorMenuListener implements IMenuListener {

	private IDiagramTypeProvider provider_;
	private TreeViewer viewer_;
	
	
	public WorkflowDiagramEditorMenuListener(TreeViewer viewer, IDiagramTypeProvider provider) {
		provider_ = provider;
		viewer_ = viewer;
	}
	
	@Override
	public void menuAboutToShow(IMenuManager manager) {
		if(provider_ == null || viewer_ == null ) {
			return;
		}
		
		
	    if (viewer_.getSelection().isEmpty()) {
	        return;
	    }

	    if (viewer_.getSelection() instanceof IStructuredSelection) {
	        IStructuredSelection selection = (IStructuredSelection) viewer_.getSelection();
	    	if(selection.size() == 1 && (selection.getFirstElement() instanceof IFile)) {
	    		
	            manager.add(new Action("Show in Project Navigator") {

	            	@Override
	            	public void run(){
	            		try {
							IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("com.strikewire.snl.apc.projectexplorer.views.ProjectExplorerView");
							
							
							if(view instanceof ISetSelectionTarget) {
								((ISetSelectionTarget) view).selectReveal(selection);
							}
						} catch (PartInitException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            });
	    		
	    		return;
	    	}
	    	
	    	
	    	
	    	Set<WFNode> nodes = new HashSet<WFNode>();
	    	boolean onlyWFNodes = true;
	    		    	
	    	
	        
	        for(Object item : selection.toList()) {
	        	if(item instanceof WFNode) {
	        		nodes.add((WFNode) item);
	        	}else {
	        		onlyWFNodes = false;
	        	}                    	
	        }
	        
	        
	        if (!nodes.isEmpty() && onlyWFNodes) {
	            manager.add(new Action("Delete") {

	            	@Override
	            	public void run(){
	    				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(provider_.getDiagram());
	    				
	    				domain.getCommandStack().execute(new RecordingCommand(domain) {
	    					@Override
	    					public void doExecute() {
	                			MultiDeleteInfo info = new MultiDeleteInfo(true, false, nodes.size());
	                			
	                			DeleteWFNodeFeature feature = new DeleteWFNodeFeature(provider_.getFeatureProvider());
	                    		for(WFNode node : nodes) {
	                    			PictogramElement pe = provider_.getFeatureProvider().getPictogramElementForBusinessObject(node);
	                    			DeleteContext context = new DeleteContext(pe);	  
	                    			context.setMultiDeleteInfo(info);
	                    			if(feature.canDelete(context)) {
	                    				feature.delete(context);
	                    			}
	                    		}
	    					}
	    				});
	            	}                        	
	            });
	        }
	    }
	}

}
