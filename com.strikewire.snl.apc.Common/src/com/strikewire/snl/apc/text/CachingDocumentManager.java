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
 * Created by mjgibso on Nov 21, 2013 at 6:59:10 AM
 */
package com.strikewire.snl.apc.text;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.listeneradapters.ElementStateAdapter;
import com.strikewire.snl.apc.util.IDelayedRunner;

/**
 * @author mjgibso
 *
 */
public abstract class CachingDocumentManager implements IDocumentManager
{
	private static final int DISPOSAL_DELAY = 1000;
	
	private final IDelayedRunner _disposalChecker;
	
	private final IDocumentProvider _provider;
	
	private final Object _lock = new Object();
	
	private FileEditorInput _input;
	
	private IDocument _document;
	
	private final IElementStateListener _elementStateListener = new ElementStateListener();
	
	private final ConcurrentMap<IDocumentHandle, IDocumentHandle> _liveHandles = new ConcurrentHashMap<IDocumentHandle, IDocumentHandle>();
	
	protected CachingDocumentManager()
	{
		this(false);
	}
	
	protected CachingDocumentManager(boolean persistentCache)
	{
		this(persistentCache, new TextFileDocumentProvider());
	}
	
	protected CachingDocumentManager(boolean persistentCache, IDocumentProvider provider)
	{
		if(provider == null)
		{
			throw new IllegalArgumentException("Document provider cannot be null");
		}
		
		this._provider = provider;
		
		this._disposalChecker = persistentCache ? null : IDelayedRunner.newRunner(new Runnable() {
			
			@Override
			public void run() {
				// we need to check again to see if the live handles collection is still empty, and
				// we do need to synch between the check for empty and the call to dispose, otherwise, it would
				// be possible for another thread to grab another handle between our check of empty and the call
				// to dispose, thus disposing and ending the document's life-cycle based on the belief that
				// nobody still cares about it, when in-fact that information is now old, and somebody now does,
				// yet the trash truck is already on its way, too late to be stopped (so to speak).
				
				synchronized (_lock) {
					if(_liveHandles.isEmpty())
					{
						dispose();
					}
				}
			}
		}, DISPOSAL_DELAY);
	}
	
	protected IDocumentProvider getDocumentProvider()
	{
		return this._provider;
	}
	
	public void saveDocument(IProgressMonitor monitor) 
	{
		// do everything we can to avoid locking if we don't have to (see DTA-9046)
		if(_input==null || _document==null)
		{
			return;
		}

		try {
			synchronized (_lock) {
				if(_input!=null && _document!=null)
				{
					_provider.saveDocument(monitor, _input, _document, true);
				}
			}
		} catch (CoreException e) {
			CommonPlugin.getDefault().log(e.getStatus());
		}
	}
	
	public boolean isDocumentDirty()
	{
		// do everything we can to avoid locking if we don't have to (see DTA-9046)
		if(_input == null)
		{
			return false;
		}
		
		synchronized (this) {
			return _input==null ? false : _provider.canSaveDocument(_input);
		}
	}

	public void dispose()
	{
		synchronized (_lock) {
			for(IDocumentHandle handle : this._liveHandles.keySet())
			{
				handle.dispose();
			}
			// shouldn't have to call the clear here, as each dispose should have
			// called back to documentHandleDisposed, which should remove the handle,
			// but no harm in calling clear to be safe
			this._liveHandles.clear();
			
			if(_document != null)
			{
				uninitializeDocument(_document);
			}
			_document = null;
			
			if(_input != null)
			{
				_provider.disconnect(_input);
			}
			_input = null;
		}
	}
	
	public IDocumentHandle getDocumentHandle() throws CoreException
	{
		synchronized (_lock) {
			IDocument document = getDocument(true);
			IDocumentHandle handle = new DocumentHandle(document, this);
			this._liveHandles.put(handle, handle);
			return handle;
		}
	}
	
	public void perform(IDocumentOperation op) throws Exception
	{
		IDocumentHandle handle = null;
		try {
			handle = getDocumentHandle();
			IDocument doc = handle.document();
			op.execute(doc);
		} finally {
			if(handle != null)
			{
				handle.dispose();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.strikewire.snl.apc.text.IDocumentManager#documentHandleDisposed(com.strikewire.snl.apc.text.IDocumentHandle)
	 */
	@Override
	public boolean documentHandleDisposed(IDocumentHandle handle)
	{
		// do everything we can to avoid locking if we don't have to (see DTA-9046)
		if(this._liveHandles.isEmpty())
		{
			return false;
		}
		
		boolean found = (this._liveHandles.remove(handle) != null);
		
		// we're not thread protected WRT another thread getting another handle after we removed this one,
		// but before we check to see if this was the last one, but that's ok, because even if this was
		// the last one when we took it out, if another one gets added before we check now to see if there
		// are none left, we'll see that there is still (newly) something there, and therefore we won't
		// dispose.  So in actuality, NOT being rigorously thread synchronous here actually has the potential
		// to save us an un-necessary document destruction and re-creation.  Again, this is all
		// in an attempt to minimize locking requirements as part of addressing DTA-9046.
		
		// Only check to see if this was the last one though if we did remove it, thus shrinking the _liveHandle
		// count, and if we would dispose if it was the last one (if we're not persistent and therefore we have
		// a non-null disposal checker).
		
		if(found && _disposalChecker!=null && this._liveHandles.isEmpty())
		{
			// Since we'll ultimately need to check for an empty list and do the dispose in a synch block to
			// avoid another handle sneaking in by another thread in between, let's push that off into another
			// thread so we eliminate entirely having to do any locking within this method.  While we're at it,
			// do it in a DelayedRunner, such that if lots of things are quickly accessing then releasing the
			// document, but one at a time, so the liveHandles count is toggling quickly between 1 & 0, we won't
			// try to clear the cache every time it goes to 0, but only after it goes to zero and has calmed down
			// for a little while (DISPOSAL_DELAY).
			_disposalChecker.schedule();
		}
		
		return found;
	}
	
	protected FileEditorInput getInput()
	{
		return _input;
	}
	
	protected IDocument getDocument(boolean create) throws CoreException
	{
		synchronized (_lock)
		{
			if(_document==null && create)
			{
				IFile file = getFile();
				if(file!=null && file.exists())
				{
					_input = new FileEditorInput(file);
					_provider.connect(_input);
					this._document = _provider.getDocument(_input);
					if(this._document != null)
					{
						initializeDocument(_document);
					}
				}
			}
			
			return _document;
		}
	}
	
	protected void initializeDocument(IDocument doc)
	{
		getDocumentProvider().addElementStateListener(_elementStateListener);
	}
	
	protected void uninitializeDocument(IDocument doc)
	{
		getDocumentProvider().removeElementStateListener(_elementStateListener);
	}
	
	protected abstract IFile getFile() throws CoreException;
	
	protected class ElementStateListener extends ElementStateAdapter
	{
		@Override
		public void elementDeleted(Object element)
		{
			if(element!=null && element.equals(getInput()))
			{
				dispose();
			}
		}
	}
}
