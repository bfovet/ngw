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

package gov.sandia.dart.common.core.reporting;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;

/**
 * A class that supports stats reporting. It provides methods for creating new
 * IStatus objects at various levels (e.g., info, error), and writing these
 * messages to the Eclipse logging.
 * 
 * @author kholson
 *
 */
public class PluginStatusReporting implements IStatusReporting
{
  /**
   * _pluginId - The ID of the plugin, used in reporting
   */
  private final String _pluginId;
  
  /**
   * _plugin - The plugin; used for logging
   */
  private final Plugin _plugin;

  /**
   * _bundle - An alternative approach for logging, use the bundle rather
   * than the plugin
   */
  private final Bundle _bundle;



  /**
   * Initialize with an ID and a plugin
   */
  public PluginStatusReporting(String pluginId, Plugin plugin)
  {
    _plugin = plugin;
    _pluginId = pluginId;
    _bundle = null;
  }

  
  /**
   * Initialize with a bundle
   * @param pluginId
   * @param bundle
   */
  public PluginStatusReporting(String pluginId, Bundle bundle)
  {
    _plugin = null;
    _pluginId = pluginId;
    _bundle = bundle;
  }



  protected String getMessage(String message)
  {
    return message != null ? message : "";
  }




  protected String getMessage(Throwable exception)
  {
    return exception != null ? exception.getMessage() : "null exception";
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
    String msg = getMessage(message);
    IStatus status = new Status(severity, _pluginId, msg, exception);
    return status;
  }




  @Override
  public IStatus newInfoStatus(String message)
  {
    IStatus status = newInfoStatus(message, null);
    return status;
  }




  @Override
  public IStatus newInfoStatus(Throwable exception)
  {
    String msg = getMessage(exception);
    IStatus status = newInfoStatus("Internal info: " + msg, exception);
    return status;
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
    IStatus status = newStatus(message, exception, IStatus.INFO);
    return status;
  }




  @Override
  public IStatus newWarningStatus(String message)
  {
    IStatus status = newWarningStatus(message, null);
    return status;
  }




  @Override
  public IStatus newWarningStatus(Throwable exception)
  {
    String msg = getMessage(exception);
    IStatus status = newWarningStatus("Internal warning: " + msg, exception);
    return status;
  }




  @Override
  public IStatus newWarningStatus(String message, Throwable exception)
  {
    IStatus status = newStatus(message, exception, IStatus.WARNING);
    return status;
  }




  @Override
  public IStatus newErrorStatus(String message)
  {
    IStatus status = newErrorStatus(message, null);
    return status;
  }




  @Override
  public IStatus newErrorStatus(Throwable exception)
  {
    String msg = getMessage(exception);
    IStatus status = newErrorStatus("Internal error: " + msg, exception);
    return status;
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
    IStatus status = newStatus(message, exception, IStatus.ERROR);
    return status;
  }



  @Override
  public CoreException newError(String message)
  {
    CoreException error = newError(message, null);
    return error;
  }




  @Override
  public CoreException newError(Throwable exception)
  {
	IStatus status = newErrorStatus(exception);
    CoreException error = newError(status);
    return error;
  }




  /*
   * (non-Javadoc)
   * 
   * @see
   * com.strikewire.snl.apc.reporting.IStatusReporting#newError(java.lang
   * .String, java.lang.Throwable)
   */
  @Override
  public CoreException newError(String message, Throwable exception)
  {
    IStatus status = newErrorStatus(message, exception);
    CoreException error = newError(status);
    return error;
  }


  @Override
  public CoreException newError(IStatus status)
  {
    CoreException error = new CoreException(status);
    return error;
  }
  
  
  
  /* (non-Javadoc)
	 * @see com.strikewire.snl.apc.reporting.IStatusReporting#mergeStatus(org.eclipse.core.runtime.IStatus, org.eclipse.core.runtime.IStatus, java.lang.String, boolean)
	 */
	@Override
	public IStatus mergeStatus(IStatus status1,
							   IStatus status2,
							   String multiMessage,
							   boolean allowMerge)
	{
		return StatusUtils.mergeStatus(status1, status2, _pluginId, multiMessage, allowMerge);
	}
  
  
  
