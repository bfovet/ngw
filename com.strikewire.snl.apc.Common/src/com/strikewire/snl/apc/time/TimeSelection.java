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

package com.strikewire.snl.apc.time;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.graphics.RGB;

/**
 * A class to help with time selection for determining if a time
 * falls between two times.
 * @author kholson
 * 
 */
public class TimeSelection
{
  private static SimpleDateFormat sdfTime =
      new SimpleDateFormat("EEE yyyy-MMM-dd HH:mm:ss aa");

  /**
   * _calNow - The current date/time
   */
  private Calendar _calNow = Calendar.getInstance();
  
  /**
   * _calMin - The minimum date/time for this selection
   */
  private Calendar _calMin;
  
  /**
   * _calMax - The maximum date/time for this selection
   */
  private Calendar _calMax;

  /**
   * _name - The "name" of this selection; also used to hold the
   * formatting
   */
  private String _name = "";

  /**
   * _iconPath - An icon path for this selection
   */
  private String _iconPath = "";
  
  /**
   * _rgb - A RGB for this selection
   */
  private RGB _rgb = null;


  /**
   * Sets the minimum calendar date to now +/- the minFromNowSeconds
   */
  private void setMin(int minFromNowSeconds)
  {
    _calMin = (Calendar) _calNow.clone();
    _calMin.add(Calendar.SECOND, minFromNowSeconds);
  }



  /**
   * Sets the maximum calendar date to now +/- the minFromNowSeconds
   */
  private void setMax(int maxFromNowSeconds)
  {
    _calMax = (Calendar) _calNow.clone();
    _calMax.add(Calendar.SECOND, maxFromNowSeconds);

  }


  /**
   * Constructor which accepts a min and max in seconds, and a name 
   */
  public TimeSelection(int minFromNowSeconds, int maxFromNowSeconds, String name)
  {
    setMin(minFromNowSeconds);
    setMax(maxFromNowSeconds);
    setName(name);
  }



  /**
   * Constructor with minimum as a calendar, and maximum as seconds, and a
   * name
   */
  public TimeSelection(Calendar startTime, int maxFromNowSeconds, String n)
  {
    _calMin = (Calendar) startTime.clone();
    setMax(maxFromNowSeconds);
    setName(n);
  }



  /**
   * Constructor with minimum as seconds, maximum as a calendar, and a name
   */
  public TimeSelection(int minFromNowSeconds, Calendar endTime, String n)
  {
    setMin(minFromNowSeconds);
    _calMax = (Calendar) endTime.clone();
    setName(n);
  }


  /**
   * Constructor with min and max as calendars, and a name
   */
  public TimeSelection(Calendar startTime, Calendar endTime, String n)
  {
    _calMin = (Calendar) startTime.clone();
    _calMax = (Calendar) endTime.clone();
    setName(n);
  }



  /**
   * Allows setting the name of this TimeSelection
   */
  public void setName(String n)
  {
    _name = n;
  }



  /**
   * Returns the name of this TimeSelection
   */
  public String getName()
  {
    return _name;
  }

  /**
   * Allows setting an iconPath; may set to null
   */
  public void setIconPath(String path)
  {
    _iconPath = path;
  }
  
  /**
   * Returns the iconPath; if the setIconPath was called with null, then
   * this method will return null; by default it returns an empty String.
   */
  public String getIconPath()
  {
    return _iconPath;
  }
  
  /**
   * Allows setting an RGB to associate with this TimeSelection; may
   * set to null
   */
  public void setRGB(RGB rgbColor)
  {
    _rgb = rgbColor;
  }
  
  /**
   * Returns the RGB associated with this TimeSelection; may return null
   */
  public RGB getRGB()
  {
    return _rgb;
  }

  /**
   * Returns true if the specified chk calendar occurs prior to the 
   * minimum setting for this TimeSelection
   */
  public boolean before(Calendar chk)
  {
    return chk.before(_calMin);
  }



  /**
   * Returns true if the specified chk calendar occurs after the
   * maximum setting for this TimeSelection
   */
  public boolean after(Calendar chk)
  {
    return chk.after(_calMax);
  }



