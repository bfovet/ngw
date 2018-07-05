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
 * Created by mjgibso on Jun 26, 2015 at 6:52:32 AM
 */
package com.strikewire.snl.apc.Common;


/**
 * @author mjgibso
 *
 */
public interface IAtomicTreeListenersHandler
{
	/**
	 * Calling this method causes all Events affecting the provided element or a descendant of it to be queued
	 * (regardless of what {@link Thread} fires the event) until the calling {@link Thread} calls
	 * {@link #endAtomicOperation(ITreeElement)} with the same element.  The provided element may be null,
	 * indicating all events for this listener handler are to be queued until this operation is 'ended' by calling
	 * {@link #endAtomicOperation(ITreeElement)}, passing null as the argument.
	 */
	public boolean beginAtomicOperation(ITreeElement element);
	
	/**
	 * @see {@link #beginAtomicOperation(ITreeElement)}
	 */
	public boolean endAtomicOperation(ITreeElement element);

}
