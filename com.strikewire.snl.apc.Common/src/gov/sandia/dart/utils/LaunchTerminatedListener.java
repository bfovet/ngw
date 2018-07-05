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
 * Created by mjgibso on Jul 5, 2014 at 7:05:27 AM
 */
package gov.sandia.dart.utils;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchesListener2;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public abstract class LaunchTerminatedListener implements ILaunchesListener2
{
	private final ILaunch _launch;
	
	private MutableBoolean _terminated = new MutableBoolean(false);
	
	/**
	 * 
	 * @param launch
	 * @param testForTerminated - if true, after registering a listener with the launch manager, this constructor
	 * 		will test to see if the launch is already terminated, in case it has terminated prior to registering
	 * 		the listener.  If false, this constructor will not make such a test, and the caller should call
	 * 		{@link #testForTerminated()} at the end of their constructor to ensure the case is handled where the
	 * 		the launch is terminated prior to a listener being registered.
	 */
	public LaunchTerminatedListener(ILaunch launch, boolean testForTerminated)
	{
		this._launch = launch;
		DebugPlugin.getDefault().getLaunchManager().addLaunchListener(this);
		if(testForTerminated)
		{
			testForTerminated();
		}
	}
	
	/**
	 * Only made final because I haven't thought about the implications of what somebody overriding it would mean.
	 */
	protected final void testForTerminated()
	{
		synchronized (_terminated) {
			if(!(_terminated.booleanValue()))
			{
				if(_launch.isTerminated())
				{
					try {
						terminated();
					} finally {
						DebugPlugin.getDefault().getLaunchManager().removeLaunchListener(this);
						_terminated.setValue(true);
						_terminated.notifyAll();
					}
				}
			}
		}
	}
	
	public ILaunch getLaunch()
	{
		return this._launch;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener#launchesRemoved(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesRemoved(ILaunch[] launches) {}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener#launchesAdded(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesAdded(ILaunch[] launches) {}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener#launchesChanged(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesChanged(ILaunch[] launches) {}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchesListener2#launchesTerminated(org.eclipse.debug.core.ILaunch[])
	 */
	@Override
	public void launchesTerminated(ILaunch[] launches)
	{
		for(ILaunch launch : launches)
		{
			if(ObjectUtils.equals(_launch, launch))
			{
				try {
					testForTerminated();
				} catch (Exception e) {
					CommonPlugin.getDefault().logError("Error checking for terminated process", e);
				}
			}
		}
	}
	
	protected abstract void terminated();
	
	protected void join() throws InterruptedException
	{
		synchronized (_terminated)
		{
			while(!_terminated.booleanValue())
			{
				_terminated.wait();
			}
		}
	}

}
