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
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author kholson
 * 
 */
public class ISODateFormatter
{
  private static final String ISO_DATE = "yyyy-MM-dd";
  private static final String ISO_TIME = "HH:mm:ss z";

  private static final SimpleDateFormat _sdfTime =
      new SimpleDateFormat(ISO_TIME);

  private static final SimpleDateFormat _sdfDate =
      new SimpleDateFormat(ISO_DATE);


  private Calendar _theTime = Calendar.getInstance();




  /**
   * 
   */
  public ISODateFormatter()
  {
  }




  public ISODateFormatter(Calendar cal)
  {
    _theTime = cal;
  }




  /**
   * Formats the time into an 24h time with time zone: <code>08:34:07 MST</code>
   * 
   * @return The time in the format HH:mm:ss z
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 4, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  public String timeISO()
  {
    return _sdfTime.format(_theTime.getTime());
  }




  /**
   * Formats the time into the ISO format: <code>2013-03-04</code>
   * 
   * @return The date in the format yyyy-MM-dd
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 4, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  public String dateISO()
  {
    return _sdfDate.format(_theTime.getTime());
  }




  /**
   * Formats the time into an ISO date with 24 hour time:
   * <code>2013-03-04 08:34:07 MST</code>
   * 
   * @return The date and time in the format yyyy-MM-dd HH:mm:ss z
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 4, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  public String dateTimeISO()
  {
    StringBuilder sb = new StringBuilder();

    sb.append(_sdfDate.format(_theTime.getTime()));
    sb.append(" ");
    sb.append(_sdfTime.format(_theTime.getTime()));

    return sb.toString();
  }




  /**
   * Formats the time into the dateTimeISO, replacing all spaces with
   * underscores and all colons with dashes. Should be file safe on all
   * operating systems.
   * 
   * @return
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 4, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  public String filesafeDateTimeISO()
  {
    String datetime = dateTimeISO();

    datetime = datetime.replaceAll(" ", "_");
    datetime = datetime.replaceAll(":", "-");

    return datetime;
  }




  public static void main(String[] args)
  {
    ISODateFormatter df = new ISODateFormatter();

    System.out.println(df.dateTimeISO());
    System.out.println(df.filesafeDateTimeISO());
  }

}
