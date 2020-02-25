/*******************************************************************************
 * Sandia Analysis Workbench Integration Framework (SAW)
 * Copyright 2018 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
 * Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
 * certain rights in this software.
 * 
 * This software is distributed under the Eclipse Public License.  For more
 * information see the files copyright.txt and license.txt included with the software.
 ******************************************************************************/
package gov.sandia.dart.aprepro.actions;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

public class TextParameterUpdater implements IParameterUpdater {
	Text text_;
	String selectedText = null;
	String originalText = null;
	Point selection = null;
	
	public TextParameterUpdater(Text text){
		text_ = text;
	}
	
	public void initialize() {
		if(text_ != null){
			originalText = text_.getText();
			selectedText = text_.getSelectionText();
			// IF it is a single line text field an nothing is selected, select the whole line
			if(((text_.getStyle() & SWT.MULTI) == 0) && (selectedText == null || selectedText.isEmpty())){
				text_.selectAll();
				selectedText = text_.getSelectionText();
			}
			selection = text_.getSelection();		
		}else{
			originalText = "";
			selectedText = "";
			selection = new Point(0,0);
		}		
	}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.aprepro.actions.IParameterUpdater#getSelectedText()
	 */
	@Override
	public String getSelectedText(){
		return selectedText;
	}

	/* (non-Javadoc)
	 * @see gov.sandia.dart.aprepro.actions.IParameterUpdater#setSelectedText(java.lang.String)
	 */
	@Override
	public void setSelectedText(String value){
		String prefix = originalText.substring(0, selection.x);
		String suffix = originalText.substring(selection.y);
		
		String newText = prefix + value + suffix;
		
		applyNewText(newText);
	}

	protected void applyNewText(String value){
		text_.setText(value);						
	};
}
