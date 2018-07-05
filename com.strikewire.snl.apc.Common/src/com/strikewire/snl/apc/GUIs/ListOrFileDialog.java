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
import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import com.strikewire.snl.apc.Common.actions.CompareAction;

public class ListOrFileDialog extends ListDialog{
 
	public ListOrFileDialog(Shell parent) {
		super(parent);
	}
		
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		super.createButtonsForButtonBar(parent);
		// Create Browse button
		Button browseButton = createButton(parent, -1, "Choose external file", false);
		// Add SelectionListener
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
				IFile file = CompareAction.selectFile();//open FileDialog
				if (file!=null)//if file is selected
				{
					ArrayList<IFile> result = new ArrayList<IFile>();
					result.add(file);
					setResult(result);
					close();
				}
				
			}
		});
	}
	
}
