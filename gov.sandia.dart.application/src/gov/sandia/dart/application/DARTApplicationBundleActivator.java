/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.application;

import gov.sandia.dart.common.core.reporting.AbsReportingBundleActivator;

import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DARTApplicationBundleActivator extends AbsReportingBundleActivator
{
	// The plug-in ID
	public static final String PLUGIN_ID = "gov.sandia.dart.application"; //$NON-NLS-1$

	// The shared instance
	private static DARTApplicationBundleActivator activator;
	
	/**
	 * The constructor
	 */
	public DARTApplicationBundleActivator()
	{
		super(PLUGIN_ID);
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.common.core.reporting.AbsReportingBundleActivator#postStart(org.osgi.framework.BundleContext)
	 */
	@Override
	protected void postStart(BundleContext bndlCtx) throws Exception
	{
		activator = this;
	}
	
	/* (non-Javadoc)
	 * @see gov.sandia.dart.common.core.reporting.AbsReportingBundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		super.stop(context);
		
		activator = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DARTApplicationBundleActivator getDefault()
	{
		return activator;
	}

}
