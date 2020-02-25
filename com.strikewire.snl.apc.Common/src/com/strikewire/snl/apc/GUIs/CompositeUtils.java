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
 *  Copyright (C) 2013
 *  Sandia National Laboratories
 *
 *  File originated by:
 *    kholson on Aug 12, 2013
 *
 *
 */
/*---------------------------------------------------------------------------*/

package com.strikewire.snl.apc.GUIs;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author kholson
 * 
 */
public class CompositeUtils
{




  /**
   * private constructor to prevent instantiation
   */
  private CompositeUtils()
  {
  }




  /**
   * Clears the composite of its children if it is not null and not disposed,
   * and will do so recursively.
   * 
   * @param composite
   *          The composite for which
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Aug 12, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  public static void removeChildrenFromComposite(Composite composite)
  {
    removeChildrenFromComposite(composite, true);
  }




  /**
   * Clears the specified composite of its children, optionally recursively
   * removing children of children, if the composite is not null and not
   * disposed.
   * 
   * @param composite
   *          The composite
   * @param recursive
   *          Whether to go recursive
   * @author kholson
   *         <p>
   *         Initial Javadoc date: Aug 12, 2013
   *         <p>
   *         Permission Checks:
   *         <p>
   *         History:
   *         <ul>
   *         <li>(kholson): created</li>
   *         </ul>
   *         <br />
   */
  public static void removeChildrenFromComposite(Composite composite, boolean recursive)
  {
    if (composite == null || composite.isDisposed()) {
      return;
    }

    Control[] children = composite.getChildren();
    if (children == null || children.length < 1) {
      return;
    }

    for (Control child : children) {
      if (child == null) {
        continue;
      }

      if (recursive && child instanceof Composite) {
        removeChildrenFromComposite((Composite) child);
      }
      if (!child.isDisposed())
    	  child.dispose();
    }
  }

	/**
	 * Display a tree showing this Composite and all its descendants,
	 * specifically calling out their Layouts and layout data. This can be used
	 * to debug complicated layout problems, especially when system-generated
	 * components are involved and you're not sure how they're set up.
	 * 
	 * @param c the Composite to display information about
	 */
  public static void dump(Composite c) {
	  dump(c, 0);
  }

  private static void dump(Control c, int indent) {
	  indent(indent);
	  System.out.println(c.getClass().getName() + "; LD=" + c.getLayoutData());
	  if (c instanceof Composite) {
		  Composite composite = (Composite) c;
		  indent(indent);
		  System.out.println("LM = " + composite.getLayout());
		  for (Control child: composite.getChildren()) {
			  dump(child, indent+1);
		  }
	  }
  }

  private static void indent(int indent) {
	  for (int i=0; i<indent*2; ++i)
		  System.out.print(' ');
  }


} // class
