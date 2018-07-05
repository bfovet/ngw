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
 * Created by mjgibso on Feb 7, 2013 at 6:13:25 AM
 */
package com.strikewire.snl.apc.commands;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.handlers.RegistryToggleState;

import com.strikewire.snl.apc.Common.CommonPlugin;

/**
 * @author mjgibso
 *
 */
public class CommandUtil
{
	public static final String INCLUDE_RESPONSES = "includeResponses";
	public static final String SELECTED_WORKFLOW_NODE = "selectedWorkflowNode";

	private CommandUtil()
	{}

	public static boolean getCommandStateNoThrow(Command command, boolean defaultVal)
	{
		try {
			return getCommandState(command);
		} catch (Throwable t) {
			CommonPlugin.getDefault().logError("Error getting command state.", t);
			return defaultVal;
		}
	}
	
	public static boolean getCommandState(Command command) throws ExecutionException
	{
		State state = command.getState(RegistryToggleState.STATE_ID);
		if(state == null)
			throw new ExecutionException("The command does not have a toggle state"); //$NON-NLS-1$
		 if(!(state.getValue() instanceof Boolean))
			throw new ExecutionException("The command's toggle state doesn't contain a boolean value"); //$NON-NLS-1$
			 
		return ((Boolean) state.getValue()).booleanValue();
	}
}
