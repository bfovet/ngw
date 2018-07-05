/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/**
 * 
 */
package gov.sandia.dart.common.core.listeners;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An improved implementation idea from the Eclipse ListenerList in
 * that it allows for the Generics, and does not work with arrays but
 * rather Collections.
 * @author mjgibso
 *
 */
public class BaseListenersHandler<L> implements IListenersProvider<L>
{
	/**
	 * listeners_ - Those who are listening
	 * 2015-12-30(kho): update to use Java concurrent and remove all
	 * the synchronization
	 */
	private final Set<L> listeners_ = Collections.newSetFromMap(new ConcurrentHashMap<L, Boolean>());

	
	public boolean addListener(L listener)
	{
		if(listener == null)
		{
			return false;
		}
		
		return this.listeners_.add(listener);
	}
	
	public boolean removeListener(L listener)
	{
		if(listener == null)
		{
			return false;
		}
		

		return this.listeners_.remove(listener);
	}
	
	/**
	 * Returns an unmodifiable Collection of the listeners.
	 */
	@Override
  public Collection<L> getListeners()
	{
	  return Collections.unmodifiableSet(this.listeners_);
	}
	
	/**
	 * Removes all of the current listeners
	 */
	public void clearListeners()
	{
	  listeners_.clear();
	}
}
