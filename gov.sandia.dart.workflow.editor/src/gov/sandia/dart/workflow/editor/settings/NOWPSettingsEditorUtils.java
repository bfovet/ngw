/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;

public class NOWPSettingsEditorUtils {

	public static WorkflowDiagramEditor getDiagramEditor(EObject bo) {
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		// String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId("gov.sandia.dart.workflow.editor.WorkflowDiagramTypeProvider");
		String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId("dartWorkflow");		
		DiagramEditorInput input = new DiagramEditorInput(bo.eResource().getURI(), providerId);
		IEditorPart part = page.findEditor(input);
		return (WorkflowDiagramEditor) part;
	}

	public static IFeatureProvider getFeatureProvider(EObject bo) {
		return getDiagramEditor(bo).getDiagramTypeProvider().getFeatureProvider();
	}

	static Button addButtons(FormToolkit toolkit, Composite comp, Object bo, Runnable addFeature, Runnable deleteFeature) {
		Composite row = new Composite(comp, SWT.NONE);
		row.setBackground(row.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	
		row.setLayout(new GridLayout(2, true));
		row.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,false));
		Button add = toolkit.createButton(row, "Add", SWT.PUSH);
		add.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(bo);
        		domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						addFeature.run();
					}
				});
			}
		});
		Button delete = toolkit.createButton(row, "Delete", SWT.PUSH);		
		delete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(bo);
        		domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						deleteFeature.run();
					}
				});
        	}
		});	
		delete.setEnabled(false);
		return delete;
	}

	final static String UNTITLED = "Untitled";
	public static String createUniqueName(EList<? extends NamedObject> ports) {
		return createUniqueName(UNTITLED, ports);
	}

	public static String createUniqueName(String suggestion, EList<? extends NamedObject> ports) {
		int index = 2;
		String name = suggestion;
		Set<String> names = new HashSet<>();
		ports.forEach(p -> names.add(p.getName()));
		
		while (true) {
			if (!names.contains(name))
				return name;
			name = suggestion + "-" + index++;
		}
	}

}
