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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Shell;

import com.strikewire.snl.apc.table.utils.ExtendedTableViewer.ExtTableViewerCfg;

/**
 * @author kholson
 *
 */
public class CheckListDialogCfg<E> implements ICheckListDialogCfg<E>
{
  private String shellTitle;
  private String dialogTitle;
  private String dialogMessage;

  private Shell parentShell;

  private List<E> choices;

  private ILabelProvider labelProvider;

  private ViewerComparator comparator;

  private List<ExtTableViewerCfg> tblViewerCfgs;

  private ImageDescriptor shellImage;


  /**
   * 
   */
  private CheckListDialogCfg(String shellTitle, String dialogTitle,
                             String dialogMessage, Shell parentShell,
                             List<E> choices, ILabelProvider labelProvider,
                             ViewerComparator comparator,
                             List<ExtTableViewerCfg> tblViewerCfgs,
                             ImageDescriptor shellImage)
  {
    this.shellTitle = shellTitle;
    this.dialogTitle = dialogTitle;
    this.dialogMessage = dialogMessage;
    
    this.parentShell = parentShell;
    this.choices = choices;
    
    this.labelProvider = labelProvider;
    this.comparator = comparator;
    
    this.tblViewerCfgs = tblViewerCfgs;
    
    this.shellImage = shellImage;
  }
  
  
  


  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getShellTitle()
   */
  @Override
  public String getShellTitle()
  {
    return shellTitle;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getDialogTitle()
   */
  @Override
  public String getDialogTitle()
  {
    return dialogTitle;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getDialogMessage()
   */
  @Override
  public String getDialogMessage()
  {
    return dialogMessage;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getParentShell()
   */
  @Override
  public Shell getParentShell()
  {
    return parentShell;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getChoices()
   */
  @Override
  public List<E> getChoices()
  {
    return choices;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getLabelProvider()
   */
  @Override
  public ILabelProvider getLabelProvider()
  {
    return labelProvider;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getComparator()
   */
  @Override
  public ViewerComparator getComparator()
  {
    return comparator;
  }





  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getTableViewer()
   */
  @Override
  public List<ExtTableViewerCfg> getTableViewerCfgs()
  {
    return tblViewerCfgs;
  }



  /**
   * @see com.strikewire.snl.apc.GUIs.ICheckListDialogCfg#getShellImage()
   */
  @Override
  public ImageDescriptor getShellImage()
  {
    return shellImage;
  }


  /**
   * <p>
   * Allows creating a CheckListDialogCfg object.
   * </p>
   * <p>
   * Must specify the following:
   * </p>
   * <ul>
   * <li>parentShell</li>
   * <li>choices</li>
   * </ul>
   */
  public static class Builder<E>
  {
    private String shellTitle = "";
    private String dialogTitle = "";
    private String dialogMessage = "";

    private Shell parentShell = null;

    private List<E> choices = new ArrayList<>();

    private ILabelProvider labelProvider = null;

    private ViewerComparator comparator = null;

    private List<ExtTableViewerCfg> tblViewerCfgs = new ArrayList<>();;

    private ImageDescriptor shellImage;


    /**
     * Sets the shell title; not required
     */
    public Builder<E> setShellTitle(String s)
    {
      shellTitle = s;
      return this;
    }




    /**
     * Sets the dialog title; not required
     */
    public Builder<E> setDialogTitle(String s)
    {
      dialogTitle = s;
      return this;
    }




    public Builder<E> setDialogMessage(String s)
    {
      dialogMessage = s;
      return this;
    }




    /**
     * Sets the shell; may not be null
     */
    public Builder<E> setParentShell(Shell s)
    {
      parentShell = s;
      return this;
    }




    /**
     * Sets the choices that will be displayed; may not be null/empty
     */
    public Builder<E> setChoices(List<E> choices)
    {
      if (choices != null) {
        this.choices.addAll(choices);
      }
      return this;
    }




    /**
     * Sets a label provider; not required
     */
    public Builder<E> setLabelProvider(ILabelProvider lp)
    {
      labelProvider = lp;
      return this;
    }




    /**
     * Sets a comparator for sorting; not required
     */
    public Builder<E> setComparator(ViewerComparator vc)
    {
      comparator = vc;
      return this;
    }




    /**
     * Sets an ExtendedTableViewer -- useful for > 1 column; not required
     */
    public Builder<E> addExtTableViewerCfg(ExtTableViewerCfg cfg)
    {
      
      if (cfg != null) {
        tblViewerCfgs.add(cfg);
      }
      return this;
    }

    
    public Builder<E> setShellImage(ImageDescriptor imgDscrptr)
    {
      shellImage = imgDscrptr;
      return this;
    }



    /**
     * Constructs a new object after ensuring minimum valid.
     * 
     * @throws IllegalStateException
     *           The settings are incorrect
     */
    public ICheckListDialogCfg<E> build() throws IllegalStateException
    {
      if (parentShell == null) {
        throw new IllegalStateException("Parent shell not set");
      }

      if (choices == null || choices.isEmpty()) {
        throw new IllegalStateException("No choices set");
      }

      return new CheckListDialogCfg<>(shellTitle,
          dialogTitle,
          dialogMessage,
          parentShell,
          choices,
          labelProvider,
          comparator,
          (! tblViewerCfgs.isEmpty() ? tblViewerCfgs : null),
          shellImage);
    }
  }
}
