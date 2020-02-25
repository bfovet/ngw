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
 *  $Author: kolson $
 *  $Date: 2005/03/11 22:44:39 $
 *  
 *  $Name:  $ 
 *
 * FILE: 
 *  $Source: /cvs_root/snl/current/apc/plugins/com.strikewire.snl.apc.Common/src/com/strikewire/snl/apc/IDataModelEntry.java,v $
 *
 *
 * Description ($Revision: 1.1 $):
 *
 */
/*****************************************************************************/

package com.strikewire.snl.apc;

import org.eclipse.compare.IPropertyChangeNotifier;

/**
 * @author kholson
 *
 */
public interface IDataModelEntry extends IPropertyChangeNotifier {
  
  //needs methods for
  //  setting the ID
  //  for getting the particular class
  
  public String getClassname();
  
  public String getID();
}
