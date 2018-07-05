/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.formatting;

import gov.sandia.dart.common.preferences.date.EDateFormats;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.Period;




/**
 * Methods to assist with the formatting of dates, especially with those
 * relating to the DART Server. These formats are bound with the
 * preference store
 * 
 */
public interface IAPCDateFormatter
{
  /**
   * For the given time in milliseconds, return a formatted String,
   * as done by SimpleDateFormat, based upon the preference
   * for the formatting as defined by eFormat
   */
  public String formatTimestamp(EDateFormats eFormat,
                                       final long millisecondTime);
  
 
  /**
   * Formats the time in the calendar to the specified format
   * retrieved from the preference store.
   */
  public String formatTimestamp(EDateFormats eFormat,
                                final Calendar cal);
  
  /**
   * Formats the time in the date to the specified format
   * retrieved from the preference store.
   */
  public String formatTimestamp(EDateFormats eFormat, final Date date);
  
  /**
   * Parses to a date value, based upon the format of the
   * server preference in the preference store for
   * EDateFormats.DATEFORMAT_SERVER, the String, which is
   * the timestamp returned from the server.
   */
  public Calendar parseServerTimestamp(final String serverTS)
    throws ParseException;  
  
  /**
   * Returns the String representation for the specified format,
   * e.g. "yyyy-MM-dd HH:mm:ss z";
   */
  public String getTimestampFormat(EDateFormats eFormat);  
  
  
  /**
   * Returns a formatted String as
   * # day(s) # hour(s) # minute(s) # second(s), where the
   * plural form is only if the amount != 1, and includes no
   * more than necessary to display the amount. For example,
   * if the period is the millsecond equivalent of 100 minutes 
   * (1 hour, 40 minutes), the
   * output would be: "1 hour, 40 minutes". An input of
   * the millisecond equivalent of 60 minutes would show
   * "1 hour". The input for 60 seconds would show as "1 minute".
   */
  public String formatPeriodInDaysHoursMinutesSeconds(Period period);
  
  
  /**
   * Takes the input, and returns it as milliseconds, which is:
   * <ul>
   * <li>days * 24 * 60 * 60</li>
   * <li>hours * 60 * 60</li>
   * <li>minutes * 60</li>
   * <li>seconds</li>
   * <li>all multiplied by 1000</li>
   * </ul>
   * <p>Note: for a large enough input, this could overflow. Note also
   * that the inputs are cast to (int), so large enough values could
   * also lose precision.</p>
   * 
   * <p>Sample Usage:</p>
   <pre>
     IAPCDateFormatter df = FormatterFactory.dateFormatter();
     long millis = _dateFormatter.daysHoursMinutesSecondsToMillis(0,
        hours,
        minutes,
        seconds);
     Period period = new Period(millis);
     String fmtdDate = _dateFormatter.formatPeriodInDaysHoursMinutesSeconds(period);
   </pre>
   */
  public long daysHoursMinutesSecondsToMillis(final long days,
                                              final long hours,
                                              final long minutes,
                                              final long seconds);
  
}
