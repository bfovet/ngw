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

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.forms.IManagedForm;

import com.strikewire.snl.apc.selection.MultiControlSelectionProvider;

/**
 * An Interface for interacting with the Settings panel
 * @author mjgibso
 *
 */
public interface ISettingsEditor<E>
{
	/**
	 * Creates the part control for the Editor on the specified form, using the specified node
	 * @param messageView TODO
	 * @param site
	 * @param parent
	 * @param node
	 * @author kholson
	 * <p>
	 * Initial Javadoc date: Aug 12, 2013
	 * <p>
	 * Permission Checks:
	 * <p>
	 * History: <ul>
	 * <li>(kholson): created</li>
	 * </ul>
	 *<br />
	 */
	
	public void createPartControl(IManagedForm mform, IMessageView messageView, MultiControlSelectionProvider selectionProvider, IContextMenuRegistrar ctxMenuReg);

	
	/**
	 * Changes the node associated with the existing Editor
	 * @param node
	 * @author kholson
	 * <p>
	 * Initial Javadoc date: Aug 12, 2013
	 * <p>
	 * Permission Checks:
	 * <p>
	 * History: <ul>
	 * <li>(kholson): created</li>
	 * </ul>
	 *<br />
	 */
	public void setNode(E node);
	
	/**
	 * @return The node associated with the Editor
	 * @author kholson
	 * <p>
	 * Initial Javadoc date: Aug 12, 2013
	 * <p>
	 * Permission Checks:
	 * <p>
	 * History: <ul>
	 * <li>(kholson): created</li>
	 * </ul>
	 *<br />
	 */
	public E getNode();
	
	/**
	 * Method invoked when the settings editor is cleared.  The caller ensures the composite
	 * provided to the createPartControl method is disposed, so there's no need for implementors
	 * to cache and dispose that object.  However, if implementors create other widgets that need
	 * to be disposed, herein is wehre they can do so.
	 */
	public void dispose();
	
	/**
	 * Standard Object equals using the class of the object for comparison
	 * @param object
	 * @return
	 * @author kholson
	 * <p>
	 * Initial Javadoc date: Aug 12, 2013
	 * <p>
	 * Permission Checks:
	 * <p>
	 * History: <ul>
	 * <li>(kholson): created</li>
	 * </ul>
	 *<br />
	 */
	public boolean equals(Object object);
	
	/**
	 * Standard Object hashCode based upon the implementing class
	 * @return
	 * @author kholson
	 * <p>
	 * Initial Javadoc date: Aug 12, 2013
	 * <p>
	 * Permission Checks:
	 * <p>
	 * History: <ul>
	 * <li>(kholson): created</li>
	 * </ul>
	 *<br />
	 */
	public int hashCode();
	
	/**
	 * The implementor must set focus on a control when this method is called.
	 * If the implementor is not able to set focus on a control, it should
	 * return false.
	 * 
	 * @see {@link IViewPart#setFocus()}
	 * 
	 * @return whether or not the implementor was able to set focus on a control 
	 */
	public boolean setFocus();

	/**
	 * Some editors are better built from scratch. Return true from this method if you'd like your
	 * editor to never be reused for multiple different nodes in succession.
	*
	 * @return whether the editor can be retargeted to a new node
	 */
	public boolean isReusable();
}
