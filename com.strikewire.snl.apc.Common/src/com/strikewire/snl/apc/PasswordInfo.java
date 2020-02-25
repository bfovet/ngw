/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*****************************************************************************/


/*****************************************************************************/
/*
 *
 *  $Author$
 *  $Date$
 *  
 *  $Name$ 
 *
 * FILE: 
 *  $Source$
 *
 *
 * Description ($Revision$):
 *
 */
/*****************************************************************************/

package com.strikewire.snl.apc;

/**
 * @author kholson
 *
 */
public class PasswordInfo extends AbsDataModelEntry
{
  
  protected String _sUsername = "";
  protected String _sPassword = "";
  
  
  public PasswordInfo()
  {
  }
  
  public PasswordInfo(String username, String password)
  {
    _sUsername = username;
    _sPassword = password;
  }

    /**
     * Implementation of the Interface method; returns the name of the class for
     * this instance; since this object is to be treated as a singleton, it
     * always returns the actual name of the class.
     * 
     * @see com.strikewire.snl.apc.IDataModelEntry#getClassname()
     */
    public String getClassname() {
        return this.getClass().getName();
    }

    /**
     * Implementation of the Interface method; returns an ID for this instance;
     * since this object is to be treated as a singleton, it always returns the
     * name of the class as the identifier.
     * 
     * @see com.strikewire.snl.apc.IDataModelEntry#getID()
     */
    public String getID() {
        return this.getClassname();
    }

  

  /**
   * @return The password for the current user; may be an empty string.
   */
  public String getPassword()
  {
    return _sPassword;
  }


  /**
   * @param password The password for the current user.
   */
  public void setPassword(String password)
  {
    _sPassword = password;
  }


  /**
   * @return The username for the current user; may be an empty string.
   */
  public String getUsername()
  {
    return _sUsername;
  }


  /**
   * @param username The username for the current user.
   */
  public void setUsername(String username)
  {
    _sUsername = username;
  }
  

}
