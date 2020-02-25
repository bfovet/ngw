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
 *  Copyright (C) 2013
 *  Sandia National Laboratories
 *
 *  File originated by:
 *    kholson on Aug 23, 2013
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.gui.propertydescriptors;

/**
 * <p>This interface helps support classes which allow for multiple selections
 * on given objects. The Eclipse property page really only supports a single
 * selected object. Selecting multiple objects usually causes the view to
 * blank out. In implementing the SettingsView, and perhaps in other
 * areas, there are IPropertyDescriptor implementations that can support
 * multiple selections. However, the way in which the values are to be
 * displayed in the case of multiple selections is not always clear. In
 * some cases, it works well to concatinate the values together; sometimes
 * it is unique displays; some times it is summation.
 * </p>
 * <p>This Interface is an Identifying interface without methods.
 * </p>
 * <p>To support multiple selection, an implementing class should
 * &quot;implement&quot; this interface, and provide an ILableProvider to
 * that will support a Collection&lt;Object&gt; in the .getText() method
 * </p>
 * 
 * @author kholson
 *
 */
public interface IMultiSelectPropertyDescriptor
{

  public boolean supportsMultiEditing();
  
}
