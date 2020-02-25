/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government
 * retains certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License v1.0.
 * For more information see the files copyright.txt and license.txt
 * included with the software. Information also available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *  File originated by:
 *  kholson on Jun 21, 2018
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Shell;

import com.strikewire.snl.apc.table.utils.ExtendedTableViewer.ExtTableViewerCfg;


/**
 * @author kholson
 *
 * @param <E>
 */
public interface ICheckListDialogCfg<E>
{

  /**
   * @return the shellTitle
   */
  String getShellTitle();




  /**
   * @return the dialogTitle
   */
  String getDialogTitle();




  /**
   * @return the dialogMessage
   */
  String getDialogMessage();




  /**
   * @return the parentShell
   */
  Shell getParentShell();




  /**
   * @return the choices
   */
  List<E> getChoices();




  /**
   * @return the labelProvider
   */
  ILabelProvider getLabelProvider();




  /**
   * @return the comparator
   */
  ViewerComparator getComparator();




  /**
   * @return the tblViewer Configurations (for each column)
   */
  List<ExtTableViewerCfg> getTableViewerCfgs();
  
  
  /**
   * Returns an image descriptor to be used for a shell icon
   */
  ImageDescriptor getShellImage();

}