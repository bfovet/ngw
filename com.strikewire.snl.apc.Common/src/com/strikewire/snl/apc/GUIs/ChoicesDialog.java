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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.strikewire.snl.apc.validation.ISelectionValidator;
import com.strikewire.snl.apc.validation.ValidationUtils;

/**
 * @author mjgibso Ripped off from InputDialog
 */
public class ChoicesDialog extends Dialog
{
  public static final int COMBO = 1 << 2;
  public static final int READ_ONLY = SWT.READ_ONLY;
  public static final int RADIO = SWT.RADIO;
  public static final int CHECK = SWT.CHECK;

  /**
   * The title of the dialog.
   */
  private String title;

  /**
   * The message to display, or <code>null</code> if none.
   */
  private String message;

  /**
   * The list of choices.
   */
  private String[] choices;

  /**
   * The input value; the empty string by default.
   */
  private String value = "";//$NON-NLS-1$

  /**
   * Ok button widget.
   */
  private Button okButton;

  /**
   * Input combo widget.
   */
  private Combo combo;

  /**
   * Input check box or radio button array
   */
  private Button[] buttons;

  /**
   * Error message label widget.
   */
  private Label errorMessageLabel;

  private final int style;

  private boolean selectionRequired = true;

  private boolean cancelable = true;

  private final int imageID;

  /**
   * The list of validators
   */
  private List<ISelectionValidator> validators_;

  /**
   * _bAllowFreeEntry - Whether free entry of the value is permitted
   */
  private boolean _bAllowFreeEntry = false;




  /**
   * Creates an input dialog with OK and Cancel buttons. Note that the dialog
   * will have no visual representation (no widgets) until it is told to open.
   * <p>
   * Note that the <code>open</code> method blocks for input dialogs.
   * </p>
   * 
   * @param parentShell
   *          the parent shell, or <code>null</code> to create a top-level shell
   * @param dialogTitle
   *          the dialog title, or <code>null</code> if none
   * @param dialogMessage
   *          the dialog message, or <code>null</code> if none
   * @param choices
   *          the choices, must not be <code>null</code>
   * @param initialValue
   *          the initial input value, or <code>null</code> if none (equivalent
   *          to the empty string)
   * @param validator
   *          an input validator, or <code>null</code> if none
   * @param style
   *          the style: COMBO, RADIO, or CHECK, and optionally READ_ONLY. One
   *          and only one of COMBO, RADIO, or CHECK must be used, and READ_ONLY
   *          may optionally be used only with COMBO to make it read only.
   */
  public ChoicesDialog(Shell parentShell, String dialogTitle,
                       String dialogMessage, String[] choices,
                       String initialValue, int style)
  {
    this(parentShell,
        dialogTitle,
        SWT.NONE,
        dialogMessage,
        choices,
        initialValue,
        style);
  }




  /**
   * Creates an input dialog with OK and Cancel buttons. Note that the dialog
   * will have no visual representation (no widgets) until it is told to open.
   * <p>
   * Note that the <code>open</code> method blocks for input dialogs.
   * </p>
   * 
   * @param parentShell
   *          the parent shell, or <code>null</code> to create a top-level shell
   * @param dialogTitle
   *          the dialog title, or <code>null</code> if none
   * @param imageType
   *          the imageType to display in the dialog options are: SWT.NONE,
   *          SWT.ICON_INFORMATION, SWT.ICON_ERROR, SWT.ICON_WARNING,
   *          SWT.ICON_QUESTION, SWT.ICON_SEARCH, SWT.ICON_CANCEL,
   *          SWT.ICON_WORKING
   * @param dialogMessage
   *          the dialog message, or <code>null</code> if none
   * @param choices
   *          the choices, must not be <code>null</code>
   * @param initialValue
   *          the initial input value, or <code>null</code> if none (equivalent
   *          to the empty string)
   * @param validator
   *          an input validator, or <code>null</code> if none
   * @param style
   *          the style: COMBO, RADIO, or CHECK, and optionally READ_ONLY. One
   *          and only one of COMBO, RADIO, or CHECK must be used, and READ_ONLY
   *          may optionally be used only with COMBO to make it read only.
   */
  public ChoicesDialog(Shell parentShell, String dialogTitle, int image,
                       String dialogMessage, String[] choices,
                       String initialValue, int style)
  {
    super(parentShell);
    this.title = dialogTitle;
    message = dialogMessage;
    if (initialValue == null) {
      value = "";//$NON-NLS-1$
    }
    else {
      value = initialValue;
    }

    if (choices == null) {
      throw new IllegalArgumentException("Choices must not be null");
    }

    this.choices = choices;

    validateStyle(style);
    this.style = style;

    this.imageID = image;
  }




