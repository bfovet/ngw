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
 * On Jan 9, 2006 at 2:29:36 PM
 */
package com.strikewire.snl.apc.validation;


/**
 * @author Marcus Gibson
 *
 */
public interface IValidatablePage extends IValidationMessageHandler
{
	public boolean validatePage();
}
