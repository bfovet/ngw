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
 * Created by mjgibso on Sep 14, 2016 at 8:35:36 PM
 */
package com.strikewire.snl.apc.util;

/**
 * @author mjgibso
 *
 */
public interface IProcessCountLimiter {

	public abstract void getProcessRight() throws InterruptedException;

	public abstract void returnProcessRight();

}
