/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package com.strikewire.snl.apc.status;

import org.eclipse.swt.SWTException;
import org.eclipse.ui.internal.ide.IDEWorkbenchErrorHandler;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.WorkbenchStatusDialogManager;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class HTMLStatusHandler extends IDEWorkbenchErrorHandler
{
	public HTMLStatusHandler()
	{
		super(null);
	}
	
	@Override
	public void handle(StatusAdapter statusAdapter, int style)
	{
		
		Throwable exception = statusAdapter.getStatus().getException();
		if (justLogThis(exception)) {
			CommonPlugin.getDefault().log(statusAdapter.getStatus());
		} else {
			super.handle(statusAdapter, style);
		}
	}
	
	/**
	 * This method returns true if we don't ever want to see a dialog about the exception. Feel free to add more clauses. 
	 * @param exception an exception we're considering reporting
	 * @return true if the exception should just be logged and not shown directly to the user
	 */
	private boolean justLogThis(Throwable exception) {
		if (exception == null)
			return false;
		
		String message = exception.getMessage();
		if (exception instanceof SWTException) {
			return true;
		} else if (exception instanceof IllegalArgumentException &&
				message != null &&
				message.contains("Comparison method violates its general contract")) {			
			return true;
		} else {
			return false;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.statushandlers.WorkbenchErrorHandler#configureStatusDialog(org.eclipse.ui.statushandlers.WorkbenchStatusDialogManager)
	 */
	@Override
	protected void configureStatusDialog(WorkbenchStatusDialogManager statusDialog)
	{
		super.configureStatusDialog(statusDialog);
		
		if(!System.getProperty("os.name").toLowerCase().contains("linux"))
		{
			statusDialog.setDetailsAreaProvider(new HTMLStatusAreaProvider(statusDialog));
			statusDialog.setMessageDecorator(new HTMLMessageDecorator());
		}
	}
}
