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
 * An implementation of IFilesizeFmtOpts that allows for setting
 * the values. By default, sets:
 * <ul>
 * <li>isByteAWord : false (will display "B" for bytes)</li>
 * <li>isReturnTrimmed : false (results are padded)</li>
 * </ul>
 * <p>May be called as:<p/>
 * <pre>
   IFileSizeFmtOpts fmtOpts = 
     new FilesizeFmtOpts().setByteAsWord(true)
       .setReturnTrimmed(true);
 * </pre>
 * @author kholson
 *
 */
public class FilesizeFmtOpts implements IFilesizeFmtOpts
{
  private boolean _bByteAsWord = false;
  private boolean _bTrimResult = false; 
  
  
  public FilesizeFmtOpts setByteAsWord(boolean asWord)
  {
    _bByteAsWord = asWord;
    return this;
  }
  
  
  public FilesizeFmtOpts setReturnTrimmed(boolean trim)
  {
    _bTrimResult = trim;
    return this;
  }
  
  
  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IFilesizeFmtOpts#isByteAWord()
   */
  @Override
  public boolean isByteAWord()
  {
    return _bByteAsWord;
  }




  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IFilesizeFmtOpts#trimReturn()
   */
  @Override
  public boolean isReturnTrimmed()
  {
    return _bTrimResult;
  }

}
