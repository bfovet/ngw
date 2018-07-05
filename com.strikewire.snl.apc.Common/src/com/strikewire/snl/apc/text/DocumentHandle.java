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
 * Created by mjgibso on Nov 26, 2013 at 4:37:51 AM
 */
package com.strikewire.snl.apc.text;

import org.eclipse.jface.text.IDocument;

/**
 * @author mjgibso
 *
 */
public class DocumentHandle implements IDocumentHandle
{
	private boolean _disposed = false;
	
	private final IDocument _document;
	
	private final IDocumentManager _manager;
	
	/**
	 * 
	 */
	public DocumentHandle(IDocument document, IDocumentManager manager)
	{
		this._document = document;
		
		this._manager = manager;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.text.IDocumentHandle#document()
	 */
	@Override
	public IDocument document()
	{
		if(this._disposed)
		{
			throw new IllegalStateException("Document has ben disposed");
		}
		
		return this._document;
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.text.IDocumentHandle#dispose()
	 */
	@Override
	public synchronized boolean dispose()
	{
		boolean oldValue = this._disposed;
		
		this._disposed = true;
		
		// if we weren't previously disposed
		if(!oldValue)
		{
			this._manager.documentHandleDisposed(this);
		}
		
		return oldValue;
	}
}
