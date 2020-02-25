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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.context.impl.MultiDeleteInfo;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;

import com.strikewire.snl.apc.GUIs.CompositeUtils;
import com.strikewire.snl.apc.GUIs.GuiUtils;
import com.strikewire.snl.apc.GUIs.settings.AbstractSettingsEditor;
import com.strikewire.snl.apc.GUIs.settings.IContextMenuRegistrar;
import com.strikewire.snl.apc.GUIs.settings.IMessageView;
import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;
import com.strikewire.snl.apc.util.ExtensionPointUtils;
import com.strikewire.snl.apc.util.ResourceUtils;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.settings.ISettingsViewPreferences;
import gov.sandia.dart.workflow.domain.DomainFactory;
import gov.sandia.dart.workflow.domain.InputPort;
import gov.sandia.dart.workflow.domain.NamedObject;
import gov.sandia.dart.workflow.domain.OutputPort;
import gov.sandia.dart.workflow.domain.Port;
import gov.sandia.dart.workflow.domain.Property;
import gov.sandia.dart.workflow.domain.WFNode;
import gov.sandia.dart.workflow.editor.DecoratorManager;
import gov.sandia.dart.workflow.editor.WorkflowEditorPlugin;
import gov.sandia.dart.workflow.editor.WorkflowValidator;
import gov.sandia.dart.workflow.editor.configuration.Prop;
import gov.sandia.dart.workflow.editor.configuration.Prop.TYPE;
import gov.sandia.dart.workflow.editor.rendering.GenericWFNodeGARenderer;
import gov.sandia.dart.workflow.util.ParameterUtils;
import gov.sandia.dart.workflow.util.PropertyUtils;
import gov.sandia.dart.workflow.util.WorkflowHelp;

public class WFNodeSettingsEditor extends AbstractSettingsEditor<WFNode>  implements Adapter {

	private class FilenameValidator implements IInputValidator {

		@Override
		public String isValid(String newText) {
			return StringUtils.isBlank(newText)  ? 
				"Enter a valid filename" : null;
		}
	}

	private static final String BUTTON_DELETE = "Delete";
	private static final String BUTTON_ADD = "Add";
	private static final String BUTTON_UP = "Up";
	private static final String BUTTON_DOWN = "Down";

	protected static ImageDescriptor helpImage;

	protected final AtomicReference<WFNode> node = new AtomicReference<>();

	private final Set<Image> _images = new HashSet<>();

	private Text type_;

	private Text name_;

	private Text label_;

	private TabFolder tabFolder_;

	protected Composite propertiesComposite_;

	protected Composite inputsComposite_;

	protected Composite outputsComposite_;

	protected TableViewer outputPortsTable_;

	protected TableViewer inputPortsTable_;

	private Map<String, IConfigurationElement> resourceContributors; 
	
	protected Composite propertiesParent;
	private Map<String, Button> ipButtons;
	private Map<String, Button> opButtons;
	
	
	static {
		helpImage = WorkflowEditorPlugin.getImageDescriptor("icons/help.png");
	}
	
