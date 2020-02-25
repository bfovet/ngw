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
 * Created by mjgibso on Jun 21, 2010 at 8:01:21 AM
 */
package com.strikewire.snl.apc.temp;

import java.io.File;

/**
 * @author mjgibso
 *
 */
public interface ITempFileUser
{
	public static final String EXTENSION_POINT_ID = "TempFileUser";
	
	/**
	 * Method is called when a shutdown is scheduled, but prior to the workbench being shutdown.
	 * 
	 * The implementor is asked if the given temp file is in use and thus should not be deleted.
	 * If any implementor of this method returns true, the file will not be deleted, otherwise
	 * the given file will be deleted when the workbench exits.
	 * 
	 * A typical mechanism to check to see if a file is in use is by looking at all open editors
	 * and testing their inputs for a matching file.  To aid in that test, a method to obtain
	 * all open editor references is available by calling {@link TempFileManager#getOpenEditorRefs()}.
	 */
	public boolean isTempFileInUse(File tmpFile);
}
