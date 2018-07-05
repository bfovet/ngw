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
 * Created by mjgibso on May 12, 2014 at 2:21:41 PM
 */
package com.strikewire.snl.apc.selection;

import org.eclipse.jface.viewers.ISelection;

/**
 * @author mjgibso
 *
 */
public interface ISelectionBroadcastListener
{
	public void selectionBroadcast(ISelection selection);

}
