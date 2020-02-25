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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.GUIs.ChoicesButtonDialog;



/**
 * TODO This class is begging for java 8.
 * 
 * @author mjgibso
 *
 */
public class CommonMessages
{
  private static final String BUNDLE_NAME =
      "com.strikewire.snl.apc.commonmessages";//$NON-NLS-1$

  private static final ResourceBundle _resourceBundle =
      ResourceBundle.getBundle(BUNDLE_NAME);




  private CommonMessages()
  {
  }


  /**
   * An enumeration which allows for obtaining overwrite.
   * 
   * @author kholson
   * 
   */
  public enum eOverwriteValues
  {
    Yes(IDialogConstants.YES_LABEL),

    YesToAll(IDialogConstants.YES_TO_ALL_LABEL),

    No(IDialogConstants.NO_LABEL),

    Cancel(IDialogConstants.CANCEL_LABEL),

    ;
    private String _txt;




    private eOverwriteValues(String txt)
    {
      _txt = txt;
    }




    /**
     * Returns the String representation of the label, without any "&"
     * characters. Note the String may have spaces, so to convert back to the
     * enum value may need to remove the spaces.
     * 
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
      return _txt.replaceAll("&", "");
    }




    /**
     * Returns the enum constant for the specified label by searching through
     * the labels. Returns null if nothing found
     */
    public static eOverwriteValues findFromLabel(final String label)
    {
      eOverwriteValues eRet = null;

      for (eOverwriteValues val : eOverwriteValues.values()) {
        if (val.getLabel().equals(label)) {
          eRet = val;
          break;
        }
      }

      return eRet;
    }




    /**
     * Returns the label which may be passed to a dialog; may contain a "&"
     * shortcut indicator.
     */
    public String getLabel()
    {
      return _txt;
    }




    /**
     * Returns all of the labels
     */
    public static String[] getLabels()
    {
      List<String> lstRet = new ArrayList<String>();

      for (eOverwriteValues val : eOverwriteValues.values()) {
        lstRet.add(val.getLabel());
      }

      return lstRet.toArray(new String[lstRet.size()]);
    }
  }; // enum




  /**
   * Displays a dialog with yes/yes to all/no/cancel options
   * 
   */
  public static eOverwriteValues showYesYesAllNoDialog(final Shell shell,
                                                       final String title,
                                                       final String msg)
  {
    eOverwriteValues eReturn = eOverwriteValues.Cancel;

    final String[] options = eOverwriteValues.getLabels();
    final String defaultChoice = eOverwriteValues.Yes.toString();


    final ChoicesButtonDialog choicesDlg =
        new ChoicesButtonDialog(null, title, msg, options, defaultChoice);


    // if we are running in the UI thread, we do not have to shift
    if (Thread.currentThread() == Display.getDefault().getThread()) {
      choicesDlg.open();
    } // if : in the UI thread
    else {
      Display.getDefault().syncExec(new Runnable() {

        @Override
        public void run()
        {
          choicesDlg.open();
        }
      });
    } // else : not in the UI thread


    String choice = choicesDlg.getValue();
    if (StringUtils.isNotBlank(choice)) {
      eReturn = eOverwriteValues.findFromLabel(choice);
      if (eReturn == null) {
        eReturn = eOverwriteValues.Cancel;
      }
    }


    return eReturn;

  }




  /**
   * Shows a dialog with a prompt for overwriting the specified filename, with
   * options for yes/yes to all/no/cancel and returns the selection.
   */
  public static eOverwriteValues showDeleteReadOnlyMessageDialog(final Shell shell,
                                                                 final String title,
                                                                 final String filename)
  {
    final String fmt =
        "The file {0} is read-only. Do you wish to delete this file?";

    final String msg = MessageFormat.format(fmt, filename);

    return showYesYesAllNoDialog(shell, title, msg);
  } // showDeleteReadOnlyMessageDialog




