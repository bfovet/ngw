/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/

package gov.sandia.dart.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * <p>A collection of utilities for assisting with the Eclipse Service
 * access.</p>
 * @author kholson
 *
 */
public class ServiceUtils
{
  /**
   * _log -- A Logger instance for ServiceUtils
   */
  private static final Logger _log = LogManager.getLogger(ServiceUtils.class);
  
  private ServiceUtils()
  {
  }
  
  
  /**
   * Returns the service associated with the specified class; uses
   * PlatformUI, so the Workbench must be running. Throws
   * IllegalStateException if the WB is not running or the 
   * Service for the specified class is not found.
   * @param clazz
   * @return The Service, will not return null
   * @throws IllegalStateException: workbench has not yet started, or
   * the specified service cannot be found
   * @author kholson
   * <p>Initial Javadoc date: Oct 5, 2016</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public static <T> T getService(Class<T> clazz)
  {
    IWorkbench wb = null;
    
    try {
      wb = PlatformUI.getWorkbench();
    }
    catch (Exception e) {
      _log.error("The workbench has not yet started", e);
      throw new IllegalStateException("The workbench has not started", e);
    }    
    
    Object osrvc = wb.getService(clazz);
    
    if (osrvc == null) {
      _log.warn("Unable to find service for {}", clazz);
      throw new IllegalStateException("Unable to find service for " +
          clazz);
    }
    
    return clazz.cast(osrvc);
  }
  
  
  /**
   * Allows obtaining the specified service from the BundleContext; it
   * does not require the workbench to be running
   */
  public static <T> T getService(BundleContext context,
                                 Class<T> clazz)
  {
    Object srvc = null;
    
    //
    // get the service reference from the bundle using the class name
    //
    ServiceReference<?> srvcRef = 
        context.getServiceReference(clazz.getName());
    
    if (srvcRef != null) {
      //
      // get the service itself
      //
      srvc = context.getService(srvcRef);

      if (srvc != null) {
        _log.info("**Found {}!!", clazz.getName());
      }
      else {
        _log.warn("{} not found from context.getService!", clazz.getName());
      }
    }
    else {
      _log.warn("Service reference for {} was null!", clazz.getName());
    }
    
    return clazz.cast(srvc);
  }
}
