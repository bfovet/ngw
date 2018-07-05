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
 * Created by mjgibso on Sep 29, 2014 at 8:03:21 AM
 */
package com.strikewire.snl.apc.listeneradapters;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;

/**
 * @author mjgibso
 *
 */
public abstract class LaunchesAdapter implements ILaunchesListener2
{

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener#launchesRemoved(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesRemoved(ILaunch[] launches)
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener#launchesAdded(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesAdded(ILaunch[] launches)
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener#launchesChanged(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesChanged(ILaunch[] launches)
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener2#launchesTerminated(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesTerminated(ILaunch[] launches)
	{}

}
