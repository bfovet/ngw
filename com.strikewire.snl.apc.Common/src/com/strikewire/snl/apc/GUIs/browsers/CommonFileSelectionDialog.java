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
package com.strikewire.snl.apc.GUIs.browsers;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.strikewire.snl.apc.Common.CommonPlugin;
import com.strikewire.snl.apc.resources.CommonResourceUtils;
import com.strikewire.snl.apc.resources.ICommonFile;
import com.strikewire.snl.apc.util.Globber;

/**
 * @author arothfu
 * @author mjgibso
 *
 */
public class CommonFileSelectionDialog extends ElementTreeSelectionDialog
{
	public static final int OS_BROWSER_ID = IDialogConstants.CLIENT_ID + 2;
	
	public static final String OS_BROWSER_LABEL = "Browse File Sytem";
	
	private final String _initialPath;
	
	private String[] filterExtensions = null;

	private String[] filterNames = null;

	private int selectedExtensionIndex = -1;
	
	private Combo extensionCombo = null;
	
	private Text filenameText = null;
	
	private String selectedFilename = null;	
	
	private boolean allowMultiple = false;

	private int _style;
	
	public CommonFileSelectionDialog(Shell parentShell, File initialRoot, String message)
	{
		this(parentShell, initialRoot.getAbsolutePath(), message);
	}
	
	public CommonFileSelectionDialog(Shell parentShell, IContainer initialRoot, String message)
	{
		this(parentShell, initialRoot.getFullPath().toString(), message);
	}
	
	public CommonFileSelectionDialog(Shell parentShell, String initialPath, String message)
	{
		this(parentShell, initialPath, message, SWT.OPEN);
	}
	
	public CommonFileSelectionDialog(Shell parentShell, String initialPath, String message, int style)
	{
		super(parentShell, new WorkbenchLabelProvider(), new BaseWorkbenchContentProvider());
		setHelpAvailable(false);
		setTitle("File Selection");
		setMessage(message);
		setInput(ResourcesPlugin.getWorkspace().getRoot());
		super.setAllowMultiple(false);
		setValidator(new ISelectionStatusValidator() {
			
			@Override
			public IStatus validate(Object[] selection) {
				if(selection == null || selection.length < 1){
					return new Status(IStatus.ERROR, CommonPlugin.ID, "No file selected");
					
				}
				
				for(Object selected: selection){
					if(isSaving()) {
						if(StringUtils.isEmpty(filenameText.getText())) {
							return new Status(IStatus.ERROR, CommonPlugin.ID, "No filename has been specified");								
						}

						String filename = null;
						String extensionFilter = null;
						
						if(filenameText != null && !filenameText.isDisposed()) {
							filename = filenameText.getText();
						}
						
						if(extensionCombo != null && !extensionCombo.isDisposed()) {
							int extensionIndex = extensionCombo.getSelectionIndex();

							if(extensionIndex >= 0 && extensionIndex < filterExtensions.length){
								extensionFilter = filterExtensions[extensionIndex];												
							}

						}
						
						String fullFilename = buildFileName(selected, filename, extensionFilter);		

						ICommonFile file = CommonResourceUtils.getCommonFileForPath(fullFilename);
						if(file.exists()) {
							return new Status(IStatus.WARNING, CommonPlugin.ID, "Selected file already exists");
						}
						
					}else if(!(selected instanceof IFile) ){
						return new Status(IStatus.ERROR, CommonPlugin.ID, "Selected item is not a file");
					}
				}
				return Status.OK_STATUS;
			}
		});
		
		addFilter(new ViewerFilter(){

				@Override
				public boolean select(Viewer viewer, Object parentElement,
						Object element) {
					if(extensionCombo == null || extensionCombo.getSelectionIndex() < 0){
						return true;
					}
					int selected = extensionCombo.getSelectionIndex();
	
					if(selected >= filterExtensions.length){
						return true;
					}
					String extensionString = filterExtensions[selected];												
						
					if(!(element instanceof IFile))
					{
						return true;
					}						
					IFile file = (IFile) element;
					String name = file.getName();
		
					for(String extension : extensionString.split(";"))
					{
						if(new Globber(extension).glob(name))
						{
							return true;
						}
					}
					
					return false;
				}					
		});
		
		this._initialPath = initialPath;
		this._style = style;
	}
	
	@Override
	public void setAllowMultiple(boolean allowMultiple){
		this.allowMultiple = allowMultiple;
		super.setAllowMultiple(allowMultiple);
	}
	
	/**
	 * @see FileDialog#setFilterExtensions(String[])
	 */
	public void setFilterExtensions(String[] filterExtensions){
		this.filterExtensions = filterExtensions;
	}
	
