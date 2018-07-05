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
 * (C) 2003 Sandia National Laboratories
 * $Id: CommandTask.java,v 1.1 2003/12/19 19:43:47 ejfried Exp $
 */
public interface CommandTask {
    void run(CommandExecutionRecord record) throws QueueException;
}
