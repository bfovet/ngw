/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.actions;

import gov.sandia.dart.aprepro.ApreproPlugin;
import gov.sandia.dart.aprepro.util.ApreproUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

public class InsertParameterAction extends Action{
	
	private IParameterUpdater updater_;
	private IParameterSource source_;
	
	public static final String INSERT_PARAMETER_ICON = "icons/tag_blue.png";

	public InsertParameterAction(IParameterUpdater updater, IParameterSource source){
		super("Use existing parameter", ApreproPlugin.imageDescriptorFromPlugin("gov.sandia.dart.aprepro", INSERT_PARAMETER_ICON));
		updater_ = updater;
		source_ = source;
	}

	@Override
	public void run() {
		
		updater_.initialize();

		
		Map<String, String> parameterMap = source_.getParameters();
		
		if(parameterMap == null){
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No parameters", "Unable to get parameters.");
			return;			
		}
		
		if(parameterMap.size() == 0)
		{
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "No parameters", "There are no parameters defined.");
			return;
		} 
				
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		TableDialog dialog = new TableDialog(shell, parameterMap);
		
		
		int result = dialog.open();

		if (result != ListDialog.OK) {
			return;
		}

		String dialogResult = dialog.getResult();

		if(dialogResult == null){
			return;
		}
		
		updater_.setSelectedText(ApreproUtil.constructApreproString(dialogResult));

		return;
	}
	
	private class TableDialog extends Dialog{
		List<Entry<String, String>> parameterList_;
		
		Table parametersTable_;
		
		int selectedIndex_;
		
		protected TableDialog(Shell parentShell, Map<String, String> parameters) {
			super(parentShell);
			setShellStyle(parentShell.getStyle() | SWT.RESIZE);
			parameterList_ = new ArrayList<Entry<String,String>>(parameters.entrySet());
			
			Collections.sort(parameterList_, new Comparator<Entry<String,String>>() {

				@Override
				public int compare(Entry<String, String> o1,
						Entry<String, String> o2) {
					return o1.getKey().compareTo(o2.getKey());
				}
			});

		}
		
		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
		    shell.setText("Use existing parameter");
		}
		
		@Override
		protected Control createDialogArea(Composite parent){
//			Composite composite = new Composite(parent, SWT.NONE);
//			GridData gd = new GridData(GridData.FILL_BOTH);
//			gd.grabExcessHorizontalSpace = true;
//			gd.grabExcessVerticalSpace = true;
//			composite.setLayout(new GridLayout());

			Composite composite = (Composite) super.createDialogArea(parent);
			
			
			Label label = new Label(composite, SWT.NONE);
			label.setText("Select an existing parameter to use for this value");
			
			parametersTable_ = new Table(composite, SWT.BORDER |  SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
			parametersTable_.setLinesVisible(true);
			parametersTable_.setHeaderVisible(true);
			GridData gd = new GridData(GridData.FILL_BOTH);
			gd.grabExcessVerticalSpace = true;
			gd.verticalAlignment = SWT.FILL;
			gd.widthHint = 100;
			parametersTable_.setLayoutData(gd);

			TableViewer viewer = new TableViewer(parametersTable_);
			gd = new GridData();
			gd.grabExcessHorizontalSpace = true;
			gd.horizontalAlignment = SWT.FILL;
			gd.grabExcessVerticalSpace = true;
			gd.verticalAlignment = SWT.FILL;
			viewer.getControl().setLayoutData(gd);

			viewer.setContentProvider(ArrayContentProvider.getInstance());

			TableViewerColumn columnKey = buildTableColumn(viewer, "Name", 100);
			columnKey.setLabelProvider(new ColumnLabelProvider(){
				@Override
				public String getText(Object element){
					Entry<String, String> parameter = (Entry<String,String>) element;
					String name = parameter.getKey();
					return ApreproUtil.constructApreproString(name);
				}
			});

			TableViewerColumn columnValue = buildTableColumn(viewer, "Value", 100);
			columnValue.setLabelProvider(new ColumnLabelProvider(){
				@Override
				public String getText(Object element){
					Entry<String, String> parameter = (Entry<String,String>) element;
					String value = parameter.getValue();
					return value;
				}
			});	
			viewer.setInput(parameterList_);
			
			return composite;
		}
		
		private TableViewerColumn buildTableColumn(TableViewer viewer, String title, int size) {
			TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
			TableColumn column = viewerColumn.getColumn();
			column.setText(title);
			column.setWidth(size);
			column.setResizable(true);
			column.setMoveable(false);
			return viewerColumn;
		}
		
		@Override
		protected void okPressed(){
			selectedIndex_ = parametersTable_.getSelectionIndex();
			
			if(selectedIndex_ < 0){
				MessageDialog.openError(getShell(), "No Selection", "No parameter has been selected.");				
			}else{
				close();
			}
		}
		
		public String getResult(){
			if(selectedIndex_ >= 0){
				return parameterList_.get(selectedIndex_).getKey();
			}
			
			return null;
		}
	}

}
