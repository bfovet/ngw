/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.tree;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.rendering.GenericWFNodeGARenderer;
import gov.sandia.dart.workflow.editor.tree.data.WFTreeRoot;

public class WorkflowFileLabelProvider extends LabelProvider implements ILabelProvider {

	private Image shapes;
	private Image port;
	
	protected WorkbenchLabelProvider wbProvider_;  

	public WorkflowFileLabelProvider() {
		shapes = WorkflowEditorPlugin.getImageDescriptor("icons/shapes.gif").createImage();
		port = WorkflowEditorPlugin.getImageDescriptor("icons/portTree.png").createImage();
	    wbProvider_ = new WorkbenchLabelProvider();    
	}

	
	@Override
	public String getText(Object element) {
		if(element instanceof WFTreeRoot) {			
			return ((WFTreeRoot) element).getName();
		} else if(element instanceof WFNode) {
			return ((WFNode) element).getLabel();
		}else if(element instanceof WFTreeNode) {
			return ((WFTreeNode)element).getNode().getLabel();
		}else if(element instanceof InputPort){
			return "(" + ((InputPort) element).getName() +")";			
		}else {
			return wbProvider_.getText(element);
		}
	}
	
	@Override
	public Image getImage(Object element) {
		if(element instanceof WFNode) {	
			return GenericWFNodeGARenderer.getIcon((WFNode) element);
		}else if(element instanceof WFTreeNode) {
			return GenericWFNodeGARenderer.getIcon(((WFTreeNode)element).getNode());
		}else if(element instanceof WFTreeRoot) {
			return shapes;
		}else if(element instanceof InputPort) {
			return port;
		}
		return wbProvider_.getImage(element);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		if (shapes != null) {
			shapes.dispose();
			shapes = null;
		}
		if (port != null) {
			port.dispose();
			port = null;
		}
	}
}
