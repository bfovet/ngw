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
 *  $Date: 2005/04/11 06:02:10 $
 *  
 *  $Name:  $ 
 *
 * FILE: 
 *  $Source: /cvs_root/snl/current/apc/plugins/com.strikewire.snl.apc.Common/src/com/strikewire/snl/apc/APCDataModels.java,v $
 *
 *
 * Description ($Revision: 1.3 $):
 *
 */
/*****************************************************************************/

package com.strikewire.snl.apc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

/**
 * @author kholson
 *
 */
/**
 * @author kholson
 * 
 */
public class APCDataModels extends AbsDataModelEntry
{

  /**
   * <code>_hmEntries</code> - A collection of the data model entries, keyed by
   * algorithm in <code>makeKey</code>.
   */
  protected Map<String, IDataModelEntry> _hmEntries =
      new HashMap<String, IDataModelEntry>();




  /**
   * Default constructor.
   */
  public APCDataModels()
  {
    addDataModel(this);
  }




  public int getNumDataModels()
  {
    return _hmEntries.size();
  }




  public final Collection<IDataModelEntry> getAllDataModels()
  {
    return Collections.unmodifiableCollection(_hmEntries.values());
  }




  /**
   * Adds a data model to the collection.
   * 
   * @param model
   *          Any data model that implements the <code>IDataModelEntry</code>
   *          Interface.
   */
  public void addDataModel(IDataModelEntry model)
  {
    String sKey = makeKey(model);


    if (!_hmEntries.containsKey(sKey)) {
      _hmEntries.put(sKey, model);
      notifyListeners(IAPCConstants.MCP_MODEL_ADDED, model);
    }
  }




  protected void notifyListeners(String sEvent, IDataModelEntry model)
  {
    if (_listeners == null) {
      return;
    }

    Collection<IPropertyChangeListener> allListeners =
        _listeners.getListeners();

    PropertyChangeEvent pce =
        new PropertyChangeEvent(this, sEvent, null, model);
    
    for (IPropertyChangeListener pcl : allListeners) {
      pcl.propertyChange(pce);
    }
  }




  /**
   * Returns the specified data model from the collection. Requires the
   * classname and unique identifier to find the object. If the data model is
   * not found, this method throws a new <code>ClassNotFoundException</code>. In
   * general, the calling method will have to cast the returned model to the
   * desired class.
   * 
   * @param classname
   *          The name of the class.
   * @param id
   *          The unique identifier for the class.
   * @return The data model.
   * @throws ClassNotFoundException
   *           If no data model for the specified parameters exists in the
   *           collection.
   */
  public IDataModelEntry getDataModel(String classname, String id)
    throws ClassNotFoundException
  {

    String key = makeKey(classname, id);
    IDataModelEntry retEntry = (IDataModelEntry) _hmEntries.get(key);

    if (retEntry == null) {
      throw new ClassNotFoundException("Didn't find datamodel for: \""
          + classname + "\" with id of: \"" + id + "\"");
    }

    return retEntry;
  }




  /**
   * Returns all the data models in the collection for the specified class.
   * 
   * @param classname
   *          The data model classname.
   * @return The data model.
   * @throws ClassNotFoundException
   *           If no data models for the specified class exist in the
   *           collection.
   */
  public Collection<IDataModelEntry> getDataModels(String classname)
    throws ClassNotFoundException
  {
    Collection<IDataModelEntry> colRet = new HashSet<IDataModelEntry>();

    return colRet;
  }




  /**
   * Implementation of the Interface method; returns the name of the class for
   * this instance; since this object is to be treated as a singleton, it always
   * returns the actual name of the class.
   * 
   * @see com.strikewire.snl.apc.IDataModelEntry#getClassname()
   */
  public String getClassname()
  {
    return this.getClass().getName();
  }




  /**
   * Implementation of the Interface method; returns an ID for this instance;
   * since this object is to be treated as a singleton, it always returns the
   * name of the class as the identifier.
   * 
   * @see com.strikewire.snl.apc.IDataModelEntry#getID()
   */
  public String getID()
  {
    return this.getClassname();
  }




  /**
   * Makes a key to be used for adding/finding the data model in the collection.
   * Uses same formula as <code>makeKey(String, String)</code>
   * 
   * @param model
   *          Any data model
   * @return A key
   */
  protected String makeKey(IDataModelEntry model)
  {
    String sClass = model.getClassname();
    String sID = model.getID();

    return makeKey(sClass, sID);
  }




  /**
   * Makes a key to be used for adding/finding the data model in the collection.
   * The current key is classname~id.
   * 
   * @param sClass
   *          A classname used for a data model in the collection
   * @param sID
   *          An id used for a data model in the collection.
   * @return A key.
   */
  protected String makeKey(String sClass, String sID)
  {
    return sClass + "~" + sID;
  }

} // class
