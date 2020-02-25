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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A class for gathering input in a TextArea
 * @author kholson
 *
 */
public class TextAreaDialog extends TitleAreaDialog
{
  
  private Text _txtDescription;
  
  private String _orgValue = "";
  
  private String _newValue = "";
  
  private String _shellTitle = "Object Property";
  
  private String _dialogTitle = "Enter a new value for the object property";
  
  
  private final Transfer[] transTypes = new Transfer[] { TextTransfer.getInstance() };
  final int transOps = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;


  /**
   * @param parentShell
   */
  public TextAreaDialog(Shell parentShell)
  {
    super(parentShell);
  }

  public TextAreaDialog(Shell parentShell,
                        String shellTitle,
                        String dialogTitle)
  {
    super(parentShell);
    
    this._shellTitle = (shellTitle != null ? shellTitle : this._shellTitle);
    this._dialogTitle = (dialogTitle != null ? dialogTitle : this._dialogTitle);
  }  
  
  @Override
  protected Control createDialogArea(Composite parent)
  {
    getShell().setText(_shellTitle);
    setTitle(_dialogTitle);
    
    int numCols = 2;
    Composite composite = new Composite(parent, SWT.NONE);
    composite.setLayout(new GridLayout(numCols, false));
    GridData data = new GridData(GridData.FILL_BOTH);
    data.grabExcessVerticalSpace = true;
    composite.setLayoutData(data);
    
    _txtDescription = new Text(composite, 
        SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
    data = new GridData(GridData.FILL_BOTH);
    data.grabExcessVerticalSpace = true;
    data.heightHint = 100;
    data.horizontalSpan = numCols;
    data.widthHint = 500;
    _txtDescription.setLayoutData(data);

    _txtDescription.setText(_orgValue);
    

    // try for a key listener
    _txtDescription.addKeyListener(new KeyListener() {
      
      
      public void keyReleased(KeyEvent e)
      {

      }  //keyReleased
      
      
      
      public void keyPressed(KeyEvent e)
      {
        int kc = e.keyCode;
        
        if (kc == 13) {
          if ( (e.stateMask & SWT.CTRL) != 0) {
            e.doit = false;
            boolean confirm = MessageDialog.openConfirm(getShell(),
                "Confirm Commit",
                "You pressed control-Enter; OK to update?");
            
            if (confirm) {
              okPressed();
            }
          }
        }        
      } //keyPressed
    });

    
    //
    // 2013-03-01 (kho): add preliminary drop support; for text at least
    //
    DropTarget target = new DropTarget(_txtDescription, transOps);
    target.setTransfer(transTypes);
    target.addDropListener(new DropTargetAdapter() {
      @Override
      public void drop(DropTargetEvent event)
      {
        if (event.data == null) {
          event.detail = DND.DROP_NONE;
          return;
        }
        
        //FIXME: put in the right stop
        _txtDescription.insert((String)event.data);
      }
    });

    return composite;
  }
  
  
  
  @Override
  protected void okPressed()
  {
    _newValue = _txtDescription.getText();
    super.okPressed();
  }



  public void setText(final String text)
  {
    _orgValue = text;
    
    if (_txtDescription != null) {
      _txtDescription.setText(_orgValue);
    }
  }
  
  public String getText()
  {
    return _newValue;
  }
  
  
} //class