  private void validateStyle(int style) throws IllegalArgumentException
  {
    switch (style) {
      case COMBO:
      case RADIO:
      case CHECK:
      case COMBO | READ_ONLY:
        return;

      default:
        throw new IllegalArgumentException("Improper use of style.");
    }

  }


  /**
   * <p>Allows for entry of text into the selection box that is not
   * in the list. Not useful if read_only is set.</p>
   * @param freeEntry If true, then the user may enter text that is not
   * in the list.
   */
  public void setFreeEntry(boolean freeEntry)
  {
    _bAllowFreeEntry = freeEntry;
  }
  


  private boolean isCombo()
  {
    return (style & COMBO) == COMBO;
  }




  /*
   * (non-Javadoc) Method declared on Dialog.
   */
  protected void buttonPressed(int buttonId)
  {
    if (buttonId == IDialogConstants.OK_ID) {
      if (isCombo()) {
        value = combo.getText();
      }
      else { // check / radio
        List<String> values = new ArrayList<String>();
        for (Button button : buttons)
          if (button.getSelection()) values.add(button.getText());
        StringBuilder sb = new StringBuilder();
        if (values.size() > 0) sb.append(values.get(0));
        for (int i = 1; i < values.size(); i++)
          sb.append("," + values.get(i));
        value = sb.toString();
      }
    }
    else {
      value = null;
    }
    super.buttonPressed(buttonId);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.
   * Shell)
   */
  protected void configureShell(Shell shell)
  {
    super.configureShell(shell);
    if (title != null) {
      shell.setText(title);
    }
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse.
   * swt.widgets.Composite)
   */
  protected void createButtonsForButtonBar(Composite parent)
  {
    // create OK and Cancel buttons by default
    okButton =
        createButton(parent,
            IDialogConstants.OK_ID,
            IDialogConstants.OK_LABEL,
            true);
    if (cancelable) {
      createButton(parent,
          IDialogConstants.CANCEL_ID,
          IDialogConstants.CANCEL_LABEL,
          false);
    }
    // do this here because setting the text will set enablement on the ok
    // button
    if (isCombo()) {
      combo.setFocus();
      if (value != null) {
        combo.setText(value);
      }
    }
    else { // check/radio
      buttons[0].setFocus();
      if (value != null) {
        List<String> values = Arrays.asList(value.split(","));
        for (Button button : buttons) {
          if (values.contains(button.getText())) {
            button.setSelection(true);
          }
        }
      }
    }
  }




  @Override
  protected boolean canHandleShellCloseEvent()
  {
    return this.cancelable;
  }




