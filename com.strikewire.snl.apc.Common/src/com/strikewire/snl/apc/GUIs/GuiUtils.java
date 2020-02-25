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
 * Created by Marcus Gibson
 * On Jan 5, 2006 at 3:41:37 PM
 */
package com.strikewire.snl.apc.GUIs;

import java.util.EventObject;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.ide.ResourceUtil;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.util.ResourceUtils;

/**
 * <p>Utilities for working with the GUI, and which may utilize 
 * eclise resources, but not domain objects.</p>
 * 
 * @author Marcus Gibson
 * @author kholson
 *
 */
public class GuiUtils
{
  /**
   * _wbLabelProvider - A workbench label provider 
   */
  private static final WorkbenchLabelProvider _wbLabelProvider =
      new WorkbenchLabelProvider();  

  
	public static Label addSeparatorToGrid(Composite parent, int numCols)
	{ return addSeparatorToGrid(parent, numCols, null); }
	
	public static Label addSeparatorToGrid(Composite parent, int numCols, Integer heightHint)
	{
		Label sep = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = numCols;
		if(heightHint != null)
		{
			data.heightHint = heightHint;
		}
		sep.setLayoutData(data);
		return sep;
	}
	
	public static Label newLabel(Composite parent, String msg, int style)
	{
		Label l = new Label(parent, style);
		l.setText(msg);
		return l;
	}
	
	public static Button newButton(Composite parent, String text, int style)
	{
		Button btn = new Button(parent, style);
		btn.setText(text);
		return btn;
	}

	public static int showMessage(final String title, final String message, final int style)
	{
		final int[] choice = new int[1];
		
		// give any progress monitors a chance to pop up.
		try { Thread.sleep(1000); } catch (InterruptedException e) {}
		Display.getDefault().syncExec(new Runnable() {
			
			@Override
      public void run() {
				MessageBox mb = new MessageBox(getShell(), style);
				mb.setText(title);
				mb.setMessage(message);
				choice[0] = mb.open();
			}
		});
		
		return choice[0];
	}
	
	public static Shell getShell()
	{
		Shell retShell = getShell(Display.getCurrent());
		if(retShell == null)
			retShell = getShell(Display.getDefault());
		return retShell;
	}
	
	public static Shell getShell(Display display)
	{
		if(display == null)
			return null;
		
		Shell retShell = null;
		try {
			retShell = display.getActiveShell(); 
		} 
		catch (Exception e) {
			// Nothing
		}
		if(retShell == null)
		{
			Shell[] shells = display.getShells();
			if(shells != null)
			{
				int i = shells.length-1;
				while(i>=0 && retShell==null)
					retShell = shells[i--];
			}
		}
		
		return retShell;
	}
	
	/**
	 * Method opens a file chooser where the user is forced to pick a single file.
	 * 
	 * @param shell - the parent shell for the dialog.  Must not be null.
	 * @param initialPath - the initial path the chooser should be opened to, can be null
	 * @return path - the path of the file selected by the user, or null if the user cancelled
	 */
	public static IPath openFileBrowser(Shell shell, IPath initialPath)
	{
		return openFileBrowser(shell, initialPath, null);
	}

	public static IPath openFileBrowser(Shell shell, IPath initialPath, String title)
	{
		FileDialog fileDialog = new FileDialog(shell, SWT.OPEN | SWT.SINGLE);
		if(title != null)
		{
			fileDialog.setText(title);
		}

		if(initialPath != null)
		{
			fileDialog.setFilterPath(initialPath.toOSString());
		}
		
		String retPath = fileDialog.open();
		
		return retPath==null ? null : new Path(retPath);
	}
	
	/**
	 * Method opens a file chooser where the user is forced to pick a single directory.
	 * 
	 * @param shell - the parent shell for the dialog.  Must not be null.
	 * @param initialPath - the initial path the chooser should be opened to, can be null
	 * @return path - the path of the directory selected by the user, or null if the user cancelled
	 */
	public static IPath openDirectoryBrowser(Shell shell, IPath initialPath)
	{
		DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN | SWT.SINGLE);

		if(initialPath != null)
			fileDialog.setFilterPath(initialPath.toOSString());
		
		String retPath = fileDialog.open();
		
