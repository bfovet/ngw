/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.Common.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class CompareWithHandler extends AbstractHandler {
	
	static AbstractHandler compareWithHandler = new CompareAction();
	/**
	 * Called when a user invokes the "Compare With" feature. It calls
	 * CompareWithAnotherEditorHandler.execute().
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		try {
			compareWithHandler.execute(null);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		return null;
	}  //  execute()
	
	static void setActiveHandler(AbstractHandler activeHandler) 
	{
		compareWithHandler = activeHandler;
	}

}




