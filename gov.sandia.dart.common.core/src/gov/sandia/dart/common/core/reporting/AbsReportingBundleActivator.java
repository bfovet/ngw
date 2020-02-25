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
 *  kholson on Apr 30, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.common.core.reporting;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * <p>An abstract class that provides reporting capabilities, and implements
 * the most basic interface (BundleActivator). In Eclipse 4, the bulk of
 * the methods in the Plugin class are deprecated (given the move to 
 * annotation rather than inheritance).<p>
 * @author kholson
 *
 */
public abstract class AbsReportingBundleActivator implements BundleActivator,
  IStatusReporting
{
  /**
   * _statusReporting - The reporting capabilities
   */
  private PluginStatusReporting _statusReporting;
  
  private static String _pluginIdForReporting;
  
  /**
   * _context - The bundle context
   */
  private BundleContext _context;
  

  /**
   * @return The context for this plugin
   * @author kholson
   * <p>Initial Javadoc date: Apr 30, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public BundleContext getContext()
  {
    return _context;
  }


  public AbsReportingBundleActivator(final String pluginId)
  {
    _pluginIdForReporting = pluginId;
  }
  
  
  public static String getPluginId()
  {
    return _pluginIdForReporting;
  }
  
  
  /**
   * <p>Initializes the reporting capabilities.</p>
   * @param bndlCtx
   * @author kholson
   * <p>Initial Javadoc date: Apr 30, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  protected void initLogging(final BundleContext bndlCtx)
  {
    _statusReporting = 
        new PluginStatusReporting(_pluginIdForReporting, bndlCtx.getBundle());
  }
  
  /**
   * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
   */
  @Override
  public final void start(BundleContext context) throws Exception
  {
    _context = context;
    initLogging(context);
    postStart(context);
  }
  
  
  @Override
  public void stop(BundleContext context) throws Exception
  {
    _context = null;
  }
  
  
  /**
   * <p>Called by the start(BundleContext) method to allow for any other
   * post start operations that may be needed. The default implementation
   * is empty.</p>
   * @param bndlCtx
   * @throws Exception
   * @author kholson
   * <p>Initial Javadoc date: Apr 30, 2015</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  protected void postStart(final BundleContext bndlCtx) throws Exception
  {
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
  
  
  
  
  @Override
  public CoreException newError(IStatus status)
  {
    return _statusReporting.newError(status);
  }


  
  @Override
  public CoreException newError(String message)
  {
    return _statusReporting.newError(message);
  }
  
  
  
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
  public IStatus mergeStatus(IStatus status1, IStatus status2, boolean allowMerge)
  {
    return _statusReporting.mergeStatus(status1, status2, allowMerge);
  }
  
  
  
  @Override
  public IStatus mergeStatus(IStatus status1, IStatus status2, String multiMessage, boolean allowMerge)
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
