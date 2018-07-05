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
 *
 * Copyright (C) 2005,2006
 *    
 *  All Rights Reserved
 *
 *  StrikeWire, LLC
 *  368 South McCaslin Blvd., #115
 *  Louisville, CO 80027
 *  (720) 890-8591
 *  support@strikewire.com
 *
 *  COMPANY PROPRIETARY
 *
 */
/*---------------------------------------------------------------------------*/

/*---------------------------------------------------------------------------*/
/*
 *
 *  $Author$
 *  $Date$
 *  
 * FILE: 
 *  $Source$
 *
 *
 * Description ($Revision$):
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.formatting;

import gov.sandia.dart.common.preferences.CommonPreferencesPlugin;
import gov.sandia.dart.common.preferences.date.EDateFormats;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.IPreferenceStore;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Provides utilities for parsing and formatting dates
 * @author kholson
 *
 */
public class APCDateFormatter implements IAPCDateFormatter
{
  /**
   * daysHoursMinutesSeconds - A Joda date period formatter for
   * formatting time
   */  
  private static final PeriodFormatter daysHoursMinutesSeconds =
      new PeriodFormatterBuilder().printZeroRarelyLast()
      .appendDays()
      .appendSuffix(" day", "days")
      .appendSuffix(" ")
      .appendHours()
      .appendSuffix(" hour", " hours")
      .appendSuffix(" ")
      .appendMinutes()
      .appendSuffix(" minute", " minutes")
      .appendSuffix(" ")
      .appendSeconds()
      .appendSuffix(" second", " seconds")
      .toFormatter();
  
  private static final Map<EDateFormats, SimpleDateFormat> _cachedFormat = new HashMap<EDateFormats, SimpleDateFormat>();
  
  APCDateFormatter()
  {
  }
  
  /**
   * For the given time in milliseconds, return a formatted String,
   * as done by SimpleDateFormat, based upon the preference
   * for the formatting as defined by eFormat
   */
  @Override
  public String formatTimestamp(EDateFormats eFormat,
                                       final long millisecondTime)
  {
	Date date = new Date(millisecondTime);
    
    return formatTimestamp(eFormat, date);
  }
  
  
  /*
   * (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IAPCDateFormatter#formatTimestamp(com.strikewire.snl.apc.Common.EDateFormats, java.util.Calendar)
   */
  @Override
  public String formatTimestamp(EDateFormats eFormat,
                                       final Calendar cal)
  {
	Date date = cal.getTime();
    
	return formatTimestamp(eFormat, date);
  }
  
  /*
   * (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IAPCDateFormatter#formatTimestamp(com.strikewire.snl.apc.Common.EDateFormats, java.util.Date)
   */
  @Override
  public String formatTimestamp(EDateFormats eFormat, Date date)
  {
    String ret = "";
    
    SimpleDateFormat sdf = getTimestampFormatter(eFormat);
    
    ret = sdf.format(date);
    
    return ret;
    
  }
  
  /**
   * Parses to a date value, based upon the format of the
   * server preference in the preference store for
   * EDateFormats.DATEFORMAT_SERVER, the String, which is
   * the timestamp returned from the server.
   */
  @Override
  public Calendar parseServerTimestamp(final String serverTS)
    throws ParseException
  {
	
    SimpleDateFormat sdf = getTimestampFormatter(EDateFormats.DATEFORMAT_SERVER);
    
    Date dte = sdf.parse(serverTS);
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(dte);
    
    return cal;
  }
  
  
  /*
   * (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IAPCDateFormatter#getTimestampFormat(com.strikewire.snl.apc.Common.EDateFormats)
   */
  @Override
  public String getTimestampFormat(EDateFormats eFormat)
  {
    String fmt = "yyyy-MM-dd HH:mm:ss z";

    CommonPreferencesPlugin plugin = CommonPreferencesPlugin.getDefault();
    
    
    IPreferenceStore prefStore = null;
    
    if (plugin != null) {
      prefStore = plugin.getPreferenceStore();
    }
    
    switch (eFormat) {
      case DATEFORMAT_CLIENT:
      case DATEFORMAT_JOBS:
      case DATEFORMAT_SERVER:
        if (prefStore != null) {
          fmt = prefStore.getString(eFormat.getPrefKey());
        }
        break;
        
      default:
        break;
    } //switch

    return fmt;
  }
  
	public SimpleDateFormat getTimestampFormatter(EDateFormats eFormat)
	{
		synchronized (_cachedFormat)
		{
			SimpleDateFormat format = _cachedFormat.get(eFormat);
			String formatStr = getTimestampFormat(eFormat);
			if(format==null || !StringUtils.equals(formatStr, format.toPattern()))
			{
				format = new SimpleDateFormat(formatStr);
				_cachedFormat.put(eFormat, format);
			}
			return format;
		}
	}
  
  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IAPCDateFormatter#getPeriodInDaysHoursMinutesSeconds(org.joda.time.Period)
   */
  @Override
  public String formatPeriodInDaysHoursMinutesSeconds(Period period)
  {
    String ret = daysHoursMinutesSeconds.print(period);
    
    return ret;
  }
 
  
  /* (non-Javadoc)
   * @see com.strikewire.snl.apc.formatting.IAPCDateFormatter#daysHoursMinutesSecondsToMillis(long, long, long, long)
   */
  @Override
  public long daysHoursMinutesSecondsToMillis(final long days,
                                              final long hours,
                                              final long minutes,
                                              final long seconds)
  {
    final long years = 0L, months = 0L, weeks = 0L, millis = 0L;
    Period period = new Period((int)years,
        (int)months,
        (int)weeks,
        (int)days,
        (int)hours,
        (int)minutes,
        (int)seconds,
        (int)millis);
    
    return (period.toStandardDuration().getStandardSeconds() * 1000);
  }

} //class
