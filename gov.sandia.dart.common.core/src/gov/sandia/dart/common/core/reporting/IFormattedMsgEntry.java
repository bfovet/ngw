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
 *  Copyright (C) 2015
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Sep 16, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.common.core.reporting;

/**
 * <p>A class that provides a "message" that <i>may</i> contain embedded formatting
 * that is later processed.</p>
 * <p>Example:</p>
 * <pre>"The session key type is {0}"</pre>
 * <pre>"The session key authorized at {0,date,yyyy-MM-dd hh:mm }"</pre>
 * 
 * 
 * @author kholson
 *
 */
public interface IFormattedMsgEntry
{

  /**
   * Returns the "message" of the object
   */
  public String getMessage();
}
