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
 * Hold the current state of the system.
 * <P>
 * (C) 2000 Sandia National Laboratories
 * @version $Id: CommandInterpreterState.java,v 1.19 2007/03/02 23:01:07 ejfried Exp $
 */

public class CommandInterpreterState implements ICommandInterpreterState {

    private ITreeNode m_currentNode;
    private IUndoManager m_undoManager;
    private Notifier m_notifier;

    public CommandInterpreterState(ITreeNode iTreeNode, IUndoManager undoManager, Notifier notifier) {
        m_notifier = notifier;
        m_currentNode = iTreeNode;
        m_undoManager = undoManager;
    }

    /* (non-Javadoc)
	 * @see gov.sandia.simba.cli.ICommandIntepreterState#getCurrentNode()
	 */
    public ITreeNode getCurrentNode() {
        return m_currentNode;
    }

    /* (non-Javadoc)
	 * @see gov.sandia.simba.cli.ICommandIntepreterState#setCurrentNode(gov.sandia.simba.command.queue.ITreeNode)
	 */
    public void setCurrentNode(ITreeNode currentNode) {
        m_currentNode = currentNode;
    }

    /* (non-Javadoc)
	 * @see gov.sandia.simba.cli.ICommandIntepreterState#shutdown()
	 */
    public void shutdown() {
        try {
            getCurrentNode().root().save();
        }
        catch(Exception e) {
            m_notifier.reportError(e);
        }
        System.exit(0);
    }

    /* (non-Javadoc)
	 * @see gov.sandia.simba.cli.ICommandIntepreterState#getUndoManager()
	 */
    public IUndoManager getUndoManager() {
        return m_undoManager;
    }

    /* (non-Javadoc)
	 * @see gov.sandia.simba.cli.ICommandIntepreterState#getNotifier()
	 */
    public Notifier getNotifier() {
        return m_notifier;
    }

    /* (non-Javadoc)
	 * @see gov.sandia.simba.cli.ICommandIntepreterState#setNotifier(gov.sandia.simba.Notifier)
	 */
    public void setNotifier(Notifier notifier) {
        m_notifier = notifier;
    }
}
