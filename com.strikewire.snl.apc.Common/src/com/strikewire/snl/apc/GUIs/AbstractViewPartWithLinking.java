/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.ICommandService;

import com.strikewire.snl.apc.commands.CommandUtil;
import com.strikewire.snl.apc.commands.ExecutionAdapter;

/**
 * Handles a toggle linking button, keeping the variable "linking" up to date. The intent is that you'll use something
 * like the following in the plugin.xml, so that the button  itself is provided by Eclipse.

	<extension
         point="org.eclipse.ui.commands">
      <command
            description="Link with selection"
            id="gov.sandia.dart.cubit.ui.command.link"
            name="Link">
         <state
               class="org.eclipse.ui.handlers.RegistryToggleState:true"
               id="org.eclipse.ui.commands.toggleState">
         </state>
      </command>
   </extension>
   
   <extension
         point="org.eclipse.ui.commandImages">
      <image
            commandId="gov.sandia.dart.cubit.ui.command.link"
            icon="icons/link.gif">
      </image>
   </extension>
      
   <extension
         point="org.eclipse.ui.handlers">
      <handler
            class="com.strikewire.snl.apc.commands.ToggleHandler"
            commandId="gov.sandia.dart.cubit.ui.command.link">
      </handler>
    </extension>     
   
    <extension
         point="org.eclipse.ui.menus">
      <menuContribution
            allPopups="false"
            locationURI="toolbar:gov.sandia.dart.cubit.ui.modelViewer">
         <command
               commandId="gov.sandia.dart.cubit.ui.command.link"
               style="toggle">
         </command>
       </menuContribution>
    </extension>

 */
public abstract class AbstractViewPartWithLinking extends AbsPersistingView
{
	protected boolean linking_;

	protected void initLinking(String commandId) {
		ICommandService commandService = (ICommandService) getSite().getService(ICommandService.class);
		final Command command = commandService.getCommand(commandId);
		command.addExecutionListener(new ExecutionAdapter() {
			/* (non-Javadoc)
			 * @see com.strikewire.snl.apc.commands.ExecutionAdapter#postExecuteSuccess(java.lang.String, java.lang.Object)
			 */
			@Override
			public void postExecuteSuccess(String commandId, Object returnValue) {
				updateLinkingState(command);
			}
			
			/* (non-Javadoc)
			 * @see com.strikewire.snl.apc.commands.ExecutionAdapter#postExecuteFailure(java.lang.String, org.eclipse.core.commands.ExecutionException)
			 */
			@Override
			public void postExecuteFailure(String commandId, ExecutionException exception) {
				updateLinkingState(command);
			}
		});
		
		updateLinkingState(command);
	}

	protected void updateLinkingState(Command linkingCommand) {
		linking_ = CommandUtil.getCommandStateNoThrow(linkingCommand, false);	
	}
}
