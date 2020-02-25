/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.settings;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import com.thoughtworks.xstream.XStream;

import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.util.PropertyUtils;

/**
 * Use this node settings editor to override the default controls with
 * your own custom {@link Composite} controls.  Model data from
 * custom controls should be stored using the {@code NODE_PROP_XML_MODEL_DATA}
 * property, which, by convention, is an {@link XStream}-serialized {@link String}.
 * 
 * @author Elliott Ridgway
 *
 */
public abstract class CustomNodeSettingsEditor extends WFNodeSettingsEditor {

	////////////
	// FIELDS //
	////////////
	
	public static final String NODE_PROP_XML_MODEL_DATA = "xmlModelData";
	
	private static final String LABEL_WORKFLOW_GROUP = "Workflow Properties";
	private static final String LABEL_EDIT_PROPERTIES = "Edit properties";
	
	protected static final String ERROR_NO_NODE_AVAILABLE = "No node available";
	
	////////////////////////////////////////////////
	// NODE MODEL SERIALIZATION / DESERIALIZATION //
	////////////////////////////////////////////////
	
	/**
	 * Serializes an {@link Object} to the {@code NODE_PROP_XML_MODEL_DATA} property
	 * using {@link XStream}.
	 * 
	 * @param model The Object to serialize.
	 * @param xstream The XStream instance to use.  Should be securely initialized
	 * before calling this method.
	 */
	protected void serializeModel(Object model, XStream xstream) {
		String szObjStr = xstream.toXML(model);		
		if(!StringUtils.isBlank(szObjStr)) {
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(getNode());
			if(domain != null) {
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					public void doExecute() {
						PropertyUtils.setProperty(getNode(), NODE_PROP_XML_MODEL_DATA, szObjStr);
						validateProperties();
					}
				});
			}
		}
	}
	
	/**
	 * Deserializes the {@link Object} stored in the {@code NODE_PROP_XML_MODEL_DATA} property.
	 * @param xstream The XStream instance to use.  Should be securely initialized
	 * before calling this method.
	 * @return The deserialized Object.
	 * @throws IllegalStateException Thrown if we could not retrieve the {@link WFNode} for
	 * this editor.
	 */
	protected Object deserializeModel(XStream xstream) throws IllegalStateException {		
		WFNode theNode = getNode();
		if(theNode != null) {
			String szObjStr = PropertyUtils.getProperty(theNode, NODE_PROP_XML_MODEL_DATA);
			if(!StringUtils.isBlank(szObjStr)) {
				Object deSzObj = xstream.fromXML(szObjStr);
				return deSzObj;
			}
		} else {
			throw new IllegalStateException(ERROR_NO_NODE_AVAILABLE);
		}
		return null;
	}
	
	//////////////////////
	// CONTROL CREATION //
	//////////////////////
	
	/**
	 * Creates a {@link Group} containing controls for editing standard workflow node properties.
	 * @param parent The parent {@link Composite}.
	 * @param node The {@link WFNode} property holder.
	 */
	protected void createWorkflowSettingsGroup(Composite parent, WFNode node) {
		Group workflowSettingsGroup = new Group(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(5, 5).applyTo(workflowSettingsGroup);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(workflowSettingsGroup);
		workflowSettingsGroup.setText(LABEL_WORKFLOW_GROUP);
		
		String async = PropertyUtils.getProperty(node, PropertyUtils.ASYNC);
		createCheckboxControl(workflowSettingsGroup, new Prop(PropertyUtils.ASYNC, Prop.TYPE.BOOLEAN, async));
		
		String hideInNavigator = PropertyUtils.getProperty(node, PropertyUtils.HIDE_IN_NAVIGATOR);
		createCheckboxControl(workflowSettingsGroup, new Prop(PropertyUtils.HIDE_IN_NAVIGATOR, Prop.TYPE.BOOLEAN, hideInNavigator));
		
		String privateWorkDirStr = PropertyUtils.getProperty(node, PropertyUtils.PRIVATE_WORK_DIR);
		createCheckboxControl(workflowSettingsGroup, new Prop(PropertyUtils.PRIVATE_WORK_DIR, Prop.TYPE.BOOLEAN, privateWorkDirStr));
		
		String clearPrivateWorkDirSetting = PropertyUtils.getProperty(node, PropertyUtils.CLEAR_WORK_DIR);
		createCheckboxControl(workflowSettingsGroup, new Prop(PropertyUtils.CLEAR_WORK_DIR, Prop.TYPE.BOOLEAN, clearPrivateWorkDirSetting));
		
		Button openDialog = toolkit.createButton(workflowSettingsGroup, LABEL_EDIT_PROPERTIES, SWT.PUSH);
		openDialog.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				new PropertiesDialog(parent.getShell(), node).open();
			}
		});
	}
	
	//////////////
	// OVERRIDE //
	//////////////
	
	@Override
	protected void setupPropertiesEditor(Composite parent, WFNode node) {
		this.node.set(node);
		this.propertiesParent = parent;
	}
	
	@Override
	protected void validateProperties(){
		IStatus status = Status.OK_STATUS;
		for (Property p: getNode().getProperties()) {
			status = WorkflowEditorPlugin.getDefault().mergeStatus(validateProperty(p), status);
		}

		messageView.setMessageFor(status);
	}
	
	//////////////
	// ABSTRACT //
	//////////////
	
	/**
	 * Implementors are responsible for taking data from the deserialized model {@link Object}
	 * (see {@link CustomNodeSettingsEditor#deserializeModel(XStream)}) and populating the
	 * custom view controls with that data.
	 */
	protected abstract void populateViewFromModel();
}
