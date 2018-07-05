/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/

package com.strikewire.snl.apc.Common;

import gov.sandia.dart.common.core.listeners.BaseListenersHandler;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * <p>Extends the BaseListenersHandler (which handles add/remove of
 * listeners for IPropertyChangeListener), and adds a notification system.
 * @author kholson
 *
 */
public class PropertyChangeListenersHandler extends BaseListenersHandler<IPropertyChangeListener>
{

  /**
   * Notifies all of the listeners with the specified PropertyChangeEvent
   */
  public void notifyListeners(final PropertyChangeEvent pce)
  {
    for (IPropertyChangeListener lsnr : getListeners()) {
      lsnr.propertyChange(pce);
    }
  }
}
