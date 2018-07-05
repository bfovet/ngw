/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.editor.packaging;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

public class JarLocationPage extends WizardPage {
	private Composite container;
	private Text mText;
	private Button mButton;
	private IPath suggested;

	public JarLocationPage(IPath suggested) {
		super("Choose Location");
		setTitle("Choose Location");
		setDescription("Choose a location for the exported file");
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
				File location = new File(mText.getText()).getAbsoluteFile().getParentFile();
				setPageComplete(location.exists());
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
				FileDialog dlg = new FileDialog(mButton.getShell(),  SWT.SAVE  );
				dlg.setText("Save");
				dlg.setFileName(suggested.lastSegment());
				dlg.setFilterPath(suggested.removeLastSegments(1).toPortableString());
				dlg.setOverwrite(true);
				String path = dlg.open();
				if (path == null) return;
				mText.setText(path);
				
				File location = new File(path).getAbsoluteFile().getParentFile();
				setPageComplete(location.exists());
			}
		});
		setControl(container);
		
		setPageComplete(true);

	}

	public IPath getPath() {
		return new Path(mText.getText());
	}
}
