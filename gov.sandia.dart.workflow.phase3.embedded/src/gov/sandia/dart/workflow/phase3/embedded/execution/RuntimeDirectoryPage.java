/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.execution;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;

import gov.sandia.dart.workflow.editor.SubdirSelectionCombo;

class RuntimeDirectoryPage extends WizardPage {
	private Composite container;
	private SubdirSelectionCombo mCombo;
	private Button mButton;
	private IFile workflowFile;
	private IPath defaultPath;

	public RuntimeDirectoryPage(IFile workflowFile, IPath defaultPath) {
		super("Choose Directory");
		setTitle("Choose Runtime Directory");
		setDescription("Choose a location for the workflow's runtime directory");
		this.workflowFile = workflowFile;
		this.defaultPath = defaultPath;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		CCombo combo = new CCombo(container, SWT.BORDER);
		mCombo = new SubdirSelectionCombo(combo);
		mCombo.getCCombo().addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				File location = new File(mCombo.getCCombo().getText());
				setPageComplete(validateLocation(location));
			}
		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		mCombo.getCCombo().setLayoutData(gd);
		mCombo.setInput(workflowFile);
		
		if(mCombo.getItemCount() <= 0) {
			mCombo.setText(defaultPath.toPortableString());
		}
		
		mButton = new Button(container, SWT.NONE);
		mButton.setText("...");
		mButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(mButton.getShell(),  SWT.OPEN | SWT.SINGLE);
				dlg.setText("Choose Runtime Directory");
				dlg.setFilterPath(defaultPath.toPortableString());
				String path = dlg.open();
				if (path == null) return;
				mCombo.getCCombo().setText(path);
				setPageComplete(validateLocation(new File(path)));
			}
		});
		setControl(container);
		
		setPageComplete(true);
	}

	private boolean validateLocation(File location) {
		return (location.isDirectory() && location.canWrite()) ||
				(!location.exists() && location.getParentFile().exists() && location.getParentFile().canWrite());
	}

	public IPath getPath() {
		return mCombo.getPath();
	}
}
