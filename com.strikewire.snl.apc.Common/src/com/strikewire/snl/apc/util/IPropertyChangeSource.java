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
 * Created on Feb 25, 2005
 *
 * Copyright (C) 2005
 *    
 *  All Rights Reserved
 *
 *  StrikeWire, LLC
 *  368 South McCaslin Blvd., #115
 *  Louisville, CO 80027
 *  (720) 890-8591
 *  support@strikewire.com
 *
 *  COMPANY PROPRIETARY
 *
 *  $Author: gpike $
 *  $Date: 2005/08/03 20:30:01 $
 *  
 *  $Name:  $ 
 *
 * FILE: 
 *  $Source: /cvs_root/snl/current/apc/plugins/com.strikewire.snl.apc.Common/src/com/strikewire/snl/apc/util/IPropertyChangeSource.java,v $
 *
 *
 * Description ($Revision: 1.1 $):
 */
package com.strikewire.snl.apc.util;

import org.eclipse.compare.IPropertyChangeNotifier;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;

/**
 * <p>
 * When this interface was first construceted, 
 * there did not seem to be an Interface for specifying the
 * handling of add/remove PropertyChangeListener, even though the
 * pattern is to add/remove from a class for the property change
 * listener.<p/>
 * <p>
 * This Interface was therefore designed to be added to a class that
 * will provide property change events.
 * </p>
 * <p>Now there is IPropertyChangeNotifier, which had the same methods
 * as this Interface used to stipulate. 
 * </p>
 * Implementing classes may wish to use the ListenerList class
 * 
 * @see IPropertyChangeNotifier
 * @see ListenerList
 * 
 * @author kholson
 * @author gpike
 * $Name:  $
 */

public interface IPropertyChangeSource
{
	/**
	 * Property Change Event key
	 */
	static final String LISTADD = "LISTADD";
	/**
	 * Property Change Event key
	 */
	static final String LISTREMOVE = "LISTREMOVE";

	
	/**
	 * Add a property change listener;
	 * @see IPropertyChangeNotifier#addPropertyChangeListener(IPropertyChangeListener)
	 */
	public void addPropertyChangeListener(IPropertyChangeListener listener);
	
	/**
	 * Remove a property change listener
	 * @see IPropertyChangeNotifier#removePropertyChangeListener(IPropertyChangeListener)
	 */
	public void removePropertyChangeListener(IPropertyChangeListener listener);
}
