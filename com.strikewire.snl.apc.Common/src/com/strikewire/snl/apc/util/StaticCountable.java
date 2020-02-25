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
 * Created by mjgibso on Apr 11, 2013 at 5:11:32 PM
 */
package com.strikewire.snl.apc.util;

import org.eclipse.core.expressions.ICountable;

/**
 * @author  mjgibso
 */
public class StaticCountable implements ICountable
{
	/**
	 * 
	 */
	private int count_;

	/**
	 * 
	 */
	public StaticCountable(int count)
	{
		this.count_ = count;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.expressions.ICountable#count()
	 */
	@Override
	public int count()
	{
		return this.count_;
	}
}
