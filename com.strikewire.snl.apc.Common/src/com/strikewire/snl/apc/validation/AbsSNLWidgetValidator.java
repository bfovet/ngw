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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author kholson
 *
 */
public abstract class AbsSNLWidgetValidator implements ISNLValidator,
    Serializable
{
  private final static IStatus defErrorStatus =
      new Status(Status.ERROR, CommonPlugin.ID, "Widget Validation undefined");

  /**
   * serialVersionUID - 
   */
  private static final long serialVersionUID = -4524705523079944182L;


  /**
   * validationParams - The validation parameters in use for this widget
   */
  protected SNLValidationParams validationParams = new SNLValidationParams();
  
  protected ControlDecoration controlDecoration = null;
  
  protected String fieldName = "";

  /**
   * 
   */
  public AbsSNLWidgetValidator()
  {
  }

  
  public void setControlDecoration(ControlDecoration decor)
  {
    controlDecoration = decor;
  }
  
  public ControlDecoration getControlDecoration()
  {
    return controlDecoration;
  }

  public SNLValidationParams getValidationParams()
  {
    return validationParams;
  }


  /* (non-Javadoc)
   * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
   */
  public abstract IStatus validate(Object value);




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.validation.ISNLValidator#validateWidget(org.eclipse.swt.widgets.Widget)
   */
  public IStatus validateWidget(Widget widget)
  {
    IStatus retStatus = defErrorStatus;
    
    String tmpFN = (String)widget.getData(SNLValidatorFactory.FIELD_NAME);
    
    if (tmpFN != null) {
      fieldName = tmpFN;
    }
    
    if (widget instanceof Text) {
      Text tw = (Text)widget;
      
      String data = tw.getText();
      
      retStatus = validate(data);
    }
    
    return retStatus;
  }
  
  public void setValidationParams(SNLValidationParams params) {
	  validationParams = params;
  }

}