	/**
	 * @see FileDialog#setFilterNames(String[])
	 */
	public void setFilterNames(String[] filterNames){
		this.filterNames = filterNames;
	}
	
	
	@Override
	public int open()
	{
		if(CommonResourceUtils.isWorkspacePath(_initialPath))
		{
			return super.open();
		} else {
			return openOSBrowser();
		}
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
        Composite composite = (Composite) super.createDialogArea(parent);
        
        
        if(isSaving()) {
        	Composite fileline = new Composite(composite, SWT.NONE);
        	GridLayout gl = new GridLayout();
        	gl.numColumns = 2;
        	fileline.setLayout(gl);
        	GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);        	
        	fileline.setLayoutData(layoutData);

        	Label label = new Label(fileline, SWT.NONE);
        	label.setText("Filename:");
        	label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
        	
        	filenameText = new Text(fileline, SWT.BORDER);
        	filenameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false)); 
        	
        	filenameText.addModifyListener(new ModifyListener() {
				
				@Override
				public void modifyText(ModifyEvent e) {
					// TODO, do a bit of validation
					updateOKStatus();
				}
			});
        	
        	getTreeViewer().addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					Object firstElement = getTreeViewer().getStructuredSelection().getFirstElement();
					
					if(firstElement instanceof IFile) {
						String newFilename = ((IFile) firstElement).getFullPath().lastSegment();
						
						filenameText.setText(newFilename);
					}
				}
			});
        }
		
        if(filterExtensions != null && filterExtensions.length > 0){
        	extensionCombo = new Combo(composite, SWT.READ_ONLY);
        	
        	for(int i = 0; i < filterExtensions.length; i++){
        		String name = filterExtensions[i];
        		
        		if(filterNames != null && filterNames.length > i){
        			name = filterNames[i];
        		}

        		extensionCombo.add(name, i);

        		extensionCombo.addSelectionListener(new SelectionAdapter(){

					@Override
					public void widgetSelected(SelectionEvent e) {
						getTreeViewer().refresh();
					}

            	});
        	}
        	extensionCombo.select(0);
        }

        return composite;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent)
	{
		createButton(parent, OS_BROWSER_ID, OS_BROWSER_LABEL, false);
		
		super.createButtonsForButtonBar(parent);
	}
	
	@Override
	protected void buttonPressed(int buttonId)
	{
		switch(buttonId)
		{
			case OS_BROWSER_ID:
				if(openOSBrowser() == Window.OK)
				{
					close();
				}
				break;
			case OK:
				if(filenameText != null) {
					selectedFilename = filenameText.getText();					
				}
				
				if(extensionCombo != null) {
					selectedExtensionIndex = extensionCombo.getSelectionIndex();
				}
				
				if(isSaving()) {
					ICommonFile file = getFirstSelectionAsCommonFile();
					if(file.exists()) {
						if(!MessageDialog.openQuestion(getShell(), "File Exists", "File '" + file.getName() + "' already exists.\nDo you wish to overwrite?")) {
							break;
						}
					}
				}
				
			default:
				super.buttonPressed(buttonId);
				break;
		}
	}
	
	protected int openOSBrowser()
	{
		Shell shell = getShell();
		int shellStyle = shell!=null ? shell.getStyle() : SWT.None;
		if(allowMultiple){
			shellStyle |= SWT.MULTI;
		}

		String shellText = shell!=null ? shell.getText() : "";
		if(shell == null)
		{
			shell = getParentShell();
		}
		
		shellStyle |= _style;
		FileDialog fd = new FileDialog(shell, shellStyle);
		fd.setText(shellText);
		fd.setFilterExtensions(filterExtensions);
		fd.setFilterNames(filterNames);		
		fd.setFilterPath(CommonResourceUtils.getExistingFilesystemSegments(new Path(_initialPath)).toOSString());
		String selection = fd.open();
		boolean goodSel = StringUtils.isNotBlank(selection);
		if(goodSel)
		{
			setSelectionResult(new String[] {selection});
		}
		
		int retCode = goodSel ? Window.OK : Window.CANCEL;
		setReturnCode(retCode);
		return retCode;
	}
	
	public ICommonFile getFirstSelectionAsCommonFile()
	{
		return CommonResourceUtils.getCommonFileForPath(getFirstSelection());
	}
	
