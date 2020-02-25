/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.pref;

import gov.sandia.dart.aprepro.ApreproPlugin;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;


//FIXME this was not done the most elegant way. i would have loved to use
//		field editors to implement this, but given the requirement that once the user
//		clicks on the 'show params' button, the comment character text field shows up,
//		i had to implement it the way i did. the field editors implemention would not allow
//		me to put a selection handler on a checkbox
public class Parameterization extends PreferencePage implements
		IWorkbenchPreferencePage {

	private Button showParams;
	private Label label;
	private Text comment;
	
//	public static final String SHOW_PARAMS = "show.params.id";
//	public static final String COMMENT_PARAMS = "comment.params.id";
	
	public Parameterization() {
		setPreferenceStore(ApreproPlugin.getDefault().getPreferenceStore());	
	}

	@Override
	public void init(IWorkbench workbench) {	
	}


	@Override
	protected Control createContents(Composite parent) {
		
		boolean show = false;
		String commentChar;
		
		IPreferenceStore store = getPreferenceStore();
		if(store == null) 
		{
			show = false;
			commentChar = "";
		} else 
		{											
//			show = store.getBoolean(SHOW_PARAMS); 
//			commentChar = store.getString(COMMENT_PARAMS);			
		}
		
		Composite main = new Composite(parent, SWT.NONE);
		GridLayout data = new GridLayout();
		data.numColumns = 2;
		data.makeColumnsEqualWidth = false;
		data.horizontalSpacing = 20;
		
		main.setLayout(data);
		main.setLayoutData(GridData.FILL_BOTH);
		
		
		FontData labelFontData = new FontData("Arial", 10, SWT.BOLD);
		Font labelFont = new Font(getShell().getDisplay(), labelFontData);
			
		Label headingLabel = new Label(main, SWT.BOLD);
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.verticalSpan = 2;
		headingLabel.setLayoutData(gd);
		headingLabel.setText("Aprepro");
		headingLabel.setFont(labelFont);

		showParams = new Button(main, SWT.CHECK);
		showParams.setText("Show aprepro parameters at the top of the file");
		showParams.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, true, false, 2, 1));
		showParams.setSelection(show);
		
		showParams.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if(showParams.getSelection())
				{
					label.setVisible(true);
					comment.setVisible(true);
				} else {
					label.setVisible(false);
					comment.setVisible(false);					
				}
			}
		});
		
		label = new Label(main, SWT.NONE);
		label.setText("Comment character: ");
		label.setVisible(false);
		
		comment = new Text(main, SWT.NONE);		
//		comment.setText(commentChar);
		
		GridData commentData = new GridData();
		commentData.widthHint = 150;
		commentData.horizontalAlignment = SWT.BEGINNING;
		commentData.verticalAlignment = SWT.TOP;
		commentData.grabExcessHorizontalSpace = true;
		commentData.grabExcessVerticalSpace = false;
		
		comment.setLayoutData(commentData);
		comment.setVisible(false);
		
		if(show) {
			label.setVisible(true);
			comment.setVisible(true);
		}		
		
		return main;		
	}
	
	@Override
	public boolean performOk() {
		IPreferenceStore store = getPreferenceStore();
		if(store == null)
			return super.performOk();
		
//		store.setValue(SHOW_PARAMS, showParams.getSelection());
//		store.setValue(COMMENT_PARAMS, comment.getText());
		
		return super.performOk();
	}
	
	@Override
	public void performDefaults() {
		super.performDefaults();

		IPreferenceStore store = getPreferenceStore();
		if(store == null)
			return;
		
//		boolean show = store.getDefaultBoolean(SHOW_PARAMS);
//		showParams.setSelection(show);
//		comment.setText(store.getDefaultString(COMMENT_PARAMS));
//		label.setVisible(show);
//		comment.setVisible(show);
	}

}
