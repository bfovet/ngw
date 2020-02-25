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
 * A generic command interface.
 * <P>
 * (C) 2000 Sandia National Laboratories
 * @version $Id: Command.java,v 1.5 2005/07/01 19:27:53 ejfried Exp $
 */
public interface Command extends Cloneable {

    String name();
    
    String getDescription();

    String usageArgs();

    Object clone();

    /** Execute the command.
     * @param argv The argument list.  argv[0] is the command name.
     * @param cis The state of the command interpreter.  Some commands may modify this ("cd", for example)
     * @return A result or side effect of executing the command. For many SIMBA
     * commands, this is a newly created or modified Node.
     */
    Object exec(String[] argv, ICommandInterpreterState cis) throws QueueException;

}
