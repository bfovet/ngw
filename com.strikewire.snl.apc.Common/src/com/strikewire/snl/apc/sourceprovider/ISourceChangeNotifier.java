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
 * Created by mjgibso on Mar 12, 2013 at 5:07:59 AM
 */
package com.strikewire.snl.apc.sourceprovider;

/**
 * @author mjgibso
 *
 */
public interface ISourceChangeNotifier
{
	public void fireSourceValueChanged(int sourcePriority, String sourceName, Object sourceValue);
}
