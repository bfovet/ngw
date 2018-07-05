/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc;

import java.util.Set;

import org.eclipse.jface.dialogs.IDialogSettings;

/**
 * Extends the basic DialogSettings to provide:
 * <ul>
 * <li>The ability to remove a setting</li>
 * <li>The ability to enumerate the keys within a section</li>
 * </ul>
 * In essence, the DialogSettings is a large association of 
 * IDialogSettings, with a root node (the initial section passed
 * to the constructor of the DialogSettings), and 0..N additional
 * IDialogSettings. Each entry, including the root, may have N key/value
 * pairs associated with it.
 * @author kholson
 *
 *@see IDialogSettings
 */
public interface IDARTDialogSettings extends IDialogSettings
{
  /**
   * Within this "section" (since all sections are IDialogSettings), 
   * return all of the keys within it. If there are no keys, this
   * method returns an empty Set 
   * @param section
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 24, 2010
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public Set<String> getKeys();
  
  
  /**
   * Adds a new section to the current settings and returns it. If the
   * specified name already exists, returns the existing one without
   * creating a new one.
   * @see org.eclipse.jface.dialogs.IDialogSettings#addNewSection(java.lang.String)
   */
  public IDARTDialogSettings addNewSection(String name);
  
  
  /**
   * Removes the specified key from the "section", and returns the value
   * (which may be either a String or String[]). The specified key is
   * not found, throws an IllegalArgumentException (so that null may
   * be distinguished as a valid return).
   * @param key
   * @return
   * @author kholson
   * <p>
   * Initial Javadoc date: Sep 24, 2010
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  public Object removeSetting(String key);
  
}
