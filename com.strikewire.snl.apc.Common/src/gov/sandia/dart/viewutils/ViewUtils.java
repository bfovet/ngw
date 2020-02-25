/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*---------------------------------------------------------------------------*/
/*
 *  Copyright (C) 2015
 *  Sandia National Laboratories
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Nov 3, 2015
 */
/*---------------------------------------------------------------------------*/

package gov.sandia.dart.viewutils;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.strikewire.snl.apc.CommonMessages;
import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * <p>
 * Provides convenience methods for creating, opening, etc. IViewParts
 * </p>
 * 
 * @author kholson
 *
 */
public class ViewUtils
{

  /**
   * 
   */
  private ViewUtils()
  {
  }




  /**
   * Obtains the current active workbench window. Throws if cannot get the
   * window;
   */
  public static IWorkbenchWindow getActiveWorkbenchWindow()
    throws IllegalStateException
  {
    // dislike this pattern
    final IWorkbenchWindow[] retWin = new IWorkbenchWindow[1];

    if (CommonMessages.currentThreadIsDisplayThread()) {
      retWin[0] = obtainActiveWBWin();
    }
    else {
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          retWin[1] = obtainActiveWBWin();
        }
      });
    }

    return retWin[0];
  }




  /**
   * Returns the active workbench window; may return null; may throw
   * IllegalStateException
   */
  private static IWorkbenchWindow obtainActiveWBWin()
    throws IllegalStateException
  {
    IWorkbench wb = null;
    IWorkbenchWindow retWin = null;

    wb = PlatformUI.getWorkbench();
    if (wb == null) {
      throw new IllegalStateException("Null workbench; likely not initialized");
    }

    retWin = wb.getActiveWorkbenchWindow();

    if (retWin == null) {
      retWin = wb.getWorkbenchWindows()[0];
    }

    return retWin;
  }


  /**
   * Returns the viewpart associated with the specified viewId; may
   * return null
   */
  public static IViewPart getPart(String viewId)
  {
    IViewPart view = null;
    
    IWorkbenchWindow window = getActiveWorkbenchWindow();

    if (window != null) {
      IWorkbenchPage page = window.getPages()[0];
      if (page != null) {
        view = page.findView(viewId);
      }
    }
    
    return view;
  }
  

  /**
   * <p>
   * Searches the
   * 
   * @param viewId
   * @param bringToFront
   * @return The found view, or null if unable to find a view with the specified
   *         Id
   * @throws IllegalStateException
   *           The Workbench is not in a state to support view showing
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Nov 3, 2015
   *         </p>
   *         <p>
   *         Permission Checks:
   *         </p>
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         </p>
   */
  public static IViewPart showView(String viewId, boolean bringToFront)
    throws IllegalStateException
  {
    IViewPart view = null;

    IWorkbenchWindow window = getActiveWorkbenchWindow();

    if (window != null) {
      IWorkbenchPage page = window.getPages()[0];
      if (page != null) {
        try {
          view = page.findView(viewId);
          if (view == null) {
            IWorkbenchPart activePart = page.getActivePart();
            view = page.showView(viewId);
            page.activate(activePart);
          }
          else {
            if (bringToFront) page.bringToTop(view);
          }

        }
        catch (PartInitException e) {
          CommonPlugin.getDefault().logError(e);
        }
      }
    }
    return view;
  }




  /**
   * Creates a view with the specified id and subid; if it cannot create the
   * view, the error is logged to the Error View, and the PartInitException is
   * thrown. The view is displayed via the workbench page with VIEW_ACTIVATE
   * 
   * @param viewId
   *          The id of the view
   * @param subId
   *          The subId; may be null
   * @return The created view
   * @throws IllegalStateException
   *           The workbench is not in a valid state to create views
   * @throws PartInitException
   *           The view could not be created for some reason
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Nov 3, 2015
   *         </p>
   *         <p>
   *         Permission Checks:
   *         </p>
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         </p>
   */
  public static IViewPart createView(String viewId, String subId)
    throws IllegalStateException, PartInitException
  {
    IViewPart view = doViewShow(viewId, subId, IWorkbenchPage.VIEW_ACTIVATE);
    return view;
  }




  /**
   * Makes the specified view visible; if cannot accomplish logs error and
   * PartInitExeptionis thrown; view is shown via VIEW_VISIBLE.
   * 
   * @param viewId
   * @param subId
   * @return
   * @throws IllegalStateException
   * @throws PartInitException
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Mar 16, 2016
   *         </p>
   *         <p>
   *         Permission Checks:
   *         </p>
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         </p>
   */
  public static IViewPart makeViewVisible(String viewId, String subId)
    throws IllegalStateException, PartInitException
  {
    IViewPart view = doViewShow(viewId, subId, IWorkbenchPage.VIEW_VISIBLE);
    return view;
  }




  private static IViewPart doViewShow(String viewId,
                                      String subId,
                                      int viewDisplay)
    throws IllegalStateException, PartInitException
  {
    IViewPart view = null;

    IWorkbench wb = PlatformUI.getWorkbench();

    if (wb == null) {
      throw new IllegalStateException("Null workbench; likely not initialized");
    }

    IWorkbenchWindow window = wb.getActiveWorkbenchWindow();

    if (window == null) {
      window = wb.getWorkbenchWindows()[0];
    }

    if (window != null) {
      try {
        IWorkbenchPage page = window.getActivePage();

        view = page.showView(viewId, subId, viewDisplay);
      }
      catch (PartInitException e) {
        CommonPlugin.getDefault().logError(e);
        throw e;
      }
    }

    return view;
  }


}
