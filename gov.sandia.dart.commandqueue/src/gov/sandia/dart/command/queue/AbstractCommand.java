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

import java.util.Arrays;

/**
 * A base command class.
 * <p/>
 * (C) 2000 Sandia National Laboratories
 *
 * @version $Id: AbstractCommand.java,v 1.15 2007/07/19 22:19:11 raw Exp $
 */
public abstract class AbstractCommand implements Command {

    protected final static int INFINITE_ARGS = -1;

    private String m_name;

    public AbstractCommand(String name) {
        m_name = name;
    }
    
    public String getDescription() {
    	return name();
    }

    public String name() {
        return m_name;
    }

    public String usage() {
        return "Usage: " + name() + " " + usageArgs().trim();
    }

    public void checkArgCount(String[] argv, int count) throws QueueException {
        checkArgCount(argv, count, count);

    }

    public void checkArgCount(String[] argv, int minArgs, int maxArgs) throws QueueException {
        int len = argv.length - 1;

        if (len < minArgs) {
            throw new UnknownCommandException("Too few arguments");
        }

        if (maxArgs != INFINITE_ARGS && len > maxArgs) {
            throw new UnknownCommandException("Too many arguments");
        }
    }

    public abstract String usageArgs();

    @Override
    public Object exec(String[] argv, ICommandInterpreterState cis) throws QueueException {		
		try {
			return execute(argv, cis);
		} catch (QueueException ex) {
			throw new CommandExecutionException(ex.getMessage(), ex);
		}
	}

	public abstract Object execute(String[] argv, ICommandInterpreterState cis) throws QueueException;

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

	
	
}
