/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
/*
 * Created by Marcus J. Gibson on Dec 20, 2006 at 2:21:29 PM
 */
package gov.sandia.dart.common.preferences.fields;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * Ripped off from org.eclipse.search.internal.ui.util.ComboFieldEditor
 * 
 * @author mjgibso
 */
public class ComboFieldEditor extends FieldEditor
{
	/**
	 * The <code>Combo</code> widget.
	 */
	private Combo fCombo;
	
	/**
	 * The value (not the name) of the currently selected item in the Combo widget.
	 */
	private String fValue;
	
	/**
	 * The names (labels) and underlying values to populate the combo widget.  These should be
	 * arranged as: { {name1, value1}, {name2, value2}, ...}
	 */
	private String[][] fEntryNamesAndValues;

	public ComboFieldEditor(String name, String labelText, String[][] entryNamesAndValues, Composite parent) {
		init(name, labelText);
		assert checkArray(entryNamesAndValues);
		fEntryNamesAndValues= entryNamesAndValues;
		createControl(parent);		
	}

	/*
	 * Checks whether given <code>String[][]</code> is of "type" 
	 * <code>String[][2]</code>.
	 *
	 * @return <code>true</code> if it is ok, and <code>false</code> otherwise
	 */
	private boolean checkArray(String[][] table) {
		if (table == null) {
			return false;
		}
		for (int i= 0; i < table.length; i++) {
			String[] array= table[i];
			if (array == null || array.length != 2) {
				return false;
			}
		}
		return true;
	}

	/*
	 * @see FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		Control control= getLabelControl();
		if (control != null) {
			((GridData)control.getLayoutData()).horizontalSpan= numColumns-1;
            numColumns=1;
		}
		((GridData)fCombo.getLayoutData()).horizontalSpan= numColumns;
	}

	/*
	 * @see FieldEditor#doFillIntoGrid(Composite, int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		Control control= getLabelControl(parent);
		GridData gd= new GridData();
		gd.horizontalSpan= numColumns;
		control.setLayoutData(gd);
		control= getComboBoxControl(parent);
		gd= new GridData();
		gd.horizontalSpan= numColumns;
		control.setLayoutData(gd);
	}

	/*
	 * @see FieldEditor#doLoad()
	 */
	protected void doLoad() {
		updateComboForValue(getPreferenceStore().getString(getPreferenceName()));
	}

	/*
	 * @see FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		updateComboForValue(getPreferenceStore().getDefaultString(getPreferenceName()));
	}

	/*
	 * @see FieldEditor#doStore()
	 */
	protected void doStore() {
		if (fValue == null) {
			getPreferenceStore().setToDefault(getPreferenceName());
			return;
		}
	
		getPreferenceStore().setValue(getPreferenceName(), fValue);
	}

	/*
	 * @see FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		return 2;
	}

	/*
	 * Lazily create and return the Combo control.
	 */
	public Combo getComboBoxControl(Composite parent) {
		if (fCombo == null) {
			fCombo= new Combo(parent, SWT.READ_ONLY);
			for (int i= 0; i < fEntryNamesAndValues.length; i++) {
				fCombo.add(fEntryNamesAndValues[i][0], i);
			}
			fCombo.setFont(parent.getFont());
			fCombo.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent evt) {
					String oldValue= fValue;
					String name= fCombo.getText();
					fValue= getValueForName(name);
					setPresentsDefaultValue(false);
					fireValueChanged(VALUE, oldValue, fValue);					
				}
			});
		}
		return fCombo;
	}
	
	/*
	 * Given the name (label) of an entry, return the corresponding value.
	 */
	protected String getValueForName(String name) {
		for (int i= 0; i < fEntryNamesAndValues.length; i++) {
			String[] entry= fEntryNamesAndValues[i];
			if (name.equals(entry[0])) {
				return entry[1];
			}
		}
		return fEntryNamesAndValues[0][0];
	}
	
	/*
	 * Set the name in the combo widget to match the specified value.
	 */
	protected void updateComboForValue(String value) {
		fValue= value;
		for (int i= 0; i < fEntryNamesAndValues.length; i++) {
			if (value.equals(fEntryNamesAndValues[i][1])) {
				fCombo.setText(fEntryNamesAndValues[i][0]);
				return;
			}
		}
		if (fEntryNamesAndValues.length > 0) {
			fValue= fEntryNamesAndValues[0][1];
			fCombo.setText(fEntryNamesAndValues[0][0]);
		}
	}
}
