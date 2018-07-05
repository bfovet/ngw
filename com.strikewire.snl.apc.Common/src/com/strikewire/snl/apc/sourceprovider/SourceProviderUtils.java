/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Mar 12, 2013 at 3:01:12 PM
 */
package com.strikewire.snl.apc.sourceprovider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

/**
 * A utility class that assists with source provider operations.
 * @author mjgibso
 *
 */
public class SourceProviderUtils
{
  /**
   * _log -- A Logger instance for SourceProviderUtils
   */
  private static final Logger _log =
      LogManager.getLogger(SourceProviderUtils.class);
  
  
  /**
   * <p>For the specified sourceName, returns the SourceProvider that
   * is registered for providing information on the name. May return
   * null.</p>
   */
	public static <T extends ISourceProvider> T getSourceProvider(String sourceName)
	{
	  IWorkbench wb = null;
	  
	  try {
	    wb = PlatformUI.getWorkbench();
	  }
	  catch (Exception e) {
	    _log.error("The workbench has not yet started", e);
	  }
	  
	  ISourceProviderService sourceProviderService = null;
	  
	  if (wb != null) {
	    sourceProviderService = 
	        (ISourceProviderService)wb.getService(ISourceProviderService.class);
	  }

		if (sourceProviderService == null) {
			return null;
		}

		@SuppressWarnings("unchecked")
		T sourceProvider = (T) sourceProviderService.getSourceProvider(sourceName);

		return sourceProvider;
	}
	
	
	/**
	 * Adds or removes the specified listener from the specified source provider,
	 * not making any adjustments if either is null.
	 */
	public static void adjustSourceProviderListener(ISourceProvider sp,
	                                                ISourceProviderListener lsnr,
	                                                ListenerAction action)
	{
	  if (sp != null && lsnr != null) {
	    switch (action) {
	      case Add:
	        sp.addSourceProviderListener(lsnr);
	        break;
	        
	      case Remove:
	        sp.removeSourceProviderListener(lsnr);
	        break;
	    }
	  }
	}
	
	
	/**
	 * Specifies the action to be taken with regards to listener registration
	 */
	public enum ListenerAction
	{
	  Add,
	  Remove,
	  
	  ;
	}
}
