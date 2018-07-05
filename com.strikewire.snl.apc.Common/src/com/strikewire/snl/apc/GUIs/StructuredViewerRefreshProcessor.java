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
 * Created by mjgibso on Jul 31, 2014 at 3:28:31 PM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Display;

import com.strikewire.snl.apc.util.BatchProcessor;

/**
 * @author mjgibso
 *
 */
public class StructuredViewerRefreshProcessor<E>
{
	private final BatchProcessor<Request> _processor;
	
	protected final StructuredViewer _viewer;
	
	public StructuredViewerRefreshProcessor(StructuredViewer viewer, String jobName, int delay)
	{
		this._processor = new Processor(jobName, delay);
		
		this._viewer = viewer;
	}
	
	// TODO add overloaded methods to allow specification of updateLabels for refresh requests,
	// and properties for update requests.  Haven't done so yet, as the primary usage of this framework
	// at present is TreeViewerRefreshProcessor which doesn't yet respect them.
	
	public void refresh(E element)
	{
		_processor.queue(new RefreshRequest(element));
	}
	
	public void refresh(Collection<E> elements)
	{
		Collection<Request> requests = new ArrayList<Request>(elements.size());
		for(E element : elements)
		{
			requests.add(new RefreshRequest(element));
		}
		_processor.queue(requests);
	}
	
	public void update(E element)
	{
		_processor.queue(new UpdateRequest(element));
	}
	
	public void update(Collection<E> elements)
	{
		Collection<Request> requests = new ArrayList<Request>(elements.size());
		for(E element : elements)
		{
			requests.add(new UpdateRequest(element));
		}
		_processor.queue(requests);
	}
	
	public void fullRefresh()
	{
		_processor.queue(new RefreshRequest(null));
	}
	
	protected void process(final Collection<Request> elements, final IProgressMonitor monitor)
	{
		// TODO first optimize the requests.  For example, if any are full-refresh requests (null element),
		// then there's no point in doing other refreshes (barring how updateLabels is set of course).
		// TODO batch updates.  Since the viewer has a batch update method use it.  Will first need to determine
		// correct behavior WRT properties.  i.e. should we send one batch of updates, with the union of all properties
		// specified?  Or do we need to collect updates into batches with unique properties sets?
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				try {
					monitor.beginTask("Processing viewer updates", elements.size());
					for(Request req : elements)
					{
//						monitor.subTask(name); // TODO get name from E maybe?
						req.process();
						monitor.worked(1);
						// TODO watch for cancelled and probably return a cancel status...
						// TODO if we do return cancel, and/or watch for error, would we want to
						// return any non-completed items to the queue if either occur?
					}
				} finally {
					monitor.done();
				}
			}
		});
	}
	
	private class Processor extends BatchProcessor<Request>
	{
		/**
		 * 
		 */
		public Processor(String jobName, int delay)
		{
			super(jobName, delay);
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.util.DelayedBatchProcessor#process(java.util.Collection, org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected void process(Collection<Request> elements, IProgressMonitor monitor)
		{
			StructuredViewerRefreshProcessor.this.process(elements, monitor);
		}
	}
	
	public abstract class Request
	{
		protected final E _element;
		
		/**
		 * 
		 */
		public Request(E element)
		{
			this._element = element;;
		}
		
		public abstract void process();
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return _element!=null ? _element.hashCode() : getClass().hashCode();
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj)
		{
			if(this == obj)
			{
				return true;
			}
			if(obj==null || !(obj instanceof StructuredViewerRefreshProcessor.Request) || !obj.getClass().equals(getClass()))
			{
				return false;
			}
			
			return Objects.equals(_element, ((StructuredViewerRefreshProcessor.Request) obj)._element);
		}
	}
	
	protected class RefreshRequest extends Request
	{
		private final boolean _updateLabels;
		
		/**
		 * 
		 */
		public RefreshRequest(E element)
		{
			this(element, true);
		}
		
		/**
		 * 
		 */
		public RefreshRequest(E element, boolean updateLabels)
		{
			super(element);
			this._updateLabels = updateLabels;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.StructuredViewerRefreshProcessor.Request#process()
		 */
		@Override
		public void process()
		{
			if(_element == null)
			{
				_viewer.refresh(_updateLabels);
			} else {
				_viewer.refresh(_element, _updateLabels);
			}
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.StructuredViewerRefreshProcessor.Request#equals(java.lang.Object)
		 */
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj)
		{
			if(!super.equals(obj))
			{
				return false;
			}
			
			return _updateLabels == ((StructuredViewerRefreshProcessor.RefreshRequest) obj)._updateLabels;
		}
	}
	
	protected class UpdateRequest extends Request
	{
		private final String[] _properties;
		
		/**
		 * 
		 */
		public UpdateRequest(E element)
		{
			this(element, null);
		}
		
		/**
		 * 
		 */
		public UpdateRequest(E element, String[] properties)
		{
			super(element);
			this._properties = properties;
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.StructuredViewerRefreshProcessor.Request#process()
		 */
		@Override
		public void process()
		{
			_viewer.update(_element, _properties);
		}
		
		/* (non-Javadoc)
		 * @see com.strikewire.snl.apc.GUIs.StructuredViewerRefreshProcessor.Request#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if(!super.equals(obj))
			{
				return false;
			}
			
			@SuppressWarnings("rawtypes")
			String[] otherProps = ((StructuredViewerRefreshProcessor.UpdateRequest) obj)._properties;
			return Objects.deepEquals(_properties, otherProps);
		}
	}
}