		return retPath==null ? null : new Path(retPath);
	}
	
	/**
	 * Method opens a file chooser where the user is forced to pick a single file.
	 * 
	 * @param shell - the parent shell for the dialog.  Must not be null.
	 * @param fields - an ordered list of fields to use for the initial path of the chooser
	 * @return path - the path of the file selected by the user, or null if the user cancelled
	 * 
	 * @see #openFileBrowser(Shell, IPath)
	 * @see #getInitialPath(List)
	 */
	public static IPath openFileBrowser(Shell shell, Text[] fields)
	{ return openFileBrowser(shell, getInitialPath(fields)); }
	
	/**
	 * Method opens a file chooser where the user is forced to pick a single directory.
	 * 
	 * @param shell - the parent shell for the dialog.  Must not be null.
	 * @param fields - an ordered list of fields to use for the initial path of the chooser
	 * @return path - the path of the file selected by the user, or null if the user cancelled
	 * 
	 * @see #openDirectoryBrowser(Shell, IPath)
	 * @see #getInitialPath(List)
	 */
	public static IPath openDirectoryBrowser(Shell shell, Text[] fields)
	{ return openDirectoryBrowser(shell, getInitialPath(fields)); }
	
	private static IPath getInitialPath(Text[] fields)
	{
		// make sure we weren't given a null list
		if(fields == null)
			return null;
		
		for(Text field : fields)
		{
			// make sure the list doesn't have a null object
			if(field == null)
				continue;
			
			// get the text from the field
			String t = null;
			try {
				t = field.getText();
			} catch (Exception e) {
			}
			
			// if we were able to get some actual text out of the field
			if(t!=null && !t.trim().equals(""))
				return new Path(t); // create a path from it
		}
		
		// if we haven't found anything, then use the currently selected project directory
		ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection("com.strikewire.snl.apc.projectexplorer.views.ProjectExplorerView");
		if(selection==null || selection.isEmpty() || !(selection instanceof IStructuredSelection))
			return null;
		
		IStructuredSelection iss = (IStructuredSelection) selection;
		Object obj = iss.getFirstElement();
		if (obj instanceof IAdaptable) {
			IAdaptable ad = (IAdaptable) obj;
			Object res = ad.getAdapter(IResource.class);
			if(res instanceof IFolder || res instanceof IProject)
				return ((IResource)res).getLocation();
			else if(res instanceof IFile)
				return ((IResource)res).getLocation().removeLastSegments(1);
		}
		
		// if we didn't find anything in the list of fields, return null
		return null;
	}
	
	/**
     * Brings the corresponding editor to top if the selected resource is open.
     */
    public static void linkToEditor(IWorkbenchPage page, ISelection selection)
    {
    	if(page==null || selection==null || !(selection instanceof IStructuredSelection))
    	{
    		return;
    	}
    	
    	Object element = ((IStructuredSelection) selection).getFirstElement();
    	if(element==null || !(element instanceof IAdaptable))
    	{
    		return; 
    	}
    	
    	Object res = ((IAdaptable) element).getAdapter(IResource.class);
    	if(res==null || !(res instanceof IFile))
    	{
    		return;
    	}
    	
    	IEditorPart editor = ResourceUtil.findEditor(page, (IFile) res);
    	if(editor != null)
    	{
    		page.bringToTop(editor);
    	}
    }
    
	public static void setFontStyle(Control c, int style) {
		FontData[] fontData = c.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setStyle(style);
		}
		Font newFont = new Font(c.getDisplay(), fontData);
		c.setFont(newFont);
	}
	
	/**
	 * A beautifully simple way to add cell traversal for a JFace TableViewer. Control-TAB tabs vertically; TAB and shift-TAB tab horizontally.
	 * @param viewer
	 */
	public static void addTabBehaviorToTableViewer(TableViewer viewer) {
		TableViewerFocusCellManager focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));

		ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(viewer) {
			@Override
			protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
				if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION) {
					EventObject source = event.sourceEvent;
					if (source instanceof MouseEvent && ((MouseEvent)source).button == 3)
						return false;
				}
				return super.isEditorActivationEvent(event) || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR);
			}
		};

		TableViewerEditor.create(viewer, focusCellManager, activationSupport,
				ColumnViewerEditor.TABBING_HORIZONTAL | 
				ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | 
				ColumnViewerEditor.TABBING_VERTICAL |
				ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}

	
	/**
	 * <p>
	 * Creates a dummy resource for the specified filename in the
	 * "hidden project", and returns the image from the WorkbenchLabelProvider
	 * based upon this potential resource. The resource is not actually created,
	 * but acts as if it were for the purposes of obtaining the icon.
	 * </p>
	 * 
	 * 
	 * @param filename
	 *            A name which could potentially exist as a resource, and for
	 *            which an icon from the WorkbenchLabelProvider will be
	 *            returned. Could return null.
	 * @return The icon from the WorkbenchLabelProvider; could be null.
	 * @author kholson
	 *         <p>
	 *         Initial Javadoc date: Mar 11, 2013
	 *         <p>
	 *         Permission Checks:
	 *         <p>
	 *         History:
	 *         <ul>
	 *         <li>(kholson): created</li>
	 *         </ul>
	 *         <br />
	 */
	public static Image getImageForResourceFile(final String filename) throws CoreException
	{
		IProject proj = ResourceUtils.getHiddenProject();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = proj.getLocation();
		IPath fPath = path.append("/" + filename);
		IResource resource;
		resource = root.getFile(fPath);
		return _wbLabelProvider.getImage(resource);
	}
	
	public static void refreshTreeViewer(TreeViewer viewer)
	{
		refreshTreeViewer(viewer, true);
	}
	
	public static void refreshTreeViewer(TreeViewer viewer, boolean preserveExpanded)
	{
		viewer.getControl().setRedraw(false);
		Object[] expanded = null;
		if(preserveExpanded)
		{
			expanded = viewer.getExpandedElements();
		}
		viewer.refresh();
		if(preserveExpanded)
		{
			viewer.setExpandedElements(expanded);
		}
		viewer.getControl().setRedraw(true);
	}
	
	public static void runInUIThread(Runnable runnable)
	{
		runInUIThread(runnable, false);
	}
	
	public static void runInUIThread(Runnable runnable, boolean sync)
	{
		Display disp = Display.getDefault();
		// TODO watch for null display?
		
		if(disp.getThread() == Thread.currentThread())
		{
			runnable.run();
		} else {
			if(sync)
			{
				disp.syncExec(runnable);
			} else {
				disp.asyncExec(runnable);
			}
		}
	}
	
	
	/**
	 * Removes all the children of the specified composite recursively
	 * @deprecated Use CompositeUtils.removeChildrenFromComposite
	 */
  @Deprecated
  public static void clearComposite(Composite composite)
  {
    CompositeUtils.removeChildrenFromComposite(composite, true);
  }

	/**
	 *  See DTA-8905. On Linux (only) putting up a confirmation dialog like this will remove the selection information from the event context -- 
	 * so we save it and put it back.
	 * But then see DTA-10322. Messing with the selection variable  broke handler identification on Eclipse 4.
	 */	
	public static IStatus confirmButPreserveSelection(Shell shell, String title, String prompt, ExecutionEvent event) {
			if (!MessageDialog.openConfirm(shell, title, prompt)) {
				return Status.CANCEL_STATUS;
			} else {
				return Status.OK_STATUS;			
			}
	}

	public static void centerOn(Shell shell, Shell parent) {
		Point parentLoc = parent.getLocation();
		Point parentSize = parent.getSize();
		Point childSize = shell.getSize();
		Point childLoc = new Point(parentLoc.x + parentSize.x/2 - childSize.x/2,
				parentLoc.y + parentSize.y/2 - childSize.y/2
				);
		shell.setLocation(childLoc);
	}
	
	public static ImageHyperlink addCommandHyperlink(FormToolkit toolkit, Composite parent, final String cmdID, ImageDescriptor imgDesc, String txt, String desc)
	{
		ICommandService cs = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		if(!cs.getDefinedCommandIds().contains(cmdID))
		{
			return null;
		}
		
		Image img = null;
		if(imgDesc != null)
		{
			img = imgDesc.createImage();
		}

		ImageHyperlink link = toolkit.createImageHyperlink(parent, SWT.NONE);
		if(img != null)
		{
			link.setImage(img);
		}
		link.addHyperlinkListener(new HyperlinkAdapter(){
			@Override
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
				try
				{
					handlerService.executeCommand(cmdID, null);
				}
				catch (Throwable t) {
					if(t instanceof CoreException)
					{
						CommonPlugin.getDefault().log(((CoreException) t).getStatus());
					} else {
						CommonPlugin.getDefault().logError(t);
					}
				}
			}
		});
		link.setToolTipText(desc);
		link.setText(txt);
		return link;
	}
}
