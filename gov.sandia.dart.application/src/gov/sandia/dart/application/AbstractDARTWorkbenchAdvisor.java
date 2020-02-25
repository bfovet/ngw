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
 * Created by mjgibso on Oct 23, 2013 at 11:55:41 AM
 */
package gov.sandia.dart.application;

import gov.sandia.dart.argv.CommandLineParser;

import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.internal.ISelectionConversionService;
import org.eclipse.ui.internal.Workbench;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDESelectionConversionService;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * A common workbench window advisor to extend that we can use to capture base-level
 * functionality across all our applications.
 * 
 * TODO see if we can make this extend IDEWorkbenchAdvisor instead of just WorkbenchAdvisor.
 * Much of the code in our sub-classes was copied from IDEWorkbenchAdvisor.  We've been
 * trying to migrate common code from them all to this base class, but ultimately a large
 * portion of it is really just a copy/paste (then modify) from IDEWorkbenchAdvisor, so
 * even consolidating it here still isn't as good as just re-using what's in
 * IDEWorkbenchAdvisor.
 * 
 * @see AbstractDARTApplication
 * 
 * @author mjgibso
 *
 */
@SuppressWarnings("restriction")
public abstract class AbstractDARTWorkbenchAdvisor extends WorkbenchAdvisor
{
	private Listener settingsChangeListener;
	
	public AbstractDARTWorkbenchAdvisor()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_CONSTRUCT);
	}
	
	@Override
	public final void initialize(IWorkbenchConfigurer configurer)
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_INITIALIZE);
		doInitialize(configurer);
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_INITIALIZE);
	}
	
	protected void doInitialize(IWorkbenchConfigurer configurer)
	{
		configurer.setSaveAndRestore(true);
		super.initialize(configurer);
	}

	@Override
	public final void preStartup()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_PRE_STARTUP);
		doPreStartup();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_PRE_STARTUP);
	}
	
	protected void doPreStartup()
	{
		super.preStartup();
	}

	@Override
	public final void postStartup()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_POST_STARTUP);
		doPostStartup();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_POST_STARTUP);
	}
	
	protected void doPostStartup()
	{
		try {
			refreshFromLocal();
			activateProxyService();
			((Workbench) PlatformUI.getWorkbench()).registerService(
					ISelectionConversionService.class,
					new IDESelectionConversionService());

			initializeSettingsChangeListener();
			Display.getCurrent().addListener(SWT.Settings,
					settingsChangeListener);
			
			CommandLineParser.get().invokeHandlers();
			
			super.postStartup();
		} finally {// Resume background jobs after we startup
			Job.getJobManager().resume();
		}
	}
	
	protected void refreshFromLocal()
	{
		String[] commandLineArgs = Platform.getCommandLineArgs();
		IPreferenceStore store = IDEWorkbenchPlugin.getDefault()
				.getPreferenceStore();
		boolean refresh = store
				.getBoolean(IDEInternalPreferences.REFRESH_WORKSPACE_ON_STARTUP);
		if (!refresh) {
			return;
		}

		// Do not refresh if it was already done by core on startup.
		for (int i = 0; i < commandLineArgs.length; i++) {
			if (commandLineArgs[i].equalsIgnoreCase("-refresh")) { //$NON-NLS-1$
				return;
			}
		}

		final IContainer root = ResourcesPlugin.getWorkspace().getRoot();
		Job job = new WorkspaceJob(IDEWorkbenchMessages.Workspace_refreshing) {
			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor)
					throws CoreException {
				root.refreshLocal(IResource.DEPTH_INFINITE, monitor);
				return Status.OK_STATUS;
			}
		};
		job.setRule(root);
		job.schedule();
	}

	/**
	 * Activate the proxy service by obtaining it.
	 */
	protected void activateProxyService()
	{
		Bundle bundle = Platform.getBundle("org.eclipse.ui.ide"); //$NON-NLS-1$
		Object proxyService = null;
		if (bundle != null) {
			ServiceReference ref = bundle.getBundleContext().getServiceReference(IProxyService.class.getName());
			if (ref != null)
				proxyService = bundle.getBundleContext().getService(ref);
		}
		if (proxyService == null) {
			IDEWorkbenchPlugin.log("Proxy service could not be found."); //$NON-NLS-1$
		}
	}
	
	/**
	 * Initialize the listener for settings changes.
	 */
	protected void initializeSettingsChangeListener()
	{
		settingsChangeListener = new Listener() {

			boolean currentHighContrast = Display.getCurrent()
					.getHighContrast();

			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				if (Display.getCurrent().getHighContrast() == currentHighContrast)
					return;

				currentHighContrast = !currentHighContrast;

				// make sure they really want to do this
				if (new MessageDialog(null,
						IDEWorkbenchMessages.SystemSettingsChange_title, null,
						IDEWorkbenchMessages.SystemSettingsChange_message,
						MessageDialog.QUESTION, new String[] {
								IDEWorkbenchMessages.SystemSettingsChange_yes,
								IDEWorkbenchMessages.SystemSettingsChange_no },
						1).open() == Window.OK) {
					PlatformUI.getWorkbench().restart();
				}
			}
		};

	}

	@Override
	public final boolean preShutdown()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_PRE_SHUTDOWN);
		boolean ret = doPreShutdown();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_PRE_SHUTDOWN);
		return ret;
	}
	
	protected boolean doPreShutdown()
	{
		Display.getCurrent().removeListener(SWT.Settings, settingsChangeListener);
		
		return super.preShutdown();
	}

	@Override
	public final void postShutdown()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_POST_SHUTDOWN);
		doPostShutdown();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_POST_SHUTDOWN);
	}
	
	protected void doPostShutdown()
	{
		super.postShutdown();
	}

	@Override
	public final boolean openWindows()
	{
		DARTApplicationEventDispatch.preNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_OPEN_WINDOWS);
		boolean ret = doOpenWindows();
		DARTApplicationEventDispatch.postNotify(DARTApplicationEvent.WORKBENCH_ADVISOR_OPEN_WINDOWS);
		return ret;
	}
	
	protected boolean doOpenWindows()
	{
		return super.openWindows();
	}
}
