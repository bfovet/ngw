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
 *  Copyright (C) 2014
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Mar 31, 2014
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.formatting;

import java.text.MessageFormat;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;

/**
 * A thread factory that follows from DefaultThreadFactory, but that requires a
 * naming pattern. In the absence of a pattern, it defaults to the same as
 * DefaultThreadFactory of pool-#-thread-#"; The pattern is passed to the Format
 * system, with the pool # as {0}, and the thread # as {1}
 * 
 * @author kholson
 * 
 */
public class NamingThreadFactory implements ThreadFactory
{
  /**
   * poolNumber - The total number of pools, increments for each new thread pool
   * that is created
   */
  private static final AtomicInteger totalPools = new AtomicInteger(1);

  private final int poolNumber;

  private final ThreadGroup group;

  /**
   * threadNumber - The thread number created in this pool
   */
  private final AtomicInteger threadNumber = new AtomicInteger(1);

  private static final String defaultPattern = "pool-{0}-thread[{1}]";

  private final String threadNamePattern;
  
  
  
  public NamingThreadFactory()
  {
    this(defaultPattern);
  }

  /**
   * Accepts a pattern which will be used in a call to the
   * MessageFormat, where {0} is the pool number, and {1} is the
   * thread number. Thus, the specified pattern may include {0} and/or {1}
   * to obtain a naming.
   * @see MessageFormat#format(String, Object...)
   */
  public NamingThreadFactory(final String pattern)
  {
    SecurityManager s = System.getSecurityManager();
    group =
        (s != null) ? s.getThreadGroup() : Thread.currentThread()
            .getThreadGroup();

    poolNumber = totalPools.getAndIncrement();

    threadNamePattern =
        (StringUtils.isNotBlank(pattern) ? pattern : defaultPattern);    
  }
  
  /**
   * Returns a new thread with the naming based upon the specified pattern.
   * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
   */
  @Override
  public Thread newThread(Runnable r)
  {
    String threadName =
        MessageFormat.format(threadNamePattern,
            poolNumber,
            threadNumber.getAndIncrement());

    Thread t = new Thread(group, r, threadName, 0);

    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    
    return t;
  }  
}
