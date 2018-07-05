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

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.workflow.domain.Response;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class ResponseSettingsEditor extends AbstractSettingsEditor<Response> {

	private final AtomicReference<Response> response = new AtomicReference<>();
	private Image image;
	private Text name, type;

	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		image = desc!=null ? desc.createImage() : null;		
		form.setImage(image);

		form.getBody().setLayout(new GridLayout(2, false));
		toolkit.createLabel(form.getBody(), "Name");
		name = WorkflowEditorSettingsUtils.createTextField(form.getBody(), "name", toolkit, response);
		name.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				setEditorTitle();
			}
		});
		toolkit.createLabel(form.getBody(), "Type");
		type = WorkflowEditorSettingsUtils.createTextField(form.getBody(), "type", toolkit, response);
	}
	
	
	protected void setEditorTitle() {
		form.setText("Response '" + name.getText() + "'");		
	}

	@Override
	public void setNode(Response node) {
		this.response.set(node);		
		name.setText(node.getName());
		type.setText(node.getType());
		setEditorTitle();
	}

	@Override
	public Response getNode() {
		return response.get();
	}

	@Override
	public void dispose() {
		response.set(null);
		if (image != null)
			image.dispose();
	}

}
