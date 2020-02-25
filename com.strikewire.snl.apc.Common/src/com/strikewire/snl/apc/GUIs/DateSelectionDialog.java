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

import java.util.Calendar;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;

/**
 * <p>Presents a dialog with the ability to select a date.</p>
 * 
 * <p>By default, will present an option to select "No Defined Date".
 * <code>setShowNoDateSelection(boolean)</code> may be called to remove this option. The
 * method <code>setShowNoDateSelection(String></code> may be used to
 * change the text.</p>
 * 
 * 
 * @author kholson
 *
 */
public class DateSelectionDialog extends TitleAreaDialog
{
  private Calendar _calInitialSelection = null;
  
  private Calendar _calResult = null;
  
  
  private final String shellTitle_;
  private final String dialogTitle_;
  private final String dialogMessage_;

  
  /**
   * _btnNoSelection - The radio button for no selection
   */
  private Button _btnNoSelection;
  
  /**
   * _btnSelectedDate - The radio button for selecting a date
   */
  private Button _btnSelectedDate;
  
  /**
   * _dtCalendar - The widget which presents date/time
   */
  private DateTime _dtCalendar;
  
  /**
   * _bShowNoSelection - Whether the button to allow not selecting a
   * date should be shown
   */
  private boolean _bShowNoSelection = true;
  
  /**
   * _noSelectionText - The text to display for not selecting a date
   */
  private String _noSelectionText = "No Defined Date";
  
  
  /**
   * _selectionText - The text to display in front of the calendar widget 
   */
  private String _selectionText = "Date:";
  

  /**
   * @param parentShell
   * @param shellTitle
   * @param dialogTitle
   * @param dialogMessage
   */
  public DateSelectionDialog(Shell parentShell,
                             String shellTitle,
                             String dialogTitle,
                             String dialogMessage)
  {
    super(parentShell);
    
    this.shellTitle_ = shellTitle;
    this.dialogTitle_ = dialogTitle;
    this.dialogMessage_ = dialogMessage;    

  }
  
  
  /**
   * Set whether the dialog should present the "No Selection" option
   * in the dialog. Default is to show it.
   */
  public void setShowNoDateSelection(boolean show)
  {
    _bShowNoSelection = show;
  }
  
  
  
  /**
   * Allows for specifying the text that will be displayed for
   * the no date selection. No change from previous setting if
   * txt is null. May be empty, but why bother?
   */
  public void setNoDateSelectionText(String txt)
  {
    if (txt != null) {
      _noSelectionText = txt;
    }
  }
  
  
  /**
   * Allows for setting the text that will be displayed in front of the
   * date selection widge
   */
  public void setDateSelectionText(String txt)
  {
    if (txt != null) {
      _selectionText = txt;
    }
  }
  
  
  protected Control createDialogArea(Composite parent)
  {
    if (StringUtils.isNotBlank(this.shellTitle_)) {
      getShell().setText(this.shellTitle_);
    }
    
    if (StringUtils.isNotBlank(this.dialogTitle_)) {
      setTitle(this.dialogTitle_);
    }
    
    if (StringUtils.isNotBlank(this.dialogMessage_)) {
      setMessage(this.dialogMessage_);
    }
    
    
    Composite composite =
        new Composite((Composite) super.createDialogArea(parent), SWT.NONE);
    composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    

    
    
//    Group grp = new Group(composite, SWT.SHADOW_OUT);
//    grp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//    grp.setText("Date Selection");
    
    Composite btnParent = composite;
    
    btnParent.setLayout(new GridLayout(2, false));    
    
    _btnNoSelection = new Button(btnParent, SWT.RADIO);
    _btnNoSelection.setText(_noSelectionText);
    GridData gd = new GridData(SWT.LEFT, SWT.FILL, true, false);
    gd.horizontalSpan = 2;
    _btnNoSelection.setLayoutData(gd);

    if ( !_bShowNoSelection) {
      _btnNoSelection.setVisible(false);
    }

    
    _btnSelectedDate = new Button(btnParent, SWT.RADIO);
    _btnSelectedDate.setText(_selectionText);
   _dtCalendar = new DateTime(btnParent, SWT.DATE | SWT.DROP_DOWN | SWT.BORDER);
    
    if (_calInitialSelection != null) {
      _dtCalendar.setDate(_calInitialSelection.get(Calendar.YEAR),
          _calInitialSelection.get(Calendar.MONTH),
          _calInitialSelection.get(Calendar.DAY_OF_MONTH));
//      ISODateFormatter iso = new ISODateFormatter(_calInitialSelection);
//      String txt = iso.dateISO();
//      _btnSelectedDate.setText(txt);
      _btnSelectedDate.setSelection(true);
      
      if (_btnNoSelection != null) {
        _btnNoSelection.setSelection(false);
      }
    }
    
    _dtCalendar.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        _btnNoSelection.setSelection(false);
        _btnSelectedDate.setSelection(true);
      }
    });
    
    return parent;
  } //createDialogArea
  
  
  
  
  /**
   * Sets the initial selection to the specified Calendar; if null
   * then uses the current date
   */
  public void setInitialSelection(Calendar cal)
  {
    _calInitialSelection = Calendar.getInstance();
    if (cal != null) {
      _calInitialSelection.setTime(cal.getTime());
    }
  }

  

  
  /* (non-Javadoc)
   * @see org.eclipse.jface.dialogs.Dialog#okPressed()
   */
  @Override
  protected void okPressed()
  {
    if (_btnNoSelection != null &&_btnNoSelection.getSelection()) {
      _calResult = null;
    }
    else if (_btnSelectedDate.getSelection()) {
      _calResult = Calendar.getInstance();
      _calResult.set(_dtCalendar.getYear(),
          _dtCalendar.getMonth(),
          _dtCalendar.getDay());
      
    }
    
    super.okPressed();
  }


  /**
   * Returns the selected date, which may be null if no date selected.
   */
  public Calendar getResult()
  {
    return _calResult;
  }
  
}
