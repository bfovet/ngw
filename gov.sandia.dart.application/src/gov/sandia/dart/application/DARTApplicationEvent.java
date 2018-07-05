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
 * Created by mjgibso on Oct 23, 2013 at 12:13:47 PM
 */
package gov.sandia.dart.application;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * For applications which use the DARTApplication and supporting
 * workbench and window advisor classes, each of the declared events in
 * this enum will be sent subsequent to executing the respective
 * interface method.
 * <p>
 * During a typical application life-cycle that follows the IDEApplication
 * and corresponding advisors, events can generally be expected in the
 * following order:
 * <p>
 * <br>PRE: APPLICATION_CONSTRUCT
 * <br>PRE: APPLICATION_START
 * <br>PRE: WORKBENCH_ADVISOR_CONSTRUCT
 * <br>PRE: WORKBENCH_ADVISOR_INITIALIZE
 * <br>POST: WORKBENCH_ADVISOR_INITIALIZE
 * <br>PRE: WORKBENCH_ADVISOR_PRE_STARTUP
 * <br>POST: WORKBENCH_ADVISOR_PRE_STARTUP
 * <br>PRE: WORKBENCH_ADVISOR_OPEN_WINDOWS
 * <br>POST: WORKBENCH_ADVISOR_OPEN_WINDOWS
 * <br>PRE: WORKBENCH_ADVISOR_POST_STARTUP
 * <br>POST: WORKBENCH_ADVISOR_POST_STARTUP
 * <br>PRE: WINDOW_ADVISOR_CONSTRUCT
 * <br>PRE: WINDOW_ADVISOR_PRE_WINDOW_OPEN
 * <br>POST: WINDOW_ADVISOR_PRE_WINDOW_OPEN
 * <br>PRE: WINDOW_ADVISOR_CREATE_ACTION_BAR_ADVISOR
 * <br>POST: WINDOW_ADVISOR_CREATE_ACTION_BAR_ADVISOR
 * <br>PRE: WINDOW_ADVISOR_POST_WINDOW_CREATE
 * <br>POST: WINDOW_ADVISOR_POST_WINDOW_CREATE
 * <br>PRE: WINDOW_ADVISOR_OPEN_INTRO
 * <br>POST: WINDOW_ADVISOR_OPEN_INTRO
 * <br>PRE: WINDOW_ADVISOR_POST_WINDOW_OPEN
 * <br>POST: WINDOW_ADVISOR_POST_WINDOW_OPEN
 * <br>Workbench up and running at this point, waiting for user to request close
 * <br>PRE: WINDOW_ADVISOR_PRE_WINDOW_SHELL_CLOSE
 * <br>POST: WINDOW_ADVISOR_PRE_WINDOW_SHELL_CLOSE
 * <br>PRE: WORKBENCH_ADVISOR_PRE_SHUTDOWN
 * <br>POST: WORKBENCH_ADVISOR_PRE_SHUTDOWN
 * <br>PRE: WINDOW_ADVISOR_DISPOSE
 * <br>POST: WINDOW_ADVISOR_DISPOSE
 * <br>PRE: WINDOW_ADVISOR_POST_WINDOW_CLOSE
 * <br>POST: WINDOW_ADVISOR_POST_WINDOW_CLOSE
 * <br>PRE: WORKBENCH_ADVISOR_POST_SHUTDOWN
 * <br>POST: WORKBENCH_ADVISOR_POST_SHUTDOWN
 * <br>PRE: WINDOW_ADVISOR_POST_WINDOW_CLOSE
 * <br>POST: WINDOW_ADVISOR_POST_WINDOW_CLOSE
 * <br>POST: APPLICATION_START
 * <p>
 * Note: the *_CONSTRUCT events for each of the three classes (application,
 * workbench advisor, and window advisor) are notified through the pre channel
 * only.
 * <p>
 * For applications and advisors which follow the patterns in the
 * eclipse IDE application and advisors, recipients of these events can
 * generally expect the IWorkbench to be initialized (calling
 * {@link PlatformUI#getWorkbench()} will return a non-null {@link IWorkbench}
 * without throwing an exception) once the PRE {@link #WORKBENCH_ADVISOR_INITIALIZE}
 * event is received, not before, and from then on.
 * <p>
 * Note: a workspace is not selected/specified until after receiving the PRE
 * {@link #WORKBENCH_ADVISOR_CONSTRUCT} event.  Therefore, attempting to access
 * workspace information prior to receiving this event will not produce expected
 * results.  Specifically, attempting to access standard workspace-scoped
 * preferences will cause the preference store to be initialized without the
 * workspace, and will then only have any specified default preferences and none
 * of the preference values persisted in the subsequently specified workspace.
 * This is of importance when a plugin is started as a result of an extension-based
 * {@link IDARTApplicationListener} registration.  It is important in that case to
 * take care to not allow anything to attempt to access the plugin's preference
 * store until after receiving the PRE {@link #WORKBENCH_ADVISOR_CONSTRUCT} event.
 * <p>
 * Note: most all of these events are notified to all registered listeners via the
 * main UI/Display thread.  The exceptions are PRE & POST
 * {@link #WORKBENCH_ADVISOR_POST_STARTUP}, and PRE & POST
 * {@link #WINDOW_ADVISOR_RESTORE_STATE}.  Even these events however which appear
 * to come on a different thread are effectively being synchronously processed
 * via the event thread, as it's waiting on the respective routines to complete
 * via code such as the following (to continue processing the UI/Display thread
 * event queue while waiting):
 * <pre>
 * <code>
 * while (!otherThreadDone) {
 *   if (!display.readAndDispatch()) {
 *     display.sleep();
 *   }
 * }
 * </code>
 * </pre>
 * Accordingly, listeners should take care to avoid executing long running
 * operations directly.  If at all possible, it would be best to spawn a
 * separate thread in which to execute the potentially long running task.
 * Then, only if necessary for synchronous execution, the listener can wait
 * for their long-running process in the other thread to complete using code
 * such as the example above.
 * <p>
 * 
 * @see AbstractDARTApplication
 * @see IDARTApplicationListener
 * 
 * @author mjgibso
 */
public enum DARTApplicationEvent
{
	APPLICATION_CONSTRUCT,
	APPLICATION_START,
	APPLICATION_STOP,
	
	/**
	 * Recipients of this event (on the PRE channel, as it will only come
	 * on the PRE channel) can generally expect the workspace to be specified.
	 * This means that subsequent to the receipt of this event it should be
	 * appropriate to load and reference workspace-scoped plugin preference
	 * stores.
	 */
	WORKBENCH_ADVISOR_CONSTRUCT,
	
	/**
	 * Recipients of this event on the PRE channel can generally expect the
	 * IWorkbench to be initialized (calling {@link PlatformUI#getWorkbench()}
	 * will return a non-null {@link IWorkbench} without throwing an exception).
	 */
	WORKBENCH_ADVISOR_INITIALIZE,
	WORKBENCH_ADVISOR_PRE_STARTUP,
	WORKBENCH_ADVISOR_POST_STARTUP,
	WORKBENCH_ADVISOR_PRE_SHUTDOWN,
	WORKBENCH_ADVISOR_POST_SHUTDOWN,
	WORKBENCH_ADVISOR_OPEN_WINDOWS,
	
	WINDOW_ADVISOR_CONSTRUCT,
	WINDOW_ADVISOR_PRE_WINDOW_OPEN,
	WINDOW_ADVISOR_CREATE_ACTION_BAR_ADVISOR,
	WINDOW_ADVISOR_OPEN_INTRO,
	WINDOW_ADVISOR_POST_WINDOW_CREATE,
	WINDOW_ADVISOR_POST_WINDOW_OPEN,
	WINDOW_ADVISOR_PRE_WINDOW_SHELL_CLOSE,
	WINDOW_ADVISOR_POST_WINDOW_CLOSE,
	
}
