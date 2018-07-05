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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * A queue of pending Sid commands
 * <p/>
 * (C) 2000 Sandia National Labs
 * $Id: CommandQueue.java,v 1.107 2008/11/05 21:59:50 ejfried Exp $
 */

public class CommandQueue implements Runnable {

    private final static String s_globalLock = "Xyzzy";

    private final List<Entry> m_queue = Collections.synchronizedList(new ArrayList<Entry>());
    private volatile boolean m_running = true;
    private volatile static CommandQueue m_singleton;
    private ArrayList<CommandTask> m_preCommandTasks  = new ArrayList<CommandTask>();
    private ArrayList<CommandTask> m_postCommandTasks = new ArrayList<CommandTask>();
    private Stack<UndoableCommand> m_undoStack = new Stack<UndoableCommand>();
    private IUndoManager m_undoManager;
    private static final String COMMAND_QUEUE_NAME = "Command Queue";
    private volatile Thread m_thread = null;

    private CommandQueue() {
		// Retrieve all commands extending the command extension point,
		// and add them to the CommandQueue
		IExtension[] extensions =
			Platform.getExtensionRegistry().getExtensionPoint(CommandQueuePlugin.PLUGIN_ID, "command").getExtensions();
		
		for(int i = 0; i < extensions.length; ++i) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; ++j) {
				if(!configElements[j].getName().equals("command")) {
					continue;
				}

				Command cmd;
				try {
					cmd = (Command) configElements[j].createExecutableExtension("class");
					overWriteCmd(cmd);
				} catch (Exception e) {
					// TODO Log this
					continue;
				}
			}
		}
		
		extensions =
			Platform.getExtensionRegistry().getExtensionPoint(CommandQueuePlugin.PLUGIN_ID, "commandsource").getExtensions();
		
		for(int i = 0; i < extensions.length; ++i) {
			IConfigurationElement[] configElements = extensions[i].getConfigurationElements();
			for (int j = 0; j < configElements.length; ++j) {
				if(!configElements[j].getName().equals("commandsource")) {
					continue;
				}

				CommandSource cmdsrc;
				try {
					cmdsrc = (CommandSource) configElements[j].createExecutableExtension("class");
					IUndoManager manager = cmdsrc.getUndoManager();
					if (manager != null)
						m_undoManager = manager;
					cmdsrc.addCommands(this);
					cmdsrc.addTasks(this);
				} catch (Exception e) {
					// TODO Log this
				}
				
			}
		}

		if (m_undoManager != null)
			m_undoManager.init();
        m_thread = new Thread(this);
        m_thread.setDaemon(true);
        m_thread.setName(COMMAND_QUEUE_NAME);
        m_thread.start();
    }
    
    public synchronized Thread getThread() {
      return m_thread;
    }

    public static synchronized CommandQueue get() {
        if (m_singleton == null) {
            m_singleton = new CommandQueue();
        }

        return m_singleton;
    }

    public static Object getLock() {
        return s_globalLock;
    }

    public synchronized void undo(Notifier notifier) throws CantUndoException {
        if (m_undoStack.size() > 0) {
            final UndoableCommand cmd = m_undoStack.pop();
            fireEvent(new UndoEvent(this));
            add(new QueuedUndoCommand(cmd), null, notifier); // TODO Need target node?
        } else {
            throw new CantUndoException("Nothing to undo.");
        }
    }

    synchronized void directUndo(ICommandInterpreterState cis) throws QueueException {
        if (m_undoStack.size() > 0) {
            final UndoableCommand cmd = m_undoStack.pop();
            fireEvent(new UndoEvent(this));
            try {
                cis.getNotifier().reportStatus(Notifier.HIGH, "Undoing: " + cmd.getUndoMessage());
                cmd.undo(cis);
            } finally {
                cmd.discardUndoState();
            }
        } else {
            throw new CantUndoException("Nothing to undo.");
        }
    }

    public synchronized boolean canUndo() {
        return m_undoStack.size() > 0;
    }

    public synchronized String topUndoMessage() {
        if (canUndo()) {
            final UndoableCommand cmd = m_undoStack.peek();
            return cmd.getUndoMessage();
        } else {
            return "";
        }
    }

    /**
     * Adds a command to the command queue, waits for it, and returns the result of the command.
     * All exceptions will be rethrown on the calling thread.
     */
    public Object addAndWait(Command cmd, String[] argv, ICommandInterpreterState ciState) throws QueueException {
        Entry entry;
        Notifier notifier = ciState.getNotifier();
        if (Thread.currentThread() == m_singleton.getThread()) {
            entry = new Entry(cmd, argv, ciState);
            entry.execute();
        } else {
            entry = add(cmd, argv, ciState);
            synchronized (entry) {
                while (!entry.isCompleted()) {
                    try {
                        entry.wait();
                    } catch (InterruptedException e) {
                        // Keep waiting
                    }
                }
                notifier.flush();
            }
        }
        if (entry.getException() != null) {
        	Throwable ex = entry.getException();
        	if (ex instanceof QueueException)
        		throw (QueueException) ex;
        	else
        		throw new QueueException(entry.getException().getMessage(), entry.getException());
        } else {
            return entry.getResult();
        }
    }


    public class Entry {
        private Command m_cmd;
        private String[] m_args;
        private Notifier m_notifier;
        private ICommandInterpreterState m_ciState;
        private Throwable m_exception;
        private boolean m_completed;
        private Object m_result;

        Entry(Command cmd, String[] args, ICommandInterpreterState cis) {
            m_cmd = cmd;
            m_args = args;
            m_ciState = cis;
            m_notifier = cis.getNotifier();
            m_completed = false;
        }

        synchronized void execute() {
            try {            	
                m_result = executeCommand(m_cmd, m_args, m_ciState, true);
            } catch (RuntimeException e) {
                // TODO Proper Eclipse logging
            	e.printStackTrace();
            	// Debug.writeImpossibleException(e);
                m_exception = e;
            } catch (Error e) {
                // Debug.writeImpossibleException(e);
                m_exception = e;
            } catch (Exception e) {
                m_exception = e;
            } finally {
                m_completed = true;
                notifyAll();
            }
        }

        @Override
		public String toString() {
            return m_cmd.name();
        }

        public Throwable getException() {
            return m_exception;
        }

        public synchronized boolean isCompleted() {
            return m_completed;
        }

        public Object getResult() {
            return m_result;
        }

        public Notifier getNotifier() {
            return m_notifier;
        }
    }

    String errorMessage(Exception e) {
        String message = e.getMessage();
        if (message == null || message.length() == 0) {
            String clazz = e.getClass().getName();
            clazz = clazz.substring(clazz.lastIndexOf('.') + 1);
            message = "Internal Error: " + clazz;
        }
        return message;
    }

    public IUndoManager getUndoManager() {
        return m_undoManager;
    }

    public void resetUndoStack() {
        m_undoStack.clear();
        fireEvent(new UndoEvent(this));
    }

    public Object executeCommand(Command cmd, String[] argv, ICommandInterpreterState cis, boolean fSave)
            throws QueueException {
		
        synchronized (getLock()) {
        	ITreeNode currentNode = cis.getCurrentNode();
            ITreeNode root = null;
            if (currentNode != null) {
                root = currentNode.root();
            }

            long startTime = System.currentTimeMillis();

            cmd = (Command) cmd.clone();

            if (cmd instanceof IdempotentCommand) {
                fSave = false;
            }

            CommandExecutionRecord record =
                    new CommandExecutionRecord(argv, cmd, currentNode, root, fSave, startTime, cis.getNotifier());

            runPreCommandActions(record);
            
            Object result = null;
            try {
            	result = cmd.exec(argv, cis);
            	record.setResult(result);
            } finally {
                runPostCommandActions(record);
            }


            return result;
        }
    }

    private void runPreCommandActions(CommandExecutionRecord record) {
        for (Iterator<CommandTask> it = m_preCommandTasks.iterator(); it.hasNext();) {
            try {
            	CommandTask task = it.next();
            	task.run(record);
            } catch (Exception ex) {
            	// Ignore errors in pre-command tasks.
            }
        }
    }

    private void runPostCommandActions(CommandExecutionRecord record) {
    	for (Iterator<CommandTask> it = m_postCommandTasks.iterator(); it.hasNext();) {
        	try {
        		// Very important that every post-command task is executed.
        		CommandTask task = it.next();
        		task.run(record);
        	} catch (Exception ex) {
        		// Ignore errors        	
        	}        	
        }
    }

    public void addPreCommandTask(CommandTask task) {
        m_preCommandTasks.remove(task);
        m_preCommandTasks.add(task);
    }

    public void removePreCommandTask(CommandTask task) {
        m_preCommandTasks.remove(task);
    }

    public void addPostCommandTask(CommandTask task) {
        m_postCommandTasks.remove(task);
        m_postCommandTasks.add(task);
    }

    public void removePostCommandTask(CommandTask task) {
        m_postCommandTasks.remove(task);
    }

    public Entry add(Command cmd, ITreeNode dir, Notifier notifier) {
        return add(cmd, new String[]{cmd.name()}, dir, notifier);
    }

    public Entry add(String cmd, String[] args, ITreeNode dir, Notifier notifier) throws UnknownCommandException {

        return add(lookupCommand(cmd), args, dir, notifier);
    }

    public void runOrAdd(Command cmd, String[] argv, ITreeNode dir, Notifier notifier) {
        Entry entry;
        if (Thread.currentThread().getName().equals(COMMAND_QUEUE_NAME)) {
            entry = new Entry(cmd, argv, new CommandInterpreterState(dir, getUndoManager(), notifier));
            try {
                entry.execute();
            } finally {
                if (entry.getException() != null) {
                    notifier.reportError(entry.getException());
                }
            }
        } else {
            add(cmd, argv, dir, notifier);
        }
    }

    public Entry add(Command cmd, String[] args, ITreeNode dir, Notifier notifier) {
        return add(cmd, args, new CommandInterpreterState(dir, getUndoManager(), notifier));        
    }

    public Entry add(Command cmd, String[] args, ICommandInterpreterState cis) {
        synchronized (m_queue) {
            Entry entry = new Entry(cmd, args, cis);
            m_queue.add(entry);
            m_queue.notifyAll();
            return entry;
        }
    }

    public void waitForAllCommands() {
        synchronized (m_queue) {
            while (m_queue.size() > 0) {
                try {
                    m_queue.wait(1000);
                } catch (InterruptedException ie) {
                    // Keep waiting
                }
            }
        }
    }

    @Override
	public void run() {
        while (m_running) {
            Entry entry;
            synchronized (m_queue) {
                while (m_queue.size() == 0) {
                    try {
                        m_queue.wait(1000);
                        if (!m_running) {
                            return;
                        }
                    } catch (InterruptedException ie) {
                    	// Just keep going
                    }
                }
                entry = m_queue.get(0);
            }
            // Leave this unsynchronized so commands can be added to
            // the queue while other ones are running.
            try {
                entry.execute();
            } finally {
                if (entry.getException() != null) {
                	System.err.flush();
                    entry.getNotifier().reportError(entry.getException());
                }
                synchronized (m_queue) {
                    // Need to remove this here, rather than up above, so that
                    // waitForAllCommands() knows a command is still running.
                    m_queue.remove(entry);
                    m_queue.notifyAll();
                }
            }
        }
    }

    /**
     * This is a map of (String, Command) pairs.  The String is
     * the name of the command ("cd" for example), and the Command
     * is the object whose "execute()" method will perform the
     * desired operation.
     */
    private TreeMap<String, Command> s_cmdMap = new TreeMap<String, Command>();

    public Iterator<String> commandNames() {
        return s_cmdMap.keySet().iterator();
    }

    public Command lookupCommand(String name) throws UnknownCommandException {

        Command cmd = s_cmdMap.get(name);
        if (cmd == null) {
            throw new UnknownCommandException("Unknown command: " + name + ".");
        } else {
            return cmd;
        }
    }

    public void addCmd(Command c) {
        s_cmdMap.put(c.name(), c);
    }

    public synchronized void overWriteCmd(Command c) {
        s_cmdMap.put(c.name(), c);
    }

    public Iterator<Command> allCommands() {
        return s_cmdMap.values().iterator();
    }
    
	private List<UndoListener> listeners = new LinkedList<UndoListener>();

    public void addUndoListener(UndoListener listener) {
    	if (listeners.contains(listener));
    		listeners.add(listener);
    }
    
    public void removeUndoListener(UndoListener listener) {
    	listeners.remove(listener);
    }
    
    private void fireEvent(UndoEvent event) {
    	for (UndoListener listener: listeners) {
    		listener.undoEvent(event);
    	}
    }

	public void setUndoStack(Stack<UndoableCommand> m_undoStack) {
		this.m_undoStack = m_undoStack;
	}

	public Stack<UndoableCommand> getUndoStack() {
		return m_undoStack;
	}
}
