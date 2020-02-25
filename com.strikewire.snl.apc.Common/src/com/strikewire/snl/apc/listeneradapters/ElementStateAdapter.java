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
 * Created by mjgibso on Sep 16, 2013 at 6:33:49 AM
 */
package com.strikewire.snl.apc.listeneradapters;

import org.eclipse.ui.texteditor.IElementStateListener;

/**
 * @author mjgibso
 *
 */
public abstract class ElementStateAdapter implements IElementStateListener
{
	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IElementStateListener#elementDirtyStateChanged(java.lang.Object, boolean)
	 */
	@Override
	public void elementDirtyStateChanged(Object element, boolean isDirty) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IElementStateListener#elementContentAboutToBeReplaced(java.lang.Object)
	 */
	@Override
	public void elementContentAboutToBeReplaced(Object element) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IElementStateListener#elementContentReplaced(java.lang.Object)
	 */
	@Override
	public void elementContentReplaced(Object element) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IElementStateListener#elementDeleted(java.lang.Object)
	 */
	@Override
	public void elementDeleted(Object element) {}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.texteditor.IElementStateListener#elementMoved(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void elementMoved(Object originalElement, Object movedElement) {}
}