//	/**
//	 * Returns the first selection as a string path to the system file.
//	 * @return
//	 */
//	public String getFirstSelectionAsSystemFile()
//	{
//		Object[] selection = getResult();
//		if(selection==null || selection.length<1)
//		{
//			return "";
//		}
//		
//		Object firstSel = selection[0];
//		if(firstSel instanceof String)
//		{
//			return (String) firstSel;
//		} 
//		
//		IPath path = null;
//		
//		if(firstSel instanceof IResource) {
//			path = ((IResource) firstSel).getLocation();
//		} else if(firstSel instanceof IPath) {
//			path = (IPath) firstSel;
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			IWorkspaceRoot root = workspace.getRoot();
//			IResource resource = root.findMember(path);
//			if (resource != null) {
//				path = resource.getLocation();
//			}
//		}
//
//		if(path == null){
//			return "";
//		}
//		return path.toOSString();
//	}
//
//	/**
//	 * Returns the first selection as an eclipse resource object.
//	 * @param includeHidden If the selected object is a file system file that is not in the workspace,
//	 * then indicate whether or not to link it to a hidden workspace.  If not, null will be returned.
//	 * @return the IResource representation of the selected file.  
//	 * If no file is selected, or an IResource cannot be constructed, then this will return null
//	 */
//	public IResource getFirstSelectionAsResource(boolean includeHidden)
//	{
//		Object[] selection = getResult();
//		if(selection==null || selection.length<1)
//		{
//			return null;
//		}
//
//		Object firstSel = selection[0];
//		if(firstSel instanceof IFile) {
//			 return ((IResource) firstSel);
//		}
//		
//		IResource resource = null;
//		
//		if(firstSel instanceof IPath) {
//			IPath path = (IPath) firstSel;
//			IWorkspace workspace = ResourcesPlugin.getWorkspace();
//			IWorkspaceRoot root = workspace.getRoot();
//			resource = root.findMember(path);
//		} 
//		else if(firstSel instanceof String)
//		{
//			String filePath = (String) firstSel;
//						
//			resource = ResourceUtils.findResourceForPath(filePath, includeHidden);
//			
//			if(resource == null && includeHidden){
//				try {
//					resource = ResourceUtils.getFileForLocation(filePath);
//				} catch (CoreException e) {
//					CommonPlugin.log(e.getStatus());
//				}
//			}
//		} 
//		
//		return resource;
//	}
	
	/**
	 * Returns the first selection as a generic path
	 * @return
	 */
	public String getFirstSelection()
	{		
		Object[] selection = getResult();
		if(selection==null || selection.length<1)
		{
			return "";
		}

		
		Object firstSel = selection[0];
		
		String extensionFilter = null;
		if(selectedExtensionIndex >= 0 && selectedExtensionIndex < filterExtensions.length){
			extensionFilter = filterExtensions[selectedExtensionIndex];												
		}


		return buildFileName(firstSel, selectedFilename, extensionFilter);		
	}

	private String buildFileName(Object firstSel, String filename, String extensionFilter) {
		if(isSaving()) {		
			if(StringUtils.isEmpty(filename)){
				return "";
			}
			
			filename = filename.trim();
			
			if(extensionFilter != null)
			{
				boolean matchesExtension = false;
				String firstExtension = null;
				for(String extension : extensionFilter.split(";"))
				{
					if(firstExtension == null && !extension.endsWith("*") && extension.contains(".")) {
						firstExtension = extension;
					}
					if(new Globber(extension).glob(filename))
					{
						matchesExtension = true; 
						break;
					}
				}
				
				if(!matchesExtension && !StringUtils.isEmpty(firstExtension)) {
					filename = filename + firstExtension.substring(firstExtension.indexOf('.'));
				}
			}
			
			if(firstSel instanceof String) {
				String path = (String) firstSel;
				if(path.endsWith(filename)) {
					return path;
				}
				
				return path + IPath.SEPARATOR + filename;
				
			}else if(firstSel instanceof IFile) {
				return ((IResource) firstSel).getParent().getFullPath().append(filename).toOSString();
			}else if(firstSel instanceof IContainer) {
				return ((IResource) firstSel).getFullPath().append(filename).toOSString();				
			}else if(firstSel instanceof IPath){
				IPath path = (IPath) firstSel;
				
				// Is it the file itself
				if(path.segment(path.segmentCount()-1).compareToIgnoreCase(filename) == 0) {
					return path.toOSString();
				}

				// otherwise append the filename
				return path.append(filename).toOSString();
			}
			
		}else {
			if(firstSel instanceof String)
			{
				return (String) firstSel;
			} else if(firstSel instanceof IResource) {
				return ((IResource) firstSel).getFullPath().toOSString();
			} else if(firstSel instanceof IPath) {
				return ((IPath) firstSel).toOSString();
			}
		}
		
		return "";
	}

private boolean isSaving() {
	return (_style & SWT.SAVE) != 0;
}

}
