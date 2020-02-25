/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by mjgibso on Mar 12, 2013 at 5:07:16 AM
 */
package com.strikewire.snl.apc.sourceprovider;

import org.eclipse.ui.ISources;

/**
 * @author mjgibso
 *
 */
public abstract class SourceVariable
{
	private final String name_;
	
	private final ISourceChangeNotifier notifier_;
	
	private int sourcePriority_ = ISources.WORKBENCH;
	
	public SourceVariable(String name, ISourceChangeNotifier broadcaster)
	{
		this(name, broadcaster, ISources.WORKBENCH);
	}
	
	public SourceVariable(String name, ISourceChangeNotifier notifier, int priority)
	{
		this.name_ = name;
		
		this.notifier_ = notifier;
		
		this.sourcePriority_ = priority;
		
		hookChangeNotification();
	}
	
	public String getName()
	{
		return this.name_;
	}
	
	public abstract Object getValue();
	
	protected abstract void hookChangeNotification();
	
	public void fireSourceValueChanged(Object newValue)
	{
		notifier_.fireSourceValueChanged(this.sourcePriority_, this.name_, newValue);
	}
	
	public abstract void dispose();
}
