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


/**
 * An IdempotentCommand doesn't <i>need</i> to be undone.
 * (C) 2005 Sandia National Laboratories<BR>
 * $Id: IdempotentCommand.java,v 1.1 2005/12/09 00:15:27 raw Exp $
 */
public interface IdempotentCommand extends Command {
}
