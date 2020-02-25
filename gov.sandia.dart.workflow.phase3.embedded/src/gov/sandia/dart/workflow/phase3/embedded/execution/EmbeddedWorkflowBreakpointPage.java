/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.util.List;
import java.util.Set;

//import org.eclipse.core.resources.IFile;
//import org.eclipse.emf.transaction.RecordingCommand;
//import org.eclipse.emf.transaction.TransactionalEditingDomain;
//import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

public class EmbeddedWorkflowBreakpointPage extends WizardPage {
	private Composite container;
	private TableViewer tableViewer;
	private Set<String> names;
	

	public EmbeddedWorkflowBreakpointPage(Set<String> names) {
		super("Specify workflow breakpoints");
		setTitle("Breakpoints");
		setDescription("Specify nodes to set breakpoints at");
		this.names = names;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		tableViewer = new TableViewer(container);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TableViewerColumn vcolumn1 = new TableViewerColumn(tableViewer, SWT.LEFT);		
		TableColumn column1 = vcolumn1.getColumn();	
		column1.setText("Name");
		column1.setWidth(80);
		
		tableViewer.setContentProvider(new ArrayContentProvider());	
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.setLabelProvider(new ITableLabelProvider() {
			@Override
			public void addListener(ILabelProviderListener listener) {}

			@Override
			public void dispose() {}

			@Override
			public boolean isLabelProperty(Object element, String property) { return false; }

			@Override
			public void removeListener(ILabelProviderListener listener) {}

			@Override
			public Image getColumnImage(Object element, int columnIndex) { return null; }

			@Override
			public String getColumnText(Object element, int columnIndex) {
				return String.valueOf(element);
			}
		});
		tableViewer.setInput(names);
		setControl(container);
		setPageComplete(true);
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible)
			tableViewer.refresh();
	}
	
	@SuppressWarnings("unchecked")
	List<String> getSelectedNodes() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		return selection.toList();
	}
}
