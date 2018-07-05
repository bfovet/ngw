/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by Marcus Gibson
 * On Feb 10, 2006 at 11:11:26 AM
 */
package com.strikewire.snl.apc.validation;

import org.eclipse.jface.dialogs.IMessageProvider;

/**
 * @author Marcus Gibson
 *
 */
public interface IValidationMessageHandler extends IMessageProvider
{
	public void setErrorMessage(String newMessage);
	
	public void setMessage(String newMessage);
	
	public void setMessage(String newMessage, int newType);
}
