/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.NamedObjectWithProperties;
import gov.sandia.dart.workflow.domain.Property;

public class PropertiesDialog extends Dialog {

	private static final int ADD = 1234;
	private static final int DELETE = 1235;
	private NamedObjectWithProperties node;
	private TableViewer propertiesTable_;
	private Button deleteButton;

	public PropertiesDialog(Shell parentShell, NamedObjectWithProperties node) {
		super(parentShell);
		this.node = node;
		setBlockOnOpen(true);
	}

	@Override
	protected void configureShell(Shell shell) {
	      super.configureShell(shell);
	      shell.setText("Properties for " + node.getName());
	   }
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite) super.createDialogArea(parent);
		propertiesTable_ = makePropertiesTable(node, dialogArea, new PropertiesContentProvider());		
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL,true, true);
		gd.heightHint = 140;
		
		propertiesTable_.getControl().setLayoutData(gd);
		
		return this.dialogArea;		
	}
	
	private TableViewer makePropertiesTable(NamedObjectWithProperties node, Composite comp, IStructuredContentProvider provider) {
		TableViewer viewer = new TableViewer(comp,SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	
		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		TableViewerColumn vcolumn1 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column1 = vcolumn1.getColumn();	
		column1.setText("Name");
		column1.setWidth(100);
			
		TableViewerColumn vcolumn2 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column2 = vcolumn2.getColumn();	
		column2.setText("Type");
		column2.setWidth(100);
			
		TableViewerColumn vcolumn3 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column3 = vcolumn3.getColumn();	
		column3.setText("Value");
		column3.setWidth(100);

		TableViewerColumn vcolumn4 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column4 = vcolumn4.getColumn();	
		column4.setText("Hidden");
		column4.setWidth(75);

		viewer.setLabelProvider(new PropertiesLabelProvider());
		viewer.setContentProvider(provider);
		viewer.setInput(node);
		vcolumn1.setEditingSupport(new PropertiesEditingSupport(viewer, 0));
		vcolumn2.setEditingSupport(new PropertiesEditingSupport(viewer, 1));
		vcolumn3.setEditingSupport(new PropertiesEditingSupport(viewer, 2));
		vcolumn4.setEditingSupport(new PropertiesEditingSupport(viewer, 3));

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				deleteButton.setEnabled(!viewer.getSelection().isEmpty());								
			}			
		});
		
		return viewer;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		createButton(parent, ADD, "Add", false);
		deleteButton = createButton(parent, DELETE, "Delete", false);
		deleteButton.setEnabled(false);

	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == DELETE) {
			ISelection s = propertiesTable_.getSelection();
			if (s instanceof IStructuredSelection) {
				Object object = ((IStructuredSelection) s).getFirstElement();
				if (object instanceof Property) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node);
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							node.getProperties().remove(object);
							EList<EObject> contents = NOWPSettingsEditorUtils.getDiagramEditor(node).getDiagramTypeProvider().getDiagram().eResource().getContents();
							contents.remove(object);
						}
					});
				}
			}
		} else if (buttonId == ADD) {		
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node);
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				public void doExecute() {

					Property property = DomainFactory.eINSTANCE.createProperty();
					property.setName(NOWPSettingsEditorUtils.createUniqueName(node.getProperties()));
					property.setType("default");
					node.getProperties().add(property);
					EList<EObject> contents = NOWPSettingsEditorUtils.getDiagramEditor(node).getDiagramTypeProvider().getDiagram().eResource().getContents();
					contents.add(property);
				}});
		} else {
			super.buttonPressed(buttonId);
		}
	}

}
