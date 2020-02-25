/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.validation;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * A Class run as a Singleton, which provides the ability to obtain
 * validators useful in dialogs, etc. The basic pattern is the following:
 * <p/>
 * In a dialog, form, etc. where a Widget (e.g., Text) is added,
 * the getData(String)/setData(String, Object) methods are used to 
 * retrieve/specify a Validator. Then, at the appropriate points
 * (e.g., canFinish(), isPageComplete(), etc.), the appropriate widgets
 * are queried for a Validator, and if found, the validator is
 * invoked, and the IStatus checked against Status.OK_Status (means
 * it was validated). If not OK, the getMessage() on the IStatus may
 * be used for showing error conditions.
 * <p/>
 * If desired, a ControlDecoration may be added, which will be displayed
 * next to the field.
 * <p/>
 * <code><pre>
       txt = new Text(composite, SWT.NONE);

       SNLValidatorFactory fctry = SNLValidatorFactory.getInstance();
      
       ControlDecoration cd = fctry.makeErrorControlDecoration(txt,
          validatorHoverText);
      
       ISNLValidator validator = 
          fctry.setValidatorOnWidget(txt, fieldName, cd);
          
       validator.getValidationParams().allowEmpty(false)
         .allowLeadingWhitespace(false);


   ------
  protected IStatus validateWidget(Widget widget)
  {
    IStatus status = SNLValidatorFactory.getInstance().validateWidget(widget);

    return status;
  }
  
  ------
  public boolean isPageComplete()
  {
    boolean bRet = true;

    IStatus errStatus = null;
    IStatus tmpStatus;
    for (Widget widget : _lstWidgetsOnPage) {
      if ((tmpStatus = validateWidget(widget)) != Status.OK_STATUS) {
        bRet = false;
        errStatus = (errStatus == null ? tmpStatus : errStatus);
      }
    }

    if (errStatus != null) {
      setErrorMessage(errStatus.getMessage());
    }
    else {
      setErrorMessage(null);
    }

    
    return bRet;
  }
  
  ------
 * </pre></code>
 * <p/>
 * @author kholson
 *
 */
/**
 * @author kholson
 *
 */
public class SNLValidatorFactory
{
  /**
   * VALIDATOR - A String which may be used in the get/setData on 
   * a Widget
   */
  public static final String VALIDATOR = "Validator";
  
  /**
   * FIELD_NAME - Many messages would like to display the name of the
   * widget/field that is exhibiting a validation failure; this key
   * is used to set/find the name of the field in the get/setData()
   */
  public static final String FIELD_NAME = "field_name";
  
  private static final SNLValidatorFactory _this = new SNLValidatorFactory();
  
  
  private SNLValidatorFactory()
  {
  }
  
  
  
