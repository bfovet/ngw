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
 * Created by mjgibso on Mar 12, 2013 at 5:06:31 AM
 */
package com.strikewire.snl.apc.sourceprovider;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISourceProvider;

/**
 * @author mjgibso
 *
 */
public abstract class DelegatingSourceProvider extends AbstractSourceProvider implements ISourceProvider, ISourceChangeNotifier
{
	protected final Map<String, SourceVariable> variables_;
	
	/**
	 * 
	 */
	public DelegatingSourceProvider()
	{
		Map<String, SourceVariable> variablesMap = new HashMap<String, SourceVariable>();
		
		Collection<SourceVariable> variables = initVariables();
		if(variables!=null && variables.size()>0)
		{
			for(SourceVariable variable : variables)
			{
				variablesMap.put(variable.getName(), variable);
			}
		}
		
		this.variables_ = Collections.unmodifiableMap(variablesMap);
	}
	
	protected abstract Collection<SourceVariable> initVariables();
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISourceProvider#dispose()
	 */
	@Override
	public void dispose()
	{
		for(SourceVariable var : variables_.values())
		{
			var.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISourceProvider#getCurrentState()
	 */
	@Override
	public Map<String, Object> getCurrentState()
	{
		Map<String, Object> state = new HashMap<String, Object>();
		
		for(SourceVariable variable : variables_.values())
		{
			state.put(variable.getName(), variable.getValue());
		}
		
		return state;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISourceProvider#getProvidedSourceNames()
	 */
	@Override
	public String[] getProvidedSourceNames()
	{
		return variables_.keySet().toArray(new String[variables_.size()]);
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.sourceprovider.ISourceChangeNotifier#fireSourceChanged(int, java.lang.String, java.lang.Object)
	 */
	@Override
	public void fireSourceValueChanged(int sourcePriority, String sourceName, Object sourceValue)
	{
		fireSourceChanged(sourcePriority, sourceName, sourceValue);
	}
}
