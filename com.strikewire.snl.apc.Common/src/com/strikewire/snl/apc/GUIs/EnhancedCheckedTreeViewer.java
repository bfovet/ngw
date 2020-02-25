/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
//package com.strikewire.snl.apc.GUIs;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.commons.lang3.StringUtils;
//import org.eclipse.core.resources.IContainer;
//import org.eclipse.core.resources.IFile;
//import org.eclipse.core.resources.IResource;
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.core.runtime.IPath;
//import org.eclipse.core.runtime.Path;
//import org.eclipse.jface.action.Action;
//import org.eclipse.jface.action.IMenuListener;
//import org.eclipse.jface.action.IMenuManager;
//import org.eclipse.jface.action.MenuManager;
//import org.eclipse.jface.viewers.BaseLabelProvider;
//import org.eclipse.jface.viewers.IBaseLabelProvider;
//import org.eclipse.jface.viewers.ICheckStateListener;
//import org.eclipse.jface.viewers.ILabelProvider;
//import org.eclipse.jface.viewers.ISelection;
//import org.eclipse.jface.viewers.IStructuredSelection;
//import org.eclipse.jface.viewers.ITreeContentProvider;
//import org.eclipse.jface.viewers.Viewer;
//import org.eclipse.jface.viewers.ViewerComparator;
//import org.eclipse.jface.viewers.ViewerFilter;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.layout.GridData;
//import org.eclipse.swt.widgets.Button;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Menu;
//import org.eclipse.swt.widgets.Tree;
//import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;
//
//public class EnhancedCheckedTreeViewer {
//
//	private ContainerCheckedTreeViewer inputFiles_;
//	private InputFilesContentProvider inputFilesContentProvider_;
//	private Button checkAllB;
//	private Button uncheckAllB;
//	
//	private final Action checkSelected_ = new Action("Check selected") {
//		public void run()
//		{
//			setSelectionChecked(true);
//		}
//	};
//	
//	private final Action uncheckSelected_ = new Action("Un-Check selected") {
//		public void run()
//		{
//			setSelectionChecked(false);
//		}
//	};		
//	
//	public Composite createControl(Composite composite, int numCols) {
//		// the input files label
//		Label inputFilesL = new Label(composite, SWT.NONE);
//		inputFilesL.setText("Input files:");
//		inputFilesL.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 2));
//		
//		// the input files list
//		inputFiles_ = new ContainerCheckedTreeViewer(composite, SWT.CHECK | SWT.MULTI | SWT.BORDER);
//		final Tree inputFilesTree = inputFiles_.getTree();
//		inputFilesTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, numCols-2, 2));
//		
//		// check all button
//		checkAllB = new Button(composite, SWT.PUSH);
//		checkAllB.setText("Check All");
//		checkAllB.setToolTipText("Select all files");
//		checkAllB.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
//		
//		// uncheck all button
//		uncheckAllB = new Button(composite, SWT.PUSH);
//		uncheckAllB.setText("Un-Check All");
//		uncheckAllB.setToolTipText("Un-Select all files");
//		uncheckAllB.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, false, false));
//		
//		// add a context menu to check or uncheck the selected items
//		MenuManager mgr = new MenuManager();
//		mgr.setRemoveAllWhenShown(true);
//		Menu inputFilesMenu = mgr.createContextMenu(inputFilesTree);
//		mgr.addMenuListener(new IMenuListener() {
//		
//			public void menuAboutToShow(IMenuManager manager) {
//				ISelection selection = inputFiles_.getSelection();
//				if(selection==null || selection.isEmpty() || !(selection instanceof IStructuredSelection))
//				{
//					return;
//				}
//				IStructuredSelection iss = (IStructuredSelection) selection;
//				if(iss.size() < 1)
//				{
//					return;
//				}
//				
//				manager.add(checkSelected_);
//				manager.add(uncheckSelected_);
//			}
//		});
//		inputFilesTree.setMenu(inputFilesMenu);	
//		
//		return composite;
//	}
//	
//	public void setAllChecked(boolean state)
//	{
//		Object[] rootElements = inputFilesContentProvider_.getElements(inputFiles_.getInput());
//		ViewerFilter[] filters = inputFiles_.getFilters();
//		if(rootElements != null)
//		{
//			for(Object rootElement : rootElements)
//			{
//				if(filters!=null && filters.length>0)
//				{
//					boolean skip = false;
//					for(ViewerFilter filter : filters)
//					{
//						if(!filter.select(inputFiles_, inputFiles_.getInput(), rootElement))
//						{
//							skip = true;
//							break;
//						}
//					}
//					if(skip)
//					{
//						continue;
//					}
//				}
//				
//				inputFiles_.setSubtreeChecked(rootElement, state);
//			}
//		}
//	}
//	
//	private void setSelectionChecked(boolean state)
//	{
//		ISelection selection = inputFiles_.getSelection();
//		if(selection==null || selection.isEmpty() || !(selection instanceof IStructuredSelection))
//		{
//			return;
//		}
//		
//		IStructuredSelection iss = (IStructuredSelection) selection;
//		if(iss.size() < 1)
//		{
//			return;
//		}
//		
//		for(Object element : iss.toList())
//		{
//			inputFiles_.setChecked(element, state);
//		}
//	}
//	
//	
//	public static class InputFilesContentProvider implements ITreeContentProvider
//	{
//		private Object input_;
//		
//		public void dispose()
//		{}
//
//		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
//		{
//			input_ = newInput;
//		}
//
//		public Object[] getElements(Object inputElement)
//		{
//			return getChildren(inputElement);
//		}
//
//		public Object[] getChildren(Object parentElement)
//		{
//			if(parentElement == null)
//			{
//				parentElement = input_;
//			}
//			
//			if(!(parentElement instanceof InputFileTreeItem) && (parentElement instanceof File || parentElement instanceof IResource))
//			{
//				parentElement = new InputFileTreeItem(parentElement);
//			}
//			
//			if(!(parentElement instanceof InputFileTreeItem))
//			{
//				return new Object[0];
//			}
//			
//			InputFileTreeItem parentTreeItem = (InputFileTreeItem) parentElement;
//			
//			return parentTreeItem.getChildren().toArray();
//		}
//
//		public Object getParent(Object element)
//		{
//			if(!(element instanceof InputFileTreeItem))
//			{
//				return null;
//			}
//			
//			InputFileTreeItem item = (InputFileTreeItem) element;
//			if(item.equals(input_))
//			{
//				return null;
//			}
//			
//			if(item.subPath.segmentCount() < 2)
//			{
//				return null;
//			}
//			IPath parentPath = item.subPath.removeLastSegments(2);
//			if(item.element instanceof File)
//			{
//				return new InputFileTreeItem(((File) item.element).getParentFile(), parentPath);
//			} else if(item.element instanceof IResource) {
//				return new InputFileTreeItem(((IResource) item.element).getParent(), parentPath);
//			} else {
//				return null;
//			}
//		}
//
//		public boolean hasChildren(Object element)
//		{
//			Object[] children = getChildren(element);
//			return children!=null && children.length>0;
//		}
//	}
//	
//	private static String getName(Object element)
//	{
//		if(element instanceof InputFileTreeItem)
//		{
//			element = ((InputFileTreeItem) element).element;
//		}
//		
//		if(element instanceof File)
//		{
//			return ((File) element).getName();
//		} else if(element instanceof IResource) {
//			return ((IResource) element).getName();
//		}
//		
//		return null;
//	}
//	
//	private static String getFileFullPath(Object element)
//	{
////		String filePath = "";
//		if(element instanceof InputFileTreeItem)
//		{
//			element = ((InputFileTreeItem) element).element;
//		}
//		
//		if(element instanceof File)
//		{
//			return ((File) element).getAbsolutePath();
//		} else if(element instanceof IResource) {
//			return ((IResource) element).getLocation().toOSString();
//		}
//		
//		return null;
//	}	
//	
//	public static class InputFileTreeItem
//	{
//		public final Object element;
//		public final IPath subPath;
//		
//		public InputFileTreeItem(Object element)
//		{
//			if(!(element instanceof File || element instanceof IResource))
//			{
//				throw new IllegalArgumentException("Element must be either a java.io.File or an IResource: "+element);
//			}
//			
//			this.element = element;
//			this.subPath = new Path("");
//		}
//		
//		public InputFileTreeItem(Object element, IPath parentPath)
//		{
//			this.element = element;
//			if(element instanceof File)
//			{
//				this.subPath = parentPath.append(((File) element).getName());
//			} else if(element instanceof IResource) {
//				this.subPath = parentPath.append(((IResource) element).getName());
//			} else {
//				throw new IllegalArgumentException("Element must be either a java.io.File or an IResource: "+element);
//			}
//		}
//		
//		public boolean isFile()
//		{
//			if(element instanceof File)
//			{
//				return ((File) element).isFile();
//			}
//			
//			return element instanceof IFile;
//		}
//		
//		public boolean hasChildFile()
//		{
//			if(isFile())
//			{
//				return true;
//			}
//			
//			List<InputFileTreeItem> children = getChildren();
//			if(children==null || children.size()<1)
//			{
//				return false;
//			}
//			
//			for(InputFileTreeItem child : children)
//			{
//				if(child.hasChildFile())
//				{
//					return true;
//				}
//			}
//			
//			return false;
//		}
//		
//		public List<InputFileTreeItem> getChildren()
//		{
//			Object[] children = null;
//			if(element instanceof File)
//			{
//				File file = (File) element;
//				if(file.isDirectory())
//				{
//					children = file.listFiles();
//				}
//			}
//			
//			if(element instanceof IContainer)
//			{
//				IContainer container = (IContainer) element;
//				try {
//					children = container.members();
//				} catch (CoreException ce) {
////					JobSubmissionPlugin.getDefault().logError(ce.getStatus());
//					//FIXME need to create an activator for the plugin, so we have access to the logs
//					ce.printStackTrace();
//				}
//			}
//			
//			List<InputFileTreeItem> retChildren = new ArrayList<InputFileTreeItem>();
//			if(children!=null && children.length>0)
//			{
//				for(Object child : children)
//				{
//					retChildren.add(new InputFileTreeItem(child, subPath));
//				}
//			}
//			return retChildren;
//		}
//		
//		@Override
//		public int hashCode()
//		{
//			return subPath.hashCode();
//		}
//		
//		@Override
//		public boolean equals(Object obj)
//		{
//			if(!(obj instanceof InputFileTreeItem))
//			{
//				return false;
//			}
//			
//			if(obj == this)
//			{
//				return true;
//			}
//			
//			InputFileTreeItem item = (InputFileTreeItem) obj;
//			return element.equals(item.element) && subPath.equals(item.subPath);
//		}
//		
//		@Override
//		public String toString()
//		{
//			return subPath + " (" + element + ")";
//		}
//	}
//	
//	public static class InputFileTreeItemComparator implements Comparator<Object>
//	{
//		public int compare(Object o1, Object o2)
//		{
//			String s1 = String.valueOf(o1);
//			String s2 = String.valueOf(o2);
//			
//			return s1.toLowerCase().compareTo(s2.toLowerCase());
//		}
//	}
//	
//	public static class InputFilesLabelProvider extends BaseLabelProvider implements ILabelProvider
//	{
//		public Image getImage(Object element)
//		{
//			return null;
//		}
//
//		public String getText(Object element)
//		{
//			String name = getName(element);
//			return name!=null ? name : "";
//		}
//	}
//	
//	public static class InputDeckFilter extends ViewerFilter
//	{
//		private String inputDeckName_;
//		
//		public void setInputDeckName(String inputDeckName)
//		{
//			this.inputDeckName_ = inputDeckName;
//		}
//		
//		@Override
//		public boolean select(Viewer viewer, Object parentElement, Object element)
//		{
//			if(StringUtils.isBlank(inputDeckName_) || !(element instanceof InputFileTreeItem))
//			{
//				return true;
//			}
//			
//			InputFileTreeItem item = (InputFileTreeItem) element;
//			
//			if(!item.isFile())
//			{
//				return true;
//			}
//			
//			IPath subPath = item.subPath;
//			return subPath.segmentCount()!=1 || !StringUtils.equalsIgnoreCase(inputDeckName_, subPath.toString());
//		}
//	}	
//	
//	
//	public static class EmptyFolderFilter extends ViewerFilter
//	{
//		@Override
//		public boolean select(Viewer viewer, Object parentElement, Object element)
//		{
//			if(!(element instanceof InputFileTreeItem))
//			{
//				return true;
//			}
//			
//			InputFileTreeItem item = (InputFileTreeItem) element;
//			
//			return item.hasChildFile();
//		}
//	}	
//	
//	public void loadCheckState(Object localDirObj, String inputFilesString) throws CoreException
//	{
//		// preserve the initial expanded state
//		Object[] expanded = inputFiles_.getExpandedElements();
//		
//		// gather up a list of the actual nodes that need to be selected
//		ArrayList<InputFileTreeItem> selectedItems = new ArrayList<InputFileTreeItem>();
//		
//		// cache what we've found so far for efficiency since the InputFileTreeItem has no persistence
//		Map<IPath, InputFileTreeItem> cachedItems = new HashMap<IPath, InputFileTreeItem>();
//		
//		// get the local directory
//		// it had better be an actual directory
////		Object localDirObj = JobUtils.getLocalDir(configuration);
//		if(localDirObj instanceof IContainer || localDirObj instanceof File)
//		{
//			// get the input files and make sure we actually have some
////			String inputFilesString = configuration.getAttribute(INPUT_FILES, (String) null);
//			if(StringUtils.isNotBlank(inputFilesString))
//			{
//				// break apart the input file sub-paths
//				String[] inputFilesArray = inputFilesString.split("\n");
//				// make sure we have at least one
//				if(inputFilesArray!=null && inputFilesArray.length>0)
//				{
//					// create the root item and cache it
//					InputFileTreeItem rootItem = new InputFileTreeItem(localDirObj);
//					cachedItems.put(rootItem.subPath, rootItem);
//					selectedItems.ensureCapacity(inputFilesArray.length);
//					
//					// go through the input file sub-paths
//					for(String inputFileString : inputFilesArray)
//					{
//						IPath subPath = new Path(inputFileString);
//						// look for it straight away and see if we find it
//						InputFileTreeItem foundItem = cachedItems.get(subPath);
//						if(foundItem != null)
//						{
//							selectedItems.add(foundItem);
//							continue;
//						}
//						InputFileTreeItem parentItem = rootItem;
//						// walk down the sub-path and find the tree items, looking in the cache first
//						for(int i=0; i<subPath.segmentCount(); i++)
//						{
//							IPath subSubPath = subPath.uptoSegment(i+1);
//							foundItem = cachedItems.get(subSubPath);
//							if(foundItem == null)
//							{
//								// didn't find it, so go through all the parent's children to find it
//								for(InputFileTreeItem child : parentItem.getChildren())
//								{
//									cachedItems.put(child.subPath, child);
//									
//									// if this matches
//									if(child.subPath.equals(subSubPath))
//									{
//										foundItem = child;
//									}
//								}
//							}
//							
//							if(foundItem != null)
//							{
//								// if we're on the actual file
//								if(subPath.equals(foundItem.subPath))
//								{
//									// this is the guy, go ahead and add it to the selected
//									selectedItems.add(foundItem);
//								} else {
//									// we just found the next parent, so set that and keep going
//									parentItem = foundItem;
//								}
//							} else {
//								// if we still didn't find this thing, bail out, because we're not going to now
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
//		
//		inputFiles_.setCheckedElements(selectedItems.toArray());
//		
//		// restore the initial expanded state
//		inputFiles_.setExpandedElements(expanded);		
//	}
//	
//	public void addCheckStateListener(ICheckStateListener listener) {
//		inputFiles_.addCheckStateListener(listener);
//	}
//	
//	public void addCheckAllButtonSelectionListener(SelectionAdapter selectionAdapter) {
//		checkAllB.addSelectionListener(selectionAdapter);		
//	}		
//	
//	public void addUnCheckAllButtonSelectionListener(SelectionAdapter selectionAdapter) {
//		uncheckAllB.addSelectionListener(selectionAdapter);
//	}
//	
//	public void setContentProvider(InputFilesContentProvider contentProvider) {
//		inputFilesContentProvider_ = contentProvider;
//		inputFiles_.setContentProvider(contentProvider);
//	}
//	
//	public void setLabelProvider(IBaseLabelProvider labelProvider) {
//		inputFiles_.setLabelProvider(labelProvider);
//	}
//	
//	public void setComparator(ViewerComparator comparator) {
//		inputFiles_.setComparator(comparator);
//	}
//	
//	public void addFilter(ViewerFilter filter) {
//		inputFiles_.addFilter(filter);
//	}
//	
//	public void setInput(Object object) {
//		inputFiles_.setInput(object);
//	}
//	
//	public void refresh() {
//		inputFiles_.refresh();
//	}	
//	
//	public Object[] getCheckedElements() {
//		return inputFiles_.getCheckedElements();
//	}	
//	
//	public String[] getCheckedElementsAsString() {
//		Object[] checkedElements = inputFiles_.getCheckedElements();
//		List<String> stringElements = new LinkedList<String>();
//		
//		for(Object element : checkedElements) {
//			stringElements.add(getFileFullPath(element));
//		}
//		
//		return (String[])stringElements.toArray(new String[0]);
//	}
//	
//	public Object getInput() {
//		return inputFiles_.getInput();
//	}
//}
