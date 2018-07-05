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
 *  Copyright (C) 2012
 *  Sandia National Laboratories
 *
 *  File originated by:
 *  StrikeWire, LLC
 *  149 South Briggs St., #102-A
 *  Erie, CO 80516
 *  (720) 890-8590
 *  support@strikewire.com
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.GUIs;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import com.strikewire.snl.apc.CommonMessages;

/**
 * @author kholson
 *
 */
public class ControlUpdater
{
  /**
   * _log -- A Logger instance for ControlUpdater
   */
  private static final Logger _log = LogManager.getLogger(ControlUpdater.class);




  /**
   * Invokes the specified method name on the specified control, with the goal
   * of setting some String value based upon the method. Assumes already shifted
   * to the UI thread.
   */
  private static boolean invokeStringMethod(final Control control,
                                            final String txt,
                                            final String methodName)
  {
    Method method = null;

    if (control == null || control.isDisposed()) {
      return false;
    }

    final String ctrlClass = control.getClass().getSimpleName();

    try {
      method = control.getClass().getMethod(methodName, String.class);
      method.invoke(control, txt);

      _log.debug("Set {} on {} for {}", txt, ctrlClass, methodName);
    }
    catch (NoSuchMethodException | SecurityException e1) {
      _log.error("Could not invoke {} on {}", methodName, ctrlClass);
      return false;
    }
    catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      _log.warn("Unable to update {} for {}", ctrlClass, methodName);
    }


