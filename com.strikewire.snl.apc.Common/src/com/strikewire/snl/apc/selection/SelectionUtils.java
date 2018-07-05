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
 * Created by mjgibso on May 12, 2014 at 2:07:03 PM
 */
package com.strikewire.snl.apc.selection;

import gov.sandia.dart.common.core.listeners.BaseListenersHandler;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.osgi.util.EventKeys;
import com.strikewire.snl.apc.osgi.util.OSGIUtils;


/**
 * @author mjgibso
 *
 */
public class SelectionUtils
{
	private static final BaseListenersHandler<ISelectionBroadcastListener> _selectionBroadcastHandler =
			new BaseListenersHandler<ISelectionBroadcastListener>();
	
	private SelectionUtils()
	{}
	
	public static void fireSelectionBroadcast(IStructuredSelection selection)
	{
		OSGIUtils.postEvent(EventKeys.SELECTION_BROADCAST, CommonPlugin.class, selection);
		Collection<ISelectionBroadcastListener> listeners = _selectionBroadcastHandler.getListeners();
		for(ISelectionBroadcastListener listener : listeners)
		{
			listener.selectionBroadcast(selection);
		}
	}
	
	public static boolean addSelectionBroadcastListener(ISelectionBroadcastListener listener)
	{
		return _selectionBroadcastHandler.addListener(listener);
	}
	
	public static boolean removeSelectionBroadcastListener(ISelectionBroadcastListener listener)
	{
		return _selectionBroadcastHandler.removeListener(listener);
	}
	
	
  /**
   * From an ExecutionEvent, return the first element from
   * a converted StructedSelection; if there is an issue
   * with obtaining the current selection, throws an Execption
   * @param event An ExecutionEvent, usually specified to
   * a Command Handler
   * @return The first object in the selection; may return
   * an empty Optional
   * @throws ExecutionException
   * @author kholson
   * <p>Initial Javadoc date: Nov 7, 2016</p>
   * <p>Permission Checks:</p>
   * <p>History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   * </p>
   */
  public static Optional<Object> getFirstObject(final ExecutionEvent event)
      throws ExecutionException
  {
    ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
    
    if (sel.isEmpty()) {
      return Optional.empty();
    }
    
    if (! (sel instanceof IStructuredSelection)) {
      return  Optional.empty();
    }
    
    IStructuredSelection ssel = (IStructuredSelection)sel;
    
    if (ssel.isEmpty()) {
      return  Optional.empty();
    }
    
    Object obj = ssel.getFirstElement();
    
    return Optional.ofNullable(obj);
  }	
}
