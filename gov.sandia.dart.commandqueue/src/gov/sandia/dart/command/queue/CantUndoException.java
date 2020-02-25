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
 * (C) 2005 Sandia National Laboratories<BR>
 * $Id: CantUndoException.java,v 1.1 2005/07/05 15:48:59 ejfried Exp $
 */
public class CantUndoException extends QueueException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 2522097417473775049L;

	public CantUndoException(String why) {
        super(why);
    }

	public CantUndoException(Exception e) {
		super(e.getMessage(), e);
	}
}
