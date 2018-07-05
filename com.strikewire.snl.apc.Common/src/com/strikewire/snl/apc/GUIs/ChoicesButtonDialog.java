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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author mjgibso
 * Ripped off from ChoicesDialog
 */
public class ChoicesButtonDialog extends Dialog
{
    /**
     * The title of the dialog.
     */
    protected String title_;

    /**
     * The message to display, or <code>null</code> if none.
     */
    protected String message_;
    
    /**
     * The list of choices.
     */
    protected String[] choices_;
    
    /**
     * The selected choice
     */
    protected String value_;
    
    /**
     * The default choice
     */
    protected String defaultChoice_;
    
    /**
     * Creates an input dialog with buttons corresponding to the given choices.  Note that the
     * dialog will have no visual representation (no widgets) until it is told to open.
     * <p>
     * Note that the <code>open</code> method blocks for input dialogs.
     * </p>
     * 
     * @param parentShell
     *            the parent shell, or <code>null</code> to create a top-level
     *            shell
     * @param dialogTitle
     *            the dialog title, or <code>null</code> if none
     * @param dialogMessage
     *            the dialog message, or <code>null</code> if none
     * @param choices
     * 			  the choices, must not be <code>null</code>
     * @param defaultChoice
     * 			  the default choice button 
     */
    public ChoicesButtonDialog(Shell parentShell, String dialogTitle,
            String dialogMessage, String[] choices, String defaultChoice) {
        super(parentShell);
        this.title_ = dialogTitle;
        this.defaultChoice_ = defaultChoice;
        message_ = dialogMessage;
        
        if(choices == null)
        	throw new IllegalArgumentException("Choices must not be null");
        
        this.choices_ = choices;
    }
    
    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void buttonPressed(int buttonId)
    {
    	if(buttonId>=0 && buttonId<choices_.length)
    		value_ = choices_[buttonId];
    	else
    		value_ = null; // if the user just closes the dialog
    	
    	super.buttonPressed(IDialogConstants.OK_ID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        if (title_ != null)
            shell.setText(title_);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
     */
    protected void createButtonsForButtonBar(Composite parent)
    {
    	for(int i=0; i<choices_.length; i++)
    		createButton(parent, i, choices_[i], choices_[i].equals(defaultChoice_));
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent)
    {
        // create composite
        Composite composite = (Composite) super.createDialogArea(parent);
        // create message
        if(message_ != null)
        {
            Label label = new Label(composite, SWT.WRAP);
            label.setText(message_);
            GridData data = new GridData(GridData.GRAB_HORIZONTAL
                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
                    | GridData.VERTICAL_ALIGN_CENTER);
            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
            label.setLayoutData(data);
            label.setFont(parent.getFont());
        }

        applyDialogFont(composite);
        return composite;
    }

    /**
     * Returns the selected choice if one was pressed, or null if the dialog was simply closed.
     */
    public String getValue()
    { return value_; }
}
