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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.ide.IDE;

import com.strikewire.snl.apc.GUIs.GuiUtils;
import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.settings.ISettingsViewPreferences;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class ImageSettingsEditor extends AbstractSettingsEditor<gov.sandia.dart.workflow.domain.Image> {

	private Image iconImage;
	private gov.sandia.dart.workflow.domain.Image image;
	private Text text;	
	/**
	 * Font and color stuff commented out for now.
	 */
	
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
//		Hyperlink hyperlink = toolkit.createHyperlink(row, "image file", SWT.NONE);
//		hyperlink.setToolTipText("Select link to open this file in an editor");
//		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
//			@Override
//			public void linkActivated(HyperlinkEvent e) {
//				IFile workflowFile = getWorkflowFile();
//				IContainer parent = workflowFile.getParent();
//				IResource resource = parent.findMember(image.getText(), false);
//				
//				if (resource instanceof IFile) {
//					IFile file = (IFile) resource;
//					try {
//						IDE.openEditor(PlatformUI.getWorkbench()
//								.getActiveWorkbenchWindow()
//								.getActivePage(), file);
//					} catch (PartInitException e1) {
//						WorkflowEditorPlugin.getDefault().logError("Error opening file", e1);
//					}				
//				} else {
//					Display.getCurrent().beep();
//				}				
//			}
//		});


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
		
/*
		updateBackgroundSwatch();
		updateForegroundSwatch();
		*/
	}

	@Override
	public void setNode(gov.sandia.dart.workflow.domain.Image node) {
		form.setText("Workflow Image Annotation");
		this.image = node;	
		text.setText(node.getText());
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
