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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages
{
  private static final String BUNDLE_NAME =
      "com.strikewire.snl.apc.validation.messages"; //$NON-NLS-1$

  private static final ResourceBundle RESOURCE_BUNDLE =
      ResourceBundle.getBundle(BUNDLE_NAME);




  private Messages()
  {
  }




  public static String getString(String key)
  {
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
  
  
  public static String formatString(String key,
                                    String ... args)
  {
    String fmtStr = getString(key);
    
    return MessageFormat.format(fmtStr, (Object[])args);
  }
  
}
