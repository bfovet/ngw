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

package com.strikewire.snl.apc.reporting;

import gov.sandia.dart.common.core.reporting.IStatusReporting;
import gov.sandia.dart.common.core.reporting.PluginStatusReporting;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * An Abstract class that extends AbstractUIPlugin, and provides a basic
 * implementation for the IStatusReporting interface. In essence, this abstract
 * class allows for a consistent approach to error and status logging. It is
 * based upon initial work by mjgibo, and then extended to a consistent
 * interface approach by kholson.
 * 
 * @author kholson
 * 
 */
public abstract class AbsReportingUIPlugin extends AbstractUIPlugin implements
    IStatusReporting
{
  private final PluginStatusReporting _statusReporting;



  /**
   * _log -- A Logger instance for AbsReportingUIPlugin
   */
  @SuppressWarnings("unused")
  private static final Logger _log =
      LogManager.getLogger(AbsReportingUIPlugin.class);




  /**
   * 
   */
  public AbsReportingUIPlugin(final String pluginid)
  {
    _statusReporting = new PluginStatusReporting(pluginid, this);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#newStatus(java.lang.String
   * , java.lang.Throwable, int)
   */
  @Override
  public IStatus newStatus(String message, Throwable exception, int severity)
  {
    return _statusReporting.newStatus(message, exception, severity);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#newInfoStatus(java.lang
   * .String, java.lang.Throwable)
   */
  @Override
  public IStatus newInfoStatus(String message, Throwable exception)
  {
    return _statusReporting.newInfoStatus(message, exception);
  }




  @Override
  public IStatus newInfoStatus(Throwable exception)
  {
    return _statusReporting.newInfoStatus(exception);
  }




  @Override
  public IStatus newInfoStatus(String message)
  {
    return _statusReporting.newInfoStatus(message);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#newWarningStatus(java
   * .lang.String, java.lang.Throwable)
   */
  @Override
  public IStatus newWarningStatus(String message, Throwable exception)
  {
    return _statusReporting.newWarningStatus(message, exception);
  }




  @Override
  public IStatus newWarningStatus(Throwable exception)
  {
    return _statusReporting.newWarningStatus(exception);
  }




  @Override
  public IStatus newWarningStatus(String message)
  {
    return _statusReporting.newWarningStatus(message);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#newErrorStatus(java.lang
   * .String, java.lang.Throwable)
   */
  @Override
  public IStatus newErrorStatus(String message, Throwable exception)
  {
    return _statusReporting.newErrorStatus(message, exception);
  }




  @Override
  public IStatus newErrorStatus(Throwable exception)
  {
    return _statusReporting.newErrorStatus(exception);
  }




  @Override
  public IStatus newErrorStatus(String message)
  {
    return _statusReporting.newErrorStatus(message);
  }




  /*
   * (non-Javadoc)
   * 
   * @see com.strikewire.snl.apc.reporting.IStatusReporting#newError(java.lang
   * .String, java.lang.Throwable)
   */
  @Override
  public CoreException newError(String message, Throwable exception)
  {
    return _statusReporting.newError(message, exception);
  }




  @Override
  public CoreException newError(Throwable exception)
  {
    return _statusReporting.newError(exception);
  }




  @Override
  public CoreException newError(String message)
  {
    return _statusReporting.newError(message);
  }




  @Override
  public CoreException newError(IStatus status)
  {
    return _statusReporting.newError(status);
  }




  @Override
  public IStatus mergeStatus(IStatus status1,
                             IStatus status2,
                             String multiMessage,
                             boolean allowMerge)
  {
    return _statusReporting.mergeStatus(status1, status2, multiMessage, allowMerge);
  }

  
  
  
  @Override
  public IStatus mergeStatus(IStatus status1,
                             IStatus status2,
                             String multiMessage)
  {
    return _statusReporting.mergeStatus(status1, status2, multiMessage);
  }



  
  @Override
  public IStatus mergeStatus(IStatus status1, IStatus status2, boolean allowMerge)
  {
    return _statusReporting.mergeStatus(status1, status2, allowMerge);
  }
  
  
  

  @Override
  public IStatus mergeStatus(IStatus status1, IStatus status2)
  {
    return _statusReporting.mergeStatus(status1, status2);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#log(org.eclipse.core.
   * runtime.IStatus)
   */
  @Override
  public void log(IStatus status)
  {
    _statusReporting.log(status);
  }




  @Override
  public void log(String message, Throwable exception, int severity)
  {
    _statusReporting.log(message, exception, severity);
  }




  @Override
  public void logInfo(String message, Throwable exception)
  {
    _statusReporting.logInfo(message, exception);
  }




  @Override
  public void logInfo(Throwable exception)
  {
    _statusReporting.logInfo(exception);
  }




  @Override
  public void logWarning(String message, Throwable exception)
  {
    _statusReporting.logWarning(message, exception);
  }




  @Override
  public void logWarning(Throwable exception)
  {
    _statusReporting.logWarning(exception);
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#logError(java.lang.String
   * , java.lang.Throwable)
   */
  @Override
  public void logError(String message, Throwable exception)
  {
    _statusReporting.logError(message, exception);
  }




  @Override
  public void logError(Throwable exception)
  {
    _statusReporting.logError(exception);
  }




  @Override
  public void throwStatus(IStatus status) throws CoreException
  {
    _statusReporting.throwStatus(status);
  }




  @Override
  public void throwInfo(String message, Throwable exception)
    throws CoreException
  {
    _statusReporting.throwInfo(message, exception);
  }




  @Override
  public void throwInfo(Throwable exception) throws CoreException
  {
    _statusReporting.throwInfo(exception);
  }




  @Override
  public void throwWarning(String message, Throwable exception)
    throws CoreException
  {
    _statusReporting.throwWarning(message, exception);
  }




  @Override
  public void throwWarning(Throwable exception) throws CoreException
  {
    _statusReporting.throwWarning(exception);
  }




  @Override
  public void throwError(String message, Throwable exception)
    throws CoreException
  {
    _statusReporting.throwError(message, exception);
  }




  @Override
  public void throwError(Throwable exception) throws CoreException
  {
    _statusReporting.throwError(exception);
  }
}
