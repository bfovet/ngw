/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.osgi.util;

import java.util.HashSet;
import java.util.Set;

/**
 * A class which is designed to keep track of class name registration. It
 * is frequently used with OSGI registration on a per class basis, but
 * anything that needs to do registration based upon a class where
 * an action should only be taken if the class has not previously 
 * registered may utilize this class.
 * @author kholson
 *
 */
public class OneTimeRegistration
{
  
  private final Set<Class<?>> _lstRegistered =
      new HashSet<Class<?>>();
  
  public OneTimeRegistration()
  {
  }
  
  
  /**
   * Checks to see if the specified class already is registered, and
   * returns true if it was added, false if not (false means it was
   * already registered previously).
   */
  public boolean register(Class<?> clazz)
  {
    boolean bRegistered = false;
    
    synchronized (_lstRegistered) {
      if (! _lstRegistered.contains(clazz)) {
        _lstRegistered.add(clazz);
        bRegistered = true;
      }
    }
    
    return bRegistered;
  }
  
  
}
