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

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * <p>Represents an entry for some history.</p>
 * @author kholson
 *
 */
public interface IHistoryEntry
{
  
  /**
   * Returns the message/text about this history entry
   */
  public IFormattedMsgEntry getHistory();
  
  /**
   * Returns supplemental details about this history entry; the
   * return may be an empty optional if no raw detail is available
   */
  public Optional<Object> getRawDetail();
  
  
  /**
   * Returns the timestamp for this history entry
   */
  public LocalDateTime getDateTime();

} //interface
