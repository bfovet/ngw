/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings.nested;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

import com.strikewire.snl.apc.GUIs.MultipleInputDialog;

import gov.sandia.dart.workflow.domain.Conductor;
import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.configuration.ConductorType;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.editor.configuration.Prop.TYPE;
import gov.sandia.dart.workflow.editor.configuration.WorkflowTypesManager;
import gov.sandia.dart.workflow.editor.settings.NOWPSettingsEditorUtils;
import gov.sandia.dart.workflow.editor.settings.WFNodeSettingsEditor;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class NestedWorkflowNodeSettingsEditor extends WFNodeSettingsEditor {

	private ListViewer viewer;
	private Combo add;
	private Button delete;
	private Button up;
	private Button down;
	private Button edit;

	@Override
	public void setNode(WFNode node) {
		super.setNode(node);
		
	}
	
	@Override
	protected Control createPropertiesControl(Composite composite, Property p) {
		if (p.getName().equals("errorMode")) {
			return createComboControl(composite, new Prop(p), new String[] {"fail", "ignore", "retry"});
		}
  
		return super.createPropertiesControl(composite, p);
	}

	@Override
	protected void addExtraPropertiesControls(Composite parent, WFNode node) {
		toolkit.createLabel(parent, "Conductors");
		viewer = new ListViewer(parent, SWT.SINGLE | SWT.BORDER);
		List list = viewer.getList();
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		list.setLayoutData(gd);
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ConductorLabelProvider());
		viewer.setInput(node.getConductors());
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				int index = viewer.getList().getSelectionIndex();
				if (index > -1) {
					Conductor c = getNode().getConductors().get(index);
					editConductor(c);
				}
			}
		});
		list.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setEnablements();
			}
		});
		Composite buttonBar = new Composite(parent, SWT.NONE);
		buttonBar.setLayout(new GridLayout(5, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		buttonBar.setLayoutData(gd);
		
		Label label = new Label(buttonBar, SWT.NONE);
		label.setText("Add");
		add = new Combo(buttonBar, SWT.READ_ONLY);
		add.setItems(getConductorTypes());
		add.select(0);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addConductor(add.getText());
			}
		});
		

		delete = new Button(buttonBar, SWT.PUSH);
		delete.setText("Delete");
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = viewer.getList().getSelectionIndex();
				if (index > -1) {
					Conductor c = node.getConductors().get(index);					
					deleteConductor(c);
				}
			}
		});
		
		edit = new Button(buttonBar, SWT.PUSH);
		edit.setText("Edit");
		edit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = viewer.getList().getSelectionIndex();
				if (index > -1) {
					Conductor c = node.getConductors().get(index);					
					editConductor(c);
				}
			}
		});
		
		up = new Button(buttonBar, SWT.PUSH);
		up.setText("Up");
		up.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				upConductor();
			}
		});

		down = new Button(buttonBar, SWT.PUSH);
		down.setText("Down");
		down.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				downConductor();
			}
		});
	}

	protected void deleteConductor(Conductor c) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			public void doExecute() {
				IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(c);
				PictogramElement pe = fp.getPictogramElementForBusinessObject(c);
				DeleteContext dc = new DeleteContext(pe);
				dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
				fp.getDeleteFeature(dc).delete(dc);  
			}		
		});	
		viewer.setInput(node.get().getConductors());
		setEnablements();
	}

	protected void addConductor(String type) {
		TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
		domain.getCommandStack().execute(new RecordingCommand(domain) {
			@Override
			public void doExecute() {
				Conductor c = DomainFactory.eINSTANCE.createConductor();
				node.get().getConductors().add(c);
				node.get().eResource().getContents().add(c);
				PropertyUtils.setProperty(c, "conductor", type);
				viewer.setInput(node.get().getConductors());
				boolean result = editConductor(c);
				if (!result) {
					deleteConductor(c);
				}
			}		
		});	
		setEnablements();
	}  
	
	protected boolean editConductor(Conductor c) {
		MultipleInputDialog dialog = new MultipleInputDialog(Display.getCurrent().getActiveShell(), "Edit Conductor");
		String conductor = PropertyUtils.getProperty(c, "conductor");
		Collection<Prop> properties = getConductorProperties(conductor);
		for (Prop prop: properties) {
			String name = prop.getName();
			String value = PropertyUtils.getProperty(c, name);
			value = value == null ? "" : value;
			if (prop.getType() == TYPE.PARAMETER) {
				dialog.addComboField(name, value, getInputPortNames(), false);

			} else {
				dialog.addTextField(name, value, false);
			}
		}

		int result = dialog.open();
		try {
			if (result == Dialog.OK) {

				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						for (Prop prop: properties) {
							String name = prop.getName();
							String value = dialog.getStringValue(name);
							PropertyUtils.setProperty(c, name, value);
						}
					}
				});
				return true;
			} else {
				return false;
			}
		} finally {
			viewer.setInput(node.get().getConductors());
			setEnablements();
		}
	}
	
	private String[] getInputPortNames() {
		return getNode().getInputPorts().stream().map(e -> e.getName()).toArray(String[]::new);
	}

	protected void upConductor() {
		int index = viewer.getList().getSelectionIndex();
		if (index > 0) {
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				public void doExecute() {
					EList<Conductor> conductors = node.get().getConductors();
					conductors.move(index, index-1);
				}		
			});	
		}
		viewer.setInput(node.get().getConductors());
		viewer.getList().select(index-1);
		setEnablements();
	}

	protected void downConductor() {
		int index = viewer.getList().getSelectionIndex();
		int count = viewer.getList().getItemCount();
		if (index < count - 1) {
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
			domain.getCommandStack().execute(new RecordingCommand(domain) {
				@Override
				public void doExecute() {
					EList<Conductor> conductors = node.get().getConductors();
					conductors.move(index, index+1);
					viewer.getList().select(index+1);
				}		
			});	
		}
		viewer.setInput(node.get().getConductors());
		viewer.getList().select(index+1);
		setEnablements();
	}

	private void setEnablements() {
		int index = viewer.getList().getSelectionIndex();
		delete.setEnabled(index > -1);
		edit.setEnabled(index > -1);
		up.setEnabled(index > 0);
		down.setEnabled(index > -1 && index < viewer.getList().getItemCount() - 1);
	}
	
	private String[] getConductorTypes() {

		Set<String> set = WorkflowTypesManager.get().getConductorTypes().keySet();
		String[] array = set.toArray(new String[set.size()]);
		Arrays.sort(array);
		
		return array;
	}
	
	private Collection<Prop> getConductorProperties(String type) {

		ConductorType ct = WorkflowTypesManager.get().getConductorTypes().get(type);
		return ct.getProperties();
	}
}
