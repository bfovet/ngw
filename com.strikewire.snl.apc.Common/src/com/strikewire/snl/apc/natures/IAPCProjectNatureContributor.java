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
 * Created by Marcus J. Gibson on Jan 29, 2007 at 2:10:35 PM
 */
package com.strikewire.snl.apc.natures;

import java.util.List;

/**
 * @author mjgibso
 *
 */
public interface IAPCProjectNatureContributor
{
	public static final String EXTENSION_POINT_ID = "APCProjectNatureContributor";
	
	public List<String> getContributedNatures();
}
