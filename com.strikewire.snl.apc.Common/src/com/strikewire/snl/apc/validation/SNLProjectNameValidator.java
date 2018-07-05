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
 *  Copyright (C) 2012
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.validation;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * A validator for project names.
 * @author kholson
 *
 */
public class SNLProjectNameValidator implements IInputValidator
{

  /**
   * 
   */
  public SNLProjectNameValidator()
  {
  }




  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  public String isValid(String newText)
  {
    String ret = null;
    if (StringUtils.isBlank(newText)) {
      ret = "Name must not be empty";
    }
    else if (newText.length() < 5) {
      ret = "Name must be at least 5 characters";
    }
    else if (newText.matches(".*[/\\:\\?\\\"<>\\|\\*].*")) {
      ret = "Invalid character in name";
    }

    
    return ret;
  
  }

}
