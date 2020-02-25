/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *
 * Copyright (C) 2005-2011
 *   Sandia National Laboratories
 *    
 *  All Rights Reserved
 *
 * Developed under contract by:
 *  StrikeWire, LLC
 *  149 South Briggs St., Suite 102-A
 *  Erie, CO 80516
 *  (720) 890-8591
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/*
 *
 *  $Author$
 *  $Date$
 *  
 * FILE: 
 *  $Source$
 *
 *
 * Description ($Revision$):
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.validation;

import java.io.Serializable;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.strikewire.util.SWflowControlException;

/**
 * @author kholson
 *
 */
public class SNLTextValidator extends AbsSNLWidgetValidator implements
    ISNLValidator, IValidator, Serializable
{

  /**
   * serialVersionUID - 
   */
  private static final long serialVersionUID = 6222767491436521332L;




  /**
   * 
   */
  public SNLTextValidator()
  {
  }




  /**
   * Validate a Text widget, where the specified value will be a String
   * (error otherwise) against the settings in the 
   * validation parameters.
   * @see com.strikewire.snl.apc.validation.AbsSNLWidgetValidator#validate(java.lang.Object)
   */
  @Override
  public IStatus validate(Object value)
  {
    IStatus retStatus = Status.OK_STATUS;
    
    // this is null unless we have an error; set to non-null, to
    // display the controlDecoration if it is not null
    String errorText = null;

    try {
      // make sure we have a String : null fails this check
      if (! (value instanceof String)) {
        errorText =
            Messages.formatString("SNLTextValidator.fmt.NotAString", //$NON-NLS-1$
                (value != null ? value.getClass().getName() : "(null object)"));

        retStatus = ValidationStatus.error(errorText);
            
        throw new SWflowControlException();
      }
      
      
      
      String data = (String)value;
      
      
      //
      // -- not empty
      // if we don't allow empty strings, then may not be 0 len or
      // just spaces
      if (! validationParams.getAllowEmpty() && StringUtils.isBlank(data)) {
        errorText =
            Messages.formatString("SNLTextValidator.fmt.NonEmptyEntryRequired", //$NON-NLS-1$
                fieldName);

        retStatus = ValidationStatus.error(errorText);
        
        throw new SWflowControlException();
      }
      
      
      
      
      //
      // -- minimum length : we've already screened out disallowing
      //   empty && blank, but it is possible to be:
      //   *allow empty && blank && minLength > 0 : which is not
      //      an error condition
      //   *spaces only : check only against length; leading/trailing
      //     is checked below
      //
      if (validationParams.getMinLength() > 0) {
        
        // empty && blank; OK so leave
        if (validationParams.getAllowEmpty() && StringUtils.isBlank(data)) {
          throw new SWflowControlException();
        }
        
        if (data.length() < validationParams.getMinLength()) {
          errorText =
              Messages.formatString("SNLTextValidator.fmt.MinimumLengthRequired", //$NON-NLS-1$
                  Integer.toString(validationParams.getMinLength()),
                  fieldName);

          retStatus = ValidationStatus.error(errorText);
          throw new SWflowControlException();
        }
        
      } //if : minimum length
      
      
      //
      // -- maximum length
      //
      if (validationParams.getMaxLength() > 0) {
        if (data.length() > validationParams.getMaxLength()) {
          errorText =
              Messages.formatString("SNLTextValidator.fmt.MaximumLengthExceeded", //$NON-NLS-1$
                  Integer.toString(validationParams.getMaxLength()),
                  fieldName);

          retStatus = ValidationStatus.error(errorText);
          throw new SWflowControlException();
        }
      }
      
      
      //
      // -- no leading whitespace
      //
      if (! validationParams.getAllowLeadingWhitespace()) {
        Matcher matchLeadingWhiteSpace = 
            validationParams.getPatLeadingWhitespace().matcher(data);
        boolean matches = matchLeadingWhiteSpace.matches();
        if (matches) {
          errorText =
              Messages.formatString("SNLTextValidator.fmt.NoLeadingWhitespace", //$NON-NLS-1$
                  fieldName);
          
          retStatus = ValidationStatus.error(errorText);
          throw new SWflowControlException();
        }
      }      
      
      
      //
      // -- no trailing whitespace
      //
      if (! validationParams.getAllowTrailingWhitespace()) {
        Matcher matchTrailingWhitespace =
            validationParams.getPatTrailingWhitespace().matcher(data);
        boolean matches = matchTrailingWhitespace.matches();
        
        if (matches) {
          errorText =
              Messages.formatString("SNLTextValidator.fmt.NoTrailingWhitespace", //$NON-NLS-1$
                  fieldName);
          
          retStatus = ValidationStatus.error(errorText);
          throw new SWflowControlException();
        }
      }
      
      
      // spaces in the string
      if (! validationParams.getAllowSapces()) {
        if (data.matches(".*\\s+.*")) {
          errorText =
              Messages.formatString("SNLTextValidator.fmt.SpacesNotAllowed", //$NON-NLS-1$
                  fieldName);
          
          retStatus = ValidationStatus.error(errorText);
          throw new SWflowControlException();
        }
      }
      
      if (validationParams.getAsEmailAddress()) {
        Matcher matchEmail =
            validationParams.getPatEmailAddress().matcher(data);
        boolean matches = matchEmail.matches();
        if (! matches) {
          errorText =
              Messages.formatString("SNLTextValidator.fmt.InvalidEmailAddress", //$NON-NLS-1$
                  fieldName);
          
          retStatus = ValidationStatus.error(errorText);
          throw new SWflowControlException();
        }
      }
      
    } //try
    catch (SWflowControlException noop) {
    }
    catch (Exception e) {
    }
    finally {
      if (controlDecoration != null) {
        if (errorText != null) {
          controlDecoration.show();
        }
        else {
          controlDecoration.hide();
        }
      }
      
    } //finally
    
    
    return retStatus;
  }

}
