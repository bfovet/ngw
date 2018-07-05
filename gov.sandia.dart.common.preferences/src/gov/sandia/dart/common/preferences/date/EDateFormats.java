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
 *  Copyright (C) 2017
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Jan 13, 2017
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.common.preferences.date;

import gov.sandia.dart.common.preferences.IPreferenceKey;

/**
 * @author kholson
 *
 */
public enum EDateFormats implements IPreferenceKey
{
  
  DATEFORMAT_SERVER("format.date.wbserver"),
  
  DATEFORMAT_CLIENT("format.date.client"),
  
  DATEFORMAT_JOBS("format.date.client.jobsubmission"),
  
  ;
  
  private String _pref;
  
  private EDateFormats(String txt)
  {
    _pref = txt;
  }
  
  @Override
  public String getPrefKey()
  {
    return _pref;
  }  

}
