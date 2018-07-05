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
 * (C) 2003 Sandia National Laboratories
 * $Id: CommandExecutionRecord.java,v 1.6 2007/03/02 23:01:17 ejfried Exp $
 */
public class CommandExecutionRecord {
    private Command m_cmd;
    private ITreeNode m_currentNode;
    private ITreeNode m_root;
    private boolean m_save;
    private long m_startTime;
    private String[] m_argv;
    private Object m_result;
    private Notifier m_notifier;

    public CommandExecutionRecord(String[] argv, Command cmd, ITreeNode currentNode,
                                  ITreeNode root, boolean save, long startTime, Notifier notifier) {
        m_argv = argv;
        m_cmd = cmd;
        m_currentNode = currentNode;
        m_root = root;
        m_save = save;
        m_startTime = startTime;
        m_notifier = notifier;
    }


    public String[] getArgv() {
        return m_argv;
    }

    public Command getCmd() {
        return m_cmd;
    }

    public ITreeNode getCurrentNode() {
        return  m_currentNode;
    }

    public ITreeNode getRoot() {
        return m_root;
    }

    public boolean isSave() {
        return m_save;
    }

    public long getStartTime() {
        return m_startTime;
    }

    public Object getResult() {
        return m_result;
    }

    void setResult(Object result) {
        m_result = result;
    }


    public Notifier getNotifier() {
        return m_notifier;
    }
}
