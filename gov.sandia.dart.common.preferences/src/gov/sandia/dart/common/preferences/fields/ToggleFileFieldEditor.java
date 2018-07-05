/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.common.preferences.fields;

import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * This class is a simple extension of the jface FileFieldEditor that
 * only performs validation when it is enabled.
 * @author ejranst
 *
 */
public class ToggleFileFieldEditor extends FileFieldEditor {
	
	/**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param parent the parent of the field editor's control
     */
    public ToggleFileFieldEditor(String name, String labelText, Composite parent) {
        this(name, labelText, false, parent);
    }
    
    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param enforceAbsolute <code>true</code> if the file path
     *  must be absolute, and <code>false</code> otherwise
     * @param parent the parent of the field editor's control
     */
    public ToggleFileFieldEditor(String name, String labelText, boolean enforceAbsolute, Composite parent) {
        this(name, labelText, enforceAbsolute, VALIDATE_ON_FOCUS_LOST, parent);
    }
    
    
    /**
     * Creates a file field editor.
     * 
     * @param name the name of the preference this field editor works on
     * @param labelText the label text of the field editor
     * @param enforceAbsolute <code>true</code> if the file path
     *  must be absolute, and <code>false</code> otherwise
     * @param validationStrategy either {@link StringButtonFieldEditor#VALIDATE_ON_KEY_STROKE}
     *  to perform on the fly checking, or {@link StringButtonFieldEditor#VALIDATE_ON_FOCUS_LOST}
     *  (the default) to perform validation only after the text has been typed in
     * @param parent the parent of the field editor's control.
     * @since 3.4
     * @see StringButtonFieldEditor#VALIDATE_ON_KEY_STROKE
     * @see StringButtonFieldEditor#VALIDATE_ON_FOCUS_LOST
     */
    public ToggleFileFieldEditor(String name, String labelText,
            boolean enforceAbsolute, int validationStrategy, Composite parent) {
    	super(name,labelText,enforceAbsolute,validationStrategy,parent);
    }
	 
    
    
    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.FieldEditor#setEnabled(boolean, org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void setEnabled(boolean enabled, Composite parent) {
        super.setEnabled(enabled, parent);
        refreshValidState();
    }
    

	 /**
	  * If the ToggleFileFieldEditor is enabled, perform FileFieldEditor::CheckState()
	  */
	 
	protected boolean checkState() {
		if(isEnabled())
		{
			return super.checkState();
		}
		else{
			clearErrorMessage();
			return true;
		}
	}
	
	/**
	 * 
	 * @return whenter the field is enabled or not
	 */
	public boolean isEnabled(){
		return this.getTextControl().getEnabled();
	}
	

}
