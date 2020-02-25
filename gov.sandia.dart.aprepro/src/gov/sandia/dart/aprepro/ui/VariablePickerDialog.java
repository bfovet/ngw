/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.ui;

import gov.sandia.dart.aprepro.util.ApreproUtil;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class VariablePickerDialog extends TitleAreaDialog {

	private Map<String, ApreproVariableData> list;
	private String selectedText;
	private String variableName = "";
	private String variableValue = "";
	private String commentValue = "";
	private Text commentText;
	private final static String INFO_MESSAGE = "Enter a name and value to create an APREPRO variable";
		
	public VariablePickerDialog(Shell parentShell, String selectedText, Map<String, ApreproVariableData> list) {
		super(parentShell);
		this.list = list;
		this.selectedText = selectedText;
		this.variableValue = this.selectedText;
		
		setHelpAvailable(false);
	}

	@Override
	public void create() {

		super.create();
		setMessage(INFO_MESSAGE);
		setTitle("Specify variable name");
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite gui = new Composite(composite, SWT.NONE);
		gui.setLayout(new GridLayout(2, false));
		gui.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label label = new Label(gui, SWT.NONE);
		label.setText("Variable name:");

		final Text nameText = new Text(gui, SWT.BORDER);
		nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label vallabel = new Label(gui, SWT.NONE);
		vallabel.setText("Variable value:");

		final Text valueText = new Text(gui, SWT.BORDER);
		valueText.setText(selectedText);
		valueText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label commentlabel = new Label(gui, SWT.NONE);
		commentlabel.setText("Comment character:");

		//retrieve current comment character
		commentValue = ApreproUtil.getCommentCharacter();
		
		commentText = new Text(gui, SWT.BORDER);
		commentText.setText(commentValue);
		commentText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));		

		nameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				variableName = nameText.getText();
				setButtonEnablement();
			}
		});
		
		nameText.addVerifyListener(new VerifyListener() {
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = e.character != ' ';
			}
		});

		valueText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				variableValue = valueText.getText();
				setButtonEnablement();
			}
		});
		
		commentText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				commentValue = commentText.getText();
				setButtonEnablement();
			}
		});
		
		return composite;
	}

	public void setButtonEnablement() {
		int enable = IMessageProvider.NONE;

		if (variableName.length() == 0) {
			setMessage("Must specify variable name", IMessageProvider.ERROR);
			enable = IMessageProvider.ERROR;
		} else if (!Pattern.matches("[a-zA-Z0-9_]*", variableName)) {
			setMessage("Variable names can only contain alphanumeric characters and underscores", IMessageProvider.ERROR);
			enable = IMessageProvider.ERROR;
		} else if (!Pattern.matches("[_A-Za-z]", variableName.substring(0,1))) {
			setMessage("Variable names must begin with a letter or an underscore", IMessageProvider.ERROR);
			enable = IMessageProvider.ERROR;
		} else if (list.containsKey(variableName)) {
			setMessage("Variable already exists. Enter new variable name.", IMessageProvider.ERROR);
			enable = IMessageProvider.ERROR;
		}  else if (StringUtils.isBlank(variableValue)) {
			setMessage("Variable value is empty", IMessageProvider.WARNING);
			enable = IMessageProvider.WARNING;

		}  else if (StringUtils.isBlank(commentValue)) {
			setMessage("Comment string is empty", IMessageProvider.WARNING);
			enable = IMessageProvider.WARNING;

		}
			
		if (enable == IMessageProvider.NONE) {
			setMessage(INFO_MESSAGE, IMessageProvider.INFORMATION);
		}
		
		getButton(IDialogConstants.OK_ID).setEnabled(enable != IMessageProvider.ERROR);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Describe new variable");
	}

	@Override
	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false);
		return c;
	}

	public String getVariableName() {
		return variableName;
	}

	public String getVariableValue() {
		return variableValue;
	}

	public String getCommentText() {
		return commentValue;
	}

}
