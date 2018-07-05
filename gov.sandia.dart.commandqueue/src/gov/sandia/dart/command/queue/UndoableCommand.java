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
 * $Id: UndoableCommand.java,v 1.1 2005/12/09 00:15:27 raw Exp $
 */
public interface UndoableCommand extends Command {
    boolean canUndo();
    void undo(ICommandInterpreterState cis) throws CantUndoException;
    void discardUndoState();
    String getUndoMessage();
    void setUndoMessage(String msg);
    // Some commands may be either undoable or idempotent depending on
    // command-line args.
    // Can call this AFTER execute() to see if, after all, the command
    // was idempotent.
    boolean wasIdempotent();
}
