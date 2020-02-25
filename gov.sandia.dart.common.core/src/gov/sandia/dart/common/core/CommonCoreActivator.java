/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.core;

import gov.sandia.dart.common.core.reporting.AbsReportingBundleActivator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CommonCoreActivator extends AbsReportingBundleActivator implements BundleActivator
{
	public static final String PLUGIN_ID = "gov.sandia.dart.common.core";

	private static CommonCoreActivator _instance;

	public CommonCoreActivator()
	{
		super(PLUGIN_ID);
	}
	
	@Override
	protected void postStart(BundleContext bndlCtx) throws Exception
	{
		_instance = this;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception
	{
		_instance = null;
		
		super.stop(context);
	}
	
	public static CommonCoreActivator getDefault()
	{
		return _instance;
	}
}