  /*
   * (non-Javadoc) Method declared on Dialog.
   */
  protected Control createDialogArea(Composite parent)
  {
    // create composite
    Composite composite = (Composite) super.createDialogArea(parent);

    createMessageArea(composite);

    // create the proper widget(s)
    if (isCombo()) {
      int cStyle = SWT.NONE;
      if ((style & READ_ONLY) == READ_ONLY) {
        cStyle |= READ_ONLY;
      }

      combo = new Combo(composite, cStyle);
      combo.setItems(choices);
      combo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
          | GridData.HORIZONTAL_ALIGN_FILL));
      combo.addModifyListener(new ModifyListener() {
        public void modifyText(ModifyEvent e)
        {
          validateInput();
        }
      });
    }
    else { // check or radio
    // Group group = new Group(composite, SWT.NONE);
      buttons = new Button[choices.length];
      for (int i = 0; i < choices.length; i++) {
        Button btn = new Button(composite, style);
        btn.setText(choices[i]);
        buttons[i] = btn;
        btn.addSelectionListener(new SelectionListener() {

          public void widgetSelected(SelectionEvent e)
          {
            validateInput();
          }




          public void widgetDefaultSelected(SelectionEvent e)
          {
          }
        });
      }
    }

    // create the error text
    errorMessageLabel = new Label(composite, SWT.READ_ONLY);
    errorMessageLabel.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
        | GridData.HORIZONTAL_ALIGN_FILL));
    errorMessageLabel.setBackground(errorMessageLabel.getDisplay()
        .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

    applyDialogFont(composite);

    return composite;
  }




  private void createMessageArea(Composite parent)
  {
    Composite composite = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(2, false);
    layout.marginHeight = 0;
    layout.marginWidth = 0;
    composite.setLayout(layout);

    // create image
    if (this.imageID != SWT.NONE) {
      Label image = new Label(composite, SWT.NONE);
      image.setImage(parent.getDisplay().getSystemImage(imageID));
    }

    // create message
    if (message != null) {
      Label label = new Label(composite, SWT.WRAP);
      label.setText(message);
      GridData data =
          new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
              | GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
      data.widthHint =
          convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
      label.setLayoutData(data);
      label.setFont(parent.getFont());
    }

  }




  /**
   * Sets whether or not a selection is required. Has no affect when used with
   * {@link #COMBO} style. Default is true;
   * 
   * @param selectionRequired
   *          - whether or not at least one selection is required for the OK
   *          button to be enabled
   */
  public void setSelectionRequired(boolean selectionRequired)
  {
    this.selectionRequired = selectionRequired;
  }




  public void setCancelable(boolean cancelable)
  {
    this.cancelable = cancelable;
    setShellStyle(SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.jface.dialogs.Dialog#createContents(org.eclipse.swt.widgets
   * .Composite)
   */
  @Override
  protected Control createContents(Composite parent)
  {
    Control control = super.createContents(parent);

    validateInput();

    return control;
  }




  /**
   * Returns the ok button.
   * 
   * @return the ok button
   */
  protected Button getOkButton()
  {
    return okButton;
  }




  /**
   * Returns the string typed into this input dialog.
   * 
   * @return the input string
   */
  public String getValue()
  {
    return value;
  }




  /**
   * Validates the input.
   * <p>
   * The default implementation of this framework method delegates the request
   * to the supplied input validator object; if it finds the input invalid, the
   * error message is displayed in the dialog's message line. This hook method
   * is called whenever the text changes in the input field.
   * </p>
   */
  protected void validateInput()
  {
    String errorMessage = null;
    if (isCombo()) {
      if (! _bAllowFreeEntry) {
        if (!Arrays.asList(choices).contains(combo.getText())) {
          errorMessage =
              "The item selected is not in the list.  Please pick an item in the list.";
        }
      }
    }
    else { // check or radio
      int selectionCount = 0;
      for (Button button : buttons) {
        if (button.getSelection()) {
          selectionCount++;
        }
      }

      if (selectionRequired && selectionCount < 1) {
        switch (style) {
          case CHECK:
            errorMessage = "Please select at least one option.";
            break;
          case RADIO:
            errorMessage = "Please make a choice.";
            break;
        }
      }

      if (style == RADIO && selectionCount > 1) {
        errorMessage = "Please select only one item.";
      }
    }

    if (errorMessage == null) {
      errorMessage = runValidators();
    }

    setErrorMessage(errorMessage);
  }




  private String runValidators()
  {
    if (validators_ == null) {
      return null;
    }

    ISelection selection;
    if (isCombo()) {
      selection = new StructuredSelection(combo.getText());
    }
    else { // check / radio
      List<String> values = new ArrayList<String>();
      for (Button button : buttons) {
        if (button.getSelection()) {
          values.add(button.getText());
        }
      }

      selection = new StructuredSelection(values);
    }

    for (ISelectionValidator validator : validators_) {
      IStatus status = validator.validateSelection(selection);
      status = ValidationUtils.getSeverest(status);
      if (status.matches(IStatus.ERROR)) {
        return status.getMessage();
      }
    }

    return null;
  }




  public void addValidator(ISelectionValidator validator)
  {
    if (validator == null) {
      return;
    }

    if (validators_ == null) {
      validators_ = new ArrayList<ISelectionValidator>();
    }

    validators_.add(validator);
  }




  public boolean removeValidator(ISelectionValidator validator)
  {
    if (validator == null) {
      return false;
    }

    if (validators_ == null) {
      return false;
    }

    boolean removed = validators_.remove(validator);

    if (validators_.isEmpty()) {
      validators_ = null;
    }

    return removed;
  }




  public List<ISelectionValidator> getValidators()
  {
    if (this.validators_ == null) {
      return new ArrayList<ISelectionValidator>();
    }
    else {
      return new ArrayList<ISelectionValidator>(this.validators_);
    }
  }




  public void setValidators(List<ISelectionValidator> validators)
  {
    this.validators_ = validators;
  }




  public void removeAllValidators()
  {
    this.validators_ = null;
  }




  /**
   * Sets or clears the error message. If not <code>null</code>, the OK button
   * is disabled.
   * 
   * @param errorMessage
   *          the error message, or <code>null</code> to clear
   * @since 3.0
   */
  public void setErrorMessage(String errorMessage)
  {
    errorMessageLabel.setText(errorMessage == null ? "" : errorMessage); //$NON-NLS-1$
    okButton.setEnabled(errorMessage == null);
    errorMessageLabel.getParent().update();
  }
}
