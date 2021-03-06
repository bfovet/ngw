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
 * Created by mjgibso on Jun 26, 2015 at 6:40:01 AM
 */
package com.strikewire.snl.apc.Common;


/**
 * @author mjgibso
 *
 */
public interface IAtomicListenersHandler
{
	/**
	 * Calling this method causes all events fired by the calling {@link Thread} to be queued and
	 * not broadcast until the calling {@link Thread} calls {@link #endAtomicOperation()}
	 */
	public boolean beginAtomicOperation();
	
	/**
	 * @see {@link #beginAtomicOperation()}
	 */
	public boolean endAtomicOperation();
}
