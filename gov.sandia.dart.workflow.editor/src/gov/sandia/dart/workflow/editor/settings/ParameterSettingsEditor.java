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

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.ParameterUtils;

public class ParameterSettingsEditor extends AbstractSettingsEditor<WFNode> {

	private final AtomicReference<WFNode> param = new AtomicReference<>();
	private Image image;
	private Text name, type, value;

	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		image = desc!=null ? desc.createImage() : null;		
		form.setImage(image);

		form.getBody().setLayout(new GridLayout(2, false));
		toolkit.createLabel(form.getBody(), "Name");		
		name = toolkit.createText(form.getBody(), "", SWT.BORDER);
		name.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));	
		name.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				final WFNode node = getNode();
				if (node != null && !Objects.equals(node.getName(), name.getText())) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(getNode());
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							node.setName(name.getText());
						}

					});
					setEditorTitle();
				}
			}
		});

		toolkit.createLabel(form.getBody(), "Type");
		type = toolkit.createText(form.getBody(), "", SWT.BORDER);
		type.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));	
		type.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				final WFNode node = getNode();
				if (node != null && !Objects.equals(ParameterUtils.getType(node), type.getText())) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(getNode());
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							ParameterUtils.setType(node, type.getText());
						}

					});
				}
			}
		});

		toolkit.createLabel(form.getBody(), "Default Value");		
		value = toolkit.createText(form.getBody(), "", SWT.BORDER);
		value.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		value.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				final WFNode node = getNode();
				if (node != null && !Objects.equals(ParameterUtils.getValue(node), value.getText())) {
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(getNode());
					domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							ParameterUtils.setValue(node, value.getText());
						}

					});
				}
			}
		});
	}
	
	protected void setEditorTitle() {
		form.setText("Parameter '" + name.getText() + "'");		
	}

	@Override
	public void setNode(WFNode node) {
		this.param.set(node);
		name.setText(node.getName());
		type.setText(ParameterUtils.getType(node));
		String v = ParameterUtils.getValue(node);
		if (v != null)
			value.setText(v);
		setEditorTitle();
	}

	@Override
	public WFNode getNode() {
		return param.get();
	}

	@Override
	public void dispose() {
		param.set(null);
		if (image != null)
			image.dispose();
	}

}
