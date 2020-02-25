/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2019 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 *  
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.workflow.phase3.embedded.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.strikewire.snl.apc.properties.PropertiesDialog;

public class EmbeddedExecutionEnvironmentVariableDialog extends PropertiesDialog<EmbeddedExecutionEnvironmentVariable> {

	private Text value_;

	private EmbeddedExecutionEnvironmentVariableDialog(Shell parentShell, MutableEmbeddedExecutionEnvironmentVariable type) {
		super(parentShell, type);
	}

	@Override
	protected boolean isResizable() {
		return true;
	};

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText("Define embedded execution environment variable");
	}

	@Override
	protected void createDialogBody(Composite parent) {
		Composite dialogArea = new Composite(parent, SWT.NONE);

		//		dialogArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true , true));
		//		dialogArea.setLayout(new GridLayout(2, false));

		dialogArea.setLayout(new GridLayout(2, false));
		dialogArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label valueLabel = new Label(dialogArea, SWT.NONE);		
		valueLabel.setText("Value:");		
		//valueLabel.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false));

		value_ = new Text(dialogArea, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		//gd.heightHint = 90;
		value_.setLayoutData(gd);




	}

	@Override
	protected void initFieldValues()
	{
		super.initFieldValues();
		String initialValue = ((MutableEmbeddedExecutionEnvironmentVariable) properties_).getValue();
		value_.setText(initialValue == null ? "" : initialValue);
	}

	@Override
	protected void saveFields()
	{
		super.saveFields();

		String nowValue = value_.getText();
		if (nowValue == null) {
			nowValue = "ZILCH";
		}
		((MutableEmbeddedExecutionEnvironmentVariable) properties_).setValue(nowValue);
	}

	public static int showDialog(Shell shell, MutableEmbeddedExecutionEnvironmentVariable type) {
		EmbeddedExecutionEnvironmentVariableDialog dialog = new EmbeddedExecutionEnvironmentVariableDialog(shell, type);

		return dialog.open();
	}

	@Override
	protected boolean validate(){		
		return super.validate();
	}


}