  public static SNLValidatorFactory getInstance()
  {
    return _this;
  }
  
  
  /**
   * Creates the control decoration
   * @param control The Control
   * @param location The location if > 0 (use SWT.TOP, BOTTOM, etc.); if
   * < 0 then uses SWT.LEFT | SWT.TOP
   * @param hoverText The text to display when hovering
   * @param decRegistry The registry icon, use
   * FieldDecorationRegistry.DEC_ERROR;, DEC_WARNING, etc.
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Oct 2, 2011
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  protected ControlDecoration makeControlDecoration(final Control control,
                                                    final int location,
                                                    final String hoverText,
                                                    final String decRegistry)
  {
    int style = (location > 0 ? location : SWT.LEFT | SWT.TOP);
    
    ControlDecoration controlDecoration =
        new ControlDecoration(control, style);
    
    controlDecoration.setDescriptionText(hoverText);

        FieldDecoration fieldDecoration =
        FieldDecorationRegistry.getDefault().getFieldDecoration(decRegistry);

    controlDecoration.setImage(fieldDecoration.getImage());

    return controlDecoration;    
    
  }
  
  
  /**
   * Makes a ControlDecoration that will be displayed on the top left
   * of the control with an error marker from 
   * FieldDecorationRegistry.DEC_ERROR, with the specified hover text. It
   * will be set on the specified control
   * @param control The control to which the error decoration will
   * apply; may not be null
   * @param hoverText The text to be displayed; may not be null, but
   * may be an empty string
   * @return A control decoration
   * @author kholson
   * <p>
   * Initial Javadoc date: Oct 1, 2011
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public ControlDecoration makeErrorControlDecoration(final Control control,
                                                      final String hoverText)
  {
    String dec = FieldDecorationRegistry.DEC_ERROR;

    return makeControlDecoration(control, -1, hoverText, dec);
  } //makeErrorControlDecoration
  
  
  
  /**
   * Makes a ControlDecoration that will be displayed on the top left of
   * the control with a warning marker for
   * @param control
   * @param hoverText
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Oct 2, 2011
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public ControlDecoration makeWarningControlDecoration(final Control control,
                                                        final String hoverText)
  {
    String dec = FieldDecorationRegistry.DEC_WARNING;

    return makeControlDecoration(control, -1, hoverText, dec);

  }
  
  public ControlDecoration makeInfoControlDecoration(final Control control,
                                                     final String hoverText)
  {
    
    String dec = FieldDecorationRegistry.DEC_INFORMATION;

    return makeControlDecoration(control, -1, hoverText, dec);

  }
  
  /**
   * Provides flexibility to obtain the control
   * @param control
   * @param hoverText
   * @param fieldDecorationRegistry FieldDecorationRegistry.DEC_WARNING,
   * DEC_ERROR, etc.
   * @param location SWT.TOP | SWT.LEFT, or other combinations; if < 0
   * then will use SWT.TOP | SWT.LEFT by default
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Oct 2, 2011
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public ControlDecoration makeControlDecoration(final Control control,
                                                 final String hoverText,
                                                 final String fieldDecorationRegistry,
                                                 final int location)
  {
    return makeControlDecoration(control, 
        location,
        hoverText, 
        fieldDecorationRegistry);
  }
  
  
  /**
   * Performs a validation against the specified widget (if it is not null).
   * If the widget does not have a Validator on it (defined in the
   * getData via VALIDATOR), the return is OK_Status (therefore, it
   * is safe to pass widgets here that do not have validators defined,
   * and the method will return an OK_STATUS). If there is a validator
   * defined, it will call the correct validation and return
   * the status accordingly.
   * 
   */
  public IStatus validateWidget(Widget widget)
  {
    IStatus status = Status.OK_STATUS;

    if (widget != null) {
      ISNLValidator validator = (ISNLValidator)widget.getData(VALIDATOR);
      
      if (validator != null) {
        status = validator.validateWidget(widget);
      }
    }
    
    return status;
  }
  
  
  /**
   * Returns the validator, if there is one, that is present on the
   * widget; may return null if no validator is present
   */
  public ISNLValidator getValidatorOnWidget(Widget widget)
  {
    ISNLValidator validator = null;
    
    if (widget != null) {
      validator = (ISNLValidator)widget.getData(VALIDATOR);
    }
    
    return validator;
  }
  
  
  public ISNLValidator setValidatorOnWidget(Widget widget,
                                      String fieldName)
      throws UnsupportedOperationException
  {
    return setValidatorOnWidget(widget, fieldName, null);
  }
  
  
  /**
   * For the given widget, ascertains its type, adds a validator to it,
   * adds the field name if it is not null, and adds the ControlDecoration
   * if not null. Returns the widget.
   * @param widget
   * @param fieldName
   * @param decor
   * @return
   * @throws UnsupportedOperationException
   * @author kholson
   * <p>
   * Initial Javadoc date: Oct 2, 2011
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public ISNLValidator setValidatorOnWidget(Widget widget,
                                      String fieldName,
                                      ControlDecoration decor)
    throws UnsupportedOperationException
  {
    ISNLValidator validator = null;

    if (fieldName != null) {
      widget.setData(FIELD_NAME, fieldName);
    }


    // 
    // try to find a validator
    //
    if (widget instanceof Text) {
      validator = new SNLTextValidator();
    }
    
    //
    // apply standard settings to the widget
    //
    if (validator != null) {
      widget.setData(VALIDATOR, validator);
      
      if (decor != null) {
        validator.setControlDecoration(decor);
      } //if : we have decor
    } //if : we got a validator
    else {
      throw new UnsupportedOperationException("Failed to find a validator for " +
          widget.getClass());
    }
    
    
    return validator;
  }
  
  
      
    
  
} //class ValidatorFactory
