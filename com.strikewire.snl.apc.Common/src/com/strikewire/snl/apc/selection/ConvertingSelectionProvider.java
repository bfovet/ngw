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
package com.strikewire.snl.apc.selection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author mjgibso
 *
 */
public abstract class ConvertingSelectionProvider extends RepeatingSelectionProvider
{
	/**
	 * @param baseProvider
	 */
	public ConvertingSelectionProvider(ISelectionProvider baseProvider)
	{
		super(baseProvider);
	}
	
	/**
	 * @param baseProvider
	 * @param workbenchPage
	 */
	public ConvertingSelectionProvider(ISelectionProvider baseProvider, IWorkbenchPage workbenchPage)
	{
		super(baseProvider, workbenchPage);
	}

	@Override
	public void setSelection(ISelection selection)
	{
		super.setSelection(convertSelection(selection));
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		super.selectionChanged(new SelectionChangedEvent(event.getSelectionProvider(), convertSelection(event.getSelection())));
	}
	
	protected abstract ISelection convertSelection(ISelection selection);
}
