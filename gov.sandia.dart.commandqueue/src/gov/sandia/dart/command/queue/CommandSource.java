/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.command.queue;

public interface CommandSource {
	void addCommands(CommandQueue queue);
	void addTasks(CommandQueue queue);
	
	// This is a kludge until I figure out how to deal with SIMBA-specific undo behavior.
	IUndoManager getUndoManager();
}
