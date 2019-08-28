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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.GUIs.GuiUtils;
import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;

public class ImageSettingsEditor extends AbstractSettingsEditor<gov.sandia.dart.workflow.domain.Image> {

	private Image iconImage;
	private gov.sandia.dart.workflow.domain.Image image;
	private Text text;	
	private Button zoomToFit, drawBorder;
	
	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		iconImage = desc!=null ? desc.createImage() : null;		
		form.setImage(iconImage);
		
		Composite row = form.getBody();
						
		row.setLayout(new GridLayout(3, false));
		row.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		toolkit.createLabel(row, "Image file");


		text = toolkit.createText(row, "", SWT.SINGLE);
		if (image != null)
			text.setText(image.getText());
		text.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));		
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					if (image == null)
						return;
					String value = text.getText();
					if (Objects.equals(value, image.getText()))
						return;

					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(image);
	        				domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							image.setText(value);
						}
					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
				}
			}
		});
		
		Button browse = new Button(row, SWT.PUSH);
		browse.setText("Browse");
		browse.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				IFile file = getWorkflowFile();
				IPath home = file.getParent().getLocation();
				IPath path = GuiUtils.openFileBrowser(row.getShell(), home);
				if (path != null && !path.toString().trim().equals(""))
					text.setText(path.makeRelativeTo(home).toString());
			}		
		});
		
		zoomToFit = createScaleControl(form.getBody());
		drawBorder = createDrawBordersControl(form.getBody());
		
	}

	
	protected Button createScaleControl(Composite composite) {
		String propertyName = "Scale to fit";		

		toolkit.createLabel(composite, propertyName);

		Button checkbox = new Button(composite, SWT.CHECK);
		checkbox.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		checkbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					if (image == null)
					{
						return;
					}
					Boolean value = checkbox.getSelection();
					Boolean current = image.isZoomToFit();
					if (Objects.equals(value, current))
						return;
					
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(image);
	        			domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() { 
							image.setZoomToFit(value);
						}
					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't process zoom flag ", e2);
				}
			}


		});
		GridData data = new GridData();
		data.horizontalSpan = 2;
		checkbox.setLayoutData(data);
		return checkbox;
	}	
	
	protected Button createDrawBordersControl(Composite composite) {
		String propertyName = "Draw borders";		

		toolkit.createLabel(composite, propertyName);

		Button checkbox = new Button(composite, SWT.CHECK);
		checkbox.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		checkbox.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					if (image == null)
					{
						return;
					}
					Boolean value = checkbox.getSelection();
					Boolean current = image.isDrawBorder();
					if (Objects.equals(value, current))
						return;
					
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(image);
	        			domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() { 
							image.setDrawBorder(value);
						}
					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't process draw flag ", e2);
				}
			}


		});
		GridData data = new GridData();
		data.horizontalSpan = 2;
		checkbox.setLayoutData(data);
		return checkbox;
	}	


	@Override
	public void setNode(gov.sandia.dart.workflow.domain.Image node) {
		form.setText("Workflow Image Annotation");
		this.image = node;	
		text.setText(node.getText());
		zoomToFit.setSelection(node.isZoomToFit());
		drawBorder.setSelection(node.isDrawBorder());

	}

	@Override
	public gov.sandia.dart.workflow.domain.Image getNode() {
		return image;
	}
	
	private IFile getWorkflowFile() {
		URI uri = getNode().eResource().getURI();
		String pathString = uri.toPlatformString(true);
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		return file;
	}

	@Override
	public void dispose() {
		image = null;
		if (iconImage != null)
			iconImage.dispose();
	}
}
