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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

class RuntimeDirectoryPage extends WizardPage {
	private Composite container;
	private Text mText;
	private Button mButton;
	private IPath suggested;

	public RuntimeDirectoryPage(IPath suggested) {
		super("Choose Directory");
		setTitle("Choose Runtime Directory");
		setDescription("Choose a location for the workflow's runtime directory");
		this.suggested = suggested;
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);

		mText = new Text(container, SWT.SINGLE | SWT.BORDER);
		mText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				File location = new File(mText.getText());
				setPageComplete(validateLocation(location));
			}

		});
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		mText.setLayoutData(gd);
		mText.setText(suggested.toPortableString());
		mButton = new Button(container, SWT.NONE);
		mButton.setText("...");
		mButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(mButton.getShell(),  SWT.OPEN | SWT.SINGLE);
				dlg.setText("Choose Runtime Directory");
				dlg.setFilterPath(suggested.toPortableString());
				String path = dlg.open();
				if (path == null) return;
				mText.setText(path);
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
		return new Path(mText.getText());
	}
}
