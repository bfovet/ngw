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
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.ResponseArc;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class ResponseArcSettingsEditor extends AbstractSettingsEditor<ResponseArc> {

	private final AtomicReference<ResponseArc> arc = new AtomicReference<>();
	private Image image;
	private Button expandWildcards, readInFile, trimData;
	private Label description;

	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg)
	{
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		ImageDescriptor desc = WorkflowEditorPlugin.getImageDescriptor("/icons/shapes.gif");
		image = desc!=null ? desc.createImage() : null;		
		form.setImage(image);
		
		form.getBody().setLayout(new GridLayout(4, false));
		description = toolkit.createLabel(form.getBody(), "", SWT.WRAP);
		GridData data = new GridData();
		data.horizontalSpan=4;
		data.grabExcessHorizontalSpace=false;
		data.widthHint=500;
		description.setLayoutData(data);
		
		Label label = toolkit.createLabel(form.getBody(), "Expand wildcards");
		label.setToolTipText("This connection transmits a file path with "
				+ "wildcards like '*' and '?', and the workflow engine "
				+ "should turn those into actual file paths.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalSpan=3;
		label.setLayoutData(data);
		
		expandWildcards = toolkit.createButton(form.getBody(), "", SWT.CHECK);
		expandWildcards.addSelectionListener(new CheckboxListener(PropertyUtils.EXPAND_WILDCARDS));

		label = toolkit.createLabel(form.getBody(), "Read in file");
		label.setToolTipText("This connection transmits a file path, and " +
				"the workflow engine should read the contents of the file " +
				"and transmit that data instead.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalSpan=3;
		label.setLayoutData(data);
		
		readInFile = toolkit.createButton(form.getBody(), "", SWT.CHECK);
		readInFile.addSelectionListener(new CheckboxListener(PropertyUtils.READ_IN_FILE));

		
		label = toolkit.createLabel(form.getBody(), "Trim whitespace");
		label.setToolTipText("This connection transmits text data. The workflow engine should remove any whitespace " +
		"(including newlines) at the beginning or end of the data.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalSpan=3;
		label.setLayoutData(data);
		
		trimData = toolkit.createButton(form.getBody(), "", SWT.CHECK);
		trimData.addSelectionListener(new CheckboxListener(PropertyUtils.TRIM_WHITESPACE));

	}
	
	protected void setEditorTitle() {
		form.setText("Connection");		
	}

	@Override
	public void setNode(ResponseArc node) {
		this.arc.set(node);
		description.setText(getDescription(node));
		expandWildcards.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.EXPAND_WILDCARDS)));
		readInFile.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.READ_IN_FILE)));
		trimData.setSelection(Boolean.valueOf(PropertyUtils.getProperty(node, PropertyUtils.TRIM_WHITESPACE)));

		setEditorTitle();
	}
	
	@Override
	public ResponseArc getNode() {
		return arc.get();
	}

	@Override
	public void dispose() {
		arc.set(null);
		if (image != null)
			image.dispose();
	}
	
	private String getDescription(ResponseArc arc) {
		OutputPort source = arc.getSource();
		String sourcePortName = source.getName();
		String sourceNodeName = source.getNode().getName();
		
		String targetName = arc.getTarget().getName();
				
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("This is a connection from the output named '%s' on the node named '%s' to the response named '%s'\n\n",
				sourcePortName, sourceNodeName, targetName));
		
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
						Diagram diagram = NOWPSettingsEditorUtils.getDiagramEditor(getNode()).getDiagramTypeProvider().getDiagram();
						IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(getNode());
						WFArcSettingsEditor.updateConnectionAppearance(diagram, fp, getNode());
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
}
