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

import gov.sandia.dart.command.queue.CantUndoException;

/**
 * (C) 2005 Sandia National Laboratories<BR>
 * $Id: QueuedUndoCommand.java,v 1.11 2007/03/02 23:01:17 ejfried Exp $
 */
class QueuedUndoCommand implements Command, IdempotentCommand {
    private final UndoableCommand m_cmd;

    public QueuedUndoCommand(UndoableCommand cmd) {
        m_cmd = cmd;
    }

    public String name() {
        return "undo";
    }

    public String getDescription() {
    	return name();
    }

    public String usageArgs() {
        return "";
    }

    public Object clone() {
        return this;
    }

    public Object exec(String[] args, ICommandInterpreterState cis) throws CantUndoException {
        try {
            cis.getNotifier().reportStatus(Notifier.HIGH, "Undoing: " + m_cmd.getUndoMessage());
            m_cmd.undo(cis);
        } catch(CantUndoException e) {
        	// Error during undo, so clear undo stack and reinitialize undo dir
        	CommandQueue.get().resetUndoStack();
        	CommandQueue.get().getUndoManager().init();
        	
        	throw e;   
        } finally {
            m_cmd.discardUndoState();
        }
        return null;
    }
}
