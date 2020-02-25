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

package gov.sandia.dart.common.core.formatting;

/**
 * Provides options on how to format a file size
 * @author kholson
 *
 */
public interface IFilesizeFmtOpts
{
  /**
   * <p>Whether the return should be a "B" (false) or "Bytes" (true). 
   * In other words, when true, and the size is in bytes (not kilobytes,
   * megabytes, etc, so < 1000/1024 bytes), it will append the word
   * "Bytes" rather than the symbol "B"<p/>
   * 
   * <p>
     <pre>
     Bytes:                true              false
         0:                0 Bytes           0 B
         1:                1 Byte            1 B
        10:               10 Bytes          10 B
        etc.
     </pre>
   * </p>
   */
  public boolean isByteAWord();
  
  /**
   * If true, and padding is removed from the return
   */
  public boolean isReturnTrimmed();
}
