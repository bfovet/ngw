/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package com.strikewire.snl.apc.GUIs;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;

import com.strikewire.snl.apc.util.IDelayedRunner;

public abstract class AbstractSelectionTextEditor extends AbstractDecoratedTextEditor {

	private EditorSelectionProvider editorSelectionProvider = new EditorSelectionProvider();
	private SelectionProviderIntermediate selectionProviderIntermediate = new SelectionProviderIntermediate();
	private SelectionRunner selectionRunner = new SelectionRunner();

	private Object currentItem;
	
	public AbstractSelectionTextEditor() {
		super();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// The following code sets up the intermediate selection provider.  This allows us to dynamically change the 
		// selection provider when we want to broadcast selection ast node selection events, yet switch back to the
		// selection provider inherited from the AbstractTextEditor.
		getSite().setSelectionProvider(selectionProviderIntermediate);
		selectionProviderIntermediate.setSelectionProviderDelegate(getSelectionProvider());
	}

	@Override
	public void setFocus(){
		setSelection();
		super.setFocus();
	}

	@Override
	protected void handleCursorPositionChanged() {
		super.handleCursorPositionChanged();
				
		setSelection();
	}

	private void setSelection() {
		Object item = getItemAtCursor();
	
		if(item == null){
			return;
		}
		
		if(currentItem == null || !item.equals(currentItem))
		{
			selectionRunner.selectItem(item);
			currentItem = item;
		}
	}
	
	protected abstract Object getItemAtCursor();

	private EditorSelectionProvider getEditorSelectionProvider() {
		return editorSelectionProvider;
	}
	
	class EditorSelectionProvider implements ISelectionProvider
	{

		ListenerList listeners = new ListenerList();  
		ISelection selection;
		
		@Override
		public void addSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.add(listener);
		}

		@Override
		public ISelection getSelection() {
			if(selection != null)
				return selection;
			return new StructuredSelection();
		}

		@Override
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			listeners.remove(listener);
		}

		@Override
		public void setSelection(ISelection select) {
			selection = select;
			  Object[] list = listeners.getListeners();  
			  for (int i = 0; i < list.length; i++) {  
				  
				  ((ISelectionChangedListener) list[i]).selectionChanged(new SelectionChangedEvent(EditorSelectionProvider.this, selection)); 
			  }
		}
		
	}

	/**
	 * This delayed runner is called whenever the cursor position changes and it swaps the selection provider to one that can
	 * broadcast the selected node, and then immediately resets the selection provider to the one inherited from AbstractTextEditor.
	 * This is a workaround for the fact that a part can only have one selection provider.  The idea of using an intermediate
	 * selection provider (and the source code for it) was taken from http://www.eclipse.org/articles/Article-WorkbenchSelections/article.html.
	 * We need two selection providers and the eclipse selection service does not support this.  We need the original one so that basic text
	 * operations (like delete) work, but we need to broadcast AST node selections so that other views are in sync.  This change fixed bug 
	 * DTA-7036 (Cannot use the "delete" key in Sierra Editor).
	 * 
	 * @author elhoffm
	 *
	 */
	class SelectionRunner implements Runnable {
		
		private IDelayedRunner runner_;
		private Object lastItem;
		
		public SelectionRunner()
		{
			super();
			runner_ = IDelayedRunner.newRunner(this, 500);
		}
		
		public void selectItem(Object item)
		{
			lastItem = item;
			runner_.schedule();
		}
		
		@Override
		public void run() {
			Display.getDefault().asyncExec(new Runnable() {				
				@Override
				public void run() {
					getEditorSelectionProvider().setSelection(new StructuredSelection(lastItem));
					selectionProviderIntermediate.setSelectionProviderDelegate(getEditorSelectionProvider());
					selectionProviderIntermediate.setSelectionProviderDelegate(getSelectionProvider());
				}
			});
		}
	};

	/**
	 * IPostSelectionProvider implementation that delegates to another
	 * ISelectionProvider or IPostSelectionProvider. The selection provider used
	 * for delegation can be exchanged dynamically. Registered listeners are
	 * adjusted accordingly. This utility class may be used in workbench parts with
	 * multiple viewers.
	 * 
	 * @author Marc R. Hoffmann
	 */	
	class SelectionProviderIntermediate implements IPostSelectionProvider {

		private final ListenerList selectionListeners = new ListenerList();

		private final ListenerList postSelectionListeners = new ListenerList();

		private ISelectionProvider delegate;

		private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelectionProvider() == delegate) {
					fireSelectionChanged(event.getSelection());
				}
			}
		};

		private ISelectionChangedListener postSelectionListener = new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelectionProvider() == delegate) {
					firePostSelectionChanged(event.getSelection());
				}
			}
		};

		/**
		 * Sets a new selection provider to delegate to. Selection listeners
		 * registered with the previous delegate are removed before. 
		 * 
		 * @param newDelegate new selection provider
		 */
		public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
			if (delegate == newDelegate) {
				return;
			}
			if (delegate != null) {
				delegate.removeSelectionChangedListener(selectionListener);
				if (delegate instanceof IPostSelectionProvider) {
					((IPostSelectionProvider)delegate).removePostSelectionChangedListener(postSelectionListener);
				}
			}
			delegate = newDelegate;
			if (newDelegate != null) {
				newDelegate.addSelectionChangedListener(selectionListener);
				if (newDelegate instanceof IPostSelectionProvider) {
					((IPostSelectionProvider)newDelegate).addPostSelectionChangedListener(postSelectionListener);
				}
				fireSelectionChanged(newDelegate.getSelection());
				firePostSelectionChanged(newDelegate.getSelection());
			}
		}

		protected void fireSelectionChanged(ISelection selection) {
			fireSelectionChanged(selectionListeners, selection);
		}

		protected void firePostSelectionChanged(ISelection selection) {
			fireSelectionChanged(postSelectionListeners, selection);
		}

		private void fireSelectionChanged(ListenerList list, ISelection selection) {
			SelectionChangedEvent event = new SelectionChangedEvent(delegate, selection);
			Object[] listeners = list.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
				listener.selectionChanged(event);
			}
		}

		// IPostSelectionProvider Implementation

		@Override
		public void addSelectionChangedListener(ISelectionChangedListener listener) {
			selectionListeners.add(listener);
		}

		@Override
		public void removeSelectionChangedListener(
				ISelectionChangedListener listener) {
			selectionListeners.remove(listener);
		}

		@Override
		public void addPostSelectionChangedListener(
				ISelectionChangedListener listener) {
			postSelectionListeners.add(listener);
		}

		@Override
		public void removePostSelectionChangedListener(
				ISelectionChangedListener listener) {
			postSelectionListeners.remove(listener);
		}

		@Override
		public ISelection getSelection() {
			return delegate == null ? null : delegate.getSelection();
		}

		@Override
		public void setSelection(ISelection selection) {
			if (delegate != null) {
				delegate.setSelection(selection);
			}
		}

	}

	


}
