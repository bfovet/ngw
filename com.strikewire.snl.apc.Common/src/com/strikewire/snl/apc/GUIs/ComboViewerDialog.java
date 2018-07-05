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

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * Designed to present a dialog that allows for selecting from a combo viewer.
 * This approach provides more flexibility than the standard Combo (or CCombo)
 * widget in that objects may be utilized rather than simple strings.
 * 
 * @author kholson
 * 
 */
public class ComboViewerDialog<E> extends TitleAreaDialog
{
  private static final Point MIN_SIZE = new Point(200, 50);
  private static final Point MAX_SIZE = new Point(700, 500);


  private final List<E> _choices;

  private final ILabelProvider _labelProvider;

  private final String _shellTitle;

  private final String _dialogTitle;

  private final String _dialogMessage;

  private E _initialSelection = null;


  private String _initialDisplay = null;


  private ComboViewer _viewer;


  /**
   * @param parentShell
   */
  public ComboViewerDialog(Shell parentShell, List<E> choices,
                           ILabelProvider labelProvider, String shellTitle,
                           String dialogTitle, String dialogMessage)
  {
    super(parentShell);

    this._choices = choices;

    this._labelProvider = labelProvider;

    this._shellTitle = shellTitle;
    this._dialogTitle = dialogTitle;
    this._dialogMessage = dialogMessage;

  }




  public void setInitialSelection(E initialSelection)
  {
    _initialSelection = initialSelection;
  }




  public void setInitialText(String txt)
  {
    _initialDisplay = txt;
  }




  @Override
  protected Control createDialogArea(Composite parent)
  {
    if (StringUtils.isNotBlank(this._shellTitle)) {
      getShell().setText(this._shellTitle);
    }

    if (StringUtils.isNotBlank(this._dialogTitle)) {
      setTitle(this._dialogTitle);
    }

    if (StringUtils.isNotBlank(this._dialogMessage)) {
      setMessage(this._dialogMessage);
    }

    Composite composite =
        new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    composite.setLayout(new FillLayout());
    
    //FIXME: do not set read_only if not necessary
    _viewer = new ComboViewer(composite, SWT.READ_ONLY);

    
    _viewer.setContentProvider(ArrayContentProvider.getInstance());
    _viewer.setLabelProvider(_labelProvider);
    
    _viewer.setInput(_choices.toArray());
    
    return parent;    
  } //createDialogArea(Composite)

} // class ComboViewerDialog