  /**
   * Returns true if the specified chk calendar falls between
   * the minimum and maximum settings for this TimeSelection
   */
  public boolean between(Calendar chk)
  {
    return (chk.after(_calMin) && chk.before(_calMax));
  }



  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  public String toString()
  {
    return _name + "; Must fall between " + sdfTime.format(_calMin.getTime())
        + " and " + sdfTime.format(_calMax.getTime());
  }



  /**
   * Returns a Calendar set to one year in the past
   */
  public static Calendar past()
  {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, -365);

    return cal;

  }



  /**
   * Returns a Calendar set to midnight of the current day as of the
   * point of the method call.
   */
  public static Calendar today_start()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH),
        0,
        0,
        0);

    return cal;
  }



  /**
   * Returns a Calendar set to 23:59:59 on the current day
   */
  public static Calendar today_end()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH),
        23,
        59,
        59);

    return cal;
  }



  /**
   * Returns a Calendar set to midnight of the day following the
   * current day as of the method invocation
   */
  public static Calendar tomorrow_start()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH) + 1,
        0,
        0,
        0);

    return cal;

  }



  /**
   * Returns a Calendar set to the last moment of tomorrow
   */
  public static Calendar tomorrow_end()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH) + 1,
        23,
        59,
        59);

    return cal;

  }



  /**
   * Calendar for the beginning of the day after tomorrow
   */
  public static Calendar day_after_start()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH) + 2,
        0,
        0,
        0);

    return cal;

  }



  /**
   * Calendar for the end of the day after tomorrow
   */
  public static Calendar day_after_end()
  {
    Calendar cal = Calendar.getInstance();
    cal.set(cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH) + 2,
        23,
        59,
        59);

    return cal;

  }



  /**
   * Calendar which returns the start of the week for the
   * current day; week start is usually Sunday
   * @see Calendar
   */
  public static Calendar this_week_start()
  {
    Calendar cal = Calendar.getInstance();
    cal.clear(Calendar.HOUR);
    cal.clear(Calendar.MINUTE);
    cal.clear(Calendar.SECOND);
    cal.clear(Calendar.MILLISECOND);

    cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());

    cal.set(Calendar.HOUR, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 1);
    cal.set(Calendar.AM_PM, Calendar.AM);



    return cal;

  }



  /**
   * Returns the end of the current week
   */
  public static Calendar this_week_end()
  {
    Calendar cal = this_week_start();
    cal.add(Calendar.DATE, 6);
    cal.set(Calendar.HOUR, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);

    return cal;

  }



  /**
   * Returns the start of next week
   */
  public static Calendar next_week_start()
  {
    Calendar cal = this_week_start();
    cal.add(Calendar.WEEK_OF_MONTH, 1);

    return cal;
  }



  /**
   * Returns the end of next week
   */
  public static Calendar next_week_end()
  {
    Calendar cal = this_week_end();
    cal.add(Calendar.WEEK_OF_MONTH, 1);

    return cal;
  }



  /**
   * Returns a Calendar 60 days in the future
   */
  public static Calendar foreseeable()
  {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DATE, 60);

    return cal;
  }



  /**
   * Returns a Calendar far in the future; you'll be dead before it
   * gets here.
   */
  public static Calendar future()
  {
    Calendar cal = Calendar.getInstance();
    cal.setTime(new Date(Long.MAX_VALUE - (1000 * 60 * 60)));

    return cal;
  }

  /**
   * A list of possible times, which may be retrieved by
   * their name, with a method ability
   */
  public enum EMacroCalendars {
    past,
    
    today_start,
    
    today_end,
    
    tomorrow_start,
    
    tomorrow_end,
    
    day_after_start,
    
    day_after_end,
    
    this_week_start,
    
    this_week_end,
    
    next_week_start,
    
    next_week_end,
    
    foreseeable,
    
    future,
    
    
    ;
    
    /**
     * Returns the Calendar associated with the Macro name
     */
    public Calendar getCalendar()
    {
      Calendar ret = Calendar.getInstance();
      try {
        final String methodName = this.toString();
        Method method = TimeSelection.class.getDeclaredMethod(methodName, 
            new Class[0]);
        ret = (Calendar)method.invoke(null, (Object[])null);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
      
      return ret;
    }
  }
  
} //class TimeSelection
