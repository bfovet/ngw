/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import java.io.File;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Text;

/**
 * Extends from FieldEditorPreferencePage. Allows user to leave the page if any
 * of the values have not been changed, even when some values are invalid.
 * 
 * @author snmuell
 * 
 */
public abstract class DefaultFieldEditorPreferencePage extends
    FieldEditorPreferencePage
{

  private boolean _bIsDirty = false;




  protected DefaultFieldEditorPreferencePage(int style)
  {
    super(FieldEditorPreferencePage.GRID);
  }




  protected DefaultFieldEditorPreferencePage(String title, int style)
  {
    super(title, style);
  }




  @Override
  public boolean performOk()
  {
    boolean ok = super.performOk();
    if (ok) {
      isDirty(false);
    }


    return ok;
  }




  @Override
  public void performDefaults()
  {
    super.performDefaults();

    isDirty(false);
  }




  @Override
  public boolean okToLeave()
  {
    return isValid();
  }




  @Override
  public boolean isValid()
  {
    if (!_bIsDirty) {
      return true;
    }

    return super.isValid();
  }

  /**
   * Calls updateApplyButton(), and if the container is not null,
   * calls getContainer().updateButtons()
   */
  protected void doButtonUpdates()
  {
    updateApplyButton();
    // update container state
    if (getContainer() != null) {
      getContainer().updateButtons();
    }
    
  }
  
  
  /**
   * Returns true if the executable as defined as existing at the specified
   * path exists and is executable
   * @param path The full path to the executable
   * @return true : exists and is executable
   * @author kholson
   * <p>
   * Initial Javadoc date: Dec 19, 2013
   * <p>
   * Permission Checks:
   * <p>
   * History: <ul>
   * <li>(kholson): created</li>
   * </ul>
   *<br />
   */
  protected boolean exeAtPath(final String path)
  {
    boolean bRet = false;
    File file = new File(path);

    bRet = file.exists() && file.canExecute();

    return bRet;
  }




  /**
   * For the specified text field, which must be a path to an executable,
   * see if the executable exists at the full path retrieved from the
   * text field; if it does not, display the specified error message.
   * @return true if an error message was displayed
   */
  protected boolean setErrorIfInvalidExe(Text txtField, final String errMsg)
  {
    boolean bRet = false;
    //
    // see if the path is valid
    //
    String pth = txtField.getText();

    if (!exeAtPath(pth)) {
      bRet = true;
      setMessage(errMsg, ERROR);
    }


    return bRet;
  }  
  
  
  /**
   * Will append the directory /Contents/MacOS/exeName and return that
   * value if the specified field.getText() ends in .app; otherwise returns
   * the value of field.getText()
   */
  protected String makePathForMacBasedOnAppSelection(final Text field,
                                                     final String exeName)
  {
    String chk = field.getText();

    if (chk.endsWith(".app")) {
      //
      // try to build, if possible, the path
      //
      chk += "/Contents/MacOS";

      chk += "/" + exeName;
    } // if : we ended with .app
    
    return chk;
  }


  @Override
  public void propertyChange(PropertyChangeEvent event)
  {
    if (event.getProperty().equals(FieldEditor.VALUE)) {
      _bIsDirty = true;
    }

    super.propertyChange(event);
  }




  protected void isDirty(boolean dirty)
  {
    _bIsDirty = dirty;
  }




  protected boolean isDirty()
  {
    return _bIsDirty;
  }

}
