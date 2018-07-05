/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.strikewire.util.SWdebug;


public class ErrorMessageDialog extends MessageDialog
{

  
  public ErrorMessageDialog(Shell parentShell,
                            String dialogTitle,
                            Image dialogTitleImage, 
                            String dialogMessage,
                            int dialogImageType,
                            String[] dialogButtonLabels,
                            int defaultIndex)
  {
    super(parentShell,
        dialogTitle,
        dialogTitleImage,
        dialogMessage,
        dialogImageType,
        dialogButtonLabels,
        defaultIndex);
  }
  
  
  public static boolean open(Shell parent,
                             String title,
                             String message)
  {
    int style = SWT.WRAP;
    String[] buttonLabels = 
      new String[] { IDialogConstants.OK_LABEL };
    
    ErrorMessageDialog dialog = 
      new ErrorMessageDialog(parent,
          title,
          null,
          message,
          MessageDialog.ERROR,
          buttonLabels,
          0);
    
    style &= SWT.WRAP;
    dialog.setShellStyle(dialog.getShellStyle() | style);
    return dialog.open() == 0;      
      
  }
  
  protected Control createMessageArea(Composite parent)
  {
    Color color = parent.getBackground();
    
    FormToolkit toolkit = new FormToolkit(parent.getDisplay());
    ScrolledForm form = toolkit.createScrolledForm(parent);
    
    TableWrapLayout layout = new TableWrapLayout();
    form.getBody().setLayout(layout);
        
    form.setBackground(color);
    
    try {

      FormText text = toolkit.createFormText(form.getBody(), true);
      
      text.setBackground(color);
      
      text.setText(this.message, false, true);
      
      TableWrapData td = new TableWrapData(TableWrapData.FILL);
      td.colspan = 1;
      
      
      text.setLayoutData(td);
      
//      layout.computeMinimumWidth(parent, true);
      
    }
    catch (Exception e) {
      String msg = e.getMessage();
      SWdebug.msg(msg);
    }
    

    return form;
  }

  
}
