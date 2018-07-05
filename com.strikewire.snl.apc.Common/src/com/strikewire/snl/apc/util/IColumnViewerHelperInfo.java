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
 * Created by mjgibso on Feb 27, 2013 at 9:05:07 AM
 */
package com.strikewire.snl.apc.util;

import java.util.Collection;

/**
 * @author mjgibso
 *
 */
public interface IColumnViewerHelperInfo
{
	public Collection<String> getAllColumnNames();
	
	public Collection<String> getRequiredColumnNames();
}
