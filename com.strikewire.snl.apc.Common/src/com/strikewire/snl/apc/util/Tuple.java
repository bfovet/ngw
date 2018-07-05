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
 * Created by mjgibso on Aug 24, 2012 at 4:47:47 AM
 */
package com.strikewire.snl.apc.util;

import java.util.Objects;

/**
 * @author mjgibso
 *
 */
public class Tuple<L, R>
{
	private L left_;
	private R right_;
	
	public Tuple(L left, R right)
	{
		this.left_ = left;
		this.right_ = right;
	}

	/**
	 * @return the left
	 */
	public L getLeft()
	{
		return this.left_;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(L left)
	{
		this.left_ = left;
	}

	/**
	 * @return the right
	 */
	public R getRight()
	{
		return this.right_;
	}

	/**
	 * @param right the right to set
	 */
	public void setRight(R right)
	{
		this.right_ = right;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		
		if(!(obj instanceof Tuple))
		{
			return false;
		}
		
		@SuppressWarnings("rawtypes")
		Tuple other = (Tuple) obj;
		
		return Objects.equals(left_, other.left_) && Objects.equals(right_, other.right_);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		return getHashCode(left_) + getHashCode(right_);
	}
	
	private static int getHashCode(Object side)
	{
		return side!=null ? side.hashCode() : System.identityHashCode(side);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.valueOf(left_) + " : " + String.valueOf(right_);
	}
}
