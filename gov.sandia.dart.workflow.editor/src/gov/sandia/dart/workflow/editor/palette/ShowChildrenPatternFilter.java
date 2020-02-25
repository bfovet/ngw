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

import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * From http://rcpexperiments.blogspot.com/2009/07/how-to-show-children-of-matched-tree.html
 *
 */
class ShowChildrenPatternFilter extends PatternFilter {

 protected boolean isChildMatch(Viewer viewer, Object element) {
  Object parent = ((ITreeContentProvider) ((AbstractTreeViewer) viewer)
             .getContentProvider()).getParent(element);

  if(parent!=null){
   return (isLeafMatch(viewer, parent)?true:isChildMatch(viewer,parent));
  }
  return false;
 
 }

 
 @Override
 protected boolean isLeafMatch(Viewer viewer, Object element) {
  String labelText = ((ILabelProvider) ((StructuredViewer) viewer)
             .getLabelProvider()).getText(element);
  
     if(labelText == null) {
   return false;
  }
  
     return (wordMatches(labelText)?true:isChildMatch(viewer, element));
 }
}
