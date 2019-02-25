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

import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.WFArc;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.util.PropertyUtils;

public class WFArcSettingsEditor extends AbstractSettingsEditor<WFArc> {

	private final AtomicReference<WFArc> arc = new AtomicReference<>();
	private Image image;
	private Button linkToTarget, copyToTarget, expandWildcards, readInFile, notALocalPath;
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
		
		Label label = toolkit.createLabel(form.getBody(), "Link file to target");
		label.setToolTipText("This connection transmits a file path, and "
				+ "the workflow engine should create a link to the file into the "
				+ "target node's working directory. The port data will be the path "
				+ "to the link");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalSpan=3;
		label.setLayoutData(data);
		
		linkToTarget = toolkit.createButton(form.getBody(), "", SWT.CHECK);
		linkToTarget.addSelectionListener(new CheckboxListener(PropertyUtils.LINK_INCOMING_FILE_TO_TARGET));

		
		label = toolkit.createLabel(form.getBody(), "Copy file to target");
		label.setToolTipText("This connection transmits a file path, and "
				+ "the workflow engine should copy the file into the "
				+ "target node's working directory. The port data will be the path " 
				+ "to the copy");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalSpan=3;
		label.setLayoutData(data);
		
		copyToTarget = toolkit.createButton(form.getBody(), "", SWT.CHECK);
		copyToTarget.addSelectionListener(new CheckboxListener(PropertyUtils.COPY_INCOMING_FILE_TO_TARGET));

		label = toolkit.createLabel(form.getBody(), "Expand wildcards");
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

		label = toolkit.createLabel(form.getBody(), "Not a local path");
		label.setToolTipText("This connection transmits data that may look like a file path, but " +
				"the workflow engine should not interpret it as a path in this workflow at runtime, or attempt " +
				"to copy, link, or transfer it automatically.");
		data = new GridData();
		data.grabExcessHorizontalSpace=false;
		data.horizontalSpan=3;
		label.setLayoutData(data);
		
		notALocalPath = toolkit.createButton(form.getBody(), "", SWT.CHECK);
		notALocalPath.addSelectionListener(new CheckboxListener(PropertyUtils.NOT_A_LOCAL_PATH));

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
						Diagram diagram = NOWPSettingsEditorUtils.getDiagramEditor(getNode()).getDiagramTypeProvider().getDiagram();
						IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(getNode());
						updateConnectionAppearance(diagram, fp, getNode());
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
	
	public static void updateConnectionAppearance(Diagram diagram, IFeatureProvider fp ,WFArc node) {
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
