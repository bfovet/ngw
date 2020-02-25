/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs.settings;

import org.eclipse.core.runtime.IStatus;

public interface IMessageView {
	
	/**
	 * If set to true, setting the message should only update if the error level is higher than the
	 * error level of the existing message 
	 * @param retainMessage
	 */
	void checkErrorLevel(boolean checkErrorLevel);

	void setMessage(String newMessage, int newType);
	
	void setMessageFor(IStatus status);
}
