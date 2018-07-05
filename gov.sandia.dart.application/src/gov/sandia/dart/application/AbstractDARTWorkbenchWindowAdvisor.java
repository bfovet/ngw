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
 * Created by mjgibso on Oct 23, 2013 at 11:56:25 AM
 */
package gov.sandia.dart.application;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

/**
 * A common workbench window advisor to extend that we can use to capture base-level
 * functionality across all our applications.
 *   
 * @see AbstractDARTApplication
 * 
 * @author mjgibso
 *
 */
public abstract class AbstractDARTWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
	/**
	 * 
	 */
	public AbstractDARTWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_CONSTRUCT);
	}

	@Override
	public final void preWindowOpen()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_PRE_WINDOW_OPEN);
		doPreWindowOpen();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_PRE_WINDOW_OPEN);
	}
	
	protected void doPreWindowOpen()
	{
		super.preWindowOpen();
	}

	@Override
	public final ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer)
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_CREATE_ACTION_BAR_ADVISOR);
		ActionBarAdvisor ret = doCreateActionBarAdvisor(configurer);
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_CREATE_ACTION_BAR_ADVISOR);
		return ret;
	}
	
	protected ActionBarAdvisor doCreateActionBarAdvisor(IActionBarConfigurer configurer)
	{
		return super.createActionBarAdvisor(configurer);
	}

	@Override
	public final void openIntro()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_OPEN_INTRO);
		doOpenIntro();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_OPEN_INTRO);
	}
	
	protected void doOpenIntro()
	{
		super.openIntro();
	}

	@Override
	public final void postWindowCreate()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_POST_WINDOW_CREATE);
		doPostWindowCreate();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_POST_WINDOW_CREATE);
	}
	
	protected void doPostWindowCreate()
	{
		super.postWindowCreate();
	}

	@Override
	public final void postWindowOpen()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_POST_WINDOW_OPEN);
		doPostWindowOpen();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_POST_WINDOW_OPEN);
	}
	
	protected void doPostWindowOpen()
	{
		super.postWindowOpen();
	}

	@Override
	public final boolean preWindowShellClose()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_PRE_WINDOW_SHELL_CLOSE);
		boolean ret = doPreWindowShellClose();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_PRE_WINDOW_SHELL_CLOSE);
		return ret;
	}
	
	protected boolean doPreWindowShellClose()
	{
		return super.preWindowShellClose();
	}

	@Override
	public final void postWindowClose()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WINDOW_ADVISOR_POST_WINDOW_CLOSE);
		doPostWindowClose();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WINDOW_ADVISOR_POST_WINDOW_CLOSE);
	}
	
	protected void doPostWindowClose()
	{
		super.postWindowClose();
	}
}
