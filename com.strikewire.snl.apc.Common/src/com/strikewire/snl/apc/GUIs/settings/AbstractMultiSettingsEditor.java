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
package com.strikewire.snl.apc.GUIs.settings;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author mjgibso
 * 
 */
public abstract class AbstractMultiSettingsEditor<E> extends
		AbstractSettingsEditor<E> implements IMultiSettingsEditor<E>
{

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.strikewire.snl.apc.GUIs.settings.ISettingsEditor#setNode(java.lang
	 * .Object)
	 */
	@Override
	public void setNode(E node) {
		setNodes(asList(node));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.strikewire.snl.apc.GUIs.settings.ISettingsEditor#getNode()
	 */
	@Override
	public E getNode() {
		Collection<E> nodes = getNodes();
		return nodes != null && nodes.size() > 0 ? nodes.iterator().next()
				: null;
	}

	/**
	 * Returns a List&lt;E&gt; with a single element of the specified node
	 * 
	 * @param node
	 * @return A List&lt;E&gt; with a single element of node
	 * @author mjgibso
	 *         <p>
	 *         Initial Javadoc date: Aug 12, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	private List<E> asList(E node)
	{
		return Arrays.asList(node);
	}
} //class
