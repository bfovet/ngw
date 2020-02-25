/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.ui;

import java.util.Map;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

public class VariableUsageDialog extends ListDialog {

	Map<String, ApreproVariableData> list;
	public VariableUsageDialog(Shell parent, final Map<String, ApreproVariableData> list) {
		super(parent);
		this.list = list;
		setMessage("Pick an existing parameter for substitution");
		setTitle("Use existing parameter");

		setContentProvider(new VariableUsageContentProvider());				
		setLabelProvider(new VariableUsageLabelProvider());
		setInput(list);			
		
		// the dialog doesn't highlight anything by default, so let's have the first item selected
		if(list.size() > 0)
		{
			setInitialSelections(new Object[]{list.values().toArray()[0]});
		}		
	}
	
	@Override
	protected Point getInitialSize() {
		return new Point(300, 400);
	}		
	
	@Override
	protected boolean isResizable() {
		return false;
	}
	
	private class VariableUsageContentProvider implements IStructuredContentProvider {		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub			
		}
		
		public void dispose() {
			// TODO Auto-generated method stub			
		}
		
		public Object[] getElements(Object inputElement) {
			return list.values().toArray();
		}
	}
	
	private class VariableUsageLabelProvider implements ILabelProvider {		
		public void removeListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub			
		}
		
		public boolean isLabelProperty(Object element, String property) {
			// TODO Auto-generated method stub
			return false;
		}
		
		public void dispose() {
			// TODO Auto-generated method stub
			
		}
		
		public void addListener(ILabelProviderListener listener) {
			// TODO Auto-generated method stub
			
		}
		
		public String getText(Object element) {
			if(element instanceof ApreproVariableData) {
				ApreproVariableData data = (ApreproVariableData)element;
				return data.getKey();
			}
			return "";
		}
		
		public Image getImage(Object element) {
			// TODO Auto-generated method stub
			return null;
		}
	};
}
