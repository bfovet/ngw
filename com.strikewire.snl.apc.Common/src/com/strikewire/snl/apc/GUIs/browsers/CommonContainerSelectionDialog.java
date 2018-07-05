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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.strikewire.snl.apc.resources.CommonResourceUtils;

/**
 * @author mjgibso
 *
 */
public class CommonContainerSelectionDialog extends ContainerSelectionDialog
{
	public static final int OS_BROWSER_ID = IDialogConstants.CLIENT_ID + 1;
	
	public static final String OS_BROWSER_LABEL = "Browse File System";
	
	private final String _initialPath;
	
	public CommonContainerSelectionDialog(Shell parentShell, File initialRoot, String message)
	{
		this(parentShell, initialRoot.getAbsolutePath(), message);
	}
	
	public CommonContainerSelectionDialog(Shell parentShell, IContainer initialRoot, String message)
	{
		this(parentShell, initialRoot.getFullPath().toString(), message);
	}
	
	public CommonContainerSelectionDialog(Shell parentShell, String initialPath, String message)
	{
		super(parentShell, CommonResourceUtils.getContainerForPath(initialPath), true, message);
		
		this._initialPath = initialPath!=null ? initialPath : "";
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
			default:
				super.buttonPressed(buttonId);
				break;
		}
	}
	
	protected int openOSBrowser()
	{
		Shell shell = getShell();
		int shellStyle = shell!=null ? shell.getStyle() : SWT.None;
		String shellText = shell!=null ? shell.getText() : "";
		if(shell == null)
		{
			shell = getParentShell();
		}
		DirectoryDialog dd = new DirectoryDialog(shell, shellStyle);
		dd.setText(shellText);
		dd.setMessage(getMessage());
		dd.setFilterPath(CommonResourceUtils.getExistingFilesystemSegments(new Path(_initialPath)).toOSString());
		String selection = dd.open();
		boolean goodSel = StringUtils.isNotBlank(selection);
		if(goodSel)
		{
			setSelectionResult(new String[] {selection});
		}
		
		int retCode = goodSel ? Window.OK : Window.CANCEL;
		setReturnCode(retCode);
		return retCode;
	}
	
	public String getFirstSelection()
	{
		Object[] selection = getResult();
		if(selection==null || selection.length<1)
		{
			return "";
		}
		
		Object firstSel = selection[0];
		if(firstSel instanceof String)
		{
			return (String) firstSel;
		} else if(firstSel instanceof IResource) {
			return ((IResource) firstSel).getFullPath().toOSString();
		} else if(firstSel instanceof IPath) {
			return ((IPath) firstSel).toOSString();
		}
		
		return "";
	}
}
