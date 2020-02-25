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

package com.strikewire.snl.apc.properties.sections;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.swt.widgets.Button;

/**
 * A given section will usually contain:
 * <ul>
 * <li>An edit button, that may pop-up a dialog</li>
 * <li>two buttons, accept and cancel</li>
 * </ul>
 * <p>
 * Generally, the current approach is a three-column like layout, with a label,
 * a text field, and an edit or accept/cancel buttons. There are some sections
 * that have more than one three column layout. Also, it is preferred that the
 * most base class, where ever possible, handles the disposing of images, etc.
 * </p>
 * 
 * <p>
 * This class holds buttons, and provides a means to access them. When creating
 * the layout, this object allows for handling the accessing of the buttons
 * </p>
 * 
 * @author kholson
 * 
 */
public class SectionButtons implements Serializable
{
  /**
   * serialVersionUID -
   */
  private static final long serialVersionUID = 4258874876662312474L;


  protected final String _uuid = UUID.randomUUID().toString();

  private enum EButtonRole
  {
    Edit, Accept, Cancel,

  };

  private transient final Map<EButtonRole, Button> _mButtons =
      new HashMap<>();




  /**
   * 
   */
  public SectionButtons()
  {
  }




  public Collection<Button> getAllocatedButtons()
  {
    return Collections.unmodifiableCollection(_mButtons.values());
  }




  /**
   * @return the _btnEdit
   */
  public Button getEditButton()
  {
    return _mButtons.get(EButtonRole.Edit);
  }




  /**
   * @param _btnEdit
   *          the _btnEdit to set
   */
  public void setEditButton(Button btnEdit)
  {
    _mButtons.put(EButtonRole.Edit, btnEdit);
  }




  /**
   * @return the _btnAccept
   */
  public Button getAcceptButton()
  {
    return _mButtons.get(EButtonRole.Accept);
  }




  /**
   * @param _btnAccept
   *          the _btnAccept to set
   */
  public void setAcceptButton(Button btnAccept)
  {
    _mButtons.put(EButtonRole.Accept, btnAccept);
  }




  /**
   * @return the _btnCancel
   */
  public Button getCancelButton()
  {
    return _mButtons.get(EButtonRole.Cancel);
  }




  /**
   * @param _btnCancel
   *          the _btnCancel to set
   */
  public void setCancelButton(Button btnCancel)
  {
    _mButtons.put(EButtonRole.Cancel, btnCancel);
  }




  public boolean hasEditButton()
  {
    return (_mButtons.get(EButtonRole.Edit) != null);
  }




  public boolean hasAcceptButton()
  {
    return (_mButtons.get(EButtonRole.Accept) != null);

  }




  public boolean hasCancelButton()
  {
    return (_mButtons.get(EButtonRole.Cancel) != null);
  }




  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    return _uuid.hashCode();
  }




  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    boolean bRet = false;

    if (this == obj) {
      bRet = true;
    }
    else if (obj instanceof SectionButtons) {
      bRet = (_uuid.equals(((SectionButtons) obj)._uuid));
    }

    return bRet;
  }




  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();

    sb.append("id: ");
    sb.append(_uuid);

    for (EButtonRole btn : EButtonRole.values()) {
      sb.append(" ");
      sb.append(btn.toString());
      sb.append(":");
      sb.append(_mButtons.get(btn) != null);
    }

    return sb.toString();
  }




}
