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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.styles.Color;
import org.eclipse.graphiti.mm.algorithms.styles.LineStyle;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.util.ColorConstant;
import org.eclipse.graphiti.util.IColorConstant;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.Section;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.workflow.domain.Arc;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.editor.WorkflowDiagramEditor;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class WFArcSettingsEditor extends AbstractSettingsEditor<WFArc> {

	private final AtomicReference<WFArc> arc = new AtomicReference<>();
	private Image image;
	private Button linkToTarget, copyToTarget, expandWildcards, readInFile, notALocalPath, trimData;
	private Text newFileName;
	private Label description;
	private Label filenameLabel;

	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		image = desc!=null ? desc.createImage() : null;		
		form.setImage(image);
		
		form.getBody().setLayout(new GridLayout(2, false));
		description = toolkit.createLabel(form.getBody(), "", SWT.WRAP);
		GridData data = new GridData();
		data.horizontalSpan=2;
		data.grabExcessHorizontalSpace=false;
		data.widthHint=400;
		description.setLayoutData(data);
		
		Section linkingGroup = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		linkingGroup.setText("Copying, linking, and renaming files");
		data = new GridData();
		data.horizontalSpan=2;
		data.grabExcessHorizontalSpace=true;
		data.horizontalAlignment=GridData.FILL;
		data.widthHint=400;
		linkingGroup.setLayoutData(data);
		linkingGroup.setExpanded(true);
		
		Composite linkingClient = toolkit.createComposite(linkingGroup, SWT.WRAP);
	    linkingClient.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		linkingClient.setLayout(new GridLayout(2, false));
		linkingGroup.setClient(linkingClient);
		
		Label label = toolkit.createLabel(linkingClient, "Link file to target", SWT.RIGHT);
		label.setToolTipText("This connection transmits a file path, and "
				+ "the workflow engine should create a link to the file into the "
				+ "target node's working directory. The port data will be the path "
				+ "to the link");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		//data.horizontalAlignment = GridData.END;
		label.setLayoutData(data);
		
		linkToTarget = toolkit.createButton(linkingClient, "", SWT.CHECK);
		linkToTarget.addSelectionListener(new CheckboxListener(PropertyUtils.LINK_INCOMING_FILE_TO_TARGET));
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalAlignment = GridData.END;
		linkToTarget.setLayoutData(data);
		
		label = toolkit.createLabel(linkingClient, "Copy file to target");
		label.setToolTipText("This connection transmits a file path, and "
				+ "the workflow engine should copy the file into the "
				+ "target node's working directory. The port data will be the path " 
				+ "to the copy");
		data = new GridData();	
		data.grabExcessHorizontalSpace=false;
		label.setLayoutData(data);
		
		copyToTarget = toolkit.createButton(linkingClient, "", SWT.CHECK);
		copyToTarget.addSelectionListener(new CheckboxListener(PropertyUtils.COPY_INCOMING_FILE_TO_TARGET));
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalAlignment = GridData.END;
		copyToTarget.setLayoutData(data);

		filenameLabel = toolkit.createLabel(linkingClient, "New filename");
		filenameLabel.setToolTipText("This connection transmits a file path, and you have "
				+ "requested that the file be copied or linked. The copy or link will be given "
				+ "this new filename");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		//data.horizontalAlignment = GridData.END;
		filenameLabel.setLayoutData(data);
		
		newFileName = toolkit.createText(linkingClient, "");
		newFileName.addModifyListener(new TextListener(PropertyUtils.NEW_FILE_NAME));	
		data = new GridData();
		data.grabExcessHorizontalSpace=true;
		data.widthHint=300;
		newFileName.setLayoutData(data);
		
		Section otherGroup = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		otherGroup.setText("Other connection actions");
		data = new GridData();
		data.horizontalSpan=2;
		data.grabExcessHorizontalSpace=true;
		data.widthHint=400;
		data.horizontalAlignment=GridData.FILL;

		otherGroup.setLayoutData(data);
		otherGroup.setExpanded(true);
		
		Composite otherClient = toolkit.createComposite(otherGroup, SWT.WRAP);
		otherClient.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		otherClient.setLayout(new GridLayout(2, false));
		otherGroup.setClient(otherClient);
		
		
		
		label = toolkit.createLabel(otherClient, "Expand wildcards");
		label.setToolTipText("This connection transmits a file path with "
				+ "wildcards like '*' and '?', and the workflow engine "
				+ "should turn those into actual file paths.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		label.setLayoutData(data);
		
		expandWildcards = toolkit.createButton(otherClient, "", SWT.CHECK);
		expandWildcards.addSelectionListener(new CheckboxListener(PropertyUtils.EXPAND_WILDCARDS));

		label = toolkit.createLabel(otherClient, "Read in file");
		label.setToolTipText("This connection transmits a file path, and " +
				"the workflow engine should read the contents of the file " +
				"and transmit that data instead.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		label.setLayoutData(data);
		
		readInFile = toolkit.createButton(otherClient, "", SWT.CHECK);
		readInFile.addSelectionListener(new CheckboxListener(PropertyUtils.READ_IN_FILE));

		label = toolkit.createLabel(otherClient, "Not a local path");
		label.setToolTipText("This connection transmits data that may look like a file path, but " +
				"the workflow engine should not interpret it as a path in this workflow at runtime, or attempt " +
				"to copy, link, or transfer it automatically.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		label.setLayoutData(data);
		
		notALocalPath = toolkit.createButton(otherClient, "", SWT.CHECK);
		notALocalPath.addSelectionListener(new CheckboxListener(PropertyUtils.NOT_A_LOCAL_PATH));

		label = toolkit.createLabel(otherClient, "Trim whitespace");
		label.setToolTipText("This connection transmits text data. The workflow engine should remove any whitespace " +
		"(including newlines) at the beginning or end of the data.");
		data = new GridData();
		data.grabExcessHorizontalSpace=true;
		label.setLayoutData(data);
		
		trimData = toolkit.createButton(otherClient, "", SWT.CHECK);
		trimData.addSelectionListener(new CheckboxListener(PropertyUtils.TRIM_WHITESPACE));	
	}
	
	protected void validateProperties(){
		IStatus status = Status.OK_STATUS;
		if (shouldBothCopyAndLinkFile()) {
			String msg = "Can't both copy and link file";
			status = WorkflowEditorPlugin.getDefault().mergeStatus(new Status(Status.ERROR, WorkflowEditorPlugin.PLUGIN_ID, msg), status);
		}	
		if (!shouldCopyOrLinkFile() && newFilenameIsSet() ) {
			String msg = "New filename will be ignored";
			status = WorkflowEditorPlugin.getDefault().mergeStatus(new Status(Status.WARNING, WorkflowEditorPlugin.PLUGIN_ID, msg), status);
		}
		messageView.setMessageFor(status);
	}

	private boolean newFilenameIsSet() {
		return newFileName != null && StringUtils.isNotBlank(newFileName.getText());
	}

	private boolean shouldBothCopyAndLinkFile() {
		return linkToTarget != null  && linkToTarget.getSelection() && copyToTarget != null && copyToTarget.getSelection();
	}

	private boolean shouldCopyOrLinkFile() {
		return ( linkToTarget != null  && linkToTarget.getSelection()) || (copyToTarget != null && copyToTarget.getSelection());
	}
	
	protected void setEditorTitle() {
		form.setText("Connection");		
	}

	@Override
	public void setNode(WFArc node) {
		this.arc.set(node);
		description.setText(getDescription(node));
		linkToTarget.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.LINK_INCOMING_FILE_TO_TARGET)));
		expandWildcards.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.EXPAND_WILDCARDS)));
		readInFile.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.READ_IN_FILE)));
		copyToTarget.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.COPY_INCOMING_FILE_TO_TARGET)));
		notALocalPath.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.NOT_A_LOCAL_PATH)));
		trimData.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.TRIM_WHITESPACE)));
		String text = PropertyUtils.getProperty(node, PropertyUtils.NEW_FILE_NAME);
		if (text != null)
			newFileName.setText(text);
		updateEnablements();
		validateProperties();
		setEditorTitle();
	}
	
	@Override
	public WFArc getNode() {
		return arc.get();
	}

	@Override
	public void dispose() {
		arc.set(null);
		if (image != null)
			image.dispose();
	}
	
	private String getDescription(WFArc arc) {
		OutputPort source = arc.getSource();
		String sourcePortName = source.getName();
		String sourceNodeName = source.getNode().getName();
		
		InputPort target = arc.getTarget();
		String targetPortName = target.getName();
		String targetNodeName = target.getNode().getName();
				
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("This is a connection from the output named '%s' on the node named '%s' to the input named '%s' on the node named '%s'\n\n",
				sourcePortName, sourceNodeName, targetPortName, targetNodeName));
		
		return builder.toString();
	}

	private class CheckboxListener implements SelectionListener {
		
		private String propertyName;
		CheckboxListener(String propertyName) {
			this.propertyName = propertyName;
			
		}
		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			try {
				if(getNode() == null) {
					return;
				}
				
				boolean value = ((Button) e.widget).getSelection();
				String current = PropertyUtils.getProperty(getNode(), propertyName);
				if (Objects.equals(value, Boolean.valueOf(current)))
					return;

				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(getNode());
        				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						PropertyUtils.setProperty(getNode(), propertyName, String.valueOf(value));	
						WorkflowDiagramEditor diagramEditor = NOWPSettingsEditorUtils.getDiagramEditor(getNode());
						if (diagramEditor != null) {
							Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
							IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(getNode());
							updateConnectionAppearance(diagram, fp, getNode());
						}
						updateEnablements();
						validateProperties();
					}

				});

			} catch (Exception e2) {
				WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
			}
		}


		@Override
		public void widgetSelected(SelectionEvent e) {
			widgetDefaultSelected(e);				
		}
	}
	
	private class TextListener implements ModifyListener {
		
		private String propertyName;
		TextListener(String propertyName) {
			this.propertyName = propertyName;
			
		}
		@Override
		public void modifyText(ModifyEvent e) {
			try {
				if(getNode() == null) {
					return;
				}
				
				String text = ((Text) e.widget).getText();
				String current = PropertyUtils.getProperty(getNode(), propertyName);
				if (Objects.equals(text, current))
					return;

				TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(getNode());
        				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						PropertyUtils.setProperty(getNode(), propertyName, text);	
						WorkflowDiagramEditor diagramEditor = NOWPSettingsEditorUtils.getDiagramEditor(getNode());
						if (diagramEditor != null) {
							Diagram diagram = diagramEditor.getDiagramTypeProvider().getDiagram();
							IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(getNode());
							updateConnectionAppearance(diagram, fp, getNode());
						}
						updateEnablements();
						validateProperties();
					}
				});

			} catch (Exception e2) {
				WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
			}
		}
	}

	private void updateEnablements() {
		boolean condition = linkToTarget.getSelection() || copyToTarget.getSelection();
		newFileName.setEnabled(condition);
		filenameLabel.setEnabled(condition);
	}
	
	public static void updateConnectionAppearance(Diagram diagram, IFeatureProvider fp ,Arc node) {
		boolean link = Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.LINK_INCOMING_FILE_TO_TARGET));
		boolean copy = Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.COPY_INCOMING_FILE_TO_TARGET));
		boolean read = Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.READ_IN_FILE));
		boolean expand =  Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.EXPAND_WILDCARDS));
		
		PictogramElement pe = fp.getPictogramElementForBusinessObject(node);
		GraphicsAlgorithm ga = pe.getGraphicsAlgorithm();
		ga.setLineStyle(expand ? LineStyle.DASH : read ? LineStyle.DOT : LineStyle.SOLID);
		ga.setLineWidth(expand ? 2 : read ? 2 : 1);
		Color color = manageColor(diagram, ColorConstant.DARK_GRAY);	
		if (copy)
			color = manageColor(diagram, ColorConstant.DARK_GREEN);
		else if (link)
			color = manageColor(diagram, ColorConstant.ORANGE);
		ga.setForeground(color);		
	}
	
	protected static Color manageColor(Diagram diagram, IColorConstant colorConstant) {
		return Graphiti.getGaService().manageColor(diagram, colorConstant);
	}
}
