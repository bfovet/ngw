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
 * 
 */
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

// TODO should this be deprecated in favor of MessageDialogWithToggle?

/**
 * Extends the functionality of the ChoicesButtonDialog to include a checkbox at the bottom to
 * "Remember my selection", and persists in the provided preference store the provided associated
 * preference value to the provided preference key.
 * 
 * @author mjgibso
 */
public class PersistableChoicesButtonDialog extends ChoicesButtonDialog
{
	protected static String[] extractChoices(String[][] labelsAndValues)
	{
		List<String> choices = new ArrayList<String>(labelsAndValues.length);
		for(String[] choice : labelsAndValues)
		{
			choices.add(choice[0]);
		}
		return choices.toArray(new String[choices.size()]);
	}
	
	protected final String[][] labelsAndValues_;
	
	protected final IPreferenceStore store_;
	
	protected final String preferenceKey_;
	
	protected final boolean nullAllowed_;
	
	protected Button persistB_;
	
	protected String persistanceLabel_ = "Remember my selection";
	
	/**
	 * Convenience constructor that simply calls the overloaded constructor
	 * {@link #PersistableChoicesButtonDialog(Shell, String, String, String[][], String, IPreferenceStore, String, boolean)},
	 * and passes nullAllowed as false.
	 */
	public PersistableChoicesButtonDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String[][] labelsAndValues, String defaultChoice,
			String persistanceLabel, IPreferenceStore store, String preferenceKey)
	{
		this(parentShell, dialogTitle, dialogMessage, labelsAndValues, defaultChoice, store, persistanceLabel, preferenceKey, false);
	}
	
	/**
	 * @param parentShell - the parent shell for the dialog (can be null)
	 * @param dialogTitle - the title for the dialog (will show in the window's header)
	 * @param dialogMessage - the message to be displayed in the body of the dialog
	 * @param labelsAndValues - a 2D array of the button names and a associated preference values.
	 *   When a button is pressed, if the "remember my selection" box is checked, the associated preference value will be
	 *   stored for the provided preference key.
	 * @param defaultChoice - the default button to give focus to when the dialog is opened
	 * @param store - the preference store to persist the selection in
	 * @param preferenceKey - the preference key to store the selected value to
	 * @param nullAllowed - if true, null values associated with button labels will be stored to the preference store
	 *   when a button is pressed.  If false, an associated null value will not be stored when a button is pressed.
	 *   For example, if a provided button is "Cancel", presumably, this is not a 'choice' the implementor would really want
	 *   to persist in the store.  So the implementor could associate null with the "Cancel" button, and set nullAllowed to
	 *   false such that if the "Remember my selection" box is checked, and the user presses "Cancel", nothing will be saved
	 *   in the preference store.
	 */
	public PersistableChoicesButtonDialog(Shell parentShell, String dialogTitle,
			String dialogMessage, String[][] labelsAndValues, String defaultChoice,
			IPreferenceStore store, String persistanceLabel, String preferenceKey, boolean nullAllowed)
	{
		super(parentShell, dialogTitle, dialogMessage, extractChoices(labelsAndValues), defaultChoice);
		
		this.labelsAndValues_ = labelsAndValues;
		this.store_ = store;
		this.preferenceKey_ = preferenceKey;
		this.nullAllowed_ = nullAllowed;
		if(persistanceLabel != null){
			persistanceLabel_ = persistanceLabel;
		}
	}
	
	@Override
	protected Control createButtonBar(Composite parent)
	{
		Control control = super.createButtonBar(parent);
		
		Composite composite = new Composite(parent, SWT.NONE);
		// create a layout with spacing and margins appropriate for the font size.
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		composite.setLayout(layout);
		GridData data = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1);
		composite.setLayoutData(data);
		composite.setFont(parent.getFont());

		// if no preferenceKey has been specified, then don't prompt to remember
		if(preferenceKey_ != null){
			persistB_ = new Button(composite, SWT.CHECK);
			persistB_.setText(persistanceLabel_);
		}
		
		return control;
	}
	
//	@Override
//	protected Control createContents(Composite parent)
//	{
//		Composite contents = (Composite) super.createContents(parent);
//		
//		persistB_ = new Button(contents, SWT.CHECK);
//		persistB_.setText("Remember my selection");
//		
//		return contents;
//	}
	
	@Override
	protected void buttonPressed(int buttonId)
	{
		// if no preferenceKey has been set, then the persistB_ item will be null
		// therefore no preference should be saved
		if(persistB_ != null && persistB_.getSelection())
		{
			String prefValue;
			if(buttonId>=0 && buttonId<labelsAndValues_.length)
			{
				prefValue = labelsAndValues_[buttonId][1];
			} else {
				prefValue = null;
			}
			
			if(preferenceKey_ != null && (prefValue!=null || nullAllowed_))
			{
				store_.setValue(preferenceKey_, prefValue);
			}
		}
		
		super.buttonPressed(buttonId);
	}
	
	public String getPreferenceValue(){
		String value = getValue();
		
		if(value == null){
			return null;
		}
		
		for(String[] labelAndValue : labelsAndValues_){
			if(value.equals(labelAndValue[0])){
				return labelAndValue[1];
			}
		}
		
		return null;
	}
	

}
