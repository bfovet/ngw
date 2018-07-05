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

import java.util.concurrent.TimeUnit;

/**
 * A class for giving parts of time based upon the millisecond difference.
 * @author kholson
 *
 */
public class TimeDifference
{
  private final long _startSeconds;

  private final long DAY = TimeUnit.SECONDS.convert(24, TimeUnit.HOURS);
  private final long HOUR = TimeUnit.SECONDS.convert(1, TimeUnit.HOURS);
  private final long MINUTE = TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES);


  public final long days;
  public final long hours;
  public final long minutes;
  public final long seconds;




  public TimeDifference(long milliseconds)
  {
    _startSeconds = milliseconds / 1000;


    long diff = _startSeconds;

    days = diff / DAY;
    diff -= (days * DAY);

    hours = diff / HOUR;
    diff -= (hours * HOUR);

    minutes = diff / MINUTE;
    diff -= (minutes * MINUTE);

    seconds = diff;

  }



} // class TimeDifference

