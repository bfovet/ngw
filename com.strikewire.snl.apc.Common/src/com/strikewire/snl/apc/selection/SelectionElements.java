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
 *  File originated by:
 *  kholson on Nov 8, 2016
 */
package com.strikewire.snl.apc.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * <p>
 * Allows obtaining Objects from a selection
 * </p>
 * 
 * @author kholson
 *
 */
public class SelectionElements
{
  public enum SelectionType
  {
    current, menu, ;
  }

  private final IAdapterManager _adptMgr = Platform.getAdapterManager();
  private final ISelection _sel;




  public SelectionElements(ISelection sel)
  {
    _sel = sel;
  }




  public SelectionElements(ExecutionEvent event)
  {
    this(event, SelectionType.current);
  }




  public SelectionElements(ExecutionEvent event, SelectionType type)
  {
    this._sel = getSelection(event, type);
  }




  private static ISelection getSelection(ExecutionEvent event,
                                         SelectionType type)
  {
    if (event == null) {
      return null;
    }

    switch (type) {
      case current:
        return HandlerUtil.getCurrentSelection(event);
      case menu:
        return HandlerUtil.getActiveMenuSelection(event);
    }

    return null;
  }




  /**
   * Returns the first object, if any, from the selection if the first object in
   * the selection matches the specific class or may be adapted to the class.
   */
  public <T extends Object> Optional<T> getFirstElement(Class<T> clazz)
  {

    Optional<T> opt = Optional.empty();

    if (_sel instanceof IStructuredSelection) {
      IStructuredSelection ssel = (IStructuredSelection) _sel;

      Object obj = ssel.getFirstElement();

      // see if we have an instance
      if (clazz.isInstance(obj)) {
        @SuppressWarnings("unchecked")
        T var = (T) obj;
        opt = Optional.ofNullable(var);
      }
      else {

        if (_adptMgr != null) {
          Object adpt = _adptMgr.getAdapter(obj, clazz);
          if (adpt != null) {

            @SuppressWarnings("unchecked")
            T var = (T) adpt;
            opt = Optional.ofNullable(var);
          }
        }
      }


    } // if : a structured selection

    return opt;
  }




  /**
   * Returns all of the elements in the selection if it is a structured
   * selection and the element matches the class or may be adapted to it.
   */
  public <T extends Object> Collection<T> getElements(Class<T> clazz)
  {
    Collection<T> col = new ArrayList<>();

    if (_sel instanceof IStructuredSelection) {
      IStructuredSelection ssel = (IStructuredSelection) _sel;

      // process all of the entries
      Iterator<?> it = ssel.iterator();
      while (it.hasNext()) {
        Object obj = it.next();
        if (clazz.isInstance(obj)) {
          @SuppressWarnings("unchecked")
          T var = (T) obj;
          col.add(var);
        }
        else {
          if (_adptMgr != null) {
            Object adpt = _adptMgr.loadAdapter(obj, clazz.getName());
            if (adpt != null) {
              @SuppressWarnings("unchecked")
              T var = (T) adpt;
              col.add(var);
            } // if : we adapted the object
          } // if : we have an adapter manager
        } // else: not directly an instance
      } // while: things in the iterator
    } // if: structured selection

    return col;
  }
}
