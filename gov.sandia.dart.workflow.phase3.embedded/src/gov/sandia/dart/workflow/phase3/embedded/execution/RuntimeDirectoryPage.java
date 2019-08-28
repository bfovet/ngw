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
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.Label;

import gov.sandia.dart.workflow.editor.SubdirSelectionCombo;

class RuntimeDirectoryPage extends WizardPage {
	private Composite container;
	private SubdirSelectionCombo mCombo;
	private Button mButton;
	private IFile workflowFile;
	private IPath defaultPath;
	private RunEmbeddedWorkflowWizard masterWizard;
	private Button mClearCheckbox;
	
	public RuntimeDirectoryPage(RunEmbeddedWorkflowWizard master, IFile workflowFile, IPath defaultPath) {
		super("Choose Directory");
		setTitle("Choose Runtime Directory");
		setDescription("Choose a location for the workflow's runtime directory");
		masterWizard = master;
		this.workflowFile = workflowFile;
		this.defaultPath = defaultPath;
	}
	
	private void assimilateParamPropsFile(IPath runDir) {
		File paramPropsFile = new File(runDir.toString(), RunEmbeddedWorkflowWizard.EMBEDDED_PARAMS_FILE);
		
		// If it doesn't exist, try using the workflowDir 
		if(!paramPropsFile.exists()) {
			paramPropsFile = new File(workflowFile.getParent().getLocation().append(runDir).toOSString(), RunEmbeddedWorkflowWizard.EMBEDDED_PARAMS_FILE);
		}

		masterWizard.setParametersFromWorkflow();
		masterWizard.updateParametersFromFile(paramPropsFile);
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
				String pathString = mCombo.getCCombo().getText();
				
				if(!pathString.isEmpty()) {
					IPath location = new Path(pathString);
					if (validateLocation(location)) {
						assimilateParamPropsFile(location);
						setPageComplete(true);
					}
				}else {
					setPageComplete(false);
				}
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
				String pathString = dlg.open();
				if (pathString == null) return;
				mCombo.getCCombo().setText(pathString);
				Path path = new Path(pathString);
				if (validateLocation(path)) {
					assimilateParamPropsFile(path);
					setPageComplete(true);
				}
			}
		});
		Label label = new Label(container, SWT.NONE);
		label.setText("Clear workdir before running");
		mClearCheckbox = new Button(container, SWT.CHECK);	
		mClearCheckbox.setSelection(masterWizard.getClearWorkdir());
		mClearCheckbox.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				masterWizard.setClearWorkdir(mClearCheckbox.getSelection());				
			}
			
		});
		setControl(container);		
		
		setPageComplete(true);
	}

	private boolean validateLocation(IPath path) {		
		
		setErrorMessage(null);
		while(!path.isEmpty()) {
			File checkLocation = new File(path.toString());
			
			// If it doesn't exist, try using the workflowDir 
			if(!checkLocation.exists()) {
				checkLocation = new File(workflowFile.getParent().getLocation().append(path).toOSString());
			}
			
			if(checkLocation.exists()) {
				if(!checkLocation.isDirectory()) {
					setErrorMessage("Selected location is not a directory");
					return false;
				}else if(!checkLocation.canWrite()){
					setErrorMessage("Cannot write to the selected location");
					return false;
				}else {
					return true;
				}
			}
			
			
			path = path.removeLastSegments(1);
			
		}
		
		// At this point, we have reached the project directory, which means that it is fine 
		return true;
	}

	public IPath getPath() {
		return mCombo.getPath();
	}
}
