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

import gov.sandia.dart.command.queue.QueueException;


/**
 * (C) 2005 Sandia National Laboratories<BR>
 * $Id: DirectUndoCommand.java,v 1.5 2005/12/09 00:15:26 raw Exp $
 */
public class DirectUndoCommand implements Command, IdempotentCommand {
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

    public Object exec(String[] argv, ICommandInterpreterState cis) throws QueueException {
        try { 
        	CommandQueue.get().directUndo(cis);
        } catch(QueueException e) {        	
        	CommandQueue.get().resetUndoStack();
        	CommandQueue.get().getUndoManager().init();        	
        	throw e;
        }
        return null;
    }
}
