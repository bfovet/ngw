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

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Widget;

/**
 * Extends IValidator by adding a validateWidget method; this approach
 * allows adding an ISNLValidator to any given widget, and then if it
 * is present, call against the widget.
 * <p/>
 * <code>
 * ISNLValidator validator = (ISNLValidator)widget.getData("Validator");
 * if (validator != null) {
 *   validator.validateWidget(widget);
 * }
 * </code>
 * <p/>
 * The validateWidget should ascertain the type of widget (e.g., Text,
 * Combo, etc.), and then pass the correct value(s) to
 * validate(...) which is specified in IValidator
 * @author kholson
 *
 */
public interface ISNLValidator extends IValidator
{

  /**
   * Validates the specified widget by (a)ascertaining the type
   * of the widget, obtaining the appropriate value(s) from the
   * widget, and then calling validate(Object) on it, returning
   * that status.
   */
  public IStatus validateWidget(final Widget widget);
  
  
  /**
   * Sets a control decoration which will be displayed for this
   * validator, if desired.
   */
  public void setControlDecoration(final ControlDecoration decor);
  
  /**
   * Obtains the control decoration for this validator; may be null
   * if not set
   */
  public ControlDecoration getControlDecoration();
  
  /**
   * Get the validation parameters that are being used on this validator
   */
  public SNLValidationParams getValidationParams();

  /**
   * Set validation parameters for this validator
   * To be used when defining a parameter object oneself; shallow copy only
   * @param params
   */
  public void setValidationParams(SNLValidationParams params);
  
} //interface
