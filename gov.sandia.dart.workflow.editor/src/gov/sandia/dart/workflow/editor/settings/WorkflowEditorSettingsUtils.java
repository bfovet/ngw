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

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.beanutils.PropertyUtils;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class WorkflowEditorSettingsUtils {

	public static Text createTextField(Composite composite, String propertyName, FormToolkit toolkit, AtomicReference<? extends NamedObject> node) {
		Text theText = toolkit.createText(composite, "", SWT.BORDER);
		theText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		theText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					if(node.get() == null)
					{
						return;
					}
					String value = theText.getText().trim();
					String name = (String) PropertyUtils.getSimpleProperty(node.get(), propertyName);
					if (Objects.equals(value, name))
						return;
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
	        		domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							try {
								PropertyUtils.setSimpleProperty(node.get(), propertyName, value);
							} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
								WorkflowEditorPlugin.getDefault().logError("Can't find property in object", ex);
							}
						}
					});	
	
				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
				}
			}
	
	
		});
		return theText;
	}
}