    return true;
  }




  private static boolean invokeImageMethod(final Control control,
                                           final Image img)
  {
    final String methodName = "setImage";

    Method method = null;

    if (control == null || control.isDisposed()) {
      return false;
    }

    final String ctrlClass = control.getClass().getSimpleName();

    try {
      method = control.getClass().getMethod(methodName, Image.class);
      method.invoke(control, img);

      _log.debug("Set image on {}", ctrlClass);
    }
    catch (NoSuchMethodException | SecurityException e1) {
      _log.error("Could not invoke {} on {}", methodName, ctrlClass);
      return false;
    }
    catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      _log.warn("Unable to update {} for {}", ctrlClass, methodName);
      return false;
    }


    return true;
  }
  
  
  private static boolean invokeRedrawMethod(final Control control)
  {
    final String methodName = "redraw";

    Method method = null;

    if (control == null || control.isDisposed()) {
      return false;
    }

    final String ctrlClass = control.getClass().getSimpleName();

    try {
      method = control.getClass().getMethod(methodName, (Class<?>[])null);
      method.invoke(control);

      _log.debug("{}() called on ", methodName, ctrlClass);
    }
    catch (NoSuchMethodException | SecurityException e1) {
      _log.error("Could not invoke {} on {}", methodName, ctrlClass);
      return false;
    }
    catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      _log.warn("Unable to update {} for {}", ctrlClass, methodName);
      return false;
    }    
    
    return true;
  }


  private static boolean invokeRefreshMethod(final Control control)
  {
    final String methodName = "refresh";

    Method method = null;

    if (control == null || control.isDisposed()) {
      return false;
    }

    final String ctrlClass = control.getClass().getSimpleName();

    try {
      method = control.getClass().getMethod(methodName, (Class<?>[])null);
      method.invoke(control);

      _log.debug("{}() called on ", methodName, ctrlClass);
    }
    catch (NoSuchMethodException | SecurityException e1) {
      _log.error("Could not invoke {} on {}", methodName, ctrlClass);
      return false;
    }
    catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      _log.warn("Unable to update {} for {}", ctrlClass, methodName);
      return false;
    }    
    
    return true;
  }  
  
  
  private static boolean invokeSetEnabledMethod(final Control control, boolean enabled)
  {
    final String methodName = "setEnabled";

    Method method = null;

    if (control == null || control.isDisposed()) {
      return false;
    }

    final String ctrlClass = control.getClass().getSimpleName();

    try {
      Class<?>[] paramTypes = new Class[1];
      paramTypes[0] = Boolean.TYPE;
      method = control.getClass().getMethod(methodName, paramTypes);
      method.invoke(control, enabled);

      _log.debug("{}({}) called on {}", methodName, enabled, ctrlClass);
    }
    catch (NoSuchMethodException | SecurityException e1) {
      _log.error("Could not invoke {} on {}", methodName, ctrlClass);
      return false;
    }
    catch (IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      _log.warn("Unable to update {} for {}", ctrlClass, methodName);
      return false;
    }    
    
    return true;
  }   


  private static void updateTextOnControl(final Control control,
                                          final String txt,
                                          int layoutParent)
  {
    if (control == null || control.isDisposed()) {
      return;
    }

    if (invokeStringMethod(control, txt, "setText")) {
      if (layoutParent >= 1) {
        Composite c = control.getParent();
        for (int i = 1; i < layoutParent; i++) {
          Composite p = c.getParent();
          if (p == null) {
            break;
          }
          c = p;
        }
        c.layout();
      }
    } // if : the invocation worked
  } // updateTextOnControl




  /**
   * Sets the specified text on the specified control if control is control that
   * has a .setText() method . Validates that the control is not null && is not
   * disposed, so a safer approach than directly setting the text. Also, handles
   * the shifting into the UI thread via a syncExec if necessary.
   */
  public static void setTextOnControl(final Control control, final String txt)
  {
    setTextOnControl(control, txt, 0);
  }




  public static void setTextOnControl(final Control control,
                                      final String txt,
                                      final int layoutParent)
  {
    if (control == null || control.isDisposed()) {
      return;
    }

    if (CommonMessages.currentThreadIsDisplayThread()) {
      updateTextOnControl(control, txt, layoutParent);
    }
    else {

      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          updateTextOnControl(control, txt, layoutParent);
        }
      });
    }

  }




  /**
   * On the specified control, sets the tooltip to the specified text
   */
  public static void setTooltipOnControl(final Control control,
                                         final String tooltip)
  {
    final String txt_clabel = "setToolTipText";

    if (control == null || control.isDisposed()) {
      return;
    }

    if (CommonMessages.currentThreadIsDisplayThread()) {
      invokeStringMethod(control, tooltip, txt_clabel);
    }
    else {

      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          invokeStringMethod(control, tooltip, txt_clabel);
        }
      });
    }

  } // setTooltipOnControl




  public static void setImageOnControl(final Control control, final Image img)
  {
    if (control == null || control.isDisposed()) {
      return;
    }

    if (CommonMessages.currentThreadIsDisplayThread()) {
      invokeImageMethod(control, img);
    }
    else {

      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          invokeImageMethod(control, img);
        }
      });
    }
  }
  
  
  public static void redrawControl(final Control control)
  {
    if (control == null || control.isDisposed()) {
      return;
    }

    if (CommonMessages.currentThreadIsDisplayThread()) {
      invokeRedrawMethod(control);
    }
    else {

      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          invokeRedrawMethod(control);
        }
      });
    }    
  }
  
  
  public static void refreshControl(final Control control)
  {
    if (control == null || control.isDisposed()) {
      return;
    }

    if (CommonMessages.currentThreadIsDisplayThread()) {
      invokeRefreshMethod(control);
    }
    else {

      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          invokeRefreshMethod(control);
        }
      });
    }    
  }  
  
  
  public static void setEnabled(final Control control, final boolean enabled)
  {
    if (control == null || control.isDisposed()) {
      return;
    }

    if (CommonMessages.currentThreadIsDisplayThread()) {
      invokeSetEnabledMethod(control, enabled);
    }
    else {

      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          invokeSetEnabledMethod(control, enabled);
        }
      });
    } 
  }
  
	
	/**
	 * Method set's both the visibility on the given control, and if the layout data is a GridData,
	 * also set's the attached GridData's exclude to the inverse of the visible argument.
	 * 
	 * @return - true if the call to this method made any changes, false otherwise 
	 */
	public static boolean setCompleteVisibilityOnGridLayoutControl(Control control, boolean visible)
	{
		boolean change = false;
		if(control.getVisible() != visible)
		{
			control.setVisible(visible);
			change = true;
		}
		
		Object ld = control.getLayoutData();
		if(ld instanceof GridData)
		{
			GridData gd = (GridData) ld;
			if(gd.exclude != !visible)
			{
				gd.exclude = !visible;
				change = true;
			}
		}
		
		return change;
	}
}
