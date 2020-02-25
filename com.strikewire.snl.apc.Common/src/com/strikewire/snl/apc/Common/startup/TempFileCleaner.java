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
 * Created by mjgibso on Oct 19, 2013 at 10:52:44 PM
 */
package com.strikewire.snl.apc.Common.startup;

import org.eclipse.ui.IStartup;

import com.strikewire.snl.apc.temp.TempFileManager;

/**
 * @author mjgibso
 *
 */
public class TempFileCleaner implements IStartup
{
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IStartup#earlyStartup()
	 */
	@Override
	public void earlyStartup()
	{
		TempFileManager.addTempDirCleanupHook();
	}
}