	protected void setupPropertiesEditor(Composite parent, WFNode node) {
		this.node.set(node);
		this.propertiesParent = parent;
		
		for (Property p: node.getProperties()) {
			createPropertiesControl(parent, p);
		}
		
		addExtraPropertiesControls(parent, node);
		
		validateProperties();

		
		Button openDialog = toolkit.createButton(propertiesParent, "Edit properties", SWT.PUSH);
		openDialog.addSelectionListener(new SelectionAdapter() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				new PropertiesDialog(propertiesParent.getShell(), node).open();
				//CompositeUtils.removeChildrenFromComposite(propertiesParent);
				//setupPropertiesEditor(propertiesParent, node);
				//propertiesParent.layout(true, true);
			}
		});
	}
	

	protected void addExtraPropertiesControls(Composite parent, WFNode node) {
		
	}

	protected Control createPropertiesControl(Composite composite, Property p) {
		Prop prop = new Prop(p);
		
		if (prop.isAdvanced())
			return null;
		
		switch (prop.getType()) {
		case HOME_FILE:
			return createPathControl(composite, prop);
		case BOOLEAN:
			return createCheckboxControl(composite, prop);
		case PARAMETER:
			return createComboControl(composite, prop, getParameterNames());
		default:
			return createTextControl(composite, prop);
		}
	}
		
	protected Combo createComboControl(Composite composite, Prop p, String[] items) {
		String propertyName = p.getName();
		String propertyValue = p.getValue();
		
		Composite row = toolkit.createComposite(composite);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
			toolkit.paintBordersFor(row);
		}

		row.setLayout(new GridLayout(2, false));
		row.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		toolkit.createLabel(row, propertyName);

		Combo combo = new Combo(row, SWT.READ_ONLY | SWT.DROP_DOWN);
		combo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		combo.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					if(node.get() == null)
					{
						return;
					}
					String value = combo.getText();
					String current = PropertyUtils.getProperty(node.get(), propertyName);
					if (Objects.equals(value, current))
						return;

					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
	        			domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							PropertyUtils.setProperty(node.get(), propertyName, value);	
							validateProperties();
							validateWorkflow();
						}

					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
				}
			}


		});
		 
		combo.setItems(items);
		
		if (propertyValue != null){
			combo.setText(propertyValue);			
		}
		
		return combo;
	}	

	public void validateWorkflow() {
		IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(getNode());
		
		if(fp == null) {
			return;
		}
		
		IDiagramTypeProvider dtp = fp.getDiagramTypeProvider();
		Map<EObject, IDecorator> decorators = DecoratorManager.getDecoratorMap(dtp.getDiagram().eResource());
		new WorkflowValidator().validate(decorators, dtp.getFeatureProvider());
		dtp.getDiagramBehavior().refresh();
		IDecorator iDecorator = decorators.get(getNode());
		if (iDecorator != null)
			messageView.setMessage(iDecorator.getMessage(), IMessageProvider.WARNING);
		else
			messageView.setMessageFor(Status.OK_STATUS);
	}

	private String[] getParameterNames() {
		EList<EObject> contents = getNode().eResource().getContents();
		List<String> names = contents.stream().filter(e -> e instanceof WFNode && ParameterUtils.isParameter((WFNode) e)).
				map(e -> ((WFNode)e).getName()).collect(Collectors.toList());
		return (String[]) names.toArray(new String[names.size()]);				
	}
	
	protected Control createPathControl(Composite composite, final Prop p) {
		String propertyName = p.getName();
		String propertyValue = p.getValue();
		
		Composite row = toolkit.createComposite(composite);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
			toolkit.paintBordersFor(row);
		}
		row.setLayout(new GridLayout(3, false));
		row.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		// toolkit.createLabel(row, propertyName);
		Hyperlink hyperlink = toolkit.createHyperlink(row, propertyName, SWT.NONE);
		hyperlink.setToolTipText("Select link to open this file in an editor");
		hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				IFile workflowFile = getWorkflowFile();
				IContainer parent = workflowFile.getParent();
				IResource resource = parent.findMember(propertyValue, false);
				if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					try {
						IDE.openEditor(PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow()
								.getActivePage(), file);
					} catch (PartInitException e1) {
						WorkflowEditorPlugin.getDefault().logError("Error opening file", e1);
					}				
				} else {
					Display.getCurrent().beep();
				}				
			}
		});


		Text theText = toolkit.createText(row, "", SWT.SINGLE);
		if (propertyValue != null)
			theText.setText(propertyValue);
		theText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));		
		theText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					if(node.get() == null)
					{
						return;
					}

					
					String value = theText.getText();
					String current = PropertyUtils.getProperty(node.get(), propertyName);
					if(Objects.equals(value, current))
					{
						return;
					}

					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
	        		domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							PropertyUtils.setProperty(node.get(), propertyName, value);	
							validateWorkflow();
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
					theText.setText(path.makeRelativeTo(home).toString());
			}		
		});
		
		Control c = possiblyAddContributedResourceControl(row, p, theText);
		if (c != null)
			row.setLayout(new GridLayout(4, false));
		return theText;

	}


	protected Text createTextControl(Composite composite, Prop p) {
		String propertyName = p.getName();
		final Prop.TYPE type = p.getType();
		String propertyValue = p.getValue();
		
		int style = (type == TYPE.MULTITEXT) ? SWT.MULTI : SWT.SINGLE;		
		style |= SWT.BORDER;
		boolean verticalGrab = ((style & SWT.MULTI) != 0);
		Composite row = toolkit.createComposite(composite);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {		
			toolkit.paintBordersFor(row);
		}

		row.setLayout(new GridLayout(2, false));
		row.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, verticalGrab));

		toolkit.createLabel(row, propertyName);

		Text theText = toolkit.createText(row, "", style);
		if ((style & SWT.MULTI) != 0)
			theText.setFont(WorkflowEditorPlugin.getDefault().getEditorAreaFont());
		//theText.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);

		theText.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, verticalGrab));
		
		if(type.equals(Prop.TYPE.DECIMAL)){
			theText.addVerifyListener(new VerifyListener() {
				@Override
				public void verifyText(VerifyEvent e) {
				    String allowedCharacters = "0123456789.,eE+-";
				    String text = e.text;
				    for (int index = 0; index < text.length(); index++) {
				        char character = text.charAt(index);
				        boolean isAllowed = allowedCharacters.indexOf(character) > -1;
				        if (!isAllowed) {
				            e.doit = false;
				            return;
				        }
				    }					
				}
			});
			
			theText.addFocusListener(new FocusListener(){

				@Override
				public void focusGained(FocusEvent e) {
				}

				@Override
				public void focusLost(FocusEvent e) {
					// Just in case the user has finished in an invalid state, 
					// reset the text value to the last accepted valid value
					String current = PropertyUtils.getProperty(node.get(), propertyName);
					theText.setText(current);
				}				
			});
		}
		
		
		theText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				try {
					if(node.get() == null)
					{
						return;
					}
					
					String value = theText.getText();
					String current = PropertyUtils.getProperty(node.get(), propertyName);
					if (Objects.equals(value, current))
						return;

					if(type.equals(Prop.TYPE.DECIMAL)){
						// ONLY set the value if it is a decimal
					    try {
					        Double.parseDouble(value);
					    } catch (NumberFormatException exception) {
					        //expressions like "5e-" are allowed while typing
					    	return;
					    }
					}

					
					
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
	        		domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							PropertyUtils.setProperty(node.get(), propertyName, value);	
							validateProperties();
							validateWorkflow();
						}
					});

				} catch (Exception e2) {
					WorkflowEditorPlugin.getDefault().logError("Can't find property in object", e2);
				}
			}


		});
		theText.setEditable(true);
		if (propertyValue != null)
			theText.setText(propertyValue);		

		Control c = possiblyAddContributedResourceControl(row, p, theText);
		if (c != null)
			row.setLayout(new GridLayout(3, false));
		
		return theText;
	}

	private Control possiblyAddContributedResourceControl(Composite composite, Prop p, Text theText) {
		if (resourceContributors != null && resourceContributors.get(p.getName()) != null) {		
			Button b = new Button(composite, SWT.PUSH);
			b.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));
			b.setToolTipText("Save default file in project");
			b.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					IConfigurationElement element = resourceContributors.get(p.getName());
					String resource = element.getAttribute("resource");
					String filename = new Path(resource).lastSegment();
					// TODO Can't believe we don't have a good filename validator somewhere
					InputDialog dialog =
							new InputDialog(Display.getCurrent().getActiveShell(),
									"Enter filename", "Specify a name for the file", filename,
									new FilenameValidator());	
					int result = dialog.open(); 
					if (result == Dialog.OK) {
						String newFilename = dialog.getValue();
						TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							public void doExecute() {
								PropertyUtils.setProperty(getNode(), p.getName(), newFilename);
								theText.setText(newFilename);
								validateProperties();
								validateWorkflow();
							}
						});
						Bundle bundle = Platform.getBundle(element.getContributor().getName());
						URL fileURL = bundle.getEntry(resource);
						try {
							if (fileURL != null) {
								IFile destination = getWorkflowFile().getParent().getFile(new Path(newFilename));

								if (destination.exists())
									destination.setContents(fileURL.openStream(), IResource.FORCE, null);
								else
									destination.create(fileURL.openStream(), IResource.FORCE, null);
							} else {
								throw new IOException("Can't find internal file");
							}
						} catch (Exception e1) {
							WorkflowEditorPlugin.getDefault().logError("Can't save file", e1);
						} 
					}
				}
			});
			return b;
		}
		return null;
	}

	
	protected void validateProperties(){
		IStatus status = Status.OK_STATUS;
		for(Property p: node.get().getProperties())
		{
			status = WorkflowEditorPlugin.getDefault().mergeStatus(validateProperty(p), status);
		}

		messageView.setMessageFor(status);
	}
	
	protected IStatus validateProperty(Property p){		
		
		Prop prop = new Prop(p);
		
		String propertyValue = p.getValue();

		if(Prop.TYPE.INTEGER == prop.getType()){								
			if(StringUtils.isNotEmpty(propertyValue)){
				if (propertyValue.matches("\\$\\{.*\\}"))
					return Status.OK_STATUS;
						
				try{
					Integer.parseInt(propertyValue);
				}catch(NumberFormatException nfe){
					String msg = "Property \'" + p.getName() + "\' is set to non-integer value \'" + p.getValue() + "\'";				
					return new Status(Status.ERROR, WorkflowEditorPlugin.PLUGIN_ID, msg);
				}
			}
			return Status.OK_STATUS;
		}
		
		if(Prop.TYPE.DECIMAL == prop.getType()){
			if(StringUtils.isNotEmpty(propertyValue)){
				if (propertyValue.matches("$\\{.*\\}"))
					return Status.OK_STATUS;

				try{
					Double.parseDouble(propertyValue);
				}catch(NumberFormatException nfe){
					String msg = "Property \'" + p.getName() + "\' is set to non-decimal value \'" + p.getValue() + "\'";				
					return new Status(Status.ERROR, WorkflowEditorPlugin.PLUGIN_ID, msg);
				}
			}
			return Status.OK_STATUS;
		}		
		
		return Status.OK_STATUS;
	}


	protected Button createCheckboxControl(Composite composite, Prop p) {
		String propertyName = p.getName();
		String propertyValue = p.getValue();
		
		Composite row = toolkit.createComposite(composite);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
			toolkit.paintBordersFor(row);
		}

		row.setLayout(new GridLayout(2, false));
		row.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));

		Button theCheckbox = toolkit.createButton(row, "", SWT.CHECK);
		theCheckbox.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
		theCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				try {
					if(node.get() == null)
					{
						return;
					}
					
					boolean value = theCheckbox.getSelection();
					String current = PropertyUtils.getProperty(node.get(), propertyName);
					if (Objects.equals(value, Boolean.valueOf(current)))
						return;

					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
	        				domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							PropertyUtils.setProperty(node.get(), propertyName, String.valueOf(value));	
							validateWorkflow();
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


		});
		
		if (propertyValue != null){
			boolean value = Boolean.parseBoolean(propertyValue);
			theCheckbox.setSelection(value);
		}
		theCheckbox.setText(propertyName);
		return theCheckbox;
	}

	protected IFile getWorkflowFile() {
		IFile file = null;

		URI uri = getNode().eResource().getURI();
		
		String pathString = uri.toPlatformString(true);
		if(pathString != null) {
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(pathString));
		}else {
			String fileString = uri.toFileString();			
			if(fileString != null) {				
				try {
					file = ResourceUtils.getFileForLocation(fileString);
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return file;
	}
	
	@Override
	public Notifier getTarget() {
		return getNode();
	}

	@Override
	public void setTarget(Notifier newTarget) {
		// ignore		
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return false;
	}


	@Override
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg) {
		super.createPartControl(mform, messageView, selectionProvider, ctxMenuReg);
		IAction helpAction = new Action() {
			@Override
			public void run() {
				WorkflowHelp.openDocumentationWebPage(node.get().getType());
			}
		};
		helpAction.setImageDescriptor(helpImage);
		
		form.getToolBarManager().add(helpAction);
		form.getToolBarManager().update(true);
		
		GridLayout grid = new GridLayout(2, false);
		form.getBody().setLayout(grid);
	
		toolkit.createLabel(form.getBody(), "Name");
		name_ = createTextField(form.getBody(), "name", toolkit, node);
		name_.setEditable(true);
		GridData gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		name_.setLayoutData(gd);
		name_.addModifyListener(new ModifyListener() {			
			@Override
			public void modifyText(ModifyEvent e) {
				setEditorTitle();
			}
		});
	
		toolkit.createLabel(form.getBody(), "Type");
		type_ = createTextField(form.getBody(), "type", toolkit, node);
		type_.setEditable(false);
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		type_.setLayoutData(gd);
		
		toolkit.createLabel(form.getBody(), "Label");
		label_ = createTextField(form.getBody(), "label", toolkit, node);
		label_.setEditable(true);
		gd = new GridData(SWT.FILL,SWT.FILL,true,false);
		label_.setLayoutData(gd);
	
		tabFolder_ = new TabFolder(form.getBody(), SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		tabFolder_.setLayoutData(gd);
		propertiesComposite_ = createTab("Properties");
		propertiesComposite_.setLayout(new GridLayout(1, false));
		inputsComposite_ = createTab("Input Ports");		
		inputsComposite_.setLayout(new GridLayout(1, false));
		outputsComposite_ = createTab("Output Ports");	
		outputsComposite_.setLayout(new GridLayout(1, false));
	
	}


	protected void setEditorTitle() {
		form.setText("Workflow Node '" + name_.getText() + "' (" + type_.getText() + ")");		
	}


	protected Composite createTab(String label) {
		TabItem propertiesTabItem = new TabItem(tabFolder_, SWT.NONE);
		propertiesTabItem.setText(label);
		Composite comp = new Composite(tabFolder_, SWT.NONE);
		IPreferenceStore store = CommonPreferencesPlugin.getDefault().getPreferenceStore();
		if (store.getBoolean(ISettingsViewPreferences.DRAW_BORDERS)) {
			toolkit.paintBordersFor(comp);
		}
		comp.setBackground(comp.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		propertiesTabItem.setControl(comp);
		return comp;
	}


	@Override
	public void setNode(WFNode node) {
		if (node == this.node.get())
			return;
		
		if(this.node.get() != null)
		{
			this.node.get().eAdapters().remove(this);
		}
	
		this.node.set(node);
		if (node != null) {
			type_.setText(node.getType());
			name_.setText(node.getName());
			label_.setText(node.getLabel());
			form.setImage(GenericWFNodeGARenderer.getIcon(node));
			
			List<IConfigurationElement> elements =
					ExtensionPointUtils.getConfigurationElements(WorkflowEditorPlugin.PLUGIN_ID, "resourceContributor").stream().
					filter(e -> e.getAttribute("node").equals(node.getType())).collect(Collectors.toList());
			// Higher-priority extensions come after lower-priority ones
			Collections.sort(elements,  new Comparator<IConfigurationElement>() {
				@Override
				public int compare(IConfigurationElement e1, IConfigurationElement e2) {
					int p1 = Integer.parseInt(e1.getAttribute("priority"));
					int p2 = Integer.parseInt(e2.getAttribute("priority"));
					return p1 - p2;
				}
			});			
			resourceContributors = new HashMap<>();
			for (IConfigurationElement e: elements) {
				resourceContributors.put(e.getAttribute("property"), e);
			}		
			
			setupPropertiesEditor(propertiesComposite_, node);
	
			setupInputPortsTab(node);		
	
			setupOutputPortsTab(node);
			setEditorTitle();
			this.node.get().eAdapters().add(this);			
		}
		validateWorkflow();
	}


	protected void setupOutputPortsTab(WFNode node) {
		Map<String, Runnable> create = new LinkedHashMap<>();
		create.put(BUTTON_ADD, new Runnable() {
			@Override
			public void run() {
				OutputPort port = DomainFactory.eINSTANCE.createOutputPort();
				port.setName(NOWPSettingsEditorUtils.createUniqueName(node.getOutputPorts()));
				port.setType("default");				
				node.getOutputPorts().add(port);
			}
		});
		create.put(BUTTON_DELETE, new Runnable() {
			@Override
			public void run() {
				ISelection s = outputPortsTable_.getSelection();
				if (s instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)s).getFirstElement();
					if (object instanceof OutputPort) {
						IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(node);
						PictogramElement pe = fp.getPictogramElementForBusinessObject(object);
						DeleteContext dc = new DeleteContext(pe);
						dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
						fp.getDeleteFeature(dc).delete(dc);  
					}
				}
			}
		});
		
		// TODO Maybe move these into custom features?
		create.put(BUTTON_UP, new Runnable() {
			@Override
			public void run() {
				ISelection s = outputPortsTable_.getSelection();
				if (s instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)s).getFirstElement();
					if (object instanceof OutputPort) {
						OutputPort movingPort = (OutputPort) object;
						int index = node.getOutputPorts().indexOf(movingPort);
						if (index > 0) {
							OutputPort otherPort = node.getOutputPorts().get(index-1);
							node.getOutputPorts().move(index-1, movingPort);
							swapPictograms(movingPort, otherPort);
							enableOpButtons();
						}
					}
				}
			}
		});
		
		create.put(BUTTON_DOWN, new Runnable() {
			@Override
			public void run() {
				ISelection s = outputPortsTable_.getSelection();
				if (s instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)s).getFirstElement();
					if (object instanceof OutputPort) {
						OutputPort movingPort = (OutputPort) object;
						int index = node.getOutputPorts().indexOf(movingPort);
						if (index < outputPortsTable_.getTable().getItemCount() - 1) {
							OutputPort otherPort = node.getOutputPorts().get(index+1);
							node.getOutputPorts().move(index+1, movingPort);	
							swapPictograms(movingPort, otherPort);
							enableOpButtons();
						}
					}
				}
			}
		});
		
		opButtons = NOWPSettingsEditorUtils.addButtons(toolkit, outputsComposite_, node, create);

		outputPortsTable_ = makeOutputPortsTable(node, outputsComposite_, new PortsContentProvider(false));			
		outputPortsTable_.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				enableOpButtons();
			}

		});
		enableOpButtons();

	}
	
	private void enableOpButtons() {
		int index = outputPortsTable_.getTable().getSelectionIndex();
		if (index == -1) {
			opButtons.get(BUTTON_DELETE).setEnabled(false);				
			opButtons.get(BUTTON_UP).setEnabled(false);
			opButtons.get(BUTTON_DOWN).setEnabled(false);
		} else {		
			opButtons.get(BUTTON_DELETE).setEnabled(true);
			opButtons.get(BUTTON_UP).setEnabled(index != 0);
			opButtons.get(BUTTON_DOWN).setEnabled(index != outputPortsTable_.getTable().getItemCount() - 1);
		}
	}


	protected void setupInputPortsTab(WFNode node) {
		Map<String, Runnable> create = new LinkedHashMap<>();
		create.put(BUTTON_ADD, new Runnable() {
			@Override
			public void run() {
				InputPort port = DomainFactory.eINSTANCE.createInputPort();
				port.setName(NOWPSettingsEditorUtils.createUniqueName(node.getInputPorts()));
				port.setType("default");
				node.getInputPorts().add(port);				
			}			
		});
		create.put(BUTTON_DELETE, new Runnable() {
			@Override
			public void run() {
				ISelection s = inputPortsTable_.getSelection();
				if (s instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)s).getFirstElement();
					if (object instanceof InputPort) {
						IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(node);
						PictogramElement pe = fp.getPictogramElementForBusinessObject(object);
						DeleteContext dc = new DeleteContext(pe);
						dc.setMultiDeleteInfo(new MultiDeleteInfo(false, false, 1));
						fp.getDeleteFeature(dc).delete(dc);  
					}
				}
			}
		});
		create.put(BUTTON_UP, new Runnable() {
			@Override
			public void run() {
				ISelection s = inputPortsTable_.getSelection();
				if (s instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)s).getFirstElement();
					if (object instanceof InputPort) {
						InputPort movingPort = (InputPort) object;
						int index = node.getInputPorts().indexOf(movingPort);
						if (index > 0) {
							InputPort otherPort = node.getInputPorts().get(index-1);
							node.getInputPorts().move(index-1, movingPort);
							swapPictograms(movingPort, otherPort);
							enableIpButtons();
						}
					}
				}
			}
		});
		create.put(BUTTON_DOWN, new Runnable() {
			@Override
			public void run() {
				ISelection s = inputPortsTable_.getSelection();
				if (s instanceof IStructuredSelection) {
					Object object = ((IStructuredSelection)s).getFirstElement();
					if (object instanceof InputPort) {
						InputPort movingPort = (InputPort) object;
						int index = node.getInputPorts().indexOf(movingPort);
						if (index < inputPortsTable_.getTable().getItemCount() -1) {
							InputPort otherPort = node.getInputPorts().get(index+1);
							node.getInputPorts().move(index+1, movingPort);	
							swapPictograms(movingPort, otherPort);
							enableIpButtons();
						}
					}
				}
			}
		});
		
		ipButtons = NOWPSettingsEditorUtils.addButtons(toolkit, inputsComposite_, node, create);
	
		inputPortsTable_ = makeInputPortsTable(node, inputsComposite_, new PortsContentProvider(true));
		inputPortsTable_.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				enableIpButtons();
			}

			
		});
		enableIpButtons();

	}

	protected void swapPictograms(Port movingPort, Port otherPort) {
		IFeatureProvider fp = NOWPSettingsEditorUtils.getFeatureProvider(movingPort);
		FixPointAnchor movingPe = (FixPointAnchor) fp.getPictogramElementForBusinessObject(movingPort);
		FixPointAnchor otherPe = (FixPointAnchor) fp.getPictogramElementForBusinessObject(otherPort);
		int otherY = otherPe.getLocation().getY();
		int movingY = movingPe.getLocation().getY();
		otherPe.getLocation().setY(movingY);
		movingPe.getLocation().setY(otherY);
	}


	private void enableIpButtons() {
		int index = inputPortsTable_.getTable().getSelectionIndex();

		if (index == -1) {
			ipButtons.get(BUTTON_DELETE).setEnabled(false);				
			ipButtons.get(BUTTON_UP).setEnabled(false);
			ipButtons.get(BUTTON_DOWN).setEnabled(false);
		} else {		
			ipButtons.get(BUTTON_DELETE).setEnabled(true);
			ipButtons.get(BUTTON_UP).setEnabled(index != 0);
			ipButtons.get(BUTTON_DOWN).setEnabled(index != inputPortsTable_.getTable().getItemCount() - 1);

		}
	}

	protected TableViewer makeOutputPortsTable(WFNode node, Composite comp, IStructuredContentProvider provider) {
		TableViewer viewer = new TableViewer(comp, SWT.NONE);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	
		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		TableViewerColumn vcolumn1 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column1 = vcolumn1.getColumn();	
		column1.setText("Name");
	
		TableViewerColumn vcolumn2 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column2 = vcolumn2.getColumn();	
		column2.setText("Type");
		column2.setWidth(80);
		
		TableViewerColumn vcolumn3 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column3 = vcolumn3.getColumn();	
		column3.setText("Filename");
		
		viewer.setLabelProvider(new PortLabelProvider());
		viewer.setContentProvider(provider);
		viewer.setInput(node);
		vcolumn1.setEditingSupport(new OutputPortEditingSupport(viewer, 0));
		vcolumn2.setEditingSupport(new OutputPortEditingSupport(viewer, 1));
		vcolumn3.setEditingSupport(new OutputPortEditingSupport(viewer, 2));
		
		WorkflowEditorSettingsUtils.pack(column1, 80);
		WorkflowEditorSettingsUtils.pack(column3, 80);

		return viewer;
	}
	
	protected TableViewer makeInputPortsTable(WFNode node, Composite comp, IStructuredContentProvider provider) {
		TableViewer viewer = new TableViewer(comp, SWT.NONE);
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);	
		table.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
		
		TableViewerColumn vcolumn1 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column1 = vcolumn1.getColumn();	
		column1.setText("Name");
	
		TableViewerColumn vcolumn2 = new TableViewerColumn(viewer, SWT.RIGHT);		
		TableColumn column2 = vcolumn2.getColumn();	
		column2.setText("Type");
		column2.setWidth(80);
				
		viewer.setLabelProvider(new PortLabelProvider());
		viewer.setContentProvider(provider);
		viewer.setInput(node);
		vcolumn1.setEditingSupport(new InputPortEditingSupport(viewer, 0));
		vcolumn2.setEditingSupport(new InputPortEditingSupport(viewer, 1));
	
		WorkflowEditorSettingsUtils.pack(column1, 80);

		return viewer;
	}


	@Override
	public WFNode getNode() {
		return node.get();
	}


	@Override
	public void dispose() {
		WFNode node = getNode();
		if (node != null) {
			node.eAdapters().remove(this);
		}		
		_images.stream().filter(i -> !i.isDisposed()).forEach(i -> i.dispose());
		_images.clear();
	}


	@Override
	public boolean isReusable() {
		return false;
	}


	@Override
	public void notifyChanged(Notification notification) {
		WFNode wfNode = node.get();
		if (wfNode != null && propertiesParent != null ) {
			Display.getDefault().asyncExec(() -> {
				if (!propertiesParent.isDisposed()) {
					CompositeUtils.removeChildrenFromComposite(propertiesParent);
					if (!propertiesParent.isDisposed()) {
						setupPropertiesEditor(propertiesParent, wfNode);
						propertiesParent.layout(true, true);
					}
				}
			});		
		}
	}


	@Override
	public boolean setFocus() {
		return form.setFocus();
		// return name_.setFocus();
	}
	
	private Text createTextField(Composite composite, String propertyName, FormToolkit toolkit, AtomicReference<? extends NamedObject> node) {
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
					String name = (String) org.apache.commons.beanutils.PropertyUtils.getSimpleProperty(node.get(), propertyName);
					if (Objects.equals(value, name))
						return;
					TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(node.get());
	        		domain.getCommandStack().execute(new RecordingCommand(domain) {
						@Override
						public void doExecute() {
							try {
								org.apache.commons.beanutils.PropertyUtils.setSimpleProperty(node.get(), propertyName, value);
								validateWorkflow();
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
