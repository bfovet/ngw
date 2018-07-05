/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * Created on Aug 27, 2007 at 4:09:35 PM
 */
package com.strikewire.snl.apc.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;


/**
 * @author mjgibso
 *
 */
public abstract class PropertiesDialog<E extends PropertiesInstance<E>> extends TitleAreaDialog
{
	protected final ModifyListener updateModifyListener_ = new ModifyListener() {
		
		public void modifyText(ModifyEvent e) {
			update();
		}
	};
	
	protected final MutablePropertiesInstance<E> properties_;
	
	protected String baseMessage_;
	protected int baseMessageType_;
	
	private Text propertiesName_;
	
	private List<FieldHolder> fields_ = new ArrayList<FieldHolder>();
	
	protected final PropertiesStore<E> propsParent_;
	protected final String propsDisplayName_;
	
	/**
	 * @param parentShell
	 */
	protected PropertiesDialog(Shell parentShell, MutablePropertiesInstance<E> props)
	{
		super(parentShell);
		
		if(props == null)
			throw new IllegalArgumentException("Null arguments not allowed");
		
		this.properties_ = props;
		this.propsParent_ = properties_.getParent();
		this.propsDisplayName_ = propsParent_.getPropertiesDisplayName();
		
		this.baseMessage_ = "Define the "+propsDisplayName_+".";
		this.baseMessageType_ = IMessageProvider.NONE;
		
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}
	
	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		
		newShell.setText("Edit "+propsDisplayName_);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.TitleAreaDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = new Composite(parent, SWT.NONE);
		
		int numCols = 2;
		GridLayout layout = new GridLayout(numCols, false);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// add the template name label
		Label templateNameL = new Label(composite, SWT.NONE);
		templateNameL.setText(propsDisplayName_+" name:");
		
		// add the properties name field
		propertiesName_ = new Text(composite, SWT.BORDER);
		propertiesName_.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		propertiesName_.addModifyListener(updateModifyListener_);
		
		createDialogBody(parent);
		
		initFieldValues();
		update();
		
		return parent;
	}
	
	/**
	 * Subclasses should override.
	 */
	protected void createDialogBody(Composite parent)
	{
		// do nothing
	}
	
	private void createLabel(Composite composite,
	                       String lblText)
	{
    Label label = new Label(composite, SWT.NONE);
    label.setText(lblText);
	  
	}
	
	protected Text createField(Composite composite, String name, String key)
	{
		// create the label
	  createLabel(composite, name);
		
		// create the field
		Text field = new Text(composite, SWT.BORDER);
		
		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		data.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns - 1;
		field.setLayoutData(data);
		
		field.addModifyListener(updateModifyListener_);
		fields_.add(new FieldHolder(name, key, field));
		
		return field;
	}
	
	
	protected Combo createOptionField(Composite composite,
	                                 String label,
	                                 String key,
	                                 String[] options)
	{
	  createLabel(composite, label);
	  Combo cmb = new Combo(composite, SWT.READ_ONLY);

    GridData data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    data.horizontalSpan = ((GridLayout) composite.getLayout()).numColumns - 1;

    cmb.setLayoutData(data);
    
    cmb.setItems(options);
    

    fields_.add(new FieldHolder(label, key, cmb));
    
    return cmb;
	}
	
	@Override
	public void create()
	{
		super.create();
		update();
	}
	
	protected void initFieldValues()
	{
		String propertiesName = this.properties_.getName();
		this.propertiesName_.setText(propertiesName!=null ? propertiesName : "");
		
		for(FieldHolder holder : fields_)
		{
			String value = this.properties_.getProperty(holder.key_);
			
			if (holder.field_ instanceof Text) {
			  ((Text)holder.field_).setText(value!=null ? value : "");
			}
			else if (holder.field_ instanceof Combo) {
			  ((Combo)holder.field_).setText(value != null ? value : "");
			}
		}
	}
	
	protected void update()
	{
		setMessage("", IMessageProvider.WARNING);
		setMessage(baseMessage_, baseMessageType_);
		setErrorMessage(null);
		
		boolean valid = validate();
		Button okB = getButton(IDialogConstants.OK_ID);
		if(okB != null)
			okB.setEnabled(valid);
	}
	
	protected final String getPropertiesName()
	{ return propertiesName_.getText(); }
	
	protected boolean validate()
	{
		// the name can't be null or blank
		String propertiesName = this.propertiesName_.getText();
		if(propertiesName==null || propertiesName.trim().equals(""))
		{
			setMessage("The "+propsDisplayName_+" name cannot be blank.", IMessageProvider.ERROR);
			return false;
		}
		// the name must be unique (ignoring case)
		List<String> names = propsParent_.getUserPropertiesNames();
		List<String> lowerCaseNames = new ArrayList<String>(names.size());
		for(String name : names)
			lowerCaseNames.add(name.toLowerCase());
		if(lowerCaseNames.contains(propertiesName.toLowerCase()) && this.properties_!=propsParent_.getPropertiesInstance(propertiesName))
		{
			setMessage("The specified "+propsDisplayName_+" name already exists.  Please specify a unique "+propsDisplayName_+" name.", IMessageProvider.ERROR);
			return false;
		}
		
		// ----------------  NO MORE ERROR CHECKING AFTER THIS POINT, ONLY WARNING CHECKING
		
		// ----------------  NO MORE WARNING CHECKING AFTER THIS POINT, ONLY INFO CHECKING
		
		// let the user know that they are overriding a built in properties
		if(propsParent_.getDefaultPropertiesNames().contains(propertiesName))
		{
			setMessage("You are overriding a built in "+propsDisplayName_+".  If you don't want to override the built in "+propsDisplayName_+", change the "+propsDisplayName_+" name to something unique.", IMessageProvider.INFORMATION);
			return true;
		}
		
		return true;
	}
	
	protected void saveFields()
	{
		this.properties_.setName(this.propertiesName_.getText());
		
		for(FieldHolder holder : fields_)
		{
		  String value = "";
		  if (holder.field_ instanceof Text) {
		    value = ((Text)holder.field_).getText();		    
		  }
		  else if (holder.field_ instanceof Combo) {
		    value = ((Combo)holder.field_).getText();
		  }
		  
			
			this.properties_.setProperty(holder.key_, value.trim().equals("") ? null : value);
		}
	}
	
	@Override
	public boolean close()
	{
		if(getReturnCode() == OK)
			saveFields();
		return super.close();
	}
	
	protected static class FieldHolder
	{
		final String name_;
		final String key_;
		final Widget field_;
		
		/**
		 * 
		 */
		public FieldHolder(String name, String key, Widget field)
		{
			this.name_ = name;
			this.key_ = key;
			this.field_ = field;
		}
	}
}
