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
 * Created on Nov 14, 2017 at 9:30:01 PM by mjgibso
 */
package com.strikewire.snl.apc.editor;

import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.ITextContentDescriber;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public interface ISubTypeContentDescriber extends ITextContentDescriber
{
	public static class SubType implements Comparable<SubType>
	{
		public final String subTypeName;
		
		/** Scale of one to ten, ten being positive, one being a wild guess. */
		public final int certainty;
		
		public SubType(String name, int certainty)
		{
			this.subTypeName = name;
			this.certainty = certainty;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(SubType subType)
		{
			return subType.certainty - certainty;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString()
		{
			return this.subTypeName;
		}
	}
	
	public final static QualifiedName SUB_TYPE = new QualifiedName(CommonPlugin.ID, "sub_type");
}
