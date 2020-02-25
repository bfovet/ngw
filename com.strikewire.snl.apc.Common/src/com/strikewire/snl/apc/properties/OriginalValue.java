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

package com.strikewire.snl.apc.properties;

import java.io.Serializable;

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import com.strikewire.snl.apc.Common.data.AssociableData;
import com.strikewire.snl.apc.Common.data.IAssociableData;

/**
 * A class designed to hold an "Original Value" -- that is some String that
 * represents a value that most likely came from a Text Widget, and needs to be
 * tracked and compared.
 * 
 * @author kholson
 * 
 */
public class OriginalValue implements IPropertySource, Serializable,
  IAssociableData
{
  /**
   * serialVersionUID - 
   */
  private static final long serialVersionUID = -2625336414588757725L;

  /**
   * _sbValue - The "value" of this original value
   */
  private final StringBuilder _sbValue = new StringBuilder();

  /**
   * _pdDesc - A description that shows in the PropertySource
   */
  private String _pdDesc = "Original Value";

  /**
   * OV_ID - Internal reference for the PropertySource key
   */
  private final String OV_ID = "ov.OriginalValue";
  
  
  private final IAssociableData _associatedData = new AssociableData();


  /**
   * 
   */
  public OriginalValue()
  {
  }



  /**
   * Allows attaching additional information to this OriginalValue
   * object, such as the originating widget.
   */
  public Object setData(Object object)
  {
    return _associatedData.setData(object);
  }
  
  /**
   * Gets an additional information associated with this OriginalValue
   * object; may return null if not previously set.
   */
  public Object getData()
  {
    return _associatedData.getData();
  }
  
  
  /**
   * Adds additional data to this OriginalValue object, under
   * the specified key.
   */
  public Object setData(String key, Object object)
  {
    return _associatedData.setData(key, object);
  }
  
  
  /**
   * Returns additional associated data for the specified key; may
   * return null if the specified key was not previously added.
   */
  public Object getData(String key)
  {
    return _associatedData.getData(key);
  }
  

  /**
   * Clears the original value
   */
  public void clear()
  {
    _sbValue.setLength(0);
  }




  /**
   * Sets the value for the holding original value to the .getText() from the
   * specified txtWidget. If the txtWidget is null, the holding original value
   * is merely cleared.
   */
  public void setValue(final Text txtWidget)
  {
    clear();
    if (txtWidget != null) {
      setOriginalValue(txtWidget.getText());
    }
  }




  /**
   * Sets the value for the holding original value to the specified value if the
   * specified value is not null. If it is null, the original value is merely
   * cleared.
   */
  protected void setOriginalValue(final String value)
  {
    clear();
    if (value != null) {
      _sbValue.append(value);
    }
  }




  /**
   * Returns the original value
   */
  public String getValue()
  {
    return _sbValue.toString();
  }
  
  
  /**
   * Indicates whether the value in the specified txtWidget (via getText())
   * is different than the "original" value. If the txtWidget is null,
   * returns false.
   */
  public boolean isDifferent(final Text txtWidget)
  {
    boolean bRet = false;
    
    if (txtWidget != null) {
      bRet = isDifferent(txtWidget.getText());
    }
    
    return bRet;
  }
  
  
  /**
   * Indicates whether the specified value is different from
   * the "original" value.
   */
  public boolean isDifferent(final String value)
  {
    return (! _sbValue.toString().equals(value));
  }




  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.views.properties.IPropertySource#getEditableValue()
   */
  @Override
  public Object getEditableValue()
  {
    // TODO Auto-generated method stub
    return null;
  }




  public void setPropertyDescriptorTitle(final String title)
  {
    _pdDesc = title;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#getPropertyDescriptors()
   */
  @Override
  public IPropertyDescriptor[] getPropertyDescriptors()
  {
    IPropertyDescriptor[] apd = new IPropertyDescriptor[1];

    apd[0] = new PropertyDescriptor(OV_ID, _pdDesc);

    return apd;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang
   * .Object)
   */
  @Override
  public Object getPropertyValue(Object id)
  {
    Object oRet = null;

    if (OV_ID.equals(id)) {
      oRet = _sbValue.toString();
    }

    return oRet;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#isPropertySet(java.lang
   * .Object)
   */
  @Override
  public boolean isPropertySet(Object id)
  {
    boolean bRet = false;
    if (OV_ID.equals(id)) {
      bRet = _sbValue.length() > 0;
    }
    return bRet;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#resetPropertyValue(java
   * .lang.Object)
   */
  @Override
  public void resetPropertyValue(Object id)
  {
    if (OV_ID.equals(id)) {
      _sbValue.setLength(0);
    }
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang
   * .Object, java.lang.Object)
   */
  @Override
  public void setPropertyValue(Object id, Object value)
  {
    if (OV_ID.equals(id)) {
      clear();
      if (value != null) {
        _sbValue.append(value.toString());
      }
    }
  }

} // class
