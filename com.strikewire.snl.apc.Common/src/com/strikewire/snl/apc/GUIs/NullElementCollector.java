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
 * Created by mjgibso on May 28, 2010 at 2:30:09 PM
 */
package com.strikewire.snl.apc.GUIs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.progress.IElementCollector;

/**
 * @author mjgibso
 *
 */
public class NullElementCollector implements IElementCollector
{
	private static NullElementCollector instance_;
	
	private NullElementCollector()
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.IElementCollector#add(java.lang.Object, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void add(Object element, IProgressMonitor monitor)
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.IElementCollector#add(java.lang.Object[], org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void add(Object[] elements, IProgressMonitor monitor)
	{}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.progress.IElementCollector#done()
	 */
	public void done()
	{}
	
	public static synchronized NullElementCollector getInstance()
	{
		if(instance_ == null)
		{
			instance_ = new NullElementCollector();
		}
		
		return instance_;
	}
}