  /**
   * Shows a dialog with a prompt for overwriting the specified filename, with
   * options for yes/yes to all/no/cancel and returns the selection.
   */
  public static eOverwriteValues showOverwriteMessageDialog(final Shell shell,
                                                            final String title,
                                                            final String filename)
  {
    final String fmt =
        "The file {0} already exists.\n\nDo you wish to overwrite this file?";

    final String msg = MessageFormat.format(fmt, filename);

    return showYesYesAllNoDialog(shell, title, msg);
  } // showOverwriteMessageDialog




  public static String getString(String key)
  {
    try {
      return _resourceBundle.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }




  public static Vector<String> getKeysStartingWith(String start)
  {
    Vector<String> vecRet = new Vector<String>();

    // go through all of the keys in the bundle looking for those that
    // begin with the specified string
    Enumeration<String> keys = _resourceBundle.getKeys();

    while (keys.hasMoreElements()) {
      String key = keys.nextElement();
      if (key.startsWith(start)) {
        vecRet.add(key);
      }
    } // while

    return vecRet;
  } // getKeysStartingWith




  /**
   * Displays a Message (information) dialog with the specified title and
   * message, tied to the specified shell.
   * 
   * @param shell
   *          (may be null)
   * @param title
   * @param message
   */
  public static void showMessage(final Shell shell,
                                 final String title,
                                 final String message)
  {
    if (currentThreadIsDisplayThread()) {
      MessageDialog.openInformation(shell, title, message);
    }
    else {
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run()
        {
          MessageDialog.openInformation(shell, title, message);
        }
      });
    }
  }
  
  
  
  /**
   * Opens the specified Window (usually a Dialog) on the
   * display thread. Checks to see if the current
   * thread is the Display thread and calls window.open() if it
   * is; otherwise it uses a syncExec to open the window.
   */
	public static int openOnDisplayThread(final Window window)
	{
		if(currentThreadIsDisplayThread())
		{
			return window.open();
		} else {
			final AtomicInteger ret = new AtomicInteger(-1);
			Display.getDefault().syncExec(new Runnable() {

				@Override
        public void run() {
					ret.set(window.open());
				}
			});
			return ret.get();
		}
	}
	
	
	public static boolean openConfirm(final Shell parent, final String title, final String message)
	{
		if(currentThreadIsDisplayThread())
		{
			return MessageDialog.openConfirm(parent, title, message);
		} else {
			final AtomicBoolean ret = new AtomicBoolean();
			Display.getDefault().syncExec(new Runnable() {

				@Override
        public void run() {
					ret.set(MessageDialog.openConfirm(parent, title, message));
				}
			});
			return ret.get();
		}
	}
	

	
  /**
   * Displays an error dialog with the specified title and message, tied to the
   * specified shell.
   * 
   * @param shell
   *          (may be null)
   * @param title
   * @param message
   */
  public static void showError(final Shell shell,
                               final String title,
                               final String message)
  {
    if (currentThreadIsDisplayThread()) {
      MessageDialog.openError(shell, title, message);
    }
    else {
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run()
        {
          MessageDialog.openError(shell, title, message);
        }
      });
    }
  }
  
  /**
   * Displays a warning dialog with the specified title and message, tied to the
   * specified shell.
   * 
   * @param shell
   *          (may be null)
   * @param title
   * @param message
   */
  public static void showWarning(final Shell shell,
                               final String title,
                               final String message)
  {
    if (currentThreadIsDisplayThread()) {
      MessageDialog.openError(shell, title, message);
    }
    else {
      Display.getDefault().asyncExec(new Runnable() {

        @Override
        public void run()
        {
          MessageDialog.openWarning(shell, title, message);
        }
      });
    }
  }
  
  public static void showErrorAsJob(String title, String message, Throwable exception)
  {
    showErrorAsJob(title, CommonPlugin.getDefault().newErrorStatus(message, exception));
  }
  
  public static void showErrorAsJob(String title, final IStatus errorStatus)
  {
    new Job(title) {
      @Override
      protected IStatus run(IProgressMonitor monitor) {
        return errorStatus;
      }
    }.schedule();
  }
  
  public static boolean currentThreadIsDisplayThread()
  {
    return (Thread.currentThread() == Display.getDefault().getThread());
  }
}
