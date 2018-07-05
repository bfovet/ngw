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
 *  $Source: /cvs_root/snl/current/apc/plugins/com.strikewire.snl.apc.Common/src/com/strikewire/snl/apc/AbsDataModelEntry.java,v $
 *
 *
 * Description ($Revision: 1.1 $):
 *
 */
/*****************************************************************************/

package com.strikewire.snl.apc;

import gov.sandia.dart.common.core.listeners.BaseListenersHandler;

import java.util.Collection;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author kholson
 *
 */
public abstract class AbsDataModelEntry implements IDataModelEntry {

  /**
   * A list of registered listeners to this DataModel.
   */
  protected final BaseListenersHandler<IPropertyChangeListener> _listeners = 
      new BaseListenersHandler<IPropertyChangeListener>();

  
  /* (non-Javadoc)
   * @see org.eclipse.compare.IPropertyChangeNotifier#addPropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
   */
 public void addPropertyChangeListener(IPropertyChangeListener listener) {
    _listeners.addListener(listener);
  }
  
 /* (non-Javadoc)
  * @see org.eclipse.compare.IPropertyChangeNotifier#removePropertyChangeListener(org.eclipse.jface.util.IPropertyChangeListener)
  */
  public void removePropertyChangeListener(IPropertyChangeListener listener) {
    _listeners.removeListener(listener);
  }

  /**
   * Sends notification to all of the registered listeners that the data model
   *  has changed by sending a PropertyChangeEvent. It is likely that an
   *  implementing class would want to override this method to provide more
   *  meaningful information in the event. 
   *
   */
  protected void notifyListeners() {
    if (_listeners == null) {
      return;
    }
    
    Collection<IPropertyChangeListener> allListeners =
        _listeners.getListeners();
    

    PropertyChangeEvent pce = new PropertyChangeEvent(this, 
                                                      "1",
                                                      new Object(),
                                                      new Object());
    
    for (IPropertyChangeListener pcl : allListeners) {
      pcl.propertyChange(pce);
    }
  }
  
}
