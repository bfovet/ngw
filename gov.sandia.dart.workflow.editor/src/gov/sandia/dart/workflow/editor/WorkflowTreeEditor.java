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

import org.eclipse.emf.transaction.ResourceSetChangeEvent;
import org.eclipse.emf.transaction.ResourceSetListenerImpl;
import org.eclipse.gef.dnd.TemplateTransfer;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;

import gov.sandia.dart.workflow.editor.tree.WorkflowFileContentProvider;
import gov.sandia.dart.workflow.editor.tree.WorkflowFileLabelProvider;
import gov.sandia.dart.workflow.editor.tree.WorkflowTreeDecorator;
import gov.sandia.dart.workflow.editor.tree.WorkflowTreePreferences;
import gov.sandia.dart.workflow.editor.tree.WorkflowTreePreferences.Mode;
import gov.sandia.dart.workflow.editor.tree.data.DiagramWFTreeRoot;
import gov.sandia.dart.workflow.editor.tree.data.WFTreeRoot;

public class WorkflowTreeEditor extends WorkflowDiagramEditor  {

	public static final String ID = "gov.sandia.dart.workflow.editor.WorkflowTreeEditor";

	private WFTreeRoot root_;
	protected TreeViewer tree_;

	@Override
	public DiagramBehavior createDiagramBehavior() {
		return new WorkflowDiagramBehavior(this) {
			@Override 
			protected DefaultRefreshBehavior createRefreshBehavior() {
				return new WorkflowRefreshBehavior(this) {
					@Override 
					public void refresh(){
						super.refresh();
						
						if (!diagramBehavior.isAlive()) {
							return;
						}
						if (Display.getCurrent() == null) {
							return;
						}

						if(tree_ != null) {
								tree_.refresh();
						}
					}
				};
			}
			
		};
	}

	
	protected Composite buildRootComposite(Composite parent) {
		
		Composite treeComposite = new Composite(parent, SWT.NONE);
//		treeComposite.setLayoutData(new FillLayout());
		treeComposite.setLayout(new GridLayout(1, false));
		WorkflowTreePreferences treePreferences = new WorkflowTreePreferences();
		treePreferences.setMode(Mode.HIERARCHICAL);				
		tree_ = buildTreeViewer(treeComposite, treePreferences);

				
		// Build a hidden composite for the workflow graph
		// this is done so that "buildRootComposite()" will hook up the behavior/palette/settings editors etc. for us
		Composite hiddenComposite = new Composite(treeComposite, SWT.NONE);
		GridData hiddenGridData = new GridData(SWT.FILL, SWT.FILL, false, false);
		hiddenGridData.exclude = true;				
		hiddenComposite.setLayoutData(hiddenGridData);
		super.buildGraphComposite(hiddenComposite);

		return treeComposite;
	}

	protected TreeViewer buildTreeViewer(Composite parent, WorkflowTreePreferences treePreferences) {
		TreeViewer tree = new TreeViewer(parent);
		tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		WorkflowFileContentProvider provider = new WorkflowFileContentProvider();
		
		provider.setPreferences(treePreferences);
		
		tree.setContentProvider(provider);
		tree.setLabelProvider(new DecoratingLabelProvider(new WorkflowFileLabelProvider(), new WorkflowTreeDecorator()));
		
		Transfer[] dragTransferTypes = {
				LocalSelectionTransfer.getTransfer(),
		};

		Transfer[] dropTransferTypes = {
				LocalSelectionTransfer.getTransfer(),
				TemplateTransfer.getInstance(),				
		};
		
		tree.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY, dragTransferTypes, new DragSourceAdapter() {
			
			IStructuredSelection selection_;
			
			@Override
			public void dragStart(DragSourceEvent event) {
				selection_ = tree.getStructuredSelection();
				event.doit = !selection_.isEmpty();
			}
			
			
			@Override
			public void dragSetData(DragSourceEvent event) {
				LocalSelectionTransfer.getTransfer().setSelection(selection_);
				event.data = selection_;
			}
		});
		
		tree.addDropSupport(DND.DROP_COPY | DND.DROP_MOVE, dropTransferTypes, getDropTargetListener(tree, getDiagramTypeProvider(), treePreferences));
		
		if(root_ != null)
		{
			tree.setInput(root_);
		}
		
		
		MenuManager menuMgr = new MenuManager();

        Menu menu = menuMgr.createContextMenu(tree.getControl());
        menuMgr.addMenuListener(new WorkflowDiagramEditorMenuListener(tree, getDiagramTypeProvider()));
        menuMgr.setRemoveAllWhenShown(true);
        tree.getControl().setMenu(menu);
        
        tree.addSelectionChangedListener(new SelectionChangedListener(this));		
		
		getEditingDomain().addResourceSetListener(new ResourceSetListenerImpl() {
			@Override
			public void resourceSetChanged(ResourceSetChangeEvent event) {				
			      PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			          
			          @Override
			          public void run()
			          {
			        	  if(!tree.getControl().isDisposed()) {
							tree.refresh();
			        	  }
			          }
		        });
			}
		});
		
		return tree;
	}

	protected WorkflowTreeDropTargetListener getDropTargetListener(TreeViewer tree, IDiagramTypeProvider provider, WorkflowTreePreferences treePreferences) {
		return new WorkflowTreeDropTargetListener(tree, provider, treePreferences);
	}
	
	protected void setInput(IEditorInput input) {
		super.setInput(input);
		openPaletteView();
		
		if (input instanceof DiagramEditorInput) {
			root_ = new DiagramWFTreeRoot(getDiagramTypeProvider(), null);						
			
			if(tree_ != null) {
				tree_.setInput(root_);
			}
		}
	}
	
	private class SelectionChangedListener implements ISelectionChangedListener{			

    	private WorkflowTreeEditor part_;
    	
    	public SelectionChangedListener(WorkflowTreeEditor part) {
    		part_ = part;
    	}
    	
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			part_.selectionChanged(part_, event.getSelection());
		}
	}		
}
