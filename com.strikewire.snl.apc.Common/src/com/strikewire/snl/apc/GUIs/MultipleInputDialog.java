/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class MultipleInputDialog extends Dialog {
	protected static final String FIELD_NAME = "FIELD_NAME"; //$NON-NLS-1$
	
	/**
	 * The types of fiels that are supported
	 */
	enum EFieldType
	{
	  TEXT,
	  BROWSE,
	  VARIABLE,
	  CHECKBOX,
	  COMBO	  
	}
	
	protected static final Object EMPTY_STRING = "";
	
	protected Composite panel;
	
	protected List<FieldSummary> fieldList = new ArrayList<>();
	protected List<Control> controlList = new ArrayList<>();
	protected List<Validator> validators = new ArrayList<>();
	protected Map<Object,String> valueMap = new HashMap<>();

	private String title;
	private String messageText;
	
	
	
	public MultipleInputDialog(Shell shell, String title) {
		super(shell);
		this.title = title;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
  protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null) {
			shell.setText(title);
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
  protected Control createButtonBar(Composite parent) {
		Control bar = super.createButtonBar(parent);
		validateFields();
		return bar;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
  protected Control createDialogArea(Composite parent) {
			
		Composite container = (Composite)super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		if(!StringUtils.isBlank(messageText)){
			createMessageField(container);
		}
				
		panel = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		panel.setLayout(layout);
		panel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		for (Iterator<FieldSummary> i = fieldList.iterator(); i.hasNext();) {
			FieldSummary field = i.next();
			switch(field.type) {
				case TEXT:
					createTextField(field.name, field.initialValue, field.allowsEmpty);
					break;
				case BROWSE:
					createBrowseField(field.name, field.initialValue, field.allowsEmpty);
					break;
				case CHECKBOX:
				  createCheckboxField(field.name,
				      Boolean.valueOf(field.initialValue),
				      field.allowsEmpty);
				  break;
				case COMBO:
					  createComboField(field.name,
					      field.initialValue,
					      field.allowedValues,
					      field.allowsEmpty);
					  break;

			}
		}
		
		fieldList = null; // allow it to be gc'd
		Dialog.applyDialogFont(container);
		return container;
	}
	
	

	public void addBrowseField(String labelText, String initialValue, boolean allowsEmpty) {
		fieldList.add(new FieldSummary(EFieldType.BROWSE, labelText, initialValue, allowsEmpty));
	}
	
	
	public void addTextField(String labelText, String initialValue, boolean allowsEmpty) {
		fieldList.add(new FieldSummary(EFieldType.TEXT, labelText, initialValue, allowsEmpty));
	}
	
	public void addComboField(String labelText, String initialValue, String[] values, boolean allowsEmpty) {
		fieldList.add(new FieldSummary(EFieldType.COMBO, labelText, initialValue, values, allowsEmpty));
	}
	
	
//	public void addVariablesField(String labelText, String initialValue, boolean allowsEmpty) {
//		fieldList.add(new FieldSummary(EFieldType.VARIABLE, labelText, initialValue, allowsEmpty));
//	}
	
	public void addCheckboxField(String labelText, boolean initialValue) {
	  fieldList.add(new FieldSummary(EFieldType.CHECKBOX, labelText, Boolean.toString(initialValue), false));
	}
	
	public void setMessageText(String message){
		messageText = message;
	}
	
	protected void createMessageField(Composite parent){
		Label label = new Label(parent, SWT.WRAP);
		label.setText(messageText);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.widthHint = 300;
		label.setLayoutData(gd);
		
		
		Label seperatorLabel = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperatorLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}
	
	
	
	protected void createTextField(String labelText, String initialValue, boolean allowEmpty) { 
		Label label = new Label(panel, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		final Text text = new Text(panel, SWT.SINGLE | SWT.BORDER);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.setData(FIELD_NAME, labelText);
		
		// make sure rows are the same height on both panels.
		label.setSize(label.getSize().x, text.getSize().y); 
		
		if (initialValue != null) {
			text.setText(initialValue);
		}
		
		if (!allowEmpty) {
			validators.add(new Validator() {
				@Override
        public boolean validate() {
					return !text.getText().equals(EMPTY_STRING);
				}
			});
			text.addModifyListener(new ModifyListener() {
				@Override
        public void modifyText(ModifyEvent e) {
					validateFields();
				}
			});
		}
		
		controlList.add(text);
	}
	

	protected void createCheckboxField(String labelText,
	                                   boolean initialValue,
	                                   boolean allowEmpty) 
	{
	  final Button chkBox = new Button(panel, SWT.CHECK);
	  chkBox.setText(labelText);
	  
	  chkBox.setData(FIELD_NAME, labelText);	  
	  
	  chkBox.setSelection(initialValue);
	  
	  GridData gd = GridDataFactory.fillDefaults().create();
	  chkBox.setLayoutData(gd);
	  
	  controlList.add(chkBox);
	}

	private void createComboField(String labelText, String initialValue, String[] allowedValues, boolean allowsEmpty) {
		Label label = new Label(panel, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		final Combo combo = new Combo(panel, SWT.READ_ONLY);
		  combo.setItems(allowedValues);	
		  if (initialValue != null) {
			  int index = combo.indexOf(initialValue);
			  if (index > -1)
				  combo.select(index);
		  }
		  
		  combo.setData(FIELD_NAME, labelText);	  
		  		  
		  GridData gd = GridDataFactory.fillDefaults().create();
		  combo.setLayoutData(gd);		  
		  controlList.add(combo);		
	}
	
	protected void createBrowseField(String labelText, String initialValue, boolean allowEmpty) {
		Label label = new Label(panel, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		Composite comp = new Composite(panel, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight=0;
		layout.marginWidth=0;
		comp.setLayout(layout);
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Text text = new Text(comp, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 200;
		text.setLayoutData(data);
		text.setData(FIELD_NAME, labelText);

		// make sure rows are the same height on both panels.
		label.setSize(label.getSize().x, text.getSize().y); 
		
		if (initialValue != null) {
			text.setText(initialValue);
		}
		
		if (!allowEmpty) {
			validators.add(new Validator() {
				@Override
        public boolean validate() {
					return !text.getText().equals(EMPTY_STRING);
				}
			});

			text.addModifyListener(new ModifyListener() {
				@Override
        public void modifyText(ModifyEvent e) {
					validateFields();
				}
			});
		}
		
		Button button = createButton(comp, IDialogConstants.IGNORE_ID, "&Browse...", false); 
		button.addSelectionListener(new SelectionAdapter() {
			@Override
      public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setMessage("Select a file:");  
				String currentWorkingDir = text.getText();
				if (!currentWorkingDir.trim().equals(EMPTY_STRING)) {
					File path = new File(currentWorkingDir);
					if (path.exists()) {
						dialog.setFilterPath(currentWorkingDir);
					}			
				}
				
				String selectedDirectory = dialog.open();
				if (selectedDirectory != null) {
					text.setText(selectedDirectory);
				}		
			}
		});

		controlList.add(text);
		
	}
	
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
  protected void okPressed() {
		for (Iterator<Control> i = controlList.iterator(); i.hasNext(); ) {
			Control control = i.next();
			if (control instanceof Text) {
				valueMap.put(control.getData(FIELD_NAME), ((Text)control).getText());
			}
			else if (control instanceof Button) {
			  Button btn = (Button)control;
			  valueMap.put(control.getData(FIELD_NAME), 
			      Boolean.toString(btn.getSelection()));
			}
			else if (control instanceof Combo) {
				  Combo combo = (Combo)control;
				  valueMap.put(control.getData(FIELD_NAME), 
				      combo.getText());
				}
		}
		controlList = null;
		super.okPressed();
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#open()
	 */
	@Override
  public int open() {
		applyDialogFont(panel);
		return super.open();
	}
	
	public Object getValue(String key) {
		return valueMap.get(key);
	}
	
	public String getStringValue(String key) {
		return  (String) getValue(key);
	}
	
	
	public boolean getBooleanValue(String key)
	{
	  Object o = getValue(key);
	  return Boolean.valueOf((String)o);
	}
	
	public void validateFields() {
		for(Iterator<Validator> i = validators.iterator(); i.hasNext(); ) {
			Validator validator = i.next();
			if (!validator.validate()) {
				getButton(IDialogConstants.OK_ID).setEnabled(false);
				return;
			}
		}
		getButton(IDialogConstants.OK_ID).setEnabled(true);
	}
    
	protected class FieldSummary {
		EFieldType type;
		String name;
		String initialValue;
		boolean allowsEmpty;
		String[] allowedValues;
		
		public FieldSummary(EFieldType type, String name, String initialValue, boolean allowsEmpty) {
			this.type = type;
			this.name = name;
			this.initialValue = initialValue;
			this.allowsEmpty = allowsEmpty;
		}
		public FieldSummary(EFieldType type, String name, String initialValue, String[] allowedValues, boolean allowsEmpty) {
			this(type, name, initialValue, allowsEmpty);
			this.allowedValues = allowedValues;
		}
	}
	
	protected class Validator {
		boolean validate() {
			return true;
		}
	}
}
