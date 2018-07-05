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
 * Created by mjgibso on Jun 26, 2015 at 6:34:55 AM
 */
package com.strikewire.snl.apc.Common;

import java.util.Objects;

/**
 * @author mjgibso
 *
 */
public interface ITreeElement
{
	/**
	 * @return the parent element, or null if this is the root of the tree
	 */
	ITreeElement getParent();
	
	static int computeDepth(ITreeElement element)
	{
		int depth = -1;
		while(element != null)
		{
			depth++;
			element = element.getParent();
		}
		return depth;
	}
	
	
	static boolean isDescendant(ITreeElement parent, ITreeElement descendant)
	{
		return isEqualOrDescendant(parent, descendant!=null ? descendant.getParent() : null);
	}
	
	static boolean isEqualOrDescendant(ITreeElement parent, ITreeElement descendant)
	{
		while(descendant != null)
		{
			if(Objects.equals(descendant, parent))
			{
				return true;
			}
			
			descendant = descendant.getParent();
		}
		
		return false;
	}
	
	static ITreeElement getDeeper(ITreeElement element1, ITreeElement element2)
	{
		int depth1 = computeDepth(element1);
		int depth2 = computeDepth(element2);
		return depth2>depth1 ? element2 : element1;
	}
}
