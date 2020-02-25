/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.palette;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PaletteTreeContentProvider implements ITreeContentProvider {

	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getChildren(Object parentElement) {
		
		List<PaletteEntryEditPart> children = null;
		
		//Handle the root node
		if (parentElement instanceof List) {
			children = (List<PaletteEntryEditPart>) parentElement;
		}
		
		//Handle the child nodes
		else if (parentElement instanceof EditPart) {
			children = ((EditPart) parentElement).getChildren();
		}
		
		if (children != null) {	
			return children.toArray();
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		
		return (children != null && children.length > 0);
	}

	@Override
	public Object[] getElements(Object inputElement) {
		Object[] elements = getChildren(inputElement);
		if (elements == null) {
			elements = new Object[0];
		}
		return elements;
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof EditPart){
			return ((EditPart) element).getParent();			
		}		
		return null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
