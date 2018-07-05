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
 * Created by mjgibso on Oct 23, 2013 at 12:07:05 PM
 */
package gov.sandia.dart.application;

import org.eclipse.ui.IWorkbench;


/**
 * One can register an implementation of this interface to receive
 * application life-cycle events via the
 * com.strikewire.snl.apc.Common.DARTApplicationListener extension
 * point.
 * <p/>
 * <b>IMPORTANT NOTES:</b> registering to this extension point causes
 * the provided implementation of this class to be constructed VERY
 * early in the application lifecycle (as the application class
 * itself is being constructed).  This could then cause the plugin
 * containing the class to be started at this very early time. This
 * means that the containing plugin should take care that it can
 * start early without too much dependency on other plugins or
 * services.  Of specific note:
 * <ul>
 * <li>
 * The Workbench won't be available yet, so neither the containing
 * plugin nor any dependently loaded plugin should attempt to access
 * it as a result of construction or call to the start method.  The
 * {@link IWorkbench} is generally available after the
 * {@link DARTApplicationEvent#WORKBENCH_ADVISOR_INITIALIZE} event
 * is broadcast over the PRE channel.  See additional notes in the
 * javadocs on {@link DARTApplicationEvent} and
 * {@link DARTApplicationEvent#WORKBENCH_ADVISOR_INITIALIZE}.
 * </li>
 * <li>
 * A workspace won't be specified yet, so care should be taken not to
 * allow attempted access to any workspace-scoped plugin preference
 * stores, as this will cause them to be initialized without
 * respective workspace-persisted values.  The workspace is generally
 * specified by the time the
 * {@link DARTApplicationEvent#WORKBENCH_ADVISOR_CONSTRUCT} event is
 * broadcast over the PRE channel.  See DTA-12451 and javadocs on
 * {@link DARTApplicationEvent} and
 * {@link DARTApplicationEvent#WINDOW_ADVISOR_CONSTRUCT}).
 * </li>
 * <li>
 * The loading of this plugin and any plugins it depends on occurs
 * before even the splash screen is created and shown. So it is
 * important to not overuse this extension or use it with a class in
 * a plugin that takes a long time to load, or depends on several
 * other plugins which may take a long time to load, as doing so
 * could lead to slow loading of the application with considerable
 * lag between when the launcher is executed and any sign of loading
 * progress occurs, namely the splash screen being displayed.
 * </li>
 * </ul>
 * 
 * 
 * @see DARTApplicationEvent
 * @see DARTApplicationAdapter
 * @see AbstractDARTApplication
 * 
 * @author mjgibso
 *
 */
public interface IDARTApplicationListener
{
	static final String EXTENSION_POINT_ID = "DARTApplicationListener";
	
	public void preApplicationEvent(DARTApplicationEvent event);
	
	public void postApplicationEvent(DARTApplicationEvent event);
}