  @Override
  public IStatus mergeStatus(IStatus status1,
                             IStatus status2,
                             String multiMessage)
  {
    return StatusUtils.mergeStatus(status1, status2, _pluginId, multiMessage);
  }
  
  
  
  
  /* (non-Javadoc)
	 * @see com.strikewire.snl.apc.reporting.IStatusReporting#mergeStatus(org.eclipse.core.runtime.IStatus, org.eclipse.core.runtime.IStatus, boolean)
	 */
	@Override
	public IStatus mergeStatus(IStatus status1,
							   IStatus status2,
							   boolean allowMerge)
	{
		return StatusUtils.mergeStatus(status1, status2, _pluginId, allowMerge);
	}




  @Override
  public IStatus mergeStatus(IStatus status1, IStatus status2)
  {
    return StatusUtils.mergeStatus(status1, status2, _pluginId);
  }




  public void log(IStatus status, boolean addStack)
  {
    if (addStack) {
      status = addStack(status);
    }

    if (_plugin != null) {
      _plugin.getLog().log(status);
    }
    else if (_bundle != null) {
      // NOTE: this will break in the future; the Platform object
      //  is being deprecated over time as Eclipse moves forward in the
      //  4.0 paradigm. However, this approach removes the previous
      //  dependency on an internal call. To fix the issue, one should
      //  use a service retrieval, but there is confusion on what
      //  service is providing what logging, so for the moment
      //  just use the Platform approach
      ILog log = Platform.getLog(_bundle);
      if (log != null) {
        log.log(status);
      }
    }
  }




  public static IStatus addStack(IStatus status)
  {
    Throwable t = status.getException();
    if (t == null) {
      IStatus[] children = status.getChildren();
      String stackMsg =
          "Exception added to status to generate a stack-trace for debugging purposes.";
      if (status.isMultiStatus() || (children != null && children.length > 0)) {
        status =
            new MultiStatus(status.getPlugin(),
                status.getCode(),
                children,
                status.getMessage(),
                new Exception(stackMsg));
      }
      else {
        status =
            new Status(status.getSeverity(),
                status.getPlugin(),
                status.getCode(),
                status.getMessage(),
                new Exception(stackMsg));
      }
    }
    return status;
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
    log(status, false);
  }




  @Override
  public void log(String message, Throwable exception, int severity)
  {
    IStatus status = newStatus(message, exception, severity);
    log(status);
  }




  @Override
  public void logInfo(String message, Throwable exception)
  {
    IStatus status = newInfoStatus(message, exception);
    log(status);
  }




  @Override
  public void logInfo(Throwable exception)
  {
    IStatus status = newInfoStatus(exception);
    log(status);
  }




  @Override
  public void logWarning(String message, Throwable exception)
  {
    IStatus status = newWarningStatus(message, exception);
    log(status);
  }




  @Override
  public void logWarning(Throwable exception)
  {
    IStatus status = newWarningStatus(exception);
    log(status);
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
    IStatus status = newErrorStatus(message, exception);
    log(status);
  }




  @Override
  public void logError(Throwable exception)
  {
    IStatus status = newErrorStatus(exception);
    log(status);
  }




  @Override
  public void throwStatus(IStatus status) throws CoreException
  {
    throw new CoreException(status);
  }




  @Override
  public void throwInfo(String message, Throwable exception)
    throws CoreException
  {
    IStatus status = newInfoStatus(message, exception);
    throwStatus(status);
  }




  @Override
  public void throwInfo(Throwable exception) throws CoreException
  {
    IStatus status = newInfoStatus(exception);
    throwStatus(status);
  }




  @Override
  public void throwWarning(String message, Throwable exception)
    throws CoreException
  {
    IStatus status = newWarningStatus(message, exception);
    throwStatus(status);
  }




  @Override
  public void throwWarning(Throwable exception) throws CoreException
  {
    IStatus status = newWarningStatus(exception);
    throwStatus(status);
  }




  @Override
  public void throwError(String message, Throwable exception)
    throws CoreException
  {
    IStatus status = newErrorStatus(message, exception);
    throwStatus(status);
  }




  @Override
  public void throwError(Throwable exception) throws CoreException
  {
    IStatus status = newErrorStatus(exception);
    throwStatus(status);
  }
}
