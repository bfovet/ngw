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
 *  Copyright (C) 2013
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IInputValidator;

/**
 * Ensures that the entry is only numbers or a single decimal
 * @author kholson
 *
 */
public class SNLRealNumberValidator implements IInputValidator
{

  /**
   * 
   */
  public SNLRealNumberValidator()
  {
    // TODO Auto-generated constructor stub
  }




  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
   */
  @Override
  public String isValid(String newText)
  {
    if (StringUtils.isBlank(newText)) {
      return "Entry must not be blank";
    }
    
    String regex = "^[\\d]+(\\.[\\d]{1,2})?$";
    
    Pattern pat = Pattern.compile(regex);
    
    Matcher mat = pat.matcher(newText);
    
    if (! mat.matches()) {
      return "Entry must contain only numbers, and possibly a " +
      		"single decimal followed by 1 or 2 digits";
    }
    
    return null;
  }

}
