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
import org.eclipse.core.runtime.IStatus;

/**
 * <p>
 * Plugins which will support IStatus creation & logging should implement this
 * interface.
 * </p>
 * 
 * <p>
 * There is a helper class which handles the basic code: PluginStatusReporting
 * </p>
 * 
 * @author kholson
 * 
 */
public interface IStatusReporting
{
	/**
	 * Creates a new IStatus object, with the specified parameters
	 * 
	 * @param message
	 *            The message; if null the message will be empty
	 * @param Exception
	 *            The exception; may be null
	 * @param severity
	 *            The severity; follow the settings in the Status class
	 * @return
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public IStatus newStatus(String message, Throwable exception, int severity);

	public IStatus newInfoStatus(String message);
	
	public IStatus newInfoStatus(Throwable exception);
	
	/**
	 * Generates a new IStatus object with a severity of IStatus.INFO
	 * 
	 * @param message
	 * @param exception
	 * @return
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public IStatus newInfoStatus(String message, Throwable exception);
	
	public IStatus newWarningStatus(String message);

	public IStatus newWarningStatus(Throwable exception);
	
	public IStatus newWarningStatus(String message, Throwable exception);

	public IStatus newErrorStatus(String message);
	
	public IStatus newErrorStatus(Throwable exception);
	
	/**
	 * Generates a new IStatus object with a severity of IStatus.ERROR
	 * 
	 * @param message
	 * @param exception
	 * @return
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public IStatus newErrorStatus(String message, Throwable exception);

	public CoreException newError(String message);
	
	public CoreException newError(Throwable exception);
	
	/**
	 * Generates a new CoreException object with a status with severity of IStatus.ERROR
	 * 
	 * @param message
	 * @param exception
	 * @return
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public CoreException newError(String message, Throwable exception);
	
	public CoreException newError(IStatus status);
	
	public IStatus mergeStatus(IStatus status1, IStatus status2, String multiMessage, boolean allowMerge);
	
	public IStatus mergeStatus(IStatus status1, IStatus status2, String multiMessage);
	
	public IStatus mergeStatus(IStatus status1, IStatus status2, boolean allowMerge);
	
	public IStatus mergeStatus(IStatus status1, IStatus status2);
	
	/**
	 * Writes the status to the plugins log
	 * 
	 * @param status
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public void log(IStatus status);
	
	/**
	 * Writes the message to the plugins log
	 * @param message The non-null message
	 * @param exception An exception stack trace, may be null
	 * @param severity The severity, following the entries in the
	 * IStatus interface
	 * @see IStatus#OK
	 * @see IStatus#CANCEL
	 * @see IStatus#INFO
	 * @see IStatus#WARNING
	 * @see IStatus#ERROR
	 */
	public void log(String message, Throwable exception, int severity);
	
	public void logInfo(String message, Throwable exception);
	
	public void logInfo(Throwable exception);
	
	public void logWarning(String message, Throwable exception);
	
	public void logWarning(Throwable exception);

	/**
	 * Writes the specified message to the plugin's log
	 * 
	 * @param message
	 *            The message to write
	 * @param exception
	 *            An optional exception; may be null
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public void logError(String message, Throwable exception);

	/**
	 * Logs the exception; if null indicates a null exception
	 * 
	 * @param exception
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Feb 27, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public void logError(Throwable exception);
	
	public void throwStatus(IStatus status) throws CoreException;
	
	public void throwInfo(String message, Throwable exception) throws CoreException;
	
	public void throwInfo(Throwable exception) throws CoreException;
	
	public void throwWarning(String message, Throwable exception) throws CoreException;
	
	public void throwWarning(Throwable exception) throws CoreException;
	
	public void throwError(String message, Throwable exception) throws CoreException;
	
	public void throwError(Throwable exception) throws CoreException;
}
